package com.petrolink.mbe.codec;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.petrolink.mbe.alertstatus.impl.AlertImpl;
import com.petrolink.mbe.model.message.AlertOpAcknowledge;
import com.petrolink.mbe.model.message.AlertCepEvent;
import com.petrolink.mbe.model.message.AlertDefinition;
import com.petrolink.mbe.model.message.AlertSnapshot;
import com.petrolink.mbe.model.message.AlertSnapshotSummary;
import com.petrolink.mbe.model.message.AlertOpComment;
import com.petrolink.mbe.model.message.AlertOpInvestigate;
import com.petrolink.mbe.model.message.ContentContainer;
import com.petrolink.mbe.model.message.AlertOpSnooze;
import com.petrolink.mbe.model.message.AlertSimpleMetadata;
import com.petrolink.mbe.model.message.WellParametersSnapshot;
import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.AlertJournal;
/**
 * Converter class for record
 * @author aristo
 *
 */
public class SmartNowCodec {
	
	/**
	 * Convert an Object to content container
	 * @param detailsObject
	 * @return ContentContainer
	 */
	public static ContentContainer toContainer(Object detailsObject) {
		ContentContainer container = new ContentContainer();
		if (detailsObject == null) {
			container = null;
		} else if(detailsObject instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)detailsObject;
			List<Object> objectList = jsonArray.toList();
			HashMap<String, Object> map = new HashMap<>();
			map.put(ContentContainer.DEFAULT_VALUELIST_KEY, objectList);
			
			container.setContentType(objectList.getClass().getName());
			container.setContent(map);
			
		} else if(detailsObject instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) detailsObject;
			
			container.setContentType(ContentType.APPLICATION_JSON.getMimeType());
			container.setContent(jsonObject.toMap());
		} else {
			HashMap<String, Object> map = new HashMap<>();
			map.put(ContentContainer.DEFAULT_VALUE_KEY, detailsObject);
			
			container.setContentType(detailsObject.getClass().getName());
			container.setContent(map);
		}
		return container;
	}
	
	/**
	 * Convert Alert journal to AlertCep Event
	 * @param journal
	 * @return AlertCepEvent
	 */
	public static AlertCepEvent toAlertCepEvent(AlertJournal journal) {
		AlertCepEvent entry = new AlertCepEvent();
		entry.setType(journal.getType());
		entry.setTimestamp(journal.getTimestamp());
		entry.setPrincipal(journal.getPrincipal());
		
		
		AlertSnapshot alertSnapshot = toAlertSnapshot(journal.getAlert());
		entry.setAlert(AlertSnapshotSummary.from(alertSnapshot));
		return entry;
	}
	/**
	 * Convert Back to Petrolink Alert 
	 * @param snapshot
	 * @return PetrolinkAlertImpl
	 */
	public static AlertImpl toPetrolinkAlertImpl(AlertSnapshot snapshot){
		AlertImpl alert = new AlertImpl();
		alert.setUuid(snapshot.getInstanceUuid());
		alert.setStatus(snapshot.getStatus()); 
		alert.setLastStatusChange(snapshot.getLatestStatusChange().toEpochMilli());
		alert.setCreated(snapshot.getCreatedTimestamp().toEpochMilli());
		alert.setLastOccurrence(snapshot.getLatestOccurrence().toEpochMilli());
		
		attachLastAcknowledge(snapshot.getLatestAcknowledgement(), alert);
		attachLastComment(snapshot.getLatestComment(), alert);
		
		alert.setTally(snapshot.getTally());
		alert.setDescription(snapshot.getDescription()); 
		alert.setSeverity(snapshot.getSeverity());
		alert.setPriority(snapshot.getPriority());
	
		attachAlertDetailsRecord(snapshot.getContextDetail(),alert);

		ContentContainer metadata = snapshot.getMetadata();
		if (metadata == null) {
			alert.setMetadata(null);
		} else if (StringUtils.equalsIgnoreCase(ContentType.APPLICATION_JSON.getMimeType(),metadata.getContentType())) {
			alert.setMetadata(new JSONObject(metadata.getContent()));
		} else {
			alert.setMetadata(metadata.getContent().get(ContentContainer.DEFAULT_VALUE_KEY));
		}
		
		attachAlertDefinition(snapshot.getDefinition(), alert);
		
		alert.setCommentedCount(snapshot.getCommentedCount());
		alert.setCreatedIndex(snapshot.getCreatedIndex());
		alert.setLastIndex(snapshot.getLatestIndex());
		attachWellParametersSnapshot(snapshot.getParameters(), alert);
		alert.setOnCreateEventsExecuted(snapshot.isOnCreateEventsExecuted());
		alert.setSnoozed(snapshot.isSnoozed());
		attachLastSnoozed(snapshot.getLatestSnoozed(), alert);
		attachLatestInvestigation(snapshot.getLatestInvestigation(), alert);
		return alert;
	}
	
	/**
	 * Convert AlertImpl to AlertSnapshot
	 * @param alert
	 * @return AlertSnapshot
	 */
	public static AlertSnapshot toAlertSnapshot(Alert alert) {
		AlertSnapshot record = new AlertSnapshot();
		record.setInstanceUuid(alert.getUuid());
		record.setStatus(alert.getStatus()); 
		record.setLatestStatusChange(Instant.ofEpochMilli(alert.getLastStatusChange()));
		record.setCreatedTimestamp(Instant.ofEpochMilli(alert.getCreated()));
		record.setLatestOccurrence(Instant.ofEpochMilli(alert.getLastOccurrence()));
		record.setLatestAcknowledgement(extractLastAcknowledge(alert));
		record.setLatestComment(extractLastComment(alert));
		
		record.setTally(alert.getTally());
		
		record.setDescription(alert.getDescription()); 
		record.setSeverity(alert.getSeverity());
		record.setPriority(alert.getPriority());
	
		record.setContextDetail(extractAlertDetailsRecord(alert));

		Object metadataObject = alert.getMetadata();
		ContentContainer metadata = new ContentContainer();
		if (metadataObject == null) {
			metadata = null;
		} else if(metadataObject instanceof JSONObject) {
			JSONObject metadataJson = (JSONObject) metadataObject;
			metadata.setContentType(ContentType.APPLICATION_JSON.getMimeType());
			metadata.setContent(metadataJson.toMap());
		} else {
			HashMap<String, Object> map = new HashMap<>();
			map.put(ContentContainer.DEFAULT_VALUE_KEY, metadataObject);
			metadata.setContentType(metadataObject.getClass().getName());
			metadata.setContent(map);
		}
		record.setMetadata(metadata);
		
		
		record.setDefinition(extractAlertDefinition(alert));
		record.setOnCreateEventsExecuted(alert.isOnCreateEventsExecuted());
		record.setSnoozed(alert.isSnoozed());
		
		if (alert instanceof AlertImpl) {
			AlertImpl plinkAlert = (AlertImpl)alert;
			record.setCommentedCount(plinkAlert.getCommentedCount());
			record.setCreatedIndex(plinkAlert.getCreatedIndex());
			record.setLatestIndex(plinkAlert.getLastIndex());
			record.setParameters(extractWellParametersSnapshot(plinkAlert));
			record.setLatestSnoozed(extractLastSnoozed(plinkAlert));
			record.setLatestInvestigation(extractLatestInvestigation(plinkAlert));
		}
		
		

		
		return record;
	}
	
	/**
	 * Extract AlertDetailsRecord from Alert Instance
	 * @param alert
	 * @return AlertDetailsRecord
	 */
	public static ContentContainer extractAlertDetailsRecord(Alert alert) {
		if (alert == null) { return null; }
		if (alert.getDetails() == null) { return null; }
		
		String detailType = alert.getDetailsContentType();
		ContentContainer details = new ContentContainer();
		details.setContentType(detailType);
		
		Object detailObject =  alert.getDetails();
		if (detailObject != null) {
			Map<String,Object> content;
			if (StringUtils.equalsIgnoreCase(Alert.DETAILS_JSON, detailType)) {
				JSONObject object = (JSONObject) detailObject;
				content = object.toMap();
			} else {
				HashMap<String, Object> map = new HashMap<>();
				map.put(ContentContainer.DEFAULT_VALUE_KEY, detailObject);
				content = map;
			}
			details.setContent(content);
		}
		
		return details;
	}
	
	/**
	 * Attach AlertDetailsRecord to Alert Instance
	 * @param details
	 * @param alert
	 * @return JSONException if there is failure
	 */
	public static JSONException attachAlertDetailsRecord(ContentContainer details,AlertImpl alert) {
		if (alert == null) return null;
		if (details == null) return null;
		JSONException exception = null;
		
		String contentType = details.getContentType();
		alert.setDetailsContentType(contentType);
		Map<String,Object> map = details.getContent();
		if (map != null) {
			if (StringUtils.equalsIgnoreCase(Alert.DETAILS_JSON, contentType)) {
				alert.setDetails(new JSONObject(map));
			} else {
				Object valueObject = map.get(ContentContainer.DEFAULT_VALUE_KEY);
				alert.setDetails(valueObject);
			}
		} else {
			if (StringUtils.equalsIgnoreCase(Alert.DETAILS_JSON, contentType)) {
				alert.setDetails(new JSONObject());
			} else {
				alert.setDetails(null);
			}
		}
		return exception;
	}
	
	/**
	 * Extract AcknowledgeAlertOperation from Alert Instance
	 * @param alert
	 * @return Operation available in alert
	 */
	public static AlertOpAcknowledge extractLastAcknowledge(Alert alert) {
		if (alert.getAcknowledgeAt() > 0) {
			AlertOpAcknowledge ackOp = new AlertOpAcknowledge();
			ackOp.setTimestamp(Instant.ofEpochMilli(alert.getAcknowledgeAt()));
			ackOp.setBy(alert.getAcknowledgeBy());
			return ackOp;
		}
		return null;
	}
	
	/**
	 * Attach AcknowledgeAlertOperation to Alert Instance
	 * @param ackOp
	 * @param alert
	 */
	public static void attachLastAcknowledge(AlertOpAcknowledge ackOp,AlertImpl alert) {
		if (alert == null) return;
		if (ackOp == null) return;
		alert.setAcknowledgeAt(ackOp.getTimestamp().toEpochMilli());
		alert.setAcknowledgeBy(ackOp.getBy());
	}
	
	/**
	 * Extract CommentAlertOperation from Alert Instance
	 * @param alert
	 * @return Operation available in alert
	 */
	public static AlertOpComment extractLastComment(Alert alert) {
		if (alert.getCommentedAt() > 0) {
			AlertOpComment lastComment =  new AlertOpComment();
			lastComment.setComment(alert.getComment());
			lastComment.setBy(alert.getCommentedBy());
			lastComment.setTimestamp(Instant.ofEpochMilli(alert.getCommentedAt()));
			return lastComment;
		}
		return null;
	}
	
	/**
	 * Extract CommentAlertOperation from Alert Instance
	 * @param alert
	 * @param lastComment Last Comment
	 */
	public static void attachLastComment(AlertOpComment lastComment,AlertImpl alert) {
		if (alert == null) return;
		if (lastComment == null) return;
		
		alert.setComment(lastComment.getComment());
		alert.setCommentedBy(lastComment.getBy());
		alert.setCommentedAt(lastComment.getTimestamp().toEpochMilli());
	}
	
	/**
	 * Extract SnoozeAlertOperation from Alert Instance
	 * @param alert
	 * @return Operation available in alert
	 */
	public static AlertOpSnooze extractLastSnoozed(AlertImpl alert) {
		if (alert.getLastSnoozedAt() > 0) {
			AlertOpSnooze snooze = new AlertOpSnooze();
			snooze.setBy(alert.getLastSnoozedBy());
			snooze.setTimestamp(Instant.ofEpochMilli(alert.getLastSnoozedAt()));
			return snooze;
		} 
		return null;
	}
	
	/**
	 * Attach SnoozeAlertOperation to Alert Instance
	 * @param snooze
	 * @param alert
	 */
	public static void attachLastSnoozed(AlertOpSnooze snooze,AlertImpl alert) {
		if (alert == null) return;
		if (snooze == null) return;
		
		alert.setLastSnoozedBy(snooze.getBy());
		alert.setLastSnoozedAt(snooze.getTimestamp().toEpochMilli());
	}
	
	/**
	 * Extract Alert Definition from Alert Instance
	 * @param alert
	 * @return AlertDefinition
	 */
	public static AlertDefinition extractAlertDefinition(Alert alert) {
		if (alert == null) return null;
		AlertDefinition definition = new AlertDefinition();
		definition.setName(alert.getName());
		definition.setClassId(alert.getClassId()); 
		definition.setDomain(alert.getDomain());
		definition.setClassification(alert.getClassification());
		return definition;
	}
	
	/**
	 * Extract Alert Definition from Alert Instance
	 * @param definition 
	 * @param alert 
	 */
	public static void attachAlertDefinition(AlertDefinition definition, Alert alert) {
		if (alert == null) return;
		if (definition == null) return;
		
		alert.setName(definition.getName());
		alert.setClassId(definition.getClassId()); 
		alert.setDomain(definition.getDomain());
		alert.setClassification(definition.getClassification());
	}
	
	/**
	 * Extract Well Parameters from plink Alert
	 * @param plinkAlert
	 * @return WellParametersSnapshot
	 */
	public static WellParametersSnapshot extractWellParametersSnapshot(AlertImpl plinkAlert) {
		if (plinkAlert == null) return null;
		WellParametersSnapshot wellParams = new WellParametersSnapshot();
		wellParams.setWellId(plinkAlert.getWellId());
		wellParams.setInitialHoleDepth(plinkAlert.getHoleDepth());
		wellParams.setLatestHoleDepth(plinkAlert.getFinalHoleDepth());
		wellParams.setInitialBitDepth(plinkAlert.getBitDepth());
		wellParams.setLatestBitDepth(plinkAlert.getFinalBitDepth());
		wellParams.setInitialRigState(plinkAlert.getRigState());
		wellParams.setLatestRigState(plinkAlert.getFinalRigState());
		return wellParams;
	}
	
	/**
	 * Set an Alert with Well Parameters
	 * @param wellParams Well parameter to attach
	 * @param alert Target alert to Attach
	 */
	public static void attachWellParametersSnapshot(WellParametersSnapshot wellParams, AlertImpl alert) {
		if (wellParams == null) return;
		if (alert == null) return;
		
		alert.setWellId(wellParams.getWellId());
		alert.setHoleDepth(wellParams.getInitialHoleDepth());
		alert.setFinalHoleDepth(wellParams.getLatestHoleDepth());
		alert.setBitDepth(wellParams.getInitialBitDepth());
		alert.setFinalBitDepth(wellParams.getLatestBitDepth());
		alert.setRigState(wellParams.getInitialRigState());
		alert.setFinalRigState(wellParams.getLatestRigState());
	}
	
	/**
	 * Extract AlertOpInvestigate from Alert Instance
	 * @param alert
	 * @return Operation available in alert
	 */
	public static AlertOpInvestigate extractLatestInvestigation(AlertImpl alert) {
		if (alert.getInvestigateAt() > 0) {
			AlertOpInvestigate op = new AlertOpInvestigate();
			op.setBy(alert.getInvestigateBy());
			op.setTimestamp(Instant.ofEpochMilli(alert.getInvestigateAt()));
			return op;
		} 
		return null;
	}
	
	/**
	 * Attach AlertOpInvestigate to Alert Instance
	 * @param op
	 * @param alert
	 */
	public static void attachLatestInvestigation(AlertOpInvestigate op,AlertImpl alert) {
		if (alert == null) return;
		if (op == null) return;
		
		alert.setInvestigateBy(op.getBy());
		alert.setInvestigateAt(op.getTimestamp().toEpochMilli());
	}
	
	/**
	 * Convert alert to AlertSimpleMetadata
	 * @param alert Alert object to convert 
	 * @return AlertSimpleMetadata
	 */
	public static AlertSimpleMetadata toAlertSimpleMetadata(Alert alert) {
		if (alert == null) return null;
		return new AlertSimpleMetadata(alert.getUuid(),alert.getClassId());
	}
	
	/**
	 * Convert alerts to AlertSimpleMetadata
	 * @param alerts Alert collection to convert
	 * @return ArrayList of AlertSimpleMetadata
	 */
	public static ArrayList<AlertSimpleMetadata> toAlertSimpleMetadata(Collection<Alert> alerts){
		if (alerts == null) return null;
		ArrayList<AlertSimpleMetadata> results = new ArrayList<AlertSimpleMetadata>();
		for(Alert alert:alerts) {
			if (alert != null) {
				results.add(toAlertSimpleMetadata(alert));
			}
		}
		return results;
	}
}
