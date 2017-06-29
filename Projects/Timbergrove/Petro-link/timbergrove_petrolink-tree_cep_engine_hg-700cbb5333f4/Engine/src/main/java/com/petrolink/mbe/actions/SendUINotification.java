package com.petrolink.mbe.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.alertstatus.Alert;
import com.petrolink.mbe.alertstatus.impl.AlertImpl;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.PetroVaultPrincipalService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.templates.NotificationTemplateService;
import com.petrolink.mbe.templates.UINotificationTemplate;
import com.petrolink.mbe.util.DateTimeHelper;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;

import Petrolink.SecurityApi.PrincipalDetails;
import freemarker.template.TemplateException;

/**
 * Action to send a notification to the user's UI. This is done through a per-user or per-well channel.
 * @author langj
 *
 */
public final class SendUINotification extends ChannelLogAction {
	private static final Logger logger = LoggerFactory.getLogger(SendUINotification.class);
	
	private PetroVaultPrincipalService principalService;
	private NotificationTemplateService templateService;
	private UUID templateUUID;
	
	@Override
	protected String getChannelLogType() {
		return "UINotification";
	}
	
	@Override
	public void load(Element e, Node parent) throws EngineException {
		super.load(e, parent);
		
		templateService = ServiceAccessor.getNotificationTemplateService();
		principalService = ServiceAccessor.getPVPrincipalService();
		
		Namespace ns = e.getNamespace();
		Element inlineTemplate = e.getChild("InlineTemplate", ns);
		if (inlineTemplate != null) {
			UINotificationTemplate t = new UINotificationTemplate();
			t.load(inlineTemplate);
			
			templateUUID = templateService.storeTemplate(t);
		}
	}
	
	@Override
	protected String getDirectoryPropertyName() {
		return "NotificationLogs";
	}

	@Override
	protected void writeTestLogHeader(BufferedWriter writer) throws Exception {
		writer.write("Timestamp,Data");
		writer.newLine();
	}

	@Override
	protected void writeTestLog(Map<String, Object> context, BufferedWriter writer) throws Exception {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		UINotificationMessage uiNotification = buildNotificationData(context, now);
		JSONObject data = uiNotification.getData();
		
		writer.write(now.toString());
		writer.write(',');
		writer.write(escapeCsv(data.toString()));
		writer.newLine();
	}

	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		UINotificationMessage uiNotification = buildNotificationData(context, now);
		
		Object index = uiNotification.getIndex();
		String dataString = uiNotification.getData().toString();
		
		updateChannelsAsync(index, dataString);
		
