/**
 * 
 */
package com.petrolink.mbe.actions;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.alertstatus.Alert;
import com.petrolink.mbe.alertstatus.impl.AlertsDAO;
import com.petrolink.mbe.alertstatus.impl.AlertsService;
import com.petrolink.mbe.codec.JsonObjectCodec;
import com.petrolink.mbe.model.message.AlertSnapshotSummary;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.setting.ActionSource;
import com.petrolink.mbe.setting.ActionSources;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.petrolink.mbe.util.DateTimeHelper;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;
import com.smartnow.engine.nodes.actions.Action;

/**
 * Action for acknowledging alert
 * @author aristo
 *
 */
public class AlertAcknowledgeAction  extends MBEAction {
	private static Logger logger = LoggerFactory.getLogger(AlertAcknowledgeAction.class);
	private ActionSource inputSource;
	private ActionSource timestampSource;
	private String by = "System";
	private AlertsDAO dao = null;
	private AlertsService alertService = null;
	private JsonObjectCodec jsonCodec = null;
	
	/**
	 * Constructor
	 */
	public AlertAcknowledgeAction() {
		jsonCodec = new JsonObjectCodec();
	}
	
	@Override
	public void load(Element e, Node parent) throws EngineException {
		super.load(e, parent);
		
		Namespace ns = e.getNamespace();
		String actionName = this.toString()+" seq "+this.getSequence();
		
		//Parsing Source
		Element messageElement = e.getChild("Source", ns);
		if (messageElement == null) {
			throw new EngineException(actionName + " has no Source defined");
		}
		inputSource = XmlSettingParser.parseActionSource(messageElement);
		
		alertService = ServiceAccessor.getAlertsService(); 
		dao = (alertService != null) ? alertService.getAlertsDAO() : null; 
		
		if (dao == null) {
			throw new EngineException(actionName + " has no AlertDAO");
		}
		
		String timestampKey = e.getAttributeValue("timestampKey");
		if (StringUtils.isNotBlank(timestampKey)) {
			ActionSource source = new ActionSource();
			source.setSourceType(ActionSources.CONTEXT);
			source.setSourceKey(timestampKey);
			timestampSource = source;
		}
		
		String specifiedBy =e.getAttributeValue("by");
		if (StringUtils.isNotBlank(specifiedBy)) {
			by = specifiedBy;
		}
	}

	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		Alert primaryAlert = getAlert(context);
		Object source = null;
		if (inputSource != null) {
			source = inputSource.getSource(context);
		}
		
		if (source != null) {
			OffsetDateTime actionTime = OffsetDateTime.now();
			
			ArrayList<String> alertsToAck = new ArrayList<>();
			if (source instanceof List) {
				List<?> alertList =(List)source;
				for (Object alertSsO : alertList) {
					if (alertSsO instanceof AlertSnapshotSummary) {
						alertsToAck.add(((AlertSnapshotSummary)alertSsO).getInstanceUuid());
					} else if (alertSsO instanceof Map) {
						Map alertMap = (Map)alertSsO;
						Object instanceObj = alertMap.get("instanceUuid");
						if (null != instanceObj) {
							alertsToAck.add(instanceObj.toString());
						}
					}
				}
			}
			
			String parentId = null;
			String parentClassId = null;
			//get Timestamp
			if (primaryAlert != null) {
				actionTime = DateTimeHelper.fromEpochMillis(primaryAlert.getCreated());
				parentId = primaryAlert.getUuid();
				parentClassId = primaryAlert.getClassId();
			}
			
			//Actual Ack
			String currentBy = by;
			dao.acknowledgeAlerts(alertsToAck, currentBy, actionTime, parentId, parentClassId);
		} else {
			logger.warn("Trying to publish null message");
		}
		return Action.SUCCESS;
	}

	@Override
	protected int executeTestAction(Map<String, Object> arg0) throws EngineException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public final void finalize(final Map<String, Object> context) throws EngineException {
		logger.info("Rule {} Alert Acknowledge action, finalized", this.getRuleFlow().getRuleId());
	}

	@Override
	public void init(Map<String, Object> arg0) throws EngineException {
		// TODO Auto-generated method stub
		
	}

	
	
	/**
	 * Cleaning up which can be called by Ruleflow when ruleflow doing deprovision
	 */
	@Override
	public void deprovision() {
		
	}

	/**
	 * @return the by
	 */
	public final String getBy() {
		return by;
	}

	/**
	 * @param by the by to set
	 */
	public final void setBy(String by) {
		this.by = by;
	}
}
