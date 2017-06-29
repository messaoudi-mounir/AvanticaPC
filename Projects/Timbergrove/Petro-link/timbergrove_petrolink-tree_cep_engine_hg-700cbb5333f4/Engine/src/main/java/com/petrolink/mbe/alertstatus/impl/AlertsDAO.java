package com.petrolink.mbe.alertstatus.impl;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.journal.AcknowledgeMultiJournalImpl;
import com.petrolink.mbe.journal.InvestigateMultiJournalImpl;
import com.petrolink.mbe.util.DateTimeHelper;
import com.petrolink.mbe.util.SQLHelper;
import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.AlertJournal;
import com.smartnow.alertstatus.AlertListener;
import com.smartnow.alertstatus.AlertTemplate;
import com.smartnow.alertstatus.AlertsFactory;
import com.smartnow.alertstatus.impl.AlertTemplateImpl;
import com.smartnow.alertstatus.journal.impl.AcknowledgeJournalImpl;
import com.smartnow.alertstatus.journal.impl.AlertChangeJournalImpl;
import com.smartnow.alertstatus.journal.impl.AlertCreateJournalImpl;
import com.smartnow.alertstatus.journal.impl.AlertStatusChangeJournalImpl;
import com.smartnow.alertstatus.journal.impl.CommentJournalImpl;
import com.smartnow.alertstatus.journal.impl.DefaultJournalTypes;
import com.smartnow.alertstatus.journal.impl.SnoozeJournalImpl;
import com.smartnow.alertstatus.serializers.AlertSerializer;
import com.smartnow.alertstatus.serializers.AlertSerializerFactory;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.scheduler.service.SchedulerService;

/**
 * H2 based Alert Status Table
 * 
 * This class includes all the methods to access the tables in h2
 * 
 * @author paul
 *
 */
public class AlertsDAO implements com.smartnow.alertstatus.AlertsDAO {
	/**
	 * Maximum length for Comments
	 */
	public static final int MAXIMUM_COMMENT_LENGTH = 500;
	private final String MAXIMUM_COMMENT_LENGTH_ERROR = "Comment is too long, maximum "+MAXIMUM_COMMENT_LENGTH+ " chars";
	
	private static final Logger logger = LoggerFactory.getLogger(AlertsDAO.class);
	List<AlertListener> listeners = new ArrayList<AlertListener>();
	private HashMap<String, Alert> activeAlerts = new HashMap<>();
	private HashMap<String, HashMap<String, Alert>> activeAlertsByClassWell = new HashMap<>();
	protected HashMap<String, AutoAlertDismissal> autoDismissByClassId = new HashMap<>();
	protected HashMap<String, DelayAlertAction> alertActionDelayByClassId = new HashMap<>();
	private SnoozeRecordsDAO snoozeRecordsDAO;
	private AlertsService service;
	private SchedulerService schedulerService;
	private Scheduler jobScheduler;
	private Set<String> alertClasses = Collections.synchronizedSet(new HashSet<String>());
	private String snoozeProcessPrincipal = "System";
	private String autoDismissPrincipal ="System";
	
	private static String UNSNOOZE_JOB_GROUP =  "snoozeAlert";
	
	// Improve performance by adding Hash Indexes for:
	// - Active Alerts by UUID
	// - Active or Inactive Alerts by Name (lastest)

	/**
	 * AlertsDAO Constructor
	 */
	public AlertsDAO() {
		// No need for this, since dependency Injection eliminates the need to
		// obtain the Service from the engine directly
		// This enables the implementation of JUnit testing
		// setSchedulerService(ServiceAccessor.getSchedulerService());
	}

	/**
	 * Creates the alerts from a ResultSet
	 * 
	 * @param alert
	 * 
	 * @param rs
	 *            - ResultSet
	 * @return Alert to be added to the list
	 * @throws SQLException
	 */
	private AlertImpl getAlert(Alert alert, ResultSet rs) throws SQLException {
		AlertImpl a = (AlertImpl) alert;
		a.setUuid(rs.getString("UUID"));
		a.setStatus(rs.getInt("status"));
		a.setLastStatusChange(SQLHelper.getTimestampAsTime(rs, "lastStatusChange"));
		a.setCreated(SQLHelper.getTimestampAsTime(rs, "created"));
		a.setLastOccurrence(SQLHelper.getTimestampAsTime(rs, "lastOccurrence"));
		a.setAcknowledgeBy(rs.getString("acknowledgeBy"));
		a.setAcknowledgeAt(SQLHelper.getTimestampAsTime(rs, "acknowledgeAt"));
		a.setComment(rs.getString("comment"));
		a.setCommentedBy(rs.getString("commentBy"));
		a.setCommentedAt(SQLHelper.getTimestampAsTime(rs, "commentedAt"));
		a.setCommentedCount(rs.getInt("commentedCount"));
		a.setTally(rs.getInt("tally"));
		a.setName(rs.getString("name"));
		a.setClassId(rs.getString("classId"));
		a.setDescription(rs.getString("description"));
		a.setDomain(rs.getString("domain"));
		a.setClassification(rs.getString("classification"));
		a.setSeverity(rs.getInt("severity"));
		a.setPriority(rs.getInt("priority"));
		a.setDetailsContentType(rs.getString("detailsContentType"));
		a.setCreatedIndex(rs.getString("createdIndex"));
		a.setLastIndex(rs.getString("lastIndex"));

		String alertDetailsText = rs.getString("details");
		if (alertDetailsText != null) {
			switch (a.getDetailsContentType()) {
			case Alert.DETAILS_JSON:
				try {
					a.setDetails(new JSONObject(alertDetailsText));
				} catch (JSONException e) {
					logger.error("Unable to parse JSON for alert details: {}", alertDetailsText, e);
				}
				break;
			case Alert.DETAILS_STRING:
			case Alert.DETAILS_LIST:
			default:
				a.setDetails(alertDetailsText);
				break;
			}
		}
		if (!"".equals(rs.getString("metadata")) && rs.getString("metadata") != null) {
			a.setMetadata(new JSONObject(rs.getString("metadata")));
		}

		a.setSnoozed(rs.getBoolean("snoozed"));
		a.setSnoozedBy(rs.getString("snoozedBy"));
		a.setSnoozedAt(SQLHelper.getTimestampAsTime(rs, "snoozedAt"));
		
		a.setLastSnoozedBy(rs.getString("lastSnoozedBy"));
		a.setLastSnoozedAt(SQLHelper.getTimestampAsTime(rs, "lastSnoozedAt"));

		a.setUnSnoozeAt(SQLHelper.getTimestampAsTime(rs, "unSnoozeAt"));

		a.setUnSnoozedBy(rs.getString("UnSnoozedBy"));
		a.setUnSnoozedAt(SQLHelper.getTimestampAsTime(rs, "UnSnoozedAt"));

		a.setWellId(rs.getString("wellId"));

		a.setBitDepth(SQLHelper.getDoubleOrNull(rs, "bitDepth"));
		a.setFinalBitDepth(SQLHelper.getDoubleOrNull(rs, "finalBitDepth"));
		a.setHoleDepth(SQLHelper.getDoubleOrNull(rs, "holeDepth"));
		a.setFinalHoleDepth(SQLHelper.getDoubleOrNull(rs, "finalHoleDepth"));
		a.setRigState(SQLHelper.getIntegerOrNull(rs, "rigState"));
		a.setFinalRigState(SQLHelper.getIntegerOrNull(rs, "finalRigState"));

		a.setOnCreateEventsExecuted(rs.getBoolean("notificationsSent"));

		
		a.setInvestigateBy(rs.getString("investigateBy"));
		a.setInvestigateAt(SQLHelper.getTimestampAsTime(rs, "investigateAt"));
		
		a.setParentClassId(rs.getString("parentClassId"));
		a.setParentUuid(rs.getString("parentUuid"));
		
		return a;
	}

	private DataSource getAlertsDataSource() {
		return getService().getAlertsDataSource();
	}

	/**
	 * @param rs
	 *            The ResultSet of the Alerts
	 * @return List of all the alerts in the DB
	 * @see com.smartnow.alertstatus.AlertsDAO#getAlerts()
	 */
	public List<Alert> getAlerts(ResultSet rs) {
		// Select all alerts
		try {
			List<Alert> alerts = new ArrayList<Alert>();
			while (rs.next()) {
				AlertImpl a = getAlert(AlertsFactory.getAlert(), rs);
				alerts.add(a);
			}
			return alerts;
		} catch (SQLException e) {
			logger.error("Error getting alerts", e);
		}

		return null;
	}

	/**
	 * @return List of all the alerts in the DB
	 * @see com.smartnow.alertstatus.AlertsDAO#getAlerts()
	 */
	@Override
	public List<Alert> getAlerts() {

		// Select all alerts
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		try  {
			alerts.addAll(service.getAlertsDataStore().getAlerts_TEMP());
		} catch (SQLException e) {
			logger.error("Error getting alerts", e);
		}
		return alerts;
	}

