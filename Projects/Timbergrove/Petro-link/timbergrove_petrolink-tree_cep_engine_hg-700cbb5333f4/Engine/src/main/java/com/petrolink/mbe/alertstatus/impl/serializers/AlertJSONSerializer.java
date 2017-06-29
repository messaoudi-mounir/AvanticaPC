package com.petrolink.mbe.alertstatus.impl.serializers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.petrolink.mbe.alertstatus.Alert;
import com.petrolink.mbe.alertstatus.impl.AlertImpl;
import com.petrolink.mbe.util.SQLHelper;
import com.smartnow.alertstatus.serializers.AlertSerializer;
/**
 * @author AndresR
 * Creates the response of the WebService in a JSONFormat
 */
// TODO JSON serializer to XML serializer adding all the new fields 
public class AlertJSONSerializer implements AlertSerializer {
	/**
	 * check the source if it is an Alert or a ResultSet
	 * @param source = Object (Alert or ResultSet)
	 * @return JSONArray
	 */
	@Override
	public Object serialize(Object source) {
		if (source instanceof ResultSet) {
			return serializeResultSet((ResultSet)source);			
		} else if (source instanceof Alert) {
			return serializeAlert((Alert)source);
		}
		
		return null;
	}
	
	/**
	 * Serialize an Alert As JSONObject
	 * @param source
	 * @return JSONObject representing the Alert
	 * @see AlertStatusResponseJsonSerializer
	 */
	public static JSONObject serializeAlertAsJSONObject(Alert source) {
		if (source == null) {
			return null;
		}
		
		return AlertStatusResponseJsonSerializer.serializeAlertAsJSONObject(source);
	}
	
	

	/**
	 * Serialize a single Alert, create a JSONObject and put it in a JSONArray
	 * @param source - Alert
	 * @return _source
	 */
	private Object serializeAlert(Alert _source) {
		
		JSONArray ja = new JSONArray();
		// create JSON
		JSONObject jo = serializeAlertAsJSONObject(_source);
		
		
//		jo.put("uuid", _source.getUuid());
//		jo.put("classId", _source.getClassId());
//		jo.put("name", _source.getName());
//		jo.put("description", _source.getDescription());
//		jo.put("domain", _source.getDomain());
//		jo.put("classification", _source.getClassification());
//		jo.put("severity", _source.getSeverity());
//		jo.put("priority", _source.getPriority());
		
//		jo.put("status", Alert.getStatusName(_source.getStatus()));
		
//		JSONObject metadata = (JSONObject) _source.getMetadata();
//		jo.put("metadata", metadata);
//		jo.put("lastStatusChange", Instant.ofEpochMilli(_source.getLastStatusChange()).toString());
//		jo.put("created", Instant.ofEpochMilli(_source.getCreated()).toString());
//		jo.put("lastOccurrence", Instant.ofEpochMilli(_source.getLastOccurrence()).toString());
//		jo.put("createdIndex", _source.getCreatedIndex());
//		jo.put("lastIndex", _source.getLastIndex());
		
//		jo.put("acknowledgeBy", _source.getAcknowledgeBy());
//		jo.put("acknowledgeAt", Instant.ofEpochMilli(_source.getAcknowledgeAt()).toString());
//
//		jo.put("comment", _source.getComment());
//		jo.put("commentBy", _source.getCommentedBy());
//		jo.put("commentedAt", Instant.ofEpochMilli(_source.getCommentedAt()).toString());

//		jo.put("tally", _source.getTally());
//		jo.put("detailsContentType", _source.getDetailsContentType());
//		jo.put("details", _source.getDetails());

//		jo.put("snoozed", _source.isSnoozed());
//		jo.put("snoozedBy", _source.getSnoozedBy());
//		jo.put("snoozedAt", Instant.ofEpochMilli(_source.getSnoozedAt()).toString());
		
//		String wellIdString = _source.getWellId();
//		jo.put("wellId", wellIdString);
//		if (StringUtils.isNotBlank(wellIdString)) {
//			// backwards compatibility
//			jo.put("wellName", metadata.optString("wellName"));
//			jo.put("rigName", metadata.optString("rigName"));
//		}
		
//		jo.put("notificationsSent", _source.isOnCreateEventsExecuted());
		
//		jo.putOpt("holeDepth", _source.getHoleDepth());
//		jo.putOpt("finalHoleDepth", _source.getFinalHoleDepth());
//		jo.putOpt("bitDepth", _source.getBitDepth());
//		jo.putOpt("finalBitDepth", _source.getFinalBitDepth());
//		jo.putOpt("rigState", _source.getRigState());
//		jo.putOpt("finalRigState", _source.getFinalRigState());
		
		ja.put(jo);

		return ja;
	}

	/**
	 * Serializes a ResultSet, creates an JSONObject for every Alert and put them in a JSONArray  
	 * @param source - ResultSet
	 * @return JSONArray
	 */
	private Object serializeResultSet(ResultSet _source) {

		JSONArray ja = new JSONArray();
		
		try {
			while(_source.next()){
				//create JSON
				JSONObject jo = new JSONObject();
				jo.put("uuid", _source.getString("UUID"));
				jo.put("name", _source.getString("name"));
				jo.put("description", _source.getString("description"));
				jo.put("domain", _source.getString("domain"));
				jo.put("classification", _source.getString("classification"));
				jo.put("severity", _source.getString("severity"));
				jo.put("priority", _source.getString("priority"));

				jo.put("status", Alert.getStatusName(_source.getInt("status")));
				
				JSONObject metadata = new JSONObject(_source.getString("metadata"));
				jo.put("metadata", metadata);

				jo.put("lastStatusChange", Instant.ofEpochMilli(_source.getLong("lastStatusChange")).toString());
				jo.put("created", Instant.ofEpochMilli(_source.getLong("created")).toString());
				jo.put("lastOccurrence", Instant.ofEpochMilli(_source.getLong("lastOccurrence")).toString());

				jo.put("createdIndex", _source.getString("createdIndex"));
				jo.put("lastIndex", _source.getString("lastIndex"));
				
				jo.put("acknowledgeBy", _source.getString("acknowledgeBy"));
				jo.put("acknowledgeAt", Instant.ofEpochMilli(_source.getLong("acknowledgeAt")).toString());

				jo.put("comment", _source.getString("comment"));
				jo.put("commentBy", _source.getString("commentBy"));
				jo.put("commentedAt", Instant.ofEpochMilli(_source.getLong("commentedAt")).toString());

				jo.put("tally", _source.getString("tally"));
				jo.put("detailsContentType", _source.getString("detailsContentType"));
				jo.put("details", _source.getString("details"));
				
				jo.put("snoozed", _source.getBoolean("snoozed"));
				jo.put("snoozedBy", _source.getString("snoozedBy"));
				jo.put("snoozedAt", Instant.ofEpochMilli(_source.getLong("snoozedAt")).toString());
				
				jo.put("wellId", _source.getString("wellId"));
				// backwards compatibility
				jo.put("wellName", metadata.optString("wellName"));
				jo.put("rigName", metadata.optString("rigName"));
				
				jo.put("notificationsSent", _source.getBoolean("notificationsSent"));

				jo.putOpt("holeDepth", SQLHelper.getDoubleOrNull(_source, "holeDepth"));
				jo.putOpt("finalHoleDepth", SQLHelper.getDoubleOrNull(_source, "finalHoleDepth"));
				jo.putOpt("bitDepth", SQLHelper.getDoubleOrNull(_source, "bitDepth"));
				jo.putOpt("finalBitDepth", SQLHelper.getDoubleOrNull(_source, "finalBitDepth"));
				jo.putOpt("rigState", SQLHelper.getIntegerOrNull(_source, "rigState"));
				jo.putOpt("finalRigState", SQLHelper.getIntegerOrNull(_source, "finalRigState"));
				
				ja.put(jo);
			}
			System.out.println(ja.toString());
			return ja;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object serializeWithJournal(Object source) {
		ResultSet _source = (ResultSet) source;
		JSONArray res = new JSONArray();
		try {
			while(_source.next()){
				JSONObject obj = new JSONObject();
				obj.put("alertUUID", _source.getString("alertUUID"));
				obj.put("type", _source.getString("type"));
				obj.put("timestamp", _source.getString("timestamp"));
				obj.put("principal", _source.getString("principal"));
				obj.put("details", _source.getString("details"));
				
				res.put(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see com.smartnow.alertstatus.serializers.AlertSerializer#serializeSnoozeTable(java.lang.Object)
	 */
	@Override
	public Object serializeSnoozeTable(Object source) {
		return null;
	}
	
	/**
	 * @param e
	 * @return a JSONObject
	 */
	public static JSONObject serializeExceptionAsAlertError(Exception e) {
		JSONObject obj = new JSONObject();
		obj.put("type", "AlertError");
		obj.put("code", "500");
		obj.put("value", "500");
		if (e != null) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			obj.put("value", (e.getMessage() + "\n" + writer.toString()));
		} else {
			obj.put("value", "Unknown Error");
		}
		return obj;
	}
}
