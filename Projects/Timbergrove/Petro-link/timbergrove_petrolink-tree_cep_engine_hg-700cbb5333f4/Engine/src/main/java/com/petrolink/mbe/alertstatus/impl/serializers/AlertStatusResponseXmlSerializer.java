package com.petrolink.mbe.alertstatus.impl.serializers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


import com.petrolink.mbe.alertstatus.impl.AlertImpl;
import com.petrolink.mbe.alertstatus.impl.SnoozeRecord;
import com.smartnow.alertstatus.Alert;

/**
 * AlertStatusResponse Serializer for XML
 * @author aristo
 *
 */
public class AlertStatusResponseXmlSerializer {
	private static Logger logger = LoggerFactory.getLogger(AlertStatusResponseXmlSerializer.class);
	/**
	 * Alert Status Namespace
	 */
	public final static String XML_NAMESPACE = "http://www.petrolink.com/mbe/alertstatus/list";
	/**
	 * Alert Status Root Element
	 */
	public final static String XML_ROOT_NAME = "AlertStatusResponse";
	/**
	 * Alert Status Number Format
	 */
	public final static NumberFormat NF = NumberFormat.getInstance(Locale.US);
	
	/**
	 * Serialize an Alert As Element
	 * @param source
	 * @return XML Element representing the Alert
	 */
	public static Element serializeAlertAsElement(Alert source) {
		if (source == null) {
			return null;
		}
		com.petrolink.mbe.alertstatus.Alert wellAlert = null;
		if (source instanceof com.petrolink.mbe.alertstatus.Alert) {
			wellAlert = (com.petrolink.mbe.alertstatus.Alert) source;
		}
		
		Namespace ns = Namespace.getNamespace(XML_NAMESPACE);
		Element alert = new Element("Alert", ns);
		alert.setAttribute("UUID", source.getUuid());

		Element name = new Element("Name", ns);
		name.addContent(source.getName());
		alert.addContent(name);

		if (source.getClassId() != null) {
			Element classId = new Element("ClassId", ns);
			classId.addContent(source.getClassId());
			alert.addContent(classId);
		}

		Element description = new Element("Description", ns);
		description.addContent(source.getDescription());
		alert.addContent(description);

		Element domain = new Element("Domain", ns);
		domain.addContent(source.getDomain());
		alert.addContent(domain);

		Element classification = new Element("Classification", ns);
		classification.addContent(source.getClassification());
		alert.addContent(classification);

		Element severity = new Element("Severity", ns);
		severity.addContent(NF.format(source.getSeverity()));
		alert.addContent(severity);

		Element priority = new Element("Priority", ns);
		priority.addContent(NF.format(source.getPriority()));
		alert.addContent(priority);

		Element status = new Element("Status", ns);
		status.addContent(com.petrolink.mbe.alertstatus.Alert.getStatusName(source.getStatus()));
		alert.addContent(status);

		Element lastStatusChange = new Element("LastStatusChange", ns);
		if (source.getLastStatusChange() != 0)
			lastStatusChange.addContent(Instant.ofEpochMilli(source.getLastStatusChange()).toString());
		alert.addContent(lastStatusChange);

		Element created = new Element("Created", ns);
		if (source.getCreated() != 0)
			created.addContent(Instant.ofEpochMilli(source.getCreated()).toString());
		alert.addContent(created);

		Element lastOccurrence = new Element("LastOccurrence", ns);
		if (source.getLastOccurrence() != 0)
			lastOccurrence.addContent(Instant.ofEpochMilli(source.getCreated()).toString());
		alert.addContent(lastOccurrence);

		if (source.getAcknowledgeAt() != 0) {
			Element acknowledgeBy = new Element("AcknowledgeBy", ns);
			acknowledgeBy.addContent(source.getAcknowledgeBy());
			alert.addContent(acknowledgeBy);

			Element acknowledgeAt = new Element("AcknowledgeAt", ns);
			acknowledgeAt.addContent(Instant.ofEpochMilli(source.getAcknowledgeAt()).toString());
			alert.addContent(acknowledgeAt);
		}

		if (wellAlert != null) {
			Element commentedCount = new Element("CommentedCount", ns);
			commentedCount.addContent(String.valueOf(wellAlert.getCommentedCount()));
			alert.addContent(commentedCount);
		}
		
		if (source.getCommentedAt() != 0) {
			Element comment = new Element("Comment", ns);
			comment.addContent(source.getComment());
			alert.addContent(comment);

			Element commentedBy = new Element("CommentedBy", ns);
			commentedBy.addContent(source.getCommentedBy());
			alert.addContent(commentedBy);

			Element commentedAt = new Element("CommentedAt", ns);
			commentedAt.addContent(Instant.ofEpochMilli(source.getCommentedAt()).toString());
			alert.addContent(commentedAt);
		}

		Element tally = new Element("Tally", ns);
		tally.addContent(NF.format(source.getTally()));
		alert.addContent(tally);

		Object metadataObject = source.getMetadata();
		if (metadataObject != null) {
			JSONObject metadataJson;
			if (metadataObject instanceof JSONObject) {
				metadataJson = (JSONObject)metadataObject;
			} else {
				metadataJson = new JSONObject(source.getMetadata());
			}
			
			Element metadata = new Element("Metadata", ns);

			for (String key : metadataJson.keySet()) {
				Element entry = new Element("Entry", ns);
				entry.setAttribute("key", key);
				entry.setText(metadataJson.get(key).toString());
				metadata.addContent(entry);
			}

			alert.addContent(metadata);
		}
		Element details = new Element("Details", ns);
		if (source.getDetailsContentType() != null) {
			details.setAttribute("contentType", source.getDetailsContentType());
			details.addContent(source.getDetails().toString());
		}
		alert.addContent(details);

		if (wellAlert != null) {
			Element createdIndex = new Element("CreatedIndex", ns);
			createdIndex.addContent(wellAlert.getCreatedIndex());
			alert.addContent(createdIndex);
	
			Element lastIndex = new Element("LastIndex", ns);
			lastIndex.addContent(wellAlert.getLastIndex());
			alert.addContent(lastIndex);
		}

//		Element snoozed = new Element("Snoozed", ns);
//		if (source.getSnoozedBy() != null) {
//			snoozed.addContent("true");
//		} else {
//			snoozed.addContent("false");
//		}
//		alert.addContent(snoozed);

		Element isSnoozed = new Element("Snoozed", ns);
		isSnoozed.addContent(String.valueOf(source.isSnoozed()));
		alert.addContent(isSnoozed);
		
		Element snoozedBy = new Element("SnoozedBy", ns);
		snoozedBy.addContent(source.getSnoozedBy());
		alert.addContent(snoozedBy);

		Element snoozedAt = new Element("SnoozedAt", ns);
		snoozedAt.addContent(Instant.ofEpochMilli(source.getSnoozedAt()).toString());
		alert.addContent(snoozedAt);
		
		Element unSnoozeAt = new Element("UnSnoozeAt", ns);
		unSnoozeAt.addContent(Instant.ofEpochMilli(source.getUnSnoozeAt()).toString());
		alert.addContent(unSnoozeAt);
		
		Element unSnoozedAt = new Element("UnSnoozedAt", ns);
		unSnoozedAt.addContent(Instant.ofEpochMilli(source.getUnSnoozedAt()).toString());
		alert.addContent(unSnoozedAt);

		Element unSnoozedBy = new Element("UnSnoozedBy", ns);
		unSnoozedBy.addContent(source.getUnSnoozedBy());
		alert.addContent(unSnoozedBy);
		
		if (wellAlert != null) {
			Element wellId = new Element("WellId", ns);
			String wellIdString = wellAlert.getWellId();
			wellId.addContent(wellIdString);
			alert.addContent(wellId);

			Double holeDepthValue = wellAlert.getHoleDepth();
			if (holeDepthValue != null) {
				Element holeDepth = new Element("HoleDepth", ns);
				holeDepth.addContent(NF.format(holeDepthValue));
				alert.addContent(holeDepth);
			}
			
	
			Double finalHoleDepthValue = wellAlert.getFinalHoleDepth();
			if (finalHoleDepthValue != null) {
				Element finalHoleDepth = new Element("FinalHoleDepth", ns);
				finalHoleDepth.addContent(NF.format(finalHoleDepthValue));
				alert.addContent(finalHoleDepth);
			}
	
			Double bitDepthValue = wellAlert.getBitDepth() ;
			if (bitDepthValue != null) {
				Element bitDepth = new Element("BitDepth", ns);
				bitDepth.addContent(NF.format(bitDepthValue));
				alert.addContent(bitDepth);
			}
	
			Double finalBitDepthValue = wellAlert.getFinalBitDepth();
			if (finalBitDepthValue != null) {
				Element finalBitDepth = new Element("FinalBitDepth", ns);
				finalBitDepth.addContent(NF.format(finalBitDepthValue));
				alert.addContent(finalBitDepth);
			}
	
			Integer rigStateValue = wellAlert.getRigState();
			if (rigStateValue != null) {
				Element rigState = new Element("RigState", ns);
				rigState.addContent(NF.format(rigStateValue));
				alert.addContent(rigState);
			}
	
			Integer finalRigStateValue = wellAlert.getFinalRigState() ;
			if (finalRigStateValue != null) {
				Element finalRigState = new Element("FinalRigState", ns);
				finalRigState.addContent(NF.format(finalRigStateValue));
				alert.addContent(finalRigState);
			}
		}
		
		Element notificationsSent = new Element("NotificationsSent", ns);
		notificationsSent.addContent(String.valueOf(source.isOnCreateEventsExecuted()));
		alert.addContent(notificationsSent);
		
		if (source instanceof AlertImpl) {
			AlertImpl rtAlert = (AlertImpl)source;
									
			Element lastSnoozedAt = new Element("LastSnoozedAt", ns);
			lastSnoozedAt.addContent(Instant.ofEpochMilli(rtAlert.getLastSnoozedAt()).toString());
			alert.addContent(lastSnoozedAt);
			
			Element lastSnoozedBy = new Element("LastSnoozedBy", ns);
			lastSnoozedBy.addContent(rtAlert.getLastSnoozedBy());
			alert.addContent(lastSnoozedBy);
			
			Element investigateAt = new Element("InvestigateAt", ns);
			investigateAt.addContent(Instant.ofEpochMilli(rtAlert.getInvestigateAt()).toString());
			alert.addContent(investigateAt);
			
			Element investigateBy = new Element("InvestigateBy", ns);
			investigateBy.addContent(rtAlert.getInvestigateBy());
			alert.addContent(investigateBy);
			
			String parentAlertUuid = rtAlert.getParentUuid();
			String parentClassId = rtAlert.getParentClassId();
			if (parentAlertUuid != null) {
				Element parentElem = new Element("Parent", ns);
				parentElem.setAttribute("UUID", parentAlertUuid);
				parentElem.setAttribute("ClassId", parentClassId);
				alert.addContent(parentElem);
			}
			
			
		}
		
		
		return alert;
	}
	
