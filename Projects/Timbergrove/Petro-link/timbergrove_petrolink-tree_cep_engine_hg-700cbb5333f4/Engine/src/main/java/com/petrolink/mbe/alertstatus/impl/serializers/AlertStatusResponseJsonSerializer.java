package com.petrolink.mbe.alertstatus.impl.serializers;

import java.time.Instant;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.petrolink.mbe.alertstatus.impl.AlertImpl;
import com.smartnow.alertstatus.Alert;

/**
 * AlertStatusResponse Serializer for JSON
 * @author aristo
 *
 */
public class AlertStatusResponseJsonSerializer {

	/**
	 * Serialize an Alert List As JSONArray
	 * @param source
	 * @return JSONObject representing the Alert
	 */
	public static JSONArray serializeAlertsAsJSONArray(List<Alert> alerts) {
		if (alerts == null) {
			return null;
		}
		JSONArray ja = new JSONArray();
		for (Alert alert : alerts) {
			JSONObject jo = serializeAlertAsJSONObject(alert);
			ja.put(jo);
		}
		return ja;
		
	}
	
	/**
	 * Serialize an Alert As JSONObject
	 * @param source
	 * @return JSONObject representing the Alert
	 */
	public static JSONObject serializeAlertAsJSONObject(Alert source) { 
		if (source == null) {
			return null;
		}
		
		com.petrolink.mbe.alertstatus.Alert wellAlert = null;
		if (source instanceof com.petrolink.mbe.alertstatus.Alert) {
			wellAlert = (com.petrolink.mbe.alertstatus.Alert) source;
		}
		
		//TODO: in future may be just used universal serializer with AlertStatusResponseXmlSerializer 
		//It must pay attention on the casing though
		JSONObject jo = new JSONObject();
		jo.put("uuid", source.getUuid());
		jo.put("name", source.getName());
		jo.putOpt("classId", source.getClassId());
		jo.put("description", source.getDescription());
		jo.put("domain", source.getDomain());
		jo.put("classification", source.getClassification());
		jo.put("severity", source.getSeverity());
		jo.put("priority", source.getPriority());
		jo.put("status", com.petrolink.mbe.alertstatus.Alert.getStatusName(source.getStatus()));
		jo.put("lastStatusChange", Instant.ofEpochMilli(source.getLastStatusChange()).toString());
		jo.put("created", Instant.ofEpochMilli(source.getCreated()).toString());
		jo.put("lastOccurrence", Instant.ofEpochMilli(source.getCreated()).toString());
		
		if (source.getAcknowledgeAt() != 0) {
			jo.put("acknowledgeBy", source.getAcknowledgeBy());
			jo.put("acknowledgeAt", Instant.ofEpochMilli(source.getAcknowledgeAt()).toString());
		}
		
		if (wellAlert != null) {
			jo.put("commentedCount", wellAlert.getCommentedCount());
		}
		
		if (source.getCommentedAt() != 0) {
			jo.put("comment", source.getComment());
			jo.put("commentedBy", source.getCommentedBy());
			jo.put("commentedAt", Instant.ofEpochMilli(source.getCommentedAt()).toString());
		}
		
		jo.put("tally", source.getTally());
		
		Object metadataObject = source.getMetadata();
		if (metadataObject != null) {
			JSONObject metadataJson;
			if (metadataObject instanceof JSONObject) {
				metadataJson = (JSONObject)metadataObject;
			} else {
				metadataJson = new JSONObject(source.getMetadata());
			}
			jo.put("metadata", metadataJson);
		}
		
		if (source.getDetailsContentType() != null) {
			JSONObject detailsJson = new JSONObject();
			detailsJson.put("contentType", source.getDetailsContentType());
			detailsJson.put("value", source.getDetails().toString());
			jo.put("details", detailsJson);
		}
		
		if (wellAlert != null) {
			jo.put("createdIndex", wellAlert.getCreatedIndex());
			jo.put("lastIndex", wellAlert.getLastIndex());
		}
		
		//Matching xml serializer, use string instead of boolean
//				if (source.getSnoozedBy() != null) {
//					jo.put("snoozed", "true");
//				} else {
//					jo.put("snoozed", "false");
//				}
		jo.put("snoozed", String.valueOf(source.isSnoozed()));
		jo.put("snoozedBy",source.getSnoozedBy());
		jo.put("snoozedAt", Instant.ofEpochMilli(source.getSnoozedAt()).toString());
		jo.put("unSnoozeAt", Instant.ofEpochMilli(source.getUnSnoozeAt()).toString());
		jo.put("unSnoozedAt", Instant.ofEpochMilli(source.getUnSnoozedAt()).toString());
		jo.put("unSnoozedBy",source.getUnSnoozedBy());
		
		
		if (wellAlert != null) {
			String wellIdString = wellAlert.getWellId();
			jo.put("wellId", wellIdString);
		
			jo.putOpt("holeDepth", wellAlert.getHoleDepth());
			jo.putOpt("finalHoleDepth", wellAlert.getFinalHoleDepth());
			jo.putOpt("bitDepth", wellAlert.getBitDepth());
			jo.putOpt("finalBitDepth", wellAlert.getFinalBitDepth());
			jo.putOpt("rigState", wellAlert.getRigState());
			jo.putOpt("finalRigState", wellAlert.getFinalRigState());
		}
		
		jo.put("notificationsSent", source.isOnCreateEventsExecuted());
		
		if (source instanceof AlertImpl) {
			AlertImpl plinkAlert = (AlertImpl)source;
			jo.put("lastSnoozedAt",Instant.ofEpochMilli(plinkAlert.getLastSnoozedAt()).toString());
			jo.put("lastSnoozedBy",plinkAlert.getLastSnoozedBy());
			jo.put("investigateAt",Instant.ofEpochMilli(plinkAlert.getInvestigateAt()).toString());
			jo.put("investigateBy",plinkAlert.getInvestigateBy());
			jo.put("parentUuid", plinkAlert.getParentUuid());
			jo.putOpt("parentClassId", plinkAlert.getParentClassId());
		}
		
		return jo;
	}
}
