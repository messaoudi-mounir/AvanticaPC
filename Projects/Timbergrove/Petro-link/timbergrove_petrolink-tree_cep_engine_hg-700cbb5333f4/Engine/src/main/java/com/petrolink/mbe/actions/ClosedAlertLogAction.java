package com.petrolink.mbe.actions;

import java.io.BufferedWriter;
import java.time.OffsetDateTime;
import java.util.Map;

import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import com.petrolink.mbe.alertstatus.Alert;
import com.petrolink.mbe.util.DateTimeHelper;
import com.smartnow.alertstatus.serializers.AlertSerializer;
import com.smartnow.alertstatus.serializers.AlertSerializerFactory;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;

/**
 * An action that logs the state of an alert to a channel when it is closed.
 * @author langj
 *
 */
public class ClosedAlertLogAction extends ChannelLogAction {
	private AlertSerializer serializer;
	
	@Override
	protected String getChannelLogType() {
		return "ClosedAlert";
	}
	
	@Override
	public void load(Element e, Node parent) throws EngineException {
		super.load(e, parent);
		
		serializer = AlertSerializerFactory.getAlertSerializer("JSON");
	}
	
	@Override
	protected String getDirectoryPropertyName() {
		return "ClosedAlertLogs";
	}

	@Override
	protected void writeTestLogHeader(BufferedWriter writer) throws Exception {
		writer.write("Created,Data");
		writer.newLine();
	}

	@Override
	protected void writeTestLog(Map<String, Object> context, BufferedWriter writer) throws Exception {
		Alert alert = getAlert(context);
		String dataString = createDataString(alert);
		OffsetDateTime index = DateTimeHelper.fromEpochMillis(alert.getCreated());
		
		writer.write(index.toString());
		writer.write(',');
		writer.write(escapeCsv(dataString));
		writer.newLine();
	}

	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		Alert alert = getAlert(context);
		String dataString = createDataString(alert);
		OffsetDateTime index = DateTimeHelper.fromEpochMillis(alert.getCreated());
		
		updateChannels(index, dataString);
		return SUCCESS;
	}
	
	private String createDataString(Alert alert) {
		JSONArray serializedAlertArray = (JSONArray) serializer.serialize(alert);
		JSONObject serializedAlert = serializedAlertArray.getJSONObject(0);
		
		// Alert is wrapped in another object in case additional info is added in the future
		JSONObject data = new JSONObject();
		data.put("alert", serializedAlert);
		return data.toString();
	}
}