	/**
	 * Serialize Multiple Alert as list of documents
	 * @param alerts
	 * @return Document which contains AlertStatusResponse containing the alerts
	 */
	public static Document serializeAlertsAsDocument(List<Alert> alerts) {
		
		Namespace ns = Namespace.getNamespace(XML_NAMESPACE);
		Element rootElement = new Element(XML_ROOT_NAME, ns);

		if (alerts != null) {
			for (Alert source:alerts) {
				Element alertElement = serializeAlertAsElement(source);
				rootElement.addContent(alertElement);
			}
		}
		
		Document doc = new Document(rootElement);
		return doc;
	}
	
	/**
	 * Serialize an Alert into Xml Document
	 * @param source
	 * @return Document which contains AlertStatusResponse containing single alert
	 */
	public static Document serializeAlertAsDocument(Alert source) {
		return serializeAlertAsDocument(source,false);
	}
	
	/**
	 * Serialize an Alert into Xml Document
	 * @param source
	 * @param serializeNullAsEmptyDocument If , True, it will serialize empty document if source is null
	 * @return  Document which contains AlertStatusResponse containing single alert
	 */
	public static Document serializeAlertAsDocument(Alert source, boolean serializeNullAsEmptyDocument) {
		if (source == null && !serializeNullAsEmptyDocument) {
			return null;
		}
		
		Namespace ns = Namespace.getNamespace(XML_NAMESPACE);
		Element rootElement = new Element(XML_ROOT_NAME, ns);
		
		if (source != null) {
			Element alert = serializeAlertAsElement(source);
			rootElement.addContent(alert);
		}

		Document doc = new Document(rootElement);
		return doc;
	}
	
	
	
