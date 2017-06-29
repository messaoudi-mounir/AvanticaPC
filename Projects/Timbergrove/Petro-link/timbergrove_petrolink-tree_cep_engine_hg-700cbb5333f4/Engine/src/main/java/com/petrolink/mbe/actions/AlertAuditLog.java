package com.petrolink.mbe.actions;

import java.io.BufferedWriter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.json.JSONObject;

import com.petrolink.mbe.alertstatus.Alert;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Alert Audit Log generation action.
 * Expects context to contain the alert (as alert) and alert details (as details)
 * @author paul
 * @author langj
 * 
 * What needs to be outputted to audit log:
 * 
 * Name of the Rig
 * Name of the Well
 * Name of the Alert Triggered
 * Priority of the Alert Triggered
 * Alert Acknowledgement Status Indicator
 * Timestamp of when the event happened on the rig (raised state)
 * Timestamp of when it became a lowered state
 * Timestamp of when the event was seen in the application (end user view)
 * Timestamp of when the alert was acknowledged
 * Acknowledged By (name of who acknowledged the alert)
 * When a rule is snoozed or silenced and by whom
 * When a rule snooze was stopped and by whom
 * What event handlers have been processed and to who
 * When an alert escalation process started and actions taken (event handlers & to who)
 * Timestamp of when the alert was Dismissed/Resolved
 * Dismissed/Resolved By (name of who Dismissed/Resolved the alert)
 * Bit/Hole Depth at time of Alert
 * End User Notes/Comments for the Alert
 */
public class AlertAuditLog extends ChannelLogAction {
	
	@Override
	protected String getChannelLogType() {
		return "AlertAudit";
	}
	
	@Override
	protected String getDirectoryPropertyName() {
		return "AlertAuditLogs";
	}
	
	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		return executeTestAction(context);
	}
	
	@Override
	protected void writeTestLogHeader(BufferedWriter output) throws Exception {
		output.write("Timestamp");
		output.write(',');
		output.write("Index");
		output.write(',');
		output.write("Alert Name");
		output.write(',');
		output.write("Alert Status");
		output.write(',');
		output.write("Tally");
		output.write(',');
		output.write("Creation Index");
		output.write(',');
		output.write("Last Index");
		output.newLine();
	}
	
	@Override
	protected void writeTestLog(Map<String, Object> context, BufferedWriter output) throws Exception {
		//{
		//	engine=Thread[Thread-2,5,main],
		//	payload=com.petrolink.mbe.alertstatus.impl.AlertImpl@1d4f1d2d,
		//	alert=com.petrolink.mbe.alertstatus.impl.AlertImpl@1d4f1d2d,
		//	logger=org.apache.logging.slf4j.Log4jLogger@3b9f461e,
		//	details={
		//		"result":true,
		//		"rop":25.0,
		//		"DRILLING":3,
		//		"index":"2016-08-11T15:05:38Z",
		//		"channelUUID":null,
		//		"value":25.0,
		//		"wellUUID":null
		//		},
		//	sharedObjects={
		//		$event=com.smartnow.engine.event.Event@72522a2
		//		},
		//	vars={},
		//	event=com.smartnow.engine.event.Event@72522a2,
		//	properties={}
		//}
		
//		Event evt = getEvent(context);
		Alert alert = getAlert(context);
		JSONObject details = getDetails(context);
		
//		AlertSerializer as = null;
//		try {
//			as = AlertSerializerFactory.getAlertSerializer("JSON");
//		} catch (EngineException e) {
//			e.printStackTrace();
//		}
		
		//String wellUUID =  evt.getProperties().getProperty("wellUUID");
		//WellCache wellCache = CacheFactory.getInstance().getLKVCache().getWell(UUID.fromString(wellUUID));
		
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		Object index = details.get("index");

		output.write(now.toString()); 
		output.write(',');
		output.write(escapeCsv(index.toString()));		
		output.write(',');
//		output.write(escapeCsv(wdef.name));
//		output.write(',');
//		output.write(alert.getUuid());
//		output.write(',');
		output.write(escapeCsv(alert.getName()));
		output.write(',');
		output.write(Alert.getStatusName(alert.getStatus()));
		output.write(',');
		output.write(Integer.toString(alert.getTally()));
		output.write(',');
		output.write(alert.getCreatedIndex());
		output.write(',');
		output.write(alert.getLastIndex());
		//output.write(',');
//		if (as != null) {
//			output.write(',');
//			output.write(escapeCsv(as.serialize(alert).toString()));
//		}
		output.newLine();
	}
}