		return SUCCESS;
	}

	/**
	 * Build notification from context
	 * @param context
	 * @param now
	 * @return
	 * @throws EngineException
	 */
	private UINotificationMessage buildNotificationData(Map<String, Object> context, OffsetDateTime now) throws EngineException {
		Alert alert = getAlert(context);
		RuleFlow rule = getRuleFlow();
		UUID wellId = rule.getWellId();
		
		JSONObject ruleProperties = new JSONObject(rule.getProperties());
		
		// Backwards compatibility. Well and rig names are now in rule properties
		String wellName = rule.getWellName();
		String rigName = rule.getRigName();
		
		//
		// Process the message template
		//
		
		String title = "";
		String message = "";
		JSONObject variables = null;
		if (templateUUID != null) {
			Map<String, Object> templateContext = createTemplateContext(context);
			
			try {
				// TREE-28 must change to Map otherwise will throw error internal error in freemarker if specific variable is null
				title = templateService.processTemplate(templateUUID, UINotificationTemplate.TITLE_ID, templateContext);
				message = templateService.processTemplate(templateUUID, UINotificationTemplate.BODY_ID, templateContext);
			} catch (IOException | TemplateException e) {
				throw new EngineException("Template parsing failed", e);
			}
			
			Object maybeVars = templateContext.get(TCK_VARIABLES);
			if (maybeVars instanceof Map)
				variables = new JSONObject((Map<?, ?>) maybeVars);
		}

		// These are included in notification even if there is no template
		if (variables == null)
			variables = new JSONObject(createVariablesObject(context));
		
		//
		// Package everything into a JSON object that will be sent to a channel.
		//
		JSONObject data = new JSONObject();
		data.put("timestamp", now.toString());
		attachAlertNotificationData(alert,data);
		data.put("ruleId", UUIDHelper.toStringFast(rule.getRuleId()));
		data.put("ruleName", rule.getRuleName());
		data.put("ruleProperties", ruleProperties);
		data.put("wellId", UUIDHelper.toStringFast(wellId));
		data.put("wellName", wellName);
		data.put("rigName", rigName);
		data.put("variables", variables);
		data.put("title", title);
		data.put("message", message);
		
		UINotificationMessage uiNotification = new UINotificationMessage();
		uiNotification.data = data;
		
		//If alert is defined, alert created is the index, otherwise is current time
		if (alert != null) {
			OffsetDateTime created = DateTimeHelper.fromEpochMillis(alert.getCreated());
			uiNotification.setIndex(created);
		} else {
			uiNotification.setIndex(now);
		}
		
		return uiNotification;
	}
	
	/**
	 * Build Alert Notification based on Alert object
	 * @param alert
	 * @param data
	 */
	private void attachAlertNotificationData(Alert alert, JSONObject data) {
		if (alert == null) return;
		if (data == null) return;
		

		// Required data checklist:
		// x Timestamp
		// x Alert message title
		// x Rule ID (needed from metadata as ruleflow uuid is different)
		// x Alert ID
		// x Variables added in flow
		// x Data Context (Well Name, etc)
		//   Is Acknowledge Avaialble
		//   Is Snooze Available
		//   Is Silence Available
		// x Notification Color (severity, priority)
		//   Tags
		// x Classification
		// x Custom message
		OffsetDateTime created = DateTimeHelper.fromEpochMillis(alert.getCreated());
		OffsetDateTime acknowledged = alert.getAcknowledgeAt() != 0 ? DateTimeHelper.fromEpochMillis(alert.getAcknowledgeAt()) : null;
		OffsetDateTime commented = alert.getCommentedAt() != 0 ? DateTimeHelper.fromEpochMillis(alert.getCommentedAt()) : null;
		OffsetDateTime snoozedAt = alert.getSnoozedAt() != 0 ? DateTimeHelper.fromEpochMillis(alert.getSnoozedAt()) : null;
		long unSnoozeAtLong = alert.getUnSnoozeAt();
		OffsetDateTime unSnoozeAt = unSnoozeAtLong != 0 ? DateTimeHelper.fromEpochMillis(unSnoozeAtLong) : null;
		long unSnoozedAtLong = alert.getUnSnoozedAt();
		OffsetDateTime unSnoozedAt = unSnoozedAtLong != 0 ? DateTimeHelper.fromEpochMillis(unSnoozedAtLong) : null;
		
		String commentMessage = alert.getComment();
		
		String acknowledgedById = alert.getAcknowledgeBy();
		String commentedById = alert.getCommentedBy();
		String snoozedById = alert.getSnoozedBy();
		String unsnoozedById =alert.getUnSnoozedBy();
				
		String acknowledgedByName = getIdentityName(acknowledgedById);
		String commentedByName = getIdentityName(commentedById);
		String snoozedByName = getIdentityName(snoozedById);
		String unsnoozedByName = getIdentityName(unsnoozedById);
		
		String statusString;
		int statusInt = alert.getStatus();
		logger.debug("Going to send alert status: {}", statusInt);
		try {
			statusString = Alert.getStatusName(alert.getStatus());
		} catch (IllegalArgumentException ie) {
			statusString = Integer.toString(alert.getStatus());
			logger.warn("Alert Status value={} is not recognized ", statusInt,ie);
		}
		
		data.put("alertId", alert.getUuid());
		data.put("alertName", alert.getName());
		data.put("alertClass", alert.getClassId());
		data.put("alertDomain", alert.getDomain());
		data.put("alertClassification", alert.getClassification());
		data.put("alertStatus", statusString);
		data.put("alertSeverity", alert.getSeverity());
		data.put("alertPriority", alert.getPriority());
		data.put("alertCreated", created.toString());
		data.put("alertAcknowledged", toStringOrJsonNull(acknowledged));
		data.put("alertAcknowledgedBy", valueOrJsonNull(acknowledgedById));
		data.put("alertAcknowledgedByName", valueOrJsonNull(acknowledgedByName));
		data.put("alertCommented", toStringOrJsonNull(commented));
		data.put("alertCommentedCount", alert.getCommentedCount());
		data.put("alertCommentMessage", toStringOrJsonNull(commentMessage));
		data.put("alertCommentedBy", valueOrJsonNull(commentedById));
		data.put("alertCommentedByName", valueOrJsonNull(commentedByName));
		data.put("alertSnoozed", toStringOrJsonNull(snoozedAt));
		data.put("alertSnoozedBy", valueOrJsonNull(snoozedById));
		data.put("alertSnoozedByName", valueOrJsonNull(snoozedByName));
		data.put("alertUnSnoozeAt", toStringOrJsonNull(unSnoozeAt));
		data.put("alertUnSnoozedAt", toStringOrJsonNull(unSnoozedAt));
		data.put("alertUnSnoozedBy", valueOrJsonNull(unsnoozedByName));
		
		if (alert instanceof AlertImpl) {
			AlertImpl rtAlert = (AlertImpl)alert;
			
			OffsetDateTime investigated = rtAlert.getInvestigateAt() != 0 ? DateTimeHelper.fromEpochMillis(rtAlert.getInvestigateAt()) : null;
			String investigatedById = rtAlert.getInvestigateBy();
			String investigatedByName = getIdentityName(investigatedById);
			data.put("alertInvestigated", toStringOrJsonNull(investigated));
			data.put("alertInvestigatedBy", valueOrJsonNull(investigatedById));
			data.put("alertInvestigatedByName", valueOrJsonNull(investigatedByName));
			
			
		}
	}
	
	private String getIdentityName(String id) {
		if (id == null)
			return null;
		
		try {
			PrincipalDetails d = principalService.getDefaultApiClient().getPrincipalDetail(id);
			return d != null ? d.getName() : null;
		} catch (IOException ex) {
			return null;
		}
	}
	
	private static Object valueOrJsonNull(Object value) {
		return value != null ? value : JSONObject.NULL;
	}
	
	private static Object toStringOrJsonNull(Object value) {
		return value != null ? value.toString() : JSONObject.NULL;
	}
	
	class UINotificationMessage {
		protected Object index;
		protected JSONObject data;
		/**
		 * @return the index
		 */
		protected final Object getIndex() {
			return index;
		}
		/**
		 * @param index the index to set
		 */
		protected final void setIndex(Object index) {
			this.index = index;
		}
		/**
		 * @return the data
		 */
		protected final JSONObject getData() {
			return data;
		}
		/**
		 * @param data the data to set
		 */
		protected final void setData(JSONObject data) {
			this.data = data;
		}
	}
}