	/**
	 * Serialize Exception as Alert Error response
	 * @param e
	 * @return Document which contains AlertStatusResponse containing the AlertError for the exception
	 */
	public static Document serializeExceptionAsAlertError(Exception e) {
		Namespace ns = Namespace.getNamespace(XML_NAMESPACE);
		Element rootElement = new Element(XML_ROOT_NAME, ns);
		Document doc = new Document(rootElement);
		Element error = new Element("AlertError",ns);
		doc.getRootElement().addContent(error);
		error.setAttribute("code", "500");
		
		if (e != null) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			error.setText(e.getMessage() + "\n" + writer.toString());
		} else {
			error.setText("Unknown Error");
		}
		return doc;
	}
	
	/**
	 * Serialize SnoozeRecord as Xml Element
	 * @param snoozeRecord
	 * @return Element which contains AlertStatusResponse containing the alerts
	 */
	public static Element serializeSnoozeRecordAsElement(SnoozeRecord snoozeRecord) {
		Namespace ns = Namespace.getNamespace(XML_NAMESPACE);
		Element classElement = new Element("SnoozeRecord", ns);

		Element classId = new Element("ClassId", ns);
		classId.addContent(snoozeRecord.getClassId());
		classElement.addContent(classId);

		Element well = new Element("Well", ns);
		well.addContent(snoozeRecord.getWellId());
		classElement.addContent(well);

		Element snoozedBy = new Element("SnoozedBy", ns);
		snoozedBy.addContent(snoozeRecord.getSnoozedBy());
		classElement.addContent(snoozedBy);

		Element snoozedAt = new Element("SnoozedAt", ns);
		snoozedAt.addContent(snoozeRecord.getSnoozedAt().toString());
		classElement.addContent(snoozedAt);

		Element expireAt = new Element("ExpireAt", ns);
		expireAt.addContent(snoozeRecord.getUnSnoozeAt().toString());
		classElement.addContent(expireAt);
		
		return classElement;
	}
	
	/**
	 * Serialize SnoozeRecord as XML Document
	 * @param snoozeRecord
	 * @return Document which contains AlertStatusResponse containing the snoozeRecord
	 */
	public static Document serializeSnoozeRecordAsDocument(SnoozeRecord snoozeRecord) {
		Namespace ns = Namespace.getNamespace(XML_NAMESPACE);
		Element rootElement = new Element(XML_ROOT_NAME, ns);

		Element element = serializeSnoozeRecordAsElement(snoozeRecord);
		rootElement.addContent(element);
		
		Document doc = new Document(rootElement);
		return doc;
	}
	
	/**
	 * Serialize multiple snooze records as XML Documents
	 * @param snoozeRecords
	 * @return XML Document which contains AlertStatusResponse containing the Snooze Records
	 */
	public static Document serializeSnoozeRecordsAsDocument(List<SnoozeRecord> snoozeRecords) {
		Namespace ns = Namespace.getNamespace(XML_NAMESPACE);
		Element rootElement = new Element(XML_ROOT_NAME, ns);

		for(SnoozeRecord snoozeRecord: snoozeRecords ) {
			Element element = serializeSnoozeRecordAsElement(snoozeRecord);
			rootElement.addContent(element);
		}
		Document doc = new Document(rootElement);
		return doc;
	}
	
	/**
	 * Serialize an integer as an AlertCount element
	 * @param count
	 * @return An XML Document which contains AlertStatusResponse containing a single AlertCount element
	 */
	public static Document serializeAlertCountAsDocument(int count) {
		Namespace ns = Namespace.getNamespace(XML_NAMESPACE);
		Element rootElement = new Element(XML_ROOT_NAME, ns);

		Element countElement = new Element("AlertCount", ns);
		countElement.setText(Integer.toString(count));
		rootElement.addContent(countElement);
		
		return new Document(rootElement);
	}
	
	
	/**
	 * Create single alert from source set
	 * @param source
	 * @return AlertImpl represented by the SQL ResultSet
	 */
	private static AlertImpl createAlertFromResultSet(ResultSet source) throws SQLException {
		//TODO: this is very similar to AlertDAO.getAlert(Alert alert, ResultSet rs)
		//with slightly different exception handling and parsing (this uses locale friendly parsing)
		//At some point, it should be unified into AlertDAO, because ideally the only one 
		//who needs to know about DB is DataAccessObject
		
		AlertImpl alert = new AlertImpl();
		alert.setUuid(source.getString("UUID"));
		alert.setName(source.getString("name"));

		String classId = source.getString("classId"); 
		if (classId != null) {
			alert.setClassId(classId);
		}
		alert.setDescription(source.getString("description"));
		alert.setDomain(source.getString("domain"));
		alert.setClassification(source.getString("classification"));
		
		Integer severity = parseIntegerFromResultSet("severity", source);
		if (severity != null){
			alert.setSeverity(severity.intValue());
		}
		
		Integer priority = parseIntegerFromResultSet("priority", source);
		if (priority != null){
			alert.setPriority(priority);
		}
		
		alert.setStatus(source.getInt("status"));
		Timestamp lastStatusChange = source.getTimestamp("lastStatusChange");
		if (lastStatusChange != null) {
			alert.setLastStatusChange(lastStatusChange.toInstant().toEpochMilli());
		}
		
		Timestamp created = source.getTimestamp("created");
		if (created != null) {
			alert.setCreated(created.toInstant().toEpochMilli());
		}
		
		Timestamp lastOccurrence = source.getTimestamp("lastOccurrence");
		if (lastOccurrence != null) {
			alert.setLastOccurrence(lastOccurrence.toInstant().toEpochMilli());
		}

		Timestamp acknowledgeAt = source.getTimestamp("acknowledgeAt");
		if (acknowledgeAt != null) {
			alert.setAcknowledgeBy(source.getString("acknowledgeBy"));
			alert.setAcknowledgeAt(acknowledgeAt.toInstant().toEpochMilli());
		}
		
		Timestamp commentedAt = source.getTimestamp("commentedAt"); 
		if (commentedAt != null) {
			alert.setCommentedAt(commentedAt.toInstant().toEpochMilli());
			alert.setComment(source.getString("comment"));
			alert.setCommentedBy(source.getString("commentBy"));
		}
		
		Integer commentedCount = parseIntegerFromResultSet("commentedCount", source);
		if (commentedCount != null) {
			alert.setCommentedCount(commentedCount.intValue());
		}
		
		Integer tally = parseIntegerFromResultSet("tally", source);
		if (tally != null) {
			alert.setTally(tally.intValue());
		}
		
		String metadataString = source.getString("metadata");
		if (StringUtils.isNotBlank(metadataString)) {
			try {
				JSONObject metadataObj = new JSONObject(metadataString);
				alert.setMetadata(metadataObj);
			} catch (JSONException e) {
				logger.warn("Unable to parse JSON for metadata: {}", metadataString, e);
			}
		}

		String alertContentType = source.getString("detailsContentType");
		alert.setDetailsContentType(alertContentType);
		
		String alertDetailsText = source.getString("details");
		if (alertDetailsText != null && StringUtils.isNotBlank(alertContentType)) {
			switch (alert.getDetailsContentType()) {
				case Alert.DETAILS_JSON:
					try {
						alert.setDetails(new JSONObject(alertDetailsText));
					} catch (JSONException e) {
						logger.warn("Unable to parse JSON for alert details: {}", alertDetailsText, e);
					}
					break;
				case Alert.DETAILS_STRING:
				case Alert.DETAILS_LIST:
				default:
					alert.setDetails(alertDetailsText);
					break;
			}
		}
		
		alert.setCreatedIndex(source.getString("createdIndex"));
		alert.setLastIndex(source.getString("lastIndex"));


		
		String snoozeBy = source.getString("snoozedBy");
		if (StringUtils.isNotBlank(snoozeBy)) {
			alert.setSnoozed(true);
		} else {
			alert.setSnoozed(false);
		}
		alert.setSnoozedBy(snoozeBy);
		

		Timestamp snoozedAtTimestamp =source.getTimestamp("snoozedAt"); 
		if (snoozedAtTimestamp != null) {
			alert.setSnoozedAt(snoozedAtTimestamp.toInstant().toEpochMilli());
		}
		
		alert.setLastSnoozedBy(source.getString("lastSnoozedBy"));
		Timestamp lastSnoozedAtTimestamp = source.getTimestamp("lastSnoozedAt");
		if (lastSnoozedAtTimestamp != null) {
			alert.setLastSnoozedAt(lastSnoozedAtTimestamp.toInstant().toEpochMilli());
		}
		
		
		Timestamp unSnoozeAtTimestamp =source.getTimestamp("unSnoozeAt");
		if (unSnoozeAtTimestamp != null) {
			alert.setUnSnoozeAt(unSnoozeAtTimestamp.toInstant().toEpochMilli());
		}
		
		Timestamp unSnoozedAtTimestamp =source.getTimestamp("unSnoozedAt");
		if (unSnoozedAtTimestamp != null) {
			alert.setUnSnoozedAt(unSnoozedAtTimestamp.toInstant().toEpochMilli());
		}
		
		alert.setUnSnoozedBy(source.getString("unSnoozedBy"));

		alert.setWellId(source.getString("wellId"));
		
		Double holeDepthValue = parseDoubleFromResultSet("holeDepth", source);
		if (holeDepthValue != null) {
			alert.setHoleDepth(holeDepthValue);
		}

		Double finalHoleDepthValue = parseDoubleFromResultSet("finalHoleDepth", source);
		if (finalHoleDepthValue != null) {
			alert.setFinalHoleDepth(finalHoleDepthValue);
		}

		Double bitDepthValue = parseDoubleFromResultSet("bitDepth", source);
		if (bitDepthValue != null) {
			alert.setBitDepth(bitDepthValue);
		}
		
		Double finalBitDepthValue = parseDoubleFromResultSet("finalBitDepth", source);
		if (finalBitDepthValue != null) {
			alert.setFinalBitDepth(finalBitDepthValue);
		}

		
		Integer rigStateValue = parseIntegerFromResultSet("rigState",source);
		if (rigStateValue != null) {
			alert.setRigState(rigStateValue);
		}
		
		Integer finalRigStateValue = parseIntegerFromResultSet("finalRigState",source);
		if (finalRigStateValue != null) {
			alert.setFinalRigState(finalRigStateValue);
		}
		
		

		String notificationsSent = source.getString("notificationsSent");
		if (StringUtils.isNotBlank(notificationsSent)) {
			alert.setOnCreateEventsExecuted(Boolean.valueOf(notificationsSent).booleanValue());
		}
		
		alert.setInvestigateBy(source.getString("investigateBy"));
		
		Timestamp investigateAtTimestamp =source.getTimestamp("investigateAt");
		if (investigateAtTimestamp != null) {
			alert.setInvestigateAt(investigateAtTimestamp.toInstant().toEpochMilli());
		}
		
		alert.setParentClassId(source.getString("parentClassId"));
		alert.setParentUuid(source.getString("parentUuid"));
		
		return alert;
	}
	/**
	 * Create alert from source set
	 * @param source
	 * @return List of Alrt
	 */
	public static List<Alert> createAlertsFromResultSet(ResultSet source) {
		
		try {
			ArrayList<Alert> alertList = new ArrayList<Alert>();
			
			while (source.next()) {
				AlertImpl alert = createAlertFromResultSet(source);
				alertList.add(alert);
			}


			return alertList;
		} catch (SQLException e) {
			logger.error("Unable to get alerts ", e);
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private static SnoozeRecord createSnoozeRecordFromResultSet(ResultSet source) throws SQLException {
		SnoozeRecord record = new SnoozeRecord(source.getString("alertClassId"), source.getString("well"), source.getString("snoozedBy"),
				source.getTimestamp("snoozedAt").toInstant(), source.getTimestamp("unSnoozeAt").toInstant());
		return record;
	}
	
	

	private static Integer parseIntegerFromResultSet(String columnName, ResultSet source) throws SQLException {
		Assert.notNull(source);
		String stringValue = source.getString(columnName);
		if (StringUtils.isNotBlank(stringValue)) {
			try{
				Integer integerValue = Integer.valueOf(stringValue);
				return integerValue;
			} catch (NumberFormatException nfe) {
				logger.warn("columnName {} with value {} can not be parsed.", columnName, stringValue, nfe);
			}
		} else {
			logger.debug("columnName {} is blank, can't be parsed to int.", columnName);
		}
		return null;
	}
	
	private static Double parseDoubleFromResultSet(String columnName, ResultSet source) throws SQLException {
		Assert.notNull(source);
		String stringValue = source.getString(columnName);
		if (StringUtils.isNotBlank(stringValue)) {
			try{
				Number doubleVal = NF.parse(stringValue);
				return new Double(doubleVal.doubleValue());
			} catch (java.text.ParseException e) {
				logger.warn("columnName {} with value {} can not be parsed.", columnName, stringValue, e);
			}
		} else {
			logger.debug("columnName {} is blank, can't be parsed to double. ", columnName);
		}
		return null;
	}
}
