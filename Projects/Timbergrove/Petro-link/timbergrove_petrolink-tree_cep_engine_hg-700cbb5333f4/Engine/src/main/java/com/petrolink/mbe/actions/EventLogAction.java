package com.petrolink.mbe.actions;

import java.io.BufferedWriter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import com.smartnow.alertstatus.AlertJournal;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;

/**
 * Publishes an update to a well's event log channel.
 * @author langj
 *
 */
public class EventLogAction extends ChannelLogAction {
	@Override
	protected String getChannelLogType() {
		return "EventLog";
	}
	
	@Override
	public void load(Element e, Node parent) throws EngineException {
		super.load(e, parent);
	}
	
	@Override
	protected String getDirectoryPropertyName() {
		return "EventLogs";
	}

	@Override
	protected void writeTestLogHeader(BufferedWriter writer) throws Exception {
		writer.write("Timestamp,Data");
		writer.newLine();
	}

	@Override
	protected void writeTestLog(Map<String, Object> context, BufferedWriter writer) throws Exception {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		JSONObject data = buildEventData(context, now);
		
		writer.write(now.toString());
		writer.write(',');
		writer.write(escapeCsv(data.toString()));
		writer.newLine();
	}

	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		String dataString = buildEventData(context, now).toString();
		updateChannels(now, dataString);
		return SUCCESS;
	}

	private JSONObject buildEventData(Map<String, Object> context, OffsetDateTime now) throws EngineException {
		AlertJournal journal = getJournal(context);
		if (journal == null)
			throw new EngineException("No alert journal provided");
		
		JSONObject evt = new JSONObject();
		evt.put("uuid", journal.getUuid());
		
		if (journal.getAlert() != null) {
			evt.put("alertUUID", journal.getAlert().getUuid());
			evt.put("alertClass", journal.getAlert().getClassId());			
		}
		
		evt.put("type", journal.getType());
		evt.put("timestamp", journal.getTimestamp().toString());
		evt.putOpt("principal", journal.getPrincipal());
	
		Object detailsObject = journal.getDetails();
		if (detailsObject instanceof JSONArray)
			evt.put("details", ((JSONArray) detailsObject).get(0));
		else if (detailsObject instanceof JSONObject)
			evt.put("details", detailsObject);
		
		return evt;
	}
}