	/**
	 * 
	 * @param where
	 * @return List of alerts based on a where query
	 */
	public List<Alert> getAlertsWhere(String where) {
		if (!where.contains("where")) {
			where = "where " + where;
		}
		String query = "select * from alerts LEFT JOIN snoozedalerts on alerts.classId = snoozedalerts.alertClassId " + where;
		// Select all alerts
		try (Connection conn = getAlertsDataSource().getConnection();
				Statement st = conn.createStatement()) {
			ResultSet rs = st.executeQuery(query);
			return getAlerts(rs);
		} catch (SQLException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * @return all active alerts (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#getActiveAlerts()
	 */
	@Override
	public List<Alert> getActiveAlerts() {
		return new ArrayList<Alert>(activeAlerts.values());
	}

	/**
	 * @param well
	 * @return List of Alerts of a specific well
	 */
	public List<Alert> getAlertsByWell(String well) {
		try (Connection conn = getAlertsDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"select * from alerts LEFT JOIN snoozedalerts on alerts.classId = snoozedalerts.alertClassId where wellId = ?")) {
			ps.setString(1, well);
			ResultSet rs = ps.executeQuery();
			return getAlerts(rs);
		} catch (SQLException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * @param well
	 * @param classId
	 * @return List
	 */
	public Collection<Alert> getActiveAlertsByWellClassId(String well, String classId) {
		if (activeAlertsByClassWell.containsKey(classId)) {
			return activeAlertsByClassWell.get(classId).values();
		}

		try (Connection conn = getAlertsDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"select * from alerts LEFT JOIN snoozedalerts on alerts.classId = snoozedalerts.alertClassId where status = "
								+ Alert.ACTIVE + " and wellId = ? and classId = ?")) {
			ps.setString(1, well);
			ps.setString(2, classId);
			ResultSet rs = ps.executeQuery();
			return getAlerts(rs);
		} catch (SQLException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * Start all needed components
	 */
	public void start() {
		reloadActiveAlertsfromStore();
		rescheduleUnsnoozeJobs();
	}
	
	/**
	 * 
	 * Reload active alerts
	 */
	private void reloadActiveAlertsfromStore() {
		logger.info("Loading Active Alerts from Database to Memory");
		
		ArrayList<AlertImpl> activeAlertsInDb = new ArrayList<AlertImpl>(); 
		try {
			for (Alert a : service.getAlertsDataStore().getAlertsByStatus(Alert.ACTIVE)) {
				activeAlertsInDb.add((AlertImpl) a);
			}
		} catch (SQLException e) {
			logger.error("Error reloadActiveAlerts", e);
		}
		
		int count = 0;
		for (AlertImpl alertImpl : activeAlertsInDb) {
			//just incase something is modified in the middle of process
			updateActiveAlertsCache(alertImpl);
			if (alertImpl.getStatus() == Alert.ACTIVE)
				count++;
		}
		logger.info("Loaded {} Active Alerts from Database to Memory", count);
	}
	
	/**
	 * After engine dies, scheduler will dies. it needs to reschedule all unsnoozejob again
	 */
	private void rescheduleUnsnoozeJobs() {
		logger.info("Loading Active UnsnoozeJob from DAO");
		List<SnoozeRecord> snoozeRecords =  snoozeRecordsDAO.getSnoozeRecords();
		int count = 0;
		for (SnoozeRecord record : snoozeRecords) {
			OffsetDateTime unsnoozeAt = OffsetDateTime.ofInstant(record.getUnSnoozeAt(),ZoneOffset.UTC);
			scheduleUnsnoozeJob(record.getClassId(), record.getWellId(), unsnoozeAt);
			count++;
		}
		logger.info("Loaded {} Active UnsnoozeJob(s) from DAO",count);
	}
	
	/**
	 * @param well
	 * @param classId
	 * @return the Active Alert by Well and Class Id
	 */
	public Alert getActiveAlertByWellClassId(String well, String classId) {
		if (activeAlertsByClassWell.containsKey(classId)) {
			if (activeAlertsByClassWell.get(classId).containsKey(well)) {
				return activeAlertsByClassWell.get(classId).get(well);
			}
		}

		try {
			return service.getAlertsDataStore().getAlertByStatusClassWell(0, classId, well);
		} catch (SQLException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * Returns the Serialized Active Alert filtered by Well and Class
	 * 
	 * @param wellId
	 * @param classId
	 * @param serializer
	 * @return the serialized alert
	 */
	public Document getActiveAlertByWellClassId(String wellId, String classId, String serializer) {
		try {
			AlertSerializer as = AlertSerializerFactory.getAlertSerializer(serializer);
			return (Document) as.serialize(getActiveAlertByWellClassId(wellId, classId));
		} catch (EngineException e) {
			logger.error("Error serializing the Alert", e);
		}

		return null;
	}

	/**
	 * @return Alert with the given class
	 * @param classId
	 *            of the alert (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#getActiveAlertByClassId(java.lang.String)
	 */
	@Override
	public Alert getActiveAlertByClassId(String classId) {
		// Not applicable to Petrolink
		return null;
	}

	/**
	 * Get active alerts for specified class
	 * @param classId
	 * @return List of Alerts
	 */
	public List<Alert> getActiveAlertsByClassId(String classId) {
		List<Alert> allActiveAlerts = getActiveAlerts();
		ArrayList<Alert> activeAlertForClass = new ArrayList<Alert>();
		for (Alert alert : allActiveAlerts) {
			if (StringUtils.equals(classId, alert.getClassId())) {
				activeAlertForClass.add(alert);
			}
		}
		return activeAlertForClass;
	}
	/**
	 * Returns the Serialized Active Alert filtered by Class
	 * 
	 * @param classId
	 * @param serializer
	 * @return the serialized Alert
	 */
	public Document getActiveAlertsByClassId(String classId, String serializer) {
		try {
			AlertSerializer as = AlertSerializerFactory.getAlertSerializer(serializer);
			if (activeAlertsByClassWell.containsKey(classId))
				return (Document) as.serialize(activeAlertsByClassWell.get(classId).values());
		} catch (EngineException e) {
			logger.error("Error serializing the Alert", e);
		}
		return null;
	}

	/**
	 * 
	 * @param classId
	 * @return Alert
	 */
	public List<Alert> getAlertsByClassId(String classId) {
		ArrayList<Alert> alerts = new ArrayList<Alert>();
		try (Connection conn = getAlertsDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"select * from alerts LEFT JOIN snoozedalerts on alerts.classId = snoozedalerts.alertClassId where classId = ?")) {
			ps.setString(1, classId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				AlertImpl a = getAlert(AlertsFactory.getAlert(), rs);
				alerts.add(a);
			}

		} catch (SQLException e) {
			logger.error("Error getting alerts with class {}", classId, e);
		}

		return alerts;
	}
	
	/**
	 * Get List of Alert UUID with specified class and status
	 * @param classId
	 * @param status 
	 * @return Alert
	 */
	public List<String> getAlertUUidsByClassIdAndStatus(String classId, int status) {
		ArrayList<String> alerts = new ArrayList<String>();
		try (Connection conn = getAlertsDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"select UUID from alerts where classId = ? AND status = ?")) {
			ps.setString(1, classId);
			ps.setInt(2, status);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				alerts.add(rs.getString("UUID"));
			}

		} catch (SQLException e) {
			logger.error("Error getting alerts's uuid with class {} and status {}", classId, status, e);
		}

		return alerts;
	}
	/**
	 * Count the number of alerts that match a WHERE clause.
	 * @param where
	 * @return The matching alert count
	 */
	public int getAlertCountWhere(String where) {
		// classId = ? AND acknowledgeAt IS NULL
		
		if (!where.contains("where"))
			where = "where " + where;
		
		String query = "SELECT COUNT(*) FROM alerts LEFT JOIN snoozedalerts ON alerts.classId = snoozedalerts.alertClassId " + where;
		
		int count = 0;
		try (Connection conn = getAlertsDataSource().getConnection();
			 Statement st = conn.createStatement()) {
			// do not use prepared statement since the query is custom
			ResultSet rs = st.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.error("error while counting alerts", e);
		}
		return count;
	}
	
	/**
	 * @return List of alerts with the given status
	 * @param status of the alert
	 */
	public List<Alert> getAlertsbyStatus(int status) {
		if (status == Alert.ACTIVE) {
			return getActiveAlerts();
		}
		try {
			return service.getAlertsDataStore().getAlertsByStatus(status);
		} catch (SQLException e) {
			logger.error("Error getting alerts", e);
		}
		return new ArrayList<>();
	}

	/**
	 * @return list of alerts based on where conditions
	 * @param where
	 *            : sql conditions for filter alerts (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#getAlertsUUIDs(java.lang.String)
	 */
	@Override
	public List<String> getAlertsUUIDs(String where) {
		// Auto-generated method stub
		try (Connection conn = getAlertsDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"select UUID from alerts LEFT JOIN snoozedalerts on alerts.classId = snoozedalerts.alertClassId "
								+ where)) {
			ResultSet rs = ps.executeQuery();
			List<String> uuids = new ArrayList<String>();

			while (rs.next()) {
				uuids.add(rs.getString("UUID"));
			}
			return uuids;
		} catch (SQLException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * 
	 * @return list of Alerts and Created time of all alerts
	 */
	public HashMap<String, Long> getAlertsUUIDSCreates() {
		try (Connection conn = getAlertsDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement("select UUID, created from alerts")) {
			ResultSet rs = ps.executeQuery();
			HashMap<String, Long> hm = new HashMap<String, Long>();
			while (rs.next()) {
				hm.put(rs.getString("UUID"), rs.getLong("created"));
			}
			return hm;
		} catch (SQLException e) {
			logger.error("Error getting alerts", e);
		}

		return null;
	}

	/**
	 * Return alerts in defined format(XML, JSON)
	 * 
	 * @return get all alerts serialized
	 * @param serializer
	 *            (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#getSerializedAlerts(java.lang.String)
	 */
	@Override
	public Object getSerializedAlerts(String serializer) {
		try {
			AlertSerializer as = AlertSerializerFactory.getAlertSerializer(serializer);

			try (Connection conn = getAlertsDataSource().getConnection();
					PreparedStatement ps = conn.prepareStatement(
							"select * from alerts LEFT JOIN snoozedalerts on alerts.classId = snoozedalerts.alertClassId")) {
				ResultSet rs = ps.executeQuery();
				return as.serialize(rs);
			} catch (SQLException e) {
				logger.error("SQL exception while querying for alerts", e);
			}

		} catch (EngineException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * Return alerts in defined format(XML, JSON)
	 * 
	 * @return get all alerts serialized
	 * @param serializer
	 *            (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#getSerializedAlerts(java.lang.String)
	 */
	@Override
	public Object getSerializedAlert(String uuid, String serializer) {
		try {
			AlertSerializer as = AlertSerializerFactory.getAlertSerializer(serializer);

			try (Connection conn = getAlertsDataSource().getConnection();
					PreparedStatement ps = conn.prepareStatement(
							"select * from alerts LEFT JOIN snoozedalerts on alerts.classId = snoozedalerts.alertClassId where uuid = ?")) {
				ps.setString(1, uuid);
				ResultSet rs = ps.executeQuery();
				return as.serialize(rs);
			} catch (SQLException e) {
				logger.error("SQL Error getting alerts", e);
			}

		} catch (EngineException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * @return of alerts based on where conditions in a define format
	 * @param where
	 *            : sql conditions for filter alerts
	 * @param serializer
	 *            : tyoe of format to re (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#getSerializedAlerts(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Object getSerializedAlerts(String where, String serializer) {
		try {
			AlertSerializer as = AlertSerializerFactory.getAlertSerializer(serializer);
			if (!where.contains("where")) {
				where = "where " + where;
			}

			try (Connection conn = getAlertsDataSource().getConnection();
					PreparedStatement ps = conn.prepareStatement(
							"select * from alerts LEFT JOIN snoozedalerts on alerts.classId = snoozedalerts.alertClassId "
									+ where)) {
				ResultSet rs = ps.executeQuery();
				return as.serialize(rs);
			} catch (SQLException e) {
				logger.error("SQL Error getting alerts", e);
			}

		} catch (EngineException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * @return Alert of the given uuid
	 * @param uuid
	 *            - uuid of the alert (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#getAlert(java.lang.String)
	 */
	@Override
	public Alert getAlert(String uuid) {
		// Lookup Active Alerts first

		Alert alert = activeAlerts.get(UUID.fromString(uuid));
		if (alert != null) {
			return alert;
		}

		logger.debug("Retreiving Alert from permanent store since it was not found on near cache");
		try (Connection conn = getAlertsDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"select * from alerts LEFT JOIN snoozedalerts on alerts.classid = snoozedalerts.alertClassId where UUID = ?")) {
			ps.setString(1, uuid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				AlertImpl a = getAlert(AlertsFactory.getAlert(), rs);
				return a;
			}
			return null;
		} catch (SQLException e) {
			logger.error("Error getting alert", e);
		} catch (Exception e) {
			logger.error("Error getting alert", e);
		}

		return null;
	}

	@Override
	public Alert getAlert(String uuid, boolean load) {
		if (load)
			return getAlert(uuid);
		else {
			AlertImpl alert = (AlertImpl) activeAlerts.get(uuid);
			if (alert != null)
				return alert;

			alert = (AlertImpl) AlertsFactory.getAlert();
			alert.setUuid(uuid);
			alert.setLoaded(false);
			return alert;
		}
	}

	@Override
	public Alert getAlert(Alert alert) {
		if (activeAlerts.containsKey(alert.getUuid())) {
			((AlertImpl) alert).copyFrom(activeAlerts.get(alert.getUuid()));
			return alert;
		}

		try (Connection conn = getAlertsDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement(
						"select * from alerts LEFT JOIN snoozedalerts on alerts.classid = snoozedalerts.alertClassId where UUID = ?")) {
			ps.setString(1, alert.getUuid());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				AlertImpl a = getAlert(alert, rs);
				return a;
			}

			return service.getAlertsDataStore().getAlert(alert.getUuid());
		} catch (SQLException e) {
			logger.error("Error getting alert", e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param alert
	 *            - alert to update (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#updateAlert(com.smartnow.alertstatus.Alert)
	 */
	@Override
	public boolean updateAlert(Alert alert) {
		Instant currentTimeInstant = Instant.now();
		AlertImpl plinkAlert = null;
		String alertWellId = null;
		if (alert instanceof AlertImpl) {
			plinkAlert = ((AlertImpl) alert);
			alertWellId = plinkAlert.getWellId();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Alert:{}, ClassId:{}, updating, well:{} , status:{}, snoozed:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					alertWellId,
					alert.getStatus(),
					alert.isSnoozed());
		}

		AutoAlertDismissal dismissConfig = autoDismissByClassId.get(alert.getClassId());
		DelayAlertAction delayConfig = alertActionDelayByClassId.get(alert.getClassId());
		
		updateActiveAlertsCache(alert);

		// Update Alert on H2
		Alert oldAlert;
		try {
			oldAlert = service.getAlertsDataStore().getAlert(alert.getUuid());
			service.getAlertsDataStore().updateAlert(alert);
		} catch (SQLException e) {
			logger.error("Error updating alert", e);
			return false;
		}
		
		//Log status
		if (logger.isInfoEnabled()) {
			logger.info("Alert:{}, ClassId:{}, updated, well:{} , status:{}, snoozed:{}, tally:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					alertWellId,
					alert.getStatus(),
					alert.isSnoozed(),
					alert.getTally());
		}

		try {
			if (canExecuteAlertCreateActions(plinkAlert, currentTimeInstant)) {
				//When not snoozed , it is likely being dismissed/delayed previously, so send on create instead
				executeAlertCreateActions(Arrays.asList(plinkAlert));
			} else {
				executeAlertUpdateActions(oldAlert, alert);
			}
		} catch (Exception ex) {
			logger.error("Failure processsing AlertListener onUpdate.", ex);
		}
		
		
		AlertChangeJournalImpl journal = AlertsFactory.getAlertJournal(alert,
		                                                               DefaultJournalTypes.ALERTCHANGE,
		                                                               AlertChangeJournalImpl.class);
		journal.setTimestamp(currentTimeInstant);
		journal.setPrincipal(((com.petrolink.mbe.alertstatus.Alert) alert).getUpdatedByRule());
		invokeListenersOnJournalEntry(journal);

		return true;
	}

	/**
	 * Creates an Alert on the DataBase
	 * 
	 * @return alert created
	 * @param alert
	 *            to be created (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#createAlert(com.smartnow.alertstatus.Alert)
	 */
	@Override
	public Alert createAlert(Alert alert) {
		Instant currentTimeInstant = Instant.now();

		AlertImpl plinkAlert = ((AlertImpl) alert);
		SnoozeRecord record = snoozeRecordsDAO.getSnoozeRecord(alert.getClassId(), plinkAlert.getWellId());
		AutoAlertDismissal dismissConfig = autoDismissByClassId.get(alert.getClassId());
		DelayAlertAction delayConfig = alertActionDelayByClassId.get(alert.getClassId());
		String autoAckPrincipal = null;
		boolean isAutoAcked = false;
		
		if (logger.isDebugEnabled()) {
			logger.debug("Alert:{}, ClassId:{}, creating, well:{} , snoozed:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					plinkAlert.getWellId(),
					alert.isSnoozed());
		}
		
		//Check Auto dismissal configuration
		if (record != null) {
			synchronized (record) {
				plinkAlert.setOnCreateEventsExecuted(false);
				plinkAlert.setSnoozed(true);
				plinkAlert.setSnoozedBy(record.getSnoozedBy());
				plinkAlert.setSnoozedAt(record.getSnoozedAt().toEpochMilli());
				plinkAlert.setLastSnoozedBy(record.getSnoozedBy());
				plinkAlert.setLastSnoozedAt(record.getSnoozedAt().toEpochMilli());
				plinkAlert.setUnSnoozeAt(record.getUnSnoozeAt().toEpochMilli());
			}
		} else {
			plinkAlert.setSnoozed(false);
			
			if (dismissConfig != null && dismissConfig.isShouldBeDismissed(currentTimeInstant, plinkAlert, logger)) {
				plinkAlert.setOnCreateEventsExecuted(false);
				isAutoAcked = true;
				autoAckPrincipal = autoDismissPrincipal;
				plinkAlert.setAcknowledgeAt(currentTimeInstant.toEpochMilli());
				plinkAlert.setAcknowledgeBy(autoAckPrincipal);
			} else if (delayConfig != null && delayConfig.isDelayOnCreateActions()) {
				plinkAlert.setOnCreateEventsExecuted(false);
			} else {
				plinkAlert.setOnCreateEventsExecuted(true);
			}
		}

		updateActiveAlertsCache(alert);

		try {
			service.getAlertsDataStore().createAlert((AlertImpl) alert);
		} catch (SQLException e) {
			logger.error("Error creating alert", e);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("Alert:{}, ClassId:{}, created, well:{} , snoozed:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					plinkAlert.getWellId(),
					alert.isSnoozed());
		}

		//Update Journal
		AlertCreateJournalImpl journal = AlertsFactory.getAlertJournal(alert, DefaultJournalTypes.ALERTCREATE,
				AlertCreateJournalImpl.class);
		journal.setTimestamp(currentTimeInstant);
		journal.setPrincipal(((com.petrolink.mbe.alertstatus.Alert) alert).getUpdatedByRule());
		invokeListenersOnJournalEntry(journal);
		
		if (isAutoAcked) {
			AcknowledgeJournalImpl ackJournal = (AcknowledgeJournalImpl) AlertsFactory.getAlertJournal(alert,
					DefaultJournalTypes.ACKNOWLEDGE);
			ackJournal.setAcknowledge(autoAckPrincipal, currentTimeInstant);
			invokeListenersOnJournalEntry(journal);
		}
				
		//Execute Action
		if (alert.isOnCreateEventsExecuted()) {
			for (AlertListener listener : listeners) {
				try {
					listener.onCreate(alert);
				} catch (Exception ex) {
					logger.error("Failure processsing AlertListener onCreate.", ex);
				}
			}
		} else {
			logger.debug("AlertListener for WellId {} and ClassId {} is skipped", ((AlertImpl) alert).getWellId(),
					alert.getClassId());
		}

		

		return alert;
	}

	/**
	 * @return a new AlertTemplateImpl to create the alert (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#createAlertTemplete()
	 */
	@Override
	public AlertTemplate createAlertTemplete() {
		return new AlertTemplateImpl();
	}

	/**
	 * @param alert
	 *            to change status
	 * @param status
	 *            - new status (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#changeAlertStatus(com.smartnow.alertstatus.Alert,
	 *      int)
	 */
	@Override
	public boolean changeAlertStatus(Alert alert, int status) {
		int old = alert.getStatus();
		if (logger.isDebugEnabled()) {
			logger.debug("Alert:{}, ClassId:{}, changingAlertStatus, from {} to {} ", alert.getUuid(), alert.getClassId(), old , status);
		}

		Instant currentTimeInstant = Instant.now();
		long curTimeMilis = currentTimeInstant.toEpochMilli();
		AlertImpl alertInstance =((AlertImpl) alert); 
		alertInstance.setStatus(status);
		boolean isAutoAcked = false;

		// Update Status Change as well // I'm getting the time from the system
		// Remove from Active Maps is old status was Active
		if (old == Alert.ACTIVE && status != Alert.ACTIVE) {
			//When the alert not acknowledged yet and become inactive, 
			if (!alert.isAcknowledge()) {
				String ackPrincipal= null;
				
				//check snooze condition, if it is being snoozed, it is automatically ack-ed
				SnoozeRecord record = snoozeRecordsDAO.getSnoozeRecord(alert.getClassId(), alertInstance.getWellId());
				if ((record != null) && (record.getSnoozedAt().toEpochMilli() < alert.getCreated())) {
					ackPrincipal = snoozeProcessPrincipal;
					isAutoAcked = true;
				}
				
				//Check Auto dismissal configuration
				AutoAlertDismissal dismissConfig = autoDismissByClassId.get(alert.getClassId());
				if (!isAutoAcked && dismissConfig != null && dismissConfig.isShouldBeDismissed(currentTimeInstant, alertInstance, logger)) {
					ackPrincipal = autoDismissPrincipal;
					isAutoAcked = true;
				}
				
				//Note that delay config should not trigger auto ack, this is mainly because delay config is either ack-ed by other system (eg parent alert) 
				//When not ack-ed it is very likely that normal workflow still on as Parent alert cause may be actually different
				if (isAutoAcked) {
					alertInstance.setAcknowledgeAt(curTimeMilis);
					alertInstance.setAcknowledgeBy(ackPrincipal);
					
					//Put Ack into journal
					AcknowledgeJournalImpl journal = (AcknowledgeJournalImpl) AlertsFactory.getAlertJournal(alertInstance,
							DefaultJournalTypes.ACKNOWLEDGE);
					journal.setAcknowledge(ackPrincipal, currentTimeInstant);
					invokeListenersOnJournalEntry(journal);
				}
			}
		}
		
		// Closing Alert if is already Acknowledge and is becoming Inactive
		if (alertInstance.getStatus() == Alert.INACTIVE && alert.isAcknowledge()) {
			alertInstance.setStatus(Alert.CLOSED);
		}

		updateActiveAlertsCache(alert);
				
		try {
			service.getAlertsDataStore().updateAlert(alertInstance);
		} catch (SQLException e) {
			logger.error("Error updating alert", e);
			return false;
		}

		if (logger.isInfoEnabled()) {
			logger.info("Alert:{}, ClassId:{}, changedAlertStatus, from {} to {} ", alert.getUuid(), alert.getClassId(), old , alert.getStatus());
		}
		
		//Write to journal
		AlertStatusChangeJournalImpl journal = AlertsFactory.getAlertJournal(alert,
				DefaultJournalTypes.ALERTSTATUSCHANGE, AlertStatusChangeJournalImpl.class);
		journal.setStatusChange(((com.petrolink.mbe.alertstatus.Alert) alert).getUpdatedByRule(),
				Instant.ofEpochMilli(((AlertImpl) alert).getLastStatusChange()));
		invokeListenersOnJournalEntry(journal);
		
		//Status change publishing to listener below, is switched to after journal writing as ClosedAlertLogAction is executed in Status Change
		for (AlertListener listener : listeners) {
			try {
				listener.onStatusChange(alert, old);
			} catch (Exception ex) {
				logger.error("Failure processsing AlertListener onStatusChange.", ex);
			}
		}
		
		return true;
	}
	

	/**
	 * Change the alert status
	 * 
	 * @param alertUUID
	 * @param status
	 *            - new status (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#changeAlertStatus(java.lang.String,
	 *      int)
	 */
	@Override
	public boolean changeAlertStatus(String alertUUID, int status) {
		Alert alert = getAlert(alertUUID);
		if (alert == null) {
			logger.warn("Trying to change Alert Status alert ID {} to {} which is not in the system ", alertUUID,
					status);
			return false;
		}

		return changeAlertStatus(alert, status);
	}

	/**
	 * put a comment on the alert
	 * 
	 * @param alert
	 * @param comment
	 *            - new comment
	 * @param commentedBy
	 *            - author of comment
	 * @param timestamp
	 *            (non-Javadoc)
	 */
	@Override
	public boolean commentAlert(Alert alert, String comment, String commentedBy, OffsetDateTime timestamp) {
		if (logger.isDebugEnabled()) {
			logger.debug("Alert:{}, ClassId:{}, commenting, status:{}, by:{} , timestamp:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					alert.getStatus(),
					commentedBy,
					timestamp);
		}

		if (comment.length() >= MAXIMUM_COMMENT_LENGTH) {
			throw new IllegalArgumentException(MAXIMUM_COMMENT_LENGTH_ERROR);
		}
		

		// This must be set so alert listener can receive correct event
		AlertImpl alertImpl = ((AlertImpl) alert);
		alertImpl.setComment(comment);
		alertImpl.setCommentedAt(timestamp.toInstant().toEpochMilli());
		alertImpl.setCommentedBy(commentedBy);
		alertImpl.incrementCommentedCount();

		updateActiveAlertsCache(alertImpl);

		try {
			service.getAlertsDataStore().updateAlert(alertImpl);
		} catch (SQLException e) {
			logger.error("Unable to comment due to error when updating alert", e);
			throw new IllegalStateException("Unable to comment due failure when trying to save it.");
		}
		
		logger.info("Alert:{}, ClassId:{}, commented, status:{}, by:{} , timestamp:{} ", 
				alert.getUuid(),
				alert.getClassId(),
				alert.getStatus(),
				commentedBy,
				timestamp);

		// Creating Journal Entry
		CommentJournalImpl journal = (CommentJournalImpl) AlertsFactory.getAlertJournal(alert,
				DefaultJournalTypes.COMMENT);
		journal.setComment(commentedBy, timestamp.toInstant(), comment);
		invokeListenersOnJournalEntry(journal);
		
		//Execute alert actions
		for (AlertListener listener : listeners) {
			try {
				listener.onComment(alert);
			} catch (Exception ex) {
				logger.error("Failure processsing AlertListener onComment.", ex);
			}
		}
		return true;
	}

	
	/**
	 * put a comment on the alert
	 * 
	 * @param alertUUID
	 *            - uuid of alert
	 * @param comment
	 *            - new comment
	 * @param commentedBy
	 *            - author of alert
	 * @param timestamp
	 *            - date (non-Javadoc)
	 */
	@Override
	public boolean commentAlert(String alertUUID, String comment, String commentedBy, OffsetDateTime timestamp ) {
		Alert alert = getAlert(alertUUID);
		if (alert == null) {
			logger.warn("Trying to comment alert ID {} with comment {} by {} on {} which is not in the system ",
					alertUUID, comment, commentedBy, timestamp);
			return false;
		}

		return commentAlert(alert, comment, commentedBy, timestamp);
	}
	
	/**
	 * acknowledge an alert
	 * 
	 * @param alertUUID
	 *            - uuid of alert
	 * @param acknowledgeBy
	 *            - acknowledge by this
	 * @param timestamp
	 *            - date of acknowledge (non-Javadoc)
	 * @param parentId Parent Alert id which acknowledge this alert
	 * @param parentClassId Parent Alert ClassId which acknowledge this alert
	 * @return whether acknowledge is successfull 
	 */
	public List<Alert> acknowledgeAlert(String alertUUID, String acknowledgeBy, OffsetDateTime timestamp, String parentId, String parentClassId) {
		ArrayList<String> alertUUIDs = new ArrayList<>();
		alertUUIDs.add(alertUUID);
		List<AlertRequestResult> result = acknowledgeAlerts(alertUUIDs, acknowledgeBy,timestamp,null,null);
		List<Alert> updatedAlerts = AlertRequestResult.getAlertsFromResults(result);
		return updatedAlerts;
	}
	
	

	/**
	 * acknowledge an alert
	 * 
	 * @param alert
	 *            - to be acknowledge
	 * @param acknowledgeBy
	 *            - acknowledge by this
	 * @param timestamp
	 *            - date of acknowledge (non-Javadoc)
	 * @deprecated This method is not exactly giving proper response, in which forcing caller to get actually update alerts which is not efficient
	 */
	@Override
	@Deprecated
	public boolean acknowledgeAlert(Alert alert, String acknowledgeBy, OffsetDateTime timestamp) {
		ArrayList<Alert> alerts= new ArrayList<>();
		alerts.add(alert);
		
		List<AlertRequestResult> result = acknowledgeMultiAlert(alerts,acknowledgeBy,timestamp,null,null);
		return (result != null && !result.isEmpty());
		
	}
	
	/**
	 * acknowledge an alert
	 * 
	 * @param alertUUID
	 *            - uuid of alert
	 * @param acknowledgeBy
	 *            - acknowledge by this
	 * @param timestamp
	 *            - date of acknowledge (non-Javadoc)
	 * @deprecated This method is not exactly giving proper response, in which forcing caller to get actually update alerts which is not efficient
	 *            
	 */
	@Override
	@Deprecated
	public boolean acknowledgeAlert(String alertUUID, String acknowledgeBy, OffsetDateTime timestamp) {
		List<Alert> result = acknowledgeAlert(alertUUID,acknowledgeBy,timestamp, null, null);
		return (result != null && !result.isEmpty());
	}
	
	/**
	 * Acknowledge multiple Alerts
	 * @param alertUUIDs
	 * @param by
	 * @param timestamp
	 * @param parentId
	 * @param parentClassId
	 * @return AlertRequestResult list
	 */
	public List<AlertRequestResult> acknowledgeAlerts(List<String> alertUUIDs, String by, OffsetDateTime timestamp, String parentId, String parentClassId) {
		ArrayList<Alert> alerts = new ArrayList<>();
		ArrayList<String> nonExistantUUIDs = new ArrayList<>();
		
		for (String alertUUID : alertUUIDs) {
			Alert alert = getAlert(alertUUID);
			if (alert != null) {
				alerts.add(alert);
			} else {
				nonExistantUUIDs.add(alertUUID);
			}
		}
		
		if (!nonExistantUUIDs.isEmpty() && logger.isWarnEnabled()) {
			logger.warn("Trying to acknowledge alerts by {} on {} which is not in the system: "
					, by
					, timestamp
					, StringUtils.join(nonExistantUUIDs, ",") 
					);
		}
		return acknowledgeMultiAlert(alerts,by, timestamp, parentId, parentClassId);
	}
	
	/**
	 * Primary method to acknowledge multiple Objects
	 * @param alerts Alerts to be acknowledged
	 * @param acknowledgeBy The acknowledger
	 * @param timestamp Time of acknowledgement
	 * @param parentId Parent Alert id which acknowledge this alert
	 * @param parentClassId Parent Alert ClassId which acknowledge this alert
	 * @return AlertRequestResult list
	 */
	private List<AlertRequestResult> acknowledgeMultiAlert(List<Alert> alerts, String acknowledgeBy, OffsetDateTime timestamp, String parentId, String parentClassId) {
		ArrayList<AlertRequestResult> ackresults = new ArrayList<>();
		for (Alert alert : alerts) {
			
			AlertRequestResult result = acknowledgeSingleAlertNoInvoke(alert,acknowledgeBy,timestamp,null,null);
			if (result != null) {
				ackresults.add(result);
			}
		}
		
		if (!ackresults.isEmpty()) {
			//Old one by one mechanism
//			AcknowledgeJournalImpl journal = (AcknowledgeJournalImpl) AlertsFactory.getAlertJournal(result,
//					DefaultJournalTypes.ACKNOWLEDGE);
//			journal.setAcknowledge(acknowledgeBy, timestamp.toInstant());
//			invokeListenersOnJournalEntry(journal);
			
			
			//New mechanism, adaptible to be able to ack one or multiple
			ArrayListValuedHashMap<String, Alert> mappedSuccessList = AlertUtils.getMappedAlertsFromResults(ackresults);
			for(String key : mappedSuccessList.keySet()) {
				List<Alert> classSuccessList = mappedSuccessList.get(key);
				AcknowledgeMultiJournalImpl journal =  new AcknowledgeMultiJournalImpl();
				journal.setAcknowledge(classSuccessList, acknowledgeBy, timestamp.toInstant());
				invokeListenersOnJournalEntry(journal);
			}
			
			
			//Publish result
			for (AlertRequestResult result : ackresults) {
				invokeListenerOnAcknowledge(result);
			}
		}
		return ackresults;
	}
	
	/**
	 * acknowledge an alert
	 * 
	 * @param alert
	 *            - to be acknowledge
	 * @param acknowledgeBy
	 *            - acknowledge by this
	 * @param timestamp
	 *            - date of acknowledge (non-Javadoc)
	 * @param parentId Parent Alert id which acknowledge this alert
	 * @param parentClassId Parent Alert ClassId which acknowledge this alert 
	 * @return 
	 */
	protected AlertRequestResult acknowledgeSingleAlertNoInvoke(Alert alert, String acknowledgeBy, OffsetDateTime timestamp, String parentId, String parentClassId) {
		if (alert == null) {
			logger.debug("{} trying to acknowledging null alert on  {}", acknowledgeBy, timestamp);
			return null;
		}		
		
		if (alert.isAcknowledge()) {
			logger.warn("{} trying to acknowledging already ack-ed alert on  {}", acknowledgeBy, timestamp);
			//Why should republish ack as nothing happen?
//			for (AlertListener listener : listeners) {
//				try {
//					listener.onAcknowledge(alert);
//				} catch (Exception ex) {
//					logger.error("Failure processsing AlertListener onAcknowledge.", ex);
//				}
//			}
			return null;
		} 
		
		//Set Result
		AlertRequestResult ackResult = new AlertRequestResult();
		ackResult.setOriginalProperties(alert);
		
		//Actual update
		AlertImpl alerti = (AlertImpl) alert;
		alerti.setAcknowledgeAt(timestamp.toInstant().toEpochMilli());
		alerti.setAcknowledgeBy(acknowledgeBy);
		if (StringUtils.isNotBlank(parentId)) {
			alerti.setParentUuid(parentId);
		}
		if (StringUtils.isNotBlank(parentClassId)) {
			alerti.setParentClassId(parentClassId);
		}
		
		boolean isCloseTriggered = alert.getStatus() == Alert.INACTIVE;
		if (isCloseTriggered) {
			// Closing alert if Acknowledge and Inactive
			alerti.setStatus(Alert.CLOSED);
		}
		
		updateActiveAlertsCache(alert);
		try {
			service.getAlertsDataStore().updateAlert(alert);
		} catch (SQLException e) {
			logger.error("Error updating alert {} during acknowledgeAlert", alert.getUuid(), e);
			return null;
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("Alert:{}, ClassId:{}, acknowledged, status:{}, by:{} , timestamp:{}, isCloseTriggered:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					alert.getStatus(),
					acknowledgeBy,
					timestamp,
					isCloseTriggered
					);
		}
		
		ackResult.setAffectedAlert(alert);
		return ackResult;
	}
	
	

	/**
	 * Investigate alert
	 * @param alert
	 * @param by
	 * @param timestamp
	 * @return Whether investigate Alert is successfull
	 */
	protected boolean investigateSingleAlertNoInvoke(AlertImpl alert, String by, OffsetDateTime timestamp) {
		if (alert == null) {
			logger.debug("{} trying to investigate null alert on  {}", by, timestamp);
			return false;
		}		
		
		if (logger.isDebugEnabled()) {
			logger.debug("Alert:{}, ClassId:{}, investigating, status:{}, by:{} , timestamp:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					alert.getStatus(),
					by,
					timestamp);
		}

		//Update Investigate by
		alert.setInvestigateAt(timestamp.toInstant().toEpochMilli());
		alert.setInvestigateBy(by);
		
		// Updating/Storing Alert into near Cache
		updateActiveAlertsCache(alert);
		try {
			service.getAlertsDataStore().updateAlert(alert);
		} catch (SQLException e) {
			logger.error("Error updating alert {} during investigateAlert", alert.getUuid(), e);
			return false;
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("Alert:{}, ClassId:{}, investigated, status:{}, by:{} , timestamp:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					alert.getStatus(),
					by,
					timestamp);
		}
		
		
		
		return true;
	}

	/**
	 * Filter out closed alerts
	 * 
	 * @param alerts
	 * @return
	 */
	private List<Alert> getNonClosedAlert(Collection<Alert> alerts) {
		ArrayList<Alert> nonClosedAlerts = new ArrayList<Alert>();
		for (Alert a : alerts) {
			if (a.getStatus() != Alert.CLOSED) {
				nonClosedAlerts.add(a);
			}
		}
		return nonClosedAlerts;
	}

	/**
	 * Acknowldeges Active Alerts by Well and Class
	 * 
	 * @param well
	 * @param classId
	 * @param acknowledgeBy
	 * @param timestamp
	 * @return if Acknowledge of Alerts was executed successfully
	 */
	public boolean acknowledgeByWellClassId(String well, String classId, String acknowledgeBy,
			OffsetDateTime timestamp) {
		Collection<Alert> alerts = this.getActiveAlertsByWellClassId(well, classId);
		List<Alert> nonClosedAlerts = getNonClosedAlert(alerts);

		logger.debug("{} acknowledges {} non closed alerts from {} total alerts", acknowledgeBy, nonClosedAlerts.size(),
				alerts.size());

		acknowledgeMultiAlert(nonClosedAlerts, acknowledgeBy, timestamp, null, null);
		return true;
	}

	/**
	 * 
	 * @param where
	 * @param acknowledgeBy
	 * @param timestamp
	 * @return Alerts serialized in defined format (XML - JSON)
	 */
	public List<Alert> acknowledgeWhere(String where, String acknowledgeBy, OffsetDateTime timestamp) {
		if (!where.contains("where")) {
			where = "where " + where;
		}

		// Search and filter (may be better through sql query
		List<Alert> alerts = this.getAlertsWhere(where);
		List<Alert> nonClosedAlerts = getNonClosedAlert(alerts);

		logger.debug("{} acknowledges {} non closed alerts from {} total alerts", acknowledgeBy, nonClosedAlerts.size(),
				alerts.size());
		
		List<AlertRequestResult> result = acknowledgeMultiAlert(nonClosedAlerts, acknowledgeBy, timestamp, null, null);
		List<Alert> updatedAlerts = AlertRequestResult.getAlertsFromResults(result);
		return updatedAlerts;
	}

	
	/**
	 * Register snooze on the DB
	 * 
	 * @param alert
	 * @param snoozedBy
	 * @param snoozedAt
	 */
	@Override
	public boolean snoozeAlert(Alert alert, String snoozedBy, OffsetDateTime snoozedAt, OffsetDateTime unSnoozeAt) {
		if (logger.isDebugEnabled()) {
			logger.debug("Alert:{}, ClassId:{}, snoozing, status:{}, by:{}, snoozedAt:{}, unSnoozeAt:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					alert.getStatus(),
					snoozedBy,
					snoozedAt.toInstant(),
					unSnoozeAt.toInstant()
					);
		}

		AlertImpl alerti = (AlertImpl) alert;
		alerti.setSnoozed(true);
		alerti.setSnoozedAt(DateTimeHelper.toEpochMillis(snoozedAt));
		alerti.setSnoozedBy(snoozedBy);
		alerti.setUnSnoozeAt(DateTimeHelper.toEpochMillis(unSnoozeAt));

		try {
			service.getAlertsDataStore().updateAlert(alert);
		} catch (SQLException e) {
			logger.error("failed to update alert", e);
			return false;
		}

		updateActiveAlertsCache(alert);

		try {
			if (!snoozeRecordsDAO.snooze(alert.getClassId(), ((AlertImpl) alert).getWellId(), snoozedBy,
					snoozedAt.toInstant(), unSnoozeAt.toInstant())) {
				logger.error("Unable to Snooze Alert with ClassId {} and WellId {} since it was already Snoozed before",
						alert.getClassId(), ((AlertImpl) alert).getWellId());
				return false;
			}
		} catch (EngineException e) {
			logger.error("Exception while saving Snooze Record", e);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("Alert:{}, ClassId:{}, snoozed, status:{}, by:{}, snoozedAt:{}, unSnoozeAt:{} ", 
					alert.getUuid(),
					alert.getClassId(),
					alert.getStatus(),
					alert.getSnoozedBy(),
					alert.getSnoozedAt(),
					alert.getUnSnoozeAt()
					);
		}
		
		if (jobScheduler == null) {
			throw new IllegalStateException("jobScheduler can not be null when snoozing Alert " + alert);
		}
		
		scheduleUnsnoozeJob(alert.getClassId(), ((AlertImpl) alert).getWellId(), unSnoozeAt);

		acknowledgeAlert(alert, snoozedBy, snoozedAt);

		//Write journal entry
		SnoozeJournalImpl journal = AlertsFactory.getAlertJournal(alert, DefaultJournalTypes.SNOOZE,
				SnoozeJournalImpl.class);
		journal.setSnooze(snoozedBy, snoozedAt.toInstant(), unSnoozeAt.toInstant());
		invokeListenersOnJournalEntry(journal);
		
		//Execute actions
		for (AlertListener listener : listeners) {
			try {
				listener.onSnooze(alert);
			} catch (Exception ex) {
				logger.error("Failure processsing AlertListener onSnooze", ex);
			}
		}

		return true;
	}

	/**
	 * @param classId
	 * @param wellId
	 * @param snoozedBy
	 * @param snoozedAt
	 * @param unSnoozeAt
	 * @return the Snoozed Alert
	 * @throws EngineException
	 */
	public boolean snoozeClass(String classId, String wellId, String snoozedBy, OffsetDateTime snoozedAt,
			OffsetDateTime unSnoozeAt) throws EngineException {
		if (logger.isInfoEnabled()) {
			logger.info("{} is snoozing class {} from {}  to {}", snoozedBy, classId, snoozedAt.toInstant(),
					unSnoozeAt.toInstant());
		}

		if (this.alertClasses.contains(classId)) {
			boolean successSnooze = false;
			try {
				successSnooze = snoozeRecordsDAO.snooze(classId, wellId, snoozedBy, snoozedAt.toInstant(),
						unSnoozeAt.toInstant());
			} catch (EngineException e) {
				logger.error("Exception while saving Snooze Record", e);
			}

			if (!successSnooze) {
				StringBuilder sb = new StringBuilder();
				sb.append("Unable to Snooze Alert with ClassId ").append(classId);
				sb.append(" and WellId ").append(wellId);
				sb.append(" as requested by ").append(snoozedBy);
				sb.append(" since it was already Snoozed previously");
				String errorMsg = sb.toString();
				logger.error(errorMsg);
				// Should Throw error to make sure client understand
				throw new EngineException(errorMsg);
			}
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Unable to Snooze Alert with ClassId ").append(classId);
			sb.append(" and WellId ").append(wellId);
			sb.append(" as requested by ").append(snoozedBy);
			sb.append(" since the Alert Class is unknown");
			String errorMsg = sb.toString();
			logger.error(errorMsg);
			// Should Throw error to make sure client understand
			throw new EngineException(errorMsg);			
		}
		
		AlertImpl alert = (AlertImpl) getActiveAlertByWellClassId(wellId, classId);

		if (alert != null) {
			alert.setSnoozed(true);
			alert.setSnoozedAt(DateTimeHelper.toEpochMillis(snoozedAt));
			alert.setSnoozedBy(snoozedBy);
			alert.setUnSnoozeAt(DateTimeHelper.toEpochMillis(unSnoozeAt));
			
			try {
				service.getAlertsDataStore().updateAlert(alert);
			} catch (SQLException e) {
				throw new EngineException("failed to update alert", e);
			}
			
			updateActiveAlertsCache(alert);
			
			for (AlertListener listener : listeners) {
				try {
					listener.onSnooze(alert);
				} catch (Exception ex) {
					logger.error("Failure processsing AlertListener onSnooze", ex);
				}
			}
		}

		if (jobScheduler == null) {
			throw new IllegalStateException(
					"jobScheduler can not be null when snoozing Alert based on Class " + classId);
		}

		scheduleUnsnoozeJob(classId, wellId, unSnoozeAt);

		if (alert != null)
			acknowledgeAlert(alert, snoozedBy, snoozedAt);

		SnoozeJournalImpl journal = AlertsFactory.getAlertJournal(alert, DefaultJournalTypes.SNOOZE,
				SnoozeJournalImpl.class);

		JSONObject details = new JSONObject();
		details.put("alertClassId", classId);
		details.put("alertWellId", wellId);

		journal.setAlertClassId(classId);
		journal.setSnooze(snoozedBy, snoozedAt.toInstant(), unSnoozeAt.toInstant(), details);
		invokeListenersOnJournalEntry(journal);

		return true;
	}

	/**
	 * Register snooze on the DB
	 * 
	 * @param alertuuid
	 * 
	 * @param snoozedBy
	 * @param snoozedAt
	 */
	@Override
	public boolean snoozeAlert(String alertuuid, String snoozedBy, OffsetDateTime snoozedAt,
			OffsetDateTime unSnoozeAt) {
		Alert alert = getAlert(alertuuid);

		if (alert == null) {
			logger.warn("Trying to snooze Alert ID {}, by {}, on {} which is not in the system ", alertuuid, snoozedBy,
					snoozedAt);
			return false;
		}

		return snoozeAlert(alert, snoozedBy, snoozedAt, unSnoozeAt);
	}

	/**
	 * Unsnoozed an alert
	 * 
	 * @param alertClassId
	 * @param wellId
	 * @param timestamp
	 * @param principal
	 * @return if the alert was unsnoozed
	 * @throws EngineException
	 */
	public boolean unSnooze(String alertClassId, String wellId, OffsetDateTime timestamp, String principal)
			throws EngineException {
		if (logger.isInfoEnabled()) {
			logger.info("{} is unsnoozing class {} on {}", principal, alertClassId, timestamp.toInstant());
		}

		boolean successUnsnooze = false;
		//Try Unsnooze dao regardless alert class is available or not
		try {
			successUnsnooze = snoozeRecordsDAO.unSnooze(alertClassId, wellId, principal, timestamp.toInstant());
		} catch (EngineException e) {
			logger.error("Exception while saving Snooze Record", e);
		}
		
		if (this.alertClasses.contains(alertClassId)) {
			

			if (!successUnsnooze) {
				StringBuilder sb = new StringBuilder();
				sb.append("Unable to UnSnooze Alert with ClassId ").append(alertClassId);
				sb.append(" and WellId ").append(wellId);
				sb.append(" as requested by ").append(principal);
				sb.append(" since it was not Snoozed before");
				String errorMsg = sb.toString();

				// Must log Before throwing exception since it may be automatic
				// task
				logger.error(errorMsg);
				// Should Throw error to make sure client understand
				throw new EngineException(errorMsg);
			}
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("Unable to UnSnooze Alert with ClassId ").append(alertClassId);
			sb.append(" and WellId ").append(wellId);
			sb.append(" as requested by ").append(principal);
			sb.append(" since the Alert Class is unknown");
			
			if(successUnsnooze) {
				//If success unsnooze it may means that flow is already deleted so it can't be published anymore
				//however entry need to be removed from db
				sb.append(" however class is available in snooze record and will be deleted there. Flow may have been deleted from engine before it unsnooze.");
			}
			
			String errorMsg = sb.toString();

			// Must log Before throwing exception since it may be automatic task
			logger.error(errorMsg);
			// Should Throw error to make sure client understand
			throw new EngineException(errorMsg);
		}

		Alert alert = getActiveAlertByWellClassId(wellId, alertClassId);

		if (alert != null) {
			((AlertImpl) alert).setSnoozed(false);
			((AlertImpl) alert).setUnSnoozedAt(timestamp.toInstant().toEpochMilli());
			((AlertImpl) alert).setUnSnoozedBy(principal);

			// Invoke onCreate actions if not invoked before
			if (!alert.isOnCreateEventsExecuted()) {
				((AlertImpl) alert).setOnCreateEventsExecuted(true);
				try {
					service.getAlertsDataStore().updateAlert((AlertImpl) alert);
				} catch (SQLException e) {
					logger.error("Error unSnoozing Alert", e);
					return false;
				}

				/**
				 * Invoking On Create actions on this alert since these were not
				 * invoked when originally created
				 */
				for (AlertListener listener : listeners) {
					try {
						listener.onCreate(alert);
					} catch (Exception ex) {
						logger.error("Failure processsing AlertListener onCreate.", ex);
					}
				}
			} else {
				try {
					service.getAlertsDataStore().updateAlert((AlertImpl) alert);
				} catch (SQLException e) {
					logger.error("Error unSnoozing Alert", e);
					return false;
				}
			}
			
			updateActiveAlertsCache(alert);
		}

		if (jobScheduler == null) {
			throw new IllegalStateException("jobScheduler can not be null when unsnoozing Alert Class " + alertClassId);
		}

		try {
			// Program Timeout JobBuilder
			String id = getUnsnoozeJobIdString(alertClassId, wellId);

			jobScheduler.deleteJob(jobKey(id, "snoozeAlert"));
		} catch (SchedulerException e) {
			logger.error("Failure in deleting Job for unSnoozing", e);
		}

		//Write to journal
		SnoozeJournalImpl journal = AlertsFactory.getAlertJournal(alert, DefaultJournalTypes.SNOOZE,
				SnoozeJournalImpl.class);
		journal.setAlertClassId(alertClassId);

		JSONObject details = new JSONObject();
		details.put("alertClassId", alertClassId);
		details.put("alertWellId", wellId);
		journal.setUnSnooze(principal, timestamp.toInstant(), details);
		invokeListenersOnJournalEntry(journal);
		return true;
	}
	
	
	

	/**
	 * change alert's stauts
	 * 
	 * @param serializer
	 *            : type of serializer
	 * @param alertUUID
	 *            : alert uuid
	 * @param status
	 *            : new status to update
	 * @return Alert with the new alert status
	 */
	public Object changeAlertStatus(String serializer, String alertUUID, int status) {
		changeAlertStatus(alertUUID, status);
		return getSerializedAlert(alertUUID, serializer);

	}

	/**
	 * comment the alert
	 * 
	 * @param serializer
	 * @param alertUUID
	 * @param comment
	 * @param commentedBy
	 * @param timestamp
	 * @return the Alert commented
	 */
	public Object commentAlert(String serializer, String alertUUID, String comment, String commentedBy,
			OffsetDateTime timestamp) {
		commentAlert(alertUUID, comment, commentedBy, timestamp);
		return getSerializedAlert(alertUUID, serializer);
	}
	
	/**
	 * Investigate the alert and return whether it is successfull or not
	 * @param alertUUIDs 
	 * @param alertClassIDs 
	 * @param by
	 * @param timestamp
	 * @param allowChangeInvestigator 
	 * @return Alert list which successfully investigated
	 */
	public List<Alert> investigateAlert(List<String> alertUUIDs, List<String> alertClassIDs , String by, OffsetDateTime timestamp, boolean allowChangeInvestigator) {
		ArrayList<Alert> successList = new ArrayList<>();
		
		//Combining into hash map will prevent investigating same alert
		HashMap<String,AlertImpl> investigationTargets = new HashMap<>();
		

		//Get from direct id
		if (alertUUIDs != null) {
			for (String alertUUID : alertUUIDs) {
				Alert alert = getAlert(alertUUID);
				AlertImpl rtAlert = null;
				if (alert instanceof AlertImpl) {
					rtAlert = (AlertImpl)alert;
				}
				if (rtAlert == null) {
					logger.warn("Fail to investigate alert ID {} by {} on {} which is not in the system ", alertUUID,	by, timestamp);
				} else if (allowChangeInvestigator || !rtAlert.isInvestigating()) {
					investigationTargets.put(rtAlert.getUuid(),rtAlert);
				} else {
					logger.warn("Fail to investigate alert ID already investigated {} by {} on {}. Was investigated by {} on {} "
							, alertUUID
							, by
							, timestamp
							, rtAlert.getInvestigateBy()
							, Instant.ofEpochMilli(rtAlert.getInvestigateAt()));
					
				}
			}
		}
		
		// Get from class id
		if (alertClassIDs != null) {
			for (String classId : alertClassIDs) {
				try {
					List<Alert> unclosedAlerts = service.getAlertsDataStore().getUnclosedAlertsByClass(classId);
					for (Alert unclosedAlert : unclosedAlerts) {
						if (unclosedAlert instanceof AlertImpl) {
							AlertImpl rtAlert = (AlertImpl) unclosedAlert;
							if (allowChangeInvestigator || !rtAlert.isInvestigating()) {
								investigationTargets.put(rtAlert.getUuid(),rtAlert);
							}
						}
					}
				} catch (SQLException e) {
					logger.warn("Fail to investigate alert Class ID {} by {} on {}. DB Problem", classId,	by, timestamp, e);
				}
			}
		}
		
		//Multi Alert
		for (AlertImpl rtAlert : investigationTargets.values()) {
			boolean success = investigateSingleAlertNoInvoke(rtAlert, by, timestamp);
			if (success) {
				successList.add(rtAlert);
			} else {
				logger.warn("Fail to investigate alert ID {} class {} by {} on {} ", rtAlert.getUuid(), rtAlert.getClassId(),	by, timestamp);
			}
		}
		
		//Write to journal
		ArrayListValuedHashMap<String, Alert> mappedSuccessList = AlertUtils.getMappedAlertsFromAlerts(successList);
		for(String key : mappedSuccessList.keySet()) {
			List<Alert> classSuccessList = mappedSuccessList.get(key);
			InvestigateMultiJournalImpl journal = new InvestigateMultiJournalImpl();
			journal.setInvestigate(classSuccessList, by, timestamp.toInstant());
			invokeListenersOnJournalEntry(journal);
		}
		
		//Listeners
		for (Alert alertImpl : successList) {
			invokeListenerOnInvestigate(alertImpl);
		}
		
		return successList;
	}

	/**
	 * @param serializer
	 * @param classId
	 * @param wellId
	 * @return the serializae snoozed class
	 */
	public Object getSerializedSnoozedClass(String serializer, String classId, String wellId) {

		try {
			SnoozeRecord record = snoozeRecordsDAO.getSnoozeRecord(classId, wellId);
			AlertSerializer as = AlertSerializerFactory.getAlertSerializer(serializer);
			return as.serializeSnoozeTable(record);
		} catch (EngineException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * snoozeAlert and return the alert in the specified format(XML,JSON)
	 * 
	 * @param serializer
	 * @param alertUUID
	 * 
	 * @param snoozedBy
	 * @param snoozedAt
	 * @param unSnoozeAt
	 * @return the alert serialized
	 */
	public Object snoozeAlert(String serializer, String alertUUID, String snoozedBy, OffsetDateTime snoozedAt,
			OffsetDateTime unSnoozeAt) {
		AlertImpl a = (AlertImpl) getAlert(alertUUID);
		snoozeAlert(alertUUID, snoozedBy, snoozedAt, unSnoozeAt);
		return getSerializedSnoozedClass(serializer, a.getClassId(), a.getWellId());
	}

	/**
	 * @param serializer
	 * @param classid
	 * @param wellid
	 * @param snoozedBy
	 * @param snoozedAt
	 * @param unSnoozeAt
	 * @return the active serialized alert that was snoozed
	 * @throws EngineException
	 */
	public Object snoozeClass(String serializer, String classid, String wellid, String snoozedBy,
			OffsetDateTime snoozedAt, OffsetDateTime unSnoozeAt) throws EngineException {
		snoozeClass(classid, wellid, snoozedBy, snoozedAt, unSnoozeAt);

		return getSerializedSnoozedClass(serializer, classid, wellid);
	}

	/**
	 * unsnooze and return the alert in he specified format(JSON, XML)
	 * 
	 * @param serializer
	 * @param alertClassId
	 * @param wellId
	 * @param timestamp
	 * @param principal
	 * @return serialized Active alert if any, null otherwise
	 * @throws EngineException
	 */
	public Alert unSnoozeAlert(String serializer, String alertClassId, String wellId, OffsetDateTime timestamp,
			String principal) throws EngineException {
		if (unSnooze(alertClassId, wellId, timestamp, principal)) {
			return getActiveAlertByWellClassId(wellId, alertClassId);
		}
		return null;
	}

	/**
	 * @param serializer
	 * @param well
	 * @return the snoozed classes for a given well
	 */
	public Object getSnoozedClassesByWell(String serializer, String well) {

		try {
			AlertSerializer as = AlertSerializerFactory.getAlertSerializer(serializer);

			try (Connection conn = getAlertsDataSource().getConnection();
					PreparedStatement ps = conn.prepareStatement("select * from snoozedalerts where well = ?")) {
				ps.setString(1, well);
				ResultSet rs = ps.executeQuery();
				return as.serializeSnoozeTable(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (EngineException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * @param serializer
	 * @return all the snoozed classes
	 */
	public Object getSnoozedClasses(String serializer) {
		try {
			AlertSerializer as = AlertSerializerFactory.getAlertSerializer(serializer);

			try (Connection conn = getAlertsDataSource().getConnection();
					PreparedStatement ps = conn.prepareStatement("select * from snoozedalerts")) {
				ResultSet rs = ps.executeQuery();
				return as.serializeSnoozeTable(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (EngineException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * @param serializer
	 * @param well
	 * @param classId
	 * @return the snoozed classes for a given well and class
	 */
	public Object getSnoozedClassesByWellClass(String serializer, String well, String classId) {
		try {
			AlertSerializer as = AlertSerializerFactory.getAlertSerializer(serializer);

			try (Connection conn = getAlertsDataSource().getConnection();
					PreparedStatement ps = conn
							.prepareStatement("select * from snoozedalerts where well = ? and alertclassid = ?")) {
				ps.setString(1, well);
				ps.setString(2, classId);
				ResultSet rs = ps.executeQuery();
				return as.serializeSnoozeTable(rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (EngineException e) {
			logger.error("Error getting alerts", e);
		}
		return null;
	}

	/**
	 * @param serializer
	 * @param alertuuid
	 * @param timestamp
	 * @param principal
	 * @return the Un Snoozed alert
	 * @throws EngineException
	 */
	public Alert unSnoozeAlert(String serializer, String alertuuid, OffsetDateTime timestamp, String principal)
			throws EngineException {
		AlertImpl a = (AlertImpl) getAlert(alertuuid);

		if (unSnooze(a.getClassId(), a.getWellId(), timestamp, principal)) {
			return getActiveAlertByWellClassId(a.getWellId(), a.getClassId());
		}
		return null;
	}

	/**
	 * @param listener
	 *            - to be added (non-Javadoc)
	 * @see com.smartnow.alertstatus.AlertsDAO#registerListener(com.smartnow.alertstatus.AlertListener)
	 */
	@Override
	public void registerListener(AlertListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * @return the service
	 */
	public AlertsService getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(AlertsService service) {
		this.service = service;
		this.snoozeRecordsDAO = new SnoozeRecordsDAO(service.getAlertsDataStore());
	}

	/**
	 * @return the scheduler
	 */
	public synchronized SchedulerService getSchedulerService() {
		return schedulerService;
	}

	/**
	 * @param schedulerSvc
	 *            the scheduler to set
	 */
	public synchronized void setSchedulerService(SchedulerService schedulerSvc) {
		this.schedulerService = schedulerSvc;
		if (schedulerService != null) {
			jobScheduler = schedulerService.getScheduler();
			if (jobScheduler == null) {
				logger.warn("job Scheduler in Scheduler service {} is NULL", schedulerSvc.getName());
			}
		}
	}

	/**
	 * Registers an alert as Known to the DAO
	 * 
	 * @param alertClassId
	 */
	public void registerAlertClass(String alertClassId) {
		this.alertClasses.add(alertClassId);
		logger.info("Alert Class {} is registered ", alertClassId);
	}
	
	/**
	 * UnRegisters an alert as Known to the DAO
	 * 
	 * @param alertClassId
	 */
	public void unRegisterAlertClass(String alertClassId) {
		logger.info("Unregistering Alert Class {}", alertClassId);
		
		boolean alertClassExists = this.alertClasses.remove(alertClassId);
		int inactivatedCount = 0;
		if (alertClassExists) {
			//Inactivate all active alert, as when unregistered now active alert become orphan and never being inactivated otherwise
			List<Alert> activeAlerts = getActiveAlertsByClassId(alertClassId);
			
			for(Alert alert : activeAlerts) {
				changeAlertStatus(alert, Alert.INACTIVE);
				inactivatedCount++;
			}
		}
		
		logger.info("Alert Class {} is unregistered, was exists = {}, inactivate = {} ", alertClassId, alertClassExists, inactivatedCount);
		

		List<String> tobeClosed = getAlertUUidsByClassIdAndStatus(alertClassId, Alert.CLOSED);
		removeAlerts(tobeClosed);
		
	}
	
	/**
	 * Registers auto dismiss configuration
	 * 
	 * @param alertClassId The Alert's class id
	 * @param config The new configuration for alert class
	 */
	public void registerAutoDismiss(String alertClassId, AutoAlertDismissal config) {
		if (StringUtils.isBlank(alertClassId)) {
			logger.warn("Should not try to register auto dismiss for blank alert class");
			return;
		}
		
		if (config != null) {
			this.autoDismissByClassId.put(alertClassId, config);
			logger.info("AutoAlertDismissal for Class {} is registered ", alertClassId);
		} else {
			this.autoDismissByClassId.remove(alertClassId);
			logger.info("AutoAlertDismissal for Class {} is removed ", alertClassId);
		}
	}
	
	
	/**
	 * UnRegisters auto dismiss configuration
	 * @param alertClassId The Alert's class id
	 */
	public void unRegisterAutoDismiss(String alertClassId) {
		registerAutoDismiss(alertClassId, null);
	}
	
	private void scheduleUnsnoozeJob(String classId, String wellId, OffsetDateTime unSnoozeAt) {
		logger.info("Scheduling unsnooze job for class {} well {} to unsnooze at {} ", classId, wellId, unSnoozeAt);
		try {
			// Program Timeout JobBuilder
			String id = getUnsnoozeJobIdString(classId, wellId);
			JobDetail job = newJob(UnSnoozeCronJob.class).withIdentity(id,UNSNOOZE_JOB_GROUP).build();

			job.getJobDataMap().put(UnSnoozeCronJob.UNSNOOZE_PRINCIPAL_JOB_DATAMAP_KEY, snoozeProcessPrincipal);
			job.getJobDataMap().put(UnSnoozeCronJob.UNSNOOZE_CLASSID_JOB_DATAMAP_KEY, classId);
			job.getJobDataMap().put(UnSnoozeCronJob.UNSNOOZE_WELLID_JOB_DATAMAP_KEY, wellId);

			// Trigger the job to run now
			String triggerId = getUnsnoozeTriggerString(id);
			Trigger trigger = newTrigger().withIdentity(triggerId, UNSNOOZE_JOB_GROUP)
					.startAt(new Date(unSnoozeAt.toInstant().toEpochMilli())).build();
			jobScheduler.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			logger.error("Scheduling error for the unSnoozing automatic action for class {} well {} to unsnooze at {}", classId, wellId, unSnoozeAt, e);
		}
	}
	
	
	/**
	 * Clear Alerts and Journal older than specified time
	 * @param maximumTimeToBeCleared Time expiration
	 */
	public void clearClosedAlertsAndJournalOlderThan(Instant maximumTimeToBeCleared) {
		logger.info("Clearing alerts older than {}", maximumTimeToBeCleared);

		try{
			List<String> uuids = service.getAlertsDataStore().getStaleAlertIds_TEMP(maximumTimeToBeCleared);
			
			logger.info("Removing {} alerts", uuids.size());
			if (logger.isDebugEnabled())
				logger.debug("Alerts to be removed: {}", StringUtils.join(uuids, ", "));
			
			removeAlerts(uuids);
		} catch (SQLException e){
			logger.error("Exception on finding Alert to be cleaned Up", e);
		}
	}
	
	/**
	 * Remove Alert and its Journal
	 * @param uuids
	 * @return the quantity of alerts removed
	 */
	public int removeAlerts(List<String> uuids) {
		if (uuids == null || uuids.isEmpty())
			return 0;
		
		int count = 0;
		try {
			for (String uuidToRemove : uuids) {
				logger.info("Removing alert {}", uuidToRemove);
				service.getAlertsDataStore().deleteAlert(uuidToRemove);
				count++;
			}
			logger.info("Removed {} alerts", count);
		} catch (SQLException e){
			logger.error("Exception on Clean Up Processes", e);
		}
		return count;
	}
	
	/**
	 * Add or remove an alert from the active alert cache.
	 * @param alert
	 */
	private void updateActiveAlertsCache(Alert alert) {
		AlertImpl alerti = (AlertImpl) alert;
		String alertid = alerti.getUuid();
		String alertcls = alerti.getClassId();
		String wellid = alerti.getWellId();
		if (alerti.getStatus() == Alert.ACTIVE) {
			activeAlerts.put(alertid, alerti);
			HashMap<String, Alert> hm = activeAlertsByClassWell.get(alertcls);
			if (hm == null) {
				hm = new HashMap<>();
				activeAlertsByClassWell.put(alertcls, hm);
			}
			hm.put(wellid, alerti);
		} else {
			activeAlerts.remove(alertid);
			HashMap<String, Alert> hm = activeAlertsByClassWell.get(alertcls);
			if (hm != null) {
				hm.remove(wellid);
				if (hm.isEmpty())
					activeAlertsByClassWell.remove(alertcls);
			}
		}
	}
	
	/**
	 * Create scheduler job key
	 * @param classId
	 * @param wellId
	 * @return
	 */
	private static String getUnsnoozeJobIdString(String classId,String wellId) {
		String id = classId + wellId;
		return id;
	}
	
	/**
	 * Create Unsnooze Trigger string for specific job id
	 * @param unsnoozeJobIdString
	 * @return
	 */
	private static String getUnsnoozeTriggerString(String unsnoozeJobIdString) {
		return "trigger_" + unsnoozeJobIdString;
	}
	
	
	
	/**
	 * Begin sending alert for delayed alert
	 * @param alertsToProcess
	 */
	public void executeAlertCreateActions(List<Alert> alertsToProcess) {
		if (alertsToProcess == null) return;
		if (alertsToProcess.isEmpty()) return;
		
		//Invoking On Create actions on this alert since these were not invoked when originally created
		ArrayList<AlertImpl> updatedAlerts = new ArrayList<>();
		for (AlertListener listener : listeners) {
			for(Alert alertObject :alertsToProcess ) {
				//Invoke onCreate actions if not invoked before
				if (!alertObject.isOnCreateEventsExecuted()
					&& !alertObject.isSnoozed()
					&& !alertObject.isAcknowledge()
					) {
					
					//Process events
					AlertImpl alertToBeSent = ((AlertImpl) alertObject);
					try {
						alertToBeSent.setOnCreateEventsExecuted(true);
						listener.onCreate(alertToBeSent);
						updatedAlerts.add(alertToBeSent);
					} catch (Exception ex) {
						alertToBeSent.setOnCreateEventsExecuted(false);
						logger.error("Failure beginCreateEvents on alert {}.", alertToBeSent.getUuid(), ex);
					}
				}
			}
		}
		
		//Update DB for sent alert, should be moved to DataStore level
		try {
			for (AlertImpl alertToUpdate : updatedAlerts) {
				service.getAlertsDataStore().updateAlert(alertToUpdate);
			}
		} catch (SQLException e) {
			//Skip for next time
			logger.error("Error updating create event status in database for {} alert(s) ", updatedAlerts.size(), e);
		}
	}
	
	/**
	 * Execute alert action
	 * @param oldAlert
	 * @param newAlert
	 */
	public void executeAlertUpdateActions(Alert oldAlert, Alert newAlert) {
		for (AlertListener listener : listeners) {
			listener.onUpdate(oldAlert, newAlert);
		}
	}
	
	/**
	 * Check delayed alert create action
	 */
	public void checkDelayedAlertCreateAction(){
		Instant currentTimeInstant = Instant.now();
		ArrayList<Alert> alertToBeginNotification = new ArrayList<>();
		List<Alert> alerts = getActiveAlerts();
		
		//Investigate active alert
		for (Alert alert : alerts) {
			if (canExecuteAlertCreateActions(alert, currentTimeInstant)) {
				alertToBeginNotification.add(alert);
			}
		}
		
		//Actual Create event
		if (!alertToBeginNotification.isEmpty()) {
			logger.info("Begin CreateEvents for {} alerts", alertToBeginNotification.size());
			executeAlertCreateActions(alertToBeginNotification);
		}
	}
	
	/**
	 * Check whether an alert need to invoke create event
	 * @param alert
	 * @param currentTimeInstant
	 * @return
	 */
	private boolean canExecuteAlertCreateActions(Alert alert, Instant currentTimeInstant) {
		if (alert.isOnCreateEventsExecuted()) {	return false;}
		if (alert.isSnoozed()) { return false;}
		if (alert.isAcknowledge()) { return false;} //Already acknowledged, nobody need to check
		
		//Check dismissal status
		AutoAlertDismissal dismissConfig = autoDismissByClassId.get(alert.getClassId());
		if ( (alert instanceof AlertImpl)) {
			AlertImpl plinkAlert = (AlertImpl)alert;
			if ((dismissConfig != null) && dismissConfig.isShouldBeDismissed(currentTimeInstant, plinkAlert,logger)) {
				return false;
			}
		}
		
		//Check Delay status
		DelayAlertAction delayConfig = alertActionDelayByClassId.get(alert.getClassId());
		if ((delayConfig != null) && delayConfig.isCreateEventShouldBeDelayed(currentTimeInstant, alert.getCreated())) {
			return false;
		} 

		return true;
	}
	
	/**
	 * Registers Alert Delay configuration
	 * 
	 * @param alertClassId The Alert's class id
	 * @param config The new configuration for alert class
	 */
	public void registerAlertActionDelay(String alertClassId,DelayAlertAction config) {
		if (StringUtils.isBlank(alertClassId)) {
			logger.warn("Should not try to register alert delay for blank alert class");
			return;
		}
		
		if (config != null) {
			this.alertActionDelayByClassId.put(alertClassId, config);
			logger.info("AlertActionDelay for Class {} is registered ", alertClassId);
		} else {
			this.alertActionDelayByClassId.remove(alertClassId);
			logger.info("AlertActionDelay for Class {} is removed ", alertClassId);
		}
	}
	
	
	/**
	 * UnRegisters auto dismiss configuration
	 * @param alertClassId The Alert's class id
	 */
	public void unRegisterAlertActionDelay(String alertClassId) {
		registerAlertActionDelay(alertClassId, null);
	}
	
	private void invokeListenersOnJournalEntry(AlertJournal journal) {
		// Invoke listener
		for (AlertListener listener : listeners) {
			try{
				listener.onJournalEntry(journal);
			} catch(Exception ex) {
				logger.error("Failure processsing AlertListener onJournalEntry.", ex);
			}
		}
	}
	
	private void invokeListenerOnAcknowledge(AlertRequestResult requestResult) {
		//Execute Action
		Alert alert = requestResult.getAffectedAlert();
		for (AlertListener listener : listeners) {
			try {
				listener.onAcknowledge(alert);
				if (alert.getStatus() != requestResult.getOriginalStatus()) {
					listener.onStatusChange(alert, requestResult.getOriginalStatus());
				}
			} catch (Exception ex) {
				logger.error("Failure processsing AlertListener onAcknowledge.", ex);
			}
		}
	}

	private void invokeListenerOnInvestigate(Alert alertBase) {
		//If alert already investigating just republish existing
		if (alertBase instanceof AlertImpl) {
			AlertImpl alert = (AlertImpl)alertBase;
			for (AlertListener listener : listeners) {
				try {
					if (listener instanceof  RuleFlowAlertListener) {
						RuleFlowAlertListener rflistener = (RuleFlowAlertListener)listener;
						rflistener.onInvestigate(alert);
					}
				} catch (Exception ex) {
					logger.error("Failure processsing AlertListener onInvestigate.", ex);
				}
			}
		}
		
		
	}
	
}