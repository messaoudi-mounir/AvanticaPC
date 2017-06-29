package com.petrolink.mbe.alertstatus.impl.serializers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

import com.smartnow.alertstatus.Alert;
import com.petrolink.mbe.alertstatus.impl.SnoozeRecord;
import com.petrolink.mbe.alertstatus.impl.SnoozeRecordsDAO;
import com.smartnow.alertstatus.serializers.AlertSerializer;

//TODO Expand Serializer to include all the added fields
/**
 * @author AndresR Creates the response of the WebService in a XMLFormat
 */
public class AlertXMLSerializer implements AlertSerializer {
	
	/**
	 * Serialize alerts in a XMLFormat the format is: "<Alerts> <Alert> </Alert>
	 * <Alert> </Alert> </Alerts>"
	 * 
	 * @param source
	 * @return Document(XML)
	 */

	// TODO Change to using Alert Object not Result Set
	@Override
	public Object serialize(Object source) {

		if (source instanceof ResultSet) {
			ResultSet resultSetSource = (ResultSet) source;
			List<Alert> alerts = AlertStatusResponseXmlSerializer.createAlertsFromResultSet(resultSetSource);
			return AlertStatusResponseXmlSerializer.serializeAlertsAsDocument(alerts);
		} else if (source instanceof Alert) {
			Alert alertSource = (Alert) source;
			return AlertStatusResponseXmlSerializer.serializeAlertAsDocument(alertSource);
		}
		return null;
	}

// TODO: FRAGILE CODE, need to use converted intermediate object or otherwise we need to fix everywhere
// This code is redirected to 	AlertStatusResponseXmlSerializer
// Use serializeAlertsFromResultSet combined with serializeAlertsAsDocument
	
//	private Object serializeResultSet(Object source) {
//		ResultSet _source = (ResultSet) source;
//		try {
//
//			Namespace ns = Namespace.getNamespace("http://www.petrolink.com/mbe/alertstatus/list");
//			Element alertsElement = new Element("Alerts", ns);
//
//			while (_source.next()) {
//				Element alert = new Element("Alert", ns);
//				alert.setAttribute("UUID", _source.getString("UUID"));
//
//				Element name = new Element("Name", ns);
//				name.addContent(_source.getString("name"));
//				alert.addContent(name);
//
//				if (_source.getString("classId") != null) {
//					Element classId = new Element("ClassId", ns);
//					classId.addContent(_source.getString("classId"));
//					alert.addContent(classId);
//				}
//
//				Element description = new Element("Description", ns);
//				description.addContent(_source.getString("description"));
//				alert.addContent(description);
//
//				Element domain = new Element("Domain", ns);
//				domain.addContent(_source.getString("domain"));
//				alert.addContent(domain);
//
//				Element classification = new Element("Classification", ns);
//				classification.addContent(_source.getString("classification"));
//				alert.addContent(classification);
//
//				Element severity = new Element("Severity", ns);
//				severity.addContent(_source.getString("severity"));
//				alert.addContent(severity);
//
//				Element priority = new Element("Priority", ns);
//				priority.addContent(_source.getString("priority"));
//				alert.addContent(priority);
//
//				Element status = new Element("Status", ns);
//				status.addContent(Alert.getStatusName(_source.getInt("status")));
//				alert.addContent(status);
//
//				Element lastStatusChange = new Element("LastStatusChange", ns);
//				if (_source.getTimestamp("lastStatusChange") != null)
//					lastStatusChange.addContent(_source.getTimestamp("lastStatusChange").toInstant().toString());
//				alert.addContent(lastStatusChange);
//
//				Element created = new Element("Created", ns);
//				if (_source.getTimestamp("created") != null)
//					created.addContent(_source.getTimestamp("created").toInstant().toString());
//				alert.addContent(created);
//
//				Element lastOccurrence = new Element("LastOccurrence", ns);
//				if (_source.getTimestamp("lastOccurrence") != null)
//					lastOccurrence.addContent(_source.getTimestamp("lastOccurrence").toInstant().toString());
//				alert.addContent(lastOccurrence);
//
//				if (_source.getTimestamp("acknowledgeAt") != null) {
//					Element acknowledgeBy = new Element("AcknowledgeBy", ns);
//					acknowledgeBy.addContent(_source.getString("acknowledgeBy"));
//					alert.addContent(acknowledgeBy);
//
//					Element acknowledgeAt = new Element("AcknowledgeAt", ns);
//					acknowledgeAt.addContent(_source.getTimestamp("acknowledgeAt").toInstant().toString());
//					alert.addContent(acknowledgeAt);
//				}
//
//				if (_source.getTimestamp("commentedAt") != null) {
//					Element comment = new Element("Comment", ns);
//					comment.addContent(_source.getString("comment"));
//					alert.addContent(comment);
//
//					Element commentedBy = new Element("CommentedBy", ns);
//					commentedBy.addContent(_source.getString("commentBy"));
//					alert.addContent(commentedBy);
//
//					Element commentedAt = new Element("CommentedAt", ns);
//					commentedAt.addContent(_source.getTimestamp("commentedAt").toInstant().toString());
//					alert.addContent(commentedAt);
//					
//				}
//				
//				Element commentedCount = new Element("CommentedCount", ns);
//				commentedCount.addContent(_source.getString("commentedCount"));
//				alert.addContent(commentedCount);
//
//				Element tally = new Element("Tally", ns);
//				tally.addContent(_source.getString("tally"));
//				alert.addContent(tally);
//
//				if (_source.getString("metadata") != null) {
//					JSONObject metadataObj = new JSONObject(_source.getString("metadata"));
//					Element metadata = new Element("Metadata", ns);
//
//					for (String key : metadataObj.keySet()) {
//						Element entry = new Element("Entry", ns);
//						entry.setAttribute("key", key);
//						entry.setText(metadataObj.get(key).toString());
//						metadata.addContent(entry);
//					}
//
//					alert.addContent(metadata);
//				}
//				Element details = new Element("Details", ns);
//				if (_source.getString("detailsContentType") != null) {
//					details.setAttribute("contentType", _source.getString("detailsContentType"));
//					details.addContent(_source.getString("details"));
//				}
//				alert.addContent(details);
//
//				Element createdIndex = new Element("CreatedIndex", ns);
//				createdIndex.addContent(_source.getString("createdIndex"));
//				alert.addContent(createdIndex);
//
//				Element lastIndex = new Element("LastIndex", ns);
//				lastIndex.addContent(_source.getString("lastIndex"));
//				alert.addContent(lastIndex);
//
//
//				Element snoozed = new Element("Snoozed", ns);
//				if (_source.getString("snoozedBy") != null) {
//					snoozed.addContent("true");
//				} else {
//					snoozed.addContent("false");
//				}
//				
//				alert.addContent(snoozed);
//
//				Element snoozedBy = new Element("SnoozedBy", ns);
//				snoozedBy.addContent(_source.getString("snoozedBy"));
//				alert.addContent(snoozedBy);
//
//				Timestamp snoozedAtTimestamp =_source.getTimestamp("snoozedAt"); 
//				if (snoozedAtTimestamp != null) {
//					Element snoozedAt = new Element("SnoozedAt", ns);
//					snoozedAt.addContent(snoozedAtTimestamp.toInstant().toString());
//					alert.addContent(snoozedAt);
//				}
//				
//				Timestamp unSnoozeAtTimestamp =_source.getTimestamp("unSnoozeAt");
//				if (unSnoozeAtTimestamp != null) {
//					Element unSnoozeAt = new Element("UnSnoozeAt", ns);
//					unSnoozeAt.addContent(unSnoozeAtTimestamp.toInstant().toString());
//					alert.addContent(unSnoozeAt);
//				}
//
//				Element wellId = new Element("WellId", ns);
//				String wellIdString = _source.getString("wellId");
//				wellId.addContent(wellIdString);
//				alert.addContent(wellId);
//
//				// Element wellName = new Element("WellName",ns);
//				// if (StringUtils.isNotBlank(wellIdString)) {
//				//// wellName.addContent(metadataObj.getString("wellName"));
//				// alert.addContent(wellName);
//				// }
//
//				String holeDepthValue = _source.getString("holeDepth");
//				if (holeDepthValue != null) {
//					Element holeDepth = new Element("HoleDepth", ns);
//					holeDepth.addContent(holeDepthValue);
//					alert.addContent(holeDepth);
//				}
//
//				String finalHoleDepthValue = _source.getString("finalHoleDepth");
//				if (finalHoleDepthValue != null) {
//					Element finalHoleDepth = new Element("FinalHoleDepth", ns);
//					finalHoleDepth.addContent(finalHoleDepthValue);
//					alert.addContent(finalHoleDepth);
//				}
//
//				String bitDepthValue = _source.getString("bitDepth");
//				if (bitDepthValue != null) {
//					Element bitDepth = new Element("BitDepth", ns);
//					bitDepth.addContent(bitDepthValue);
//					alert.addContent(bitDepth);
//				}
//
//				String finalBitDepthValue = _source.getString("finalBitDepth");
//				if (finalBitDepthValue != null) {
//					Element finalBitDepth = new Element("FinalBitDepth", ns);
//					finalBitDepth.addContent(finalBitDepthValue);
//					alert.addContent(finalBitDepth);
//				}
//
//				String rigStateValue = _source.getString("rigState");
//				if (rigStateValue != null) {
//					Element rigState = new Element("RigState", ns);
//					rigState.addContent(rigStateValue);
//					alert.addContent(rigState);
//				}
//
//				String finalRigStateValue = _source.getString("finalRigState");
//				if (finalRigStateValue != null) {
//					Element finalRigState = new Element("FinalRigState", ns);
//					finalRigState.addContent(finalRigStateValue);
//					alert.addContent(finalRigState);
//				}
//
//				Element notificationsSent = new Element("NotificationsSent", ns);
//				if (_source.getString("notificationsSent") != null) {
//					notificationsSent.addContent(_source.getString("notificationsSent").toLowerCase());
//				}
//				alert.addContent(notificationsSent);
//
//				alertsElement.addContent(alert);
//			}
//
//			Document doc = new Document(alertsElement);
//			return doc;
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	@Override
	public Object serializeWithJournal(Object source) {
		ResultSet _source = (ResultSet) source;
		Element alertJournal = new Element("AlertJournal");
		try {
			while (_source.next()) {

				Element journal = new Element("Journal");

				Element alertUUID = new Element("AlertUUID");
				alertUUID.addContent(_source.getString("alertUUID"));
				journal.addContent(alertUUID);

				Element type = new Element("Type");
				type.addContent(_source.getString("type"));
				alertJournal.addContent(type);

				Element timestamp = new Element("Timestamp");
				timestamp.addContent(_source.getTimestamp("timestamp").toInstant().toString());
				alertJournal.addContent(timestamp);

				Element principal = new Element("Principal");
				principal.addContent(_source.getString("principal"));
				alertJournal.addContent(principal);

				Element details = new Element("Details");
				details.addContent(_source.getString("details"));
				alertJournal.addContent(details);

				alertJournal.addContent(journal);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Document doc = new Document(alertJournal);
		return doc;
	}

	/**
	 * @param source
	 * @return the snooze table serialized
	 */
	@Override
	public Object serializeSnoozeTable(Object source){
		if (source instanceof ResultSet) {
			ResultSet rs = (ResultSet)source;
			try {
				List<SnoozeRecord> snoozeRecords = SnoozeRecordsDAO.createSnoozeRecordsFromResultSet(rs, false);
				return AlertStatusResponseXmlSerializer.serializeSnoozeRecordsAsDocument(snoozeRecords);
			} catch(SQLException sq) {
				return null;
			}
		} else if (source instanceof SnoozeRecord) {
			SnoozeRecord snoozeRecord = (SnoozeRecord) source;
			Document doc = AlertStatusResponseXmlSerializer.serializeSnoozeRecordAsDocument(snoozeRecord);
			return doc;
		}
		return null;
			
	}

}
