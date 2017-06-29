package com.petrolink.mbe.alertstatus.store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.h2.tools.Server;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.alertstatus.impl.AlertImpl;
import com.petrolink.mbe.alertstatus.impl.SnoozeRecord;
import com.petrolink.mbe.util.ResourceHelper;
import com.petrolink.mbe.util.SQLHelper;
import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.AlertsFactory;

/**
 * A data store for alerts backed by an H2 database.
 */
public class AlertH2DataStore implements AlertDataStore {
	// This stores the current database version. Versions numbers should be integers. The version number
	// in the database will be set to CURRENT_VERSION when it is opened.
	//
	// Versions:
	//
	// 2 - Use new versioning system. Equivalent to old version 1.992
	// 3 -
	// 4 - Add property tables
	// 5 - Increase description column size to 8192
	// 6 -
	// 7 - Remove obsolete snooze history and alert journal tables
	// 8 - Increase Detail column size to 8192
	private static final int CURRENT_VERSION = 8;
	
	private static Logger logger = LoggerFactory.getLogger(AlertH2DataStore.class);
	private static final Object dbLock = new Object();
	private BasicDataSource alertsDataSource;
	
	private Server server;
	
	/**
	 * Initialize Datastore
	 * @param config
	 */
	public void init(H2Config config) {
		if (config == null) {
			throw new IllegalArgumentException("H2 Configuration Must not be null");
		}
		synchronized (dbLock) {
			// Create Database structures (Tables and Indexes)
			if (alertsDataSource == null) {
				startH2Service(config);
				
				BasicDataSource ds = null;
				ds = new BasicDataSource();
				ds.setValidationQuery("SELECT 1");
				ds.setDriverClassName("org.h2.Driver");
				// QUERY_CACHE_SIZE is 8 by default which is too small to be really useful
				ds.setUrl(config.getConnectionURL() + ";COMPRESS=TRUE;QUERY_CACHE_SIZE=100;CACHE_SIZE="+config.getCacheSize());
				ds.setUsername("sa");
				ds.setPassword("");
				ds.setDefaultAutoCommit(true);
				ds.setDefaultReadOnly(false);
				ds.setInitialSize(2);
				ds.setMaxWaitMillis(10000);
				ds.setMinIdle(3);
				ds.setMaxIdle(8);
				ds.setPoolPreparedStatements(true);
				
				this.alertsDataSource = ds;
			}
		}
	}
	
	@Override
	public BasicDataSource getBasicDataSource() {
		return alertsDataSource;
	}

	@Override
	public Alert getAlert(String alertId) throws SQLException  {
		final String query = "SELECT * FROM alerts LEFT JOIN snoozedalerts ON alerts.classid = snoozedalerts.alertClassId WHERE UUID = ?";
		
		try (Connection conn = alertsDataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, alertId.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return getAlert(rs);
		}
		return null;
	}
	
	@Override
	public Alert getAlertByStatusClassWell(int status, String classId, String wellId) throws SQLException {
		final String query = "SELECT * FROM alerts LEFT JOIN snoozedAlerts ON alerts.classId = snoozedAlerts.alertClassId " +
	                         "WHERE status = ? AND classId = ? AND wellId = ?";
		
		try (Connection conn = alertsDataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
				ps.setInt(1, status);
				ps.setString(2, classId);
				ps.setString(3, wellId);
				ResultSet rs = ps.executeQuery();
				if (rs.next())
					return getAlert(rs);
		}
		return null;
	}
	
	@Override
	public List<Alert> getAlertsByStatus(int status) throws SQLException {
		final String query = "SELECT * FROM alerts LEFT JOIN snoozedAlerts ON alerts.classId = snoozedAlerts.alertClassId WHERE status = ?";
		
		ArrayList<Alert> results = new ArrayList<>();
		try (Connection conn = alertsDataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, status);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				results.add(getAlert(rs));
		}
		
		return results;
	}

	@Override
	public List<Alert> getUnclosedAlerts(List<String> wells) throws SQLException  {
		String query = "SELECT * FROM alerts LEFT JOIN snoozedalerts ON alerts.classid = snoozedalerts.alertClassId WHERE status IN (0, 1) AND {wf}";
		query = formatWellFilter(query, wells);
		
		ArrayList<Alert> results = new ArrayList<>();
		try (Connection conn = alertsDataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement(query)) {
			setWellFilter(ps, 1, wells);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(getAlert(rs));
			}
		}
		return results;
	}

	@Override
	public List<Alert> getSnoozedAlerts(List<String> wells) throws SQLException {
		String query = "SELECT * FROM alerts INNER JOIN snoozedalerts ON alerts.classid = snoozedalerts.alertClassId WHERE {wf}";
		query = formatWellFilter(query, wells);
		
		ArrayList<Alert> results = new ArrayList<>();
		try (Connection conn = alertsDataSource.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {
			setWellFilter(ps, 1, wells);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(getAlert(rs));
			}
		}
		return results;
	}

	@Override
	public int getUnclosedAlertCountByClass(String classId) throws SQLException {
		final String query = "SELECT COUNT(*) FROM alerts WHERE status IN (0, 1) AND classid = ?";
		
		try (Connection conn = alertsDataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, classId.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		}
		
		return 0;
	}
	
	@Override
	public List<Alert> getUnclosedAlertsByClass(String classId) throws SQLException {
		final String query = "SELECT * FROM alerts LEFT JOIN snoozedAlerts ON alerts.classId = snoozedAlerts.alertClassId WHERE status IN (0, 1) AND classid = ?";
		
		ArrayList<Alert> results = new ArrayList<>();
		try (Connection conn = alertsDataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, classId.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				results.add(getAlert(rs));
		}
		
		return results;
	}
	
	@Override
	public List<Alert> getAlertsInCreatedRange(Instant inclusiveStart, Instant inclusiveEnd, List<String> wellFilter, int limit) throws SQLException {
		String query = "SELECT * FROM alerts LEFT JOIN snoozedalerts ON alerts.classid = snoozedalerts.alertClassId " +
	                   "WHERE created BETWEEN ? AND ? AND {wf} LIMIT ?";
		query = formatWellFilter(query, wellFilter);
		
		ArrayList<Alert> results = new ArrayList<>();
		try (Connection conn = alertsDataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setTimestamp(1, Timestamp.from(inclusiveStart));
			ps.setTimestamp(2, Timestamp.from(inclusiveEnd));
			int wfc = setWellFilter(ps, 3, wellFilter);
			if (limit > 0)
				ps.setInt(wfc + 3, limit);
			else
				ps.setNull(wfc + 3, Types.INTEGER);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(getAlert(rs));
			}
		}
		return results;
	}

	@Override
	public List<Alert> getUnclosedAlertsInCreatedRange(Instant inclusiveStart, Instant inclusiveEnd, List<String> wellFilter, int limit) throws SQLException {
		String query = "SELECT * FROM alerts LEFT JOIN snoozedalerts ON alerts.classid = snoozedalerts.alertClassId " +
	                   "WHERE status IN (0, 1) AND created BETWEEN ? AND ? AND {wf} LIMIT ?";
		query = formatWellFilter(query, wellFilter);
		
		ArrayList<Alert> results = new ArrayList<>();
		try (Connection conn = alertsDataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setTimestamp(1, Timestamp.from(inclusiveStart));
			ps.setTimestamp(2, Timestamp.from(inclusiveEnd));
			int wfc = setWellFilter(ps, 3, wellFilter);
			if (limit > 0)
				ps.setInt(wfc + 3, limit);
			else
				ps.setNull(wfc + 3, Types.INTEGER);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(getAlert(rs));
			}
		}
		return results;
	}
	
	private static String formatWellFilter(String queryFormat, List<String> wellFilter) {
		int count = wellFilter == null ? 0 : wellFilter.size();
		if (count == 0)
			return queryFormat.replace("{wf}", "TRUE");
		StringBuilder sb = new StringBuilder("wellID IN (");
		for (int i = 0; i < count; i++) {
			sb.append("?,");
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return queryFormat.replace("{wf}", sb.toString());
	}
	
	private static int setWellFilter(PreparedStatement ps, int first, List<String> wellFilter) throws SQLException {
		if (wellFilter == null)
			return 0;
		int i = first;
		for (String u : wellFilter)
			ps.setString(i++, u);
		return i - first;
	}

	@Override
	public boolean createAlert(Alert alertBase) throws SQLException {
		final String query = "INSERT INTO alerts" 
			+ "(uuid, status, lastStatusChange, created, lastOccurrence, "
			+ "acknowledgeBy, acknowledgeAt, comment, commentBy, commentedAt, commentedCount, tally, name, classId, description, "
			+ "domain, classification, severity, priority, detailsContentType, details, metadata, createdIndex, "
			+ "lastIndex, wellId, holeDepth, finalHoleDepth, bitDepth, finalBitDepth, rigState, finalRigState, notificationsSent, snoozed, "
			+ "unSnoozedAt, unSnoozedBy, lastSnoozedBy, lastSnoozedAt, "
			+ "investigateBy,  investigateAt, "
			+ "parentClassId,  parentUuid"		
			+ ") "
			+ "values"
			+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		

		AlertImpl alert = (AlertImpl) alertBase;
		try (Connection conn = alertsDataSource.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, alert.getUuid());
			ps.setInt(2, alert.getStatus());
			SQLHelper.setTimestampOrNull(ps, 3, alert.getLastStatusChange());
			SQLHelper.setTimestampOrNull(ps, 4, alert.getCreated());
			SQLHelper.setTimestampOrNull(ps, 5, alert.getLastOccurrence());
			ps.setString(6, alert.getAcknowledgeBy());
			SQLHelper.setTimestampOrNull(ps, 7, alert.getAcknowledgeAt());
			ps.setString(8, alert.getComment());
			ps.setString(9, alert.getCommentedBy());
			SQLHelper.setTimestampOrNull(ps, 10, alert.getCommentedAt());
			ps.setInt(11, alert.getCommentedCount());
			ps.setInt(12, alert.getTally());
			ps.setString(13, alert.getName());
			ps.setString(14, alert.getClassId());
			ps.setString(15, alert.getDescription());
			ps.setString(16, alert.getDomain());
			ps.setString(17, alert.getClassification());
			ps.setInt(18, alert.getSeverity());
			ps.setInt(19, alert.getPriority());
			ps.setString(20, alert.getDetailsContentType());

			if (alert.getDetails() != null) {
				switch (alert.getDetailsContentType()) {
				case Alert.DETAILS_JSON:
					JSONObject object = (JSONObject) alert.getDetails();
					ps.setString(21, object.toString());
					break;
				case Alert.DETAILS_STRING:
				case Alert.DETAILS_LIST:
					ps.setString(21, (String) alert.getDetails());
					break;
				}
			} else {
				ps.setNull(21, java.sql.Types.VARCHAR);
			}

			if (alert.getMetadata() != null) {
				JSONObject metadata = (JSONObject) alert.getMetadata();
				ps.setString(22, metadata.toString());
			} else {
				ps.setString(22, "");
			}
			ps.setString(23, ((AlertImpl) alert).getCreatedIndex());
			ps.setString(24, ((AlertImpl) alert).getCreatedIndex());

			ps.setString(25, ((AlertImpl) alert).getWellId());

			SQLHelper.setDoubleOrNull(ps, 26, alert.getHoleDepth());
			SQLHelper.setDoubleOrNull(ps, 27, alert.getFinalHoleDepth());
			SQLHelper.setDoubleOrNull(ps, 28, alert.getBitDepth());
			SQLHelper.setDoubleOrNull(ps, 29, alert.getFinalBitDepth());
			SQLHelper.setIntegerOrNull(ps, 30, alert.getRigState());
			SQLHelper.setIntegerOrNull(ps, 31, alert.getFinalRigState());

			ps.setBoolean(32, alert.isOnCreateEventsExecuted());
			ps.setBoolean(33, alert.isSnoozed());

			SQLHelper.setTimestampOrNull(ps, 34, alert.getUnSnoozedAt());
			ps.setString(35, alert.getUnSnoozedBy());
			ps.setString(36, alert.getLastSnoozedBy());
			SQLHelper.setTimestampOrNull(ps, 37, alert.getLastSnoozedAt());

			ps.setString(38, alert.getInvestigateBy());
			SQLHelper.setTimestampOrNull(ps, 39, alert.getInvestigateAt());
			ps.setString(40, alert.getParentClassId());
			ps.setString(41, alert.getParentUuid());
			boolean success = ps.executeUpdate() != 0;

			logger.debug("Alert created for WellId {} and ClassId {} , snoozed is {} ", alert.getWellId(), alert.getClassId(), alert.isSnoozed());
			return success;
		}
	}

	@Override
	public boolean updateAlert(Alert alertBase) throws SQLException {
		final String query = "UPDATE alerts SET status = ?, lastStatusChange = ?, created = ?, "
				+ "lastOccurrence = ?, acknowledgeBy = ?, acknowledgeAt = ?, comment = ?, commentBy = ?, commentedAt = ?, "
				+ "commentedCount = ?, tally = ?, name = ?,  classId = ?, description = ?, domain = ?, classification = ?, "
				+ "severity = ?, priority = ?, detailsContentType = ? , details = ?, metadata = ?, createdIndex = ?, lastIndex = ?, "
				+ "wellId = ?, holeDepth = ?, finalHoleDepth = ?, bitDepth = ?, finalBitDepth = ?, rigState = ?, finalRigState = ?, "
				+ "notificationsSent = ?, snoozed = ?, unSnoozedAt = ?, unSnoozedBy = ?, lastSnoozedBy = ?, lastSnoozedAt = ?, "
				+ "investigateBy = ?,  investigateAt = ?, "
				+ "parentClassId = ?,  parentUuid = ? "
				+ " WHERE UUID = ? ";
		
		AlertImpl alert = (AlertImpl) alertBase;
		try (Connection conn = alertsDataSource.getConnection();
				PreparedStatement ps = conn.prepareStatement(query)) {

			ps.setInt(1, alert.getStatus());
			SQLHelper.setTimestampOrNull(ps, 2, alert.getLastStatusChange());
			SQLHelper.setTimestampOrNull(ps, 3, alert.getCreated());
			SQLHelper.setTimestampOrNull(ps, 4, alert.getLastOccurrence());
			ps.setString(5, alert.getAcknowledgeBy());
			SQLHelper.setTimestampOrNull(ps, 6, alert.getAcknowledgeAt());
			ps.setString(7, alert.getComment());
			ps.setString(8, alert.getCommentedBy());
			SQLHelper.setTimestampOrNull(ps, 9, alert.getCommentedAt());
			ps.setInt(10, alert.getCommentedCount());
			ps.setInt(11, alert.getTally());
			ps.setString(12, alert.getName());
			ps.setString(13, alert.getClassId());
			ps.setString(14, alert.getDescription());
			ps.setString(15, alert.getDomain());
			ps.setString(16, alert.getClassification());
			ps.setInt(17, alert.getSeverity());
			ps.setInt(18, alert.getPriority());
			ps.setString(19, alert.getDetailsContentType());

			if (alert.getDetails() != null) {
				switch (alert.getDetailsContentType()) {
				case Alert.DETAILS_JSON:
					JSONObject object = (JSONObject) alert.getDetails();
					ps.setString(20, object.toString());
					break;
				case Alert.DETAILS_STRING:
				case Alert.DETAILS_LIST:
					ps.setString(20, (String) alert.getDetails());
					break;
				}
			} else {
				ps.setNull(20, java.sql.Types.VARCHAR);
			}

			JSONObject metadata = (JSONObject) alert.getMetadata();
			String metadataString = metadata != null ? metadata.toString() : StringUtils.EMPTY;
			ps.setString(21, metadataString);

			ps.setString(22, alert.getCreatedIndex());
			ps.setString(23, alert.getLastIndex());

			ps.setString(24, alert.getWellId());

			SQLHelper.setDoubleOrNull(ps, 25, alert.getHoleDepth());
			SQLHelper.setDoubleOrNull(ps, 26, alert.getFinalHoleDepth());
			SQLHelper.setDoubleOrNull(ps, 27, alert.getBitDepth());
			SQLHelper.setDoubleOrNull(ps, 28, alert.getFinalBitDepth());
			SQLHelper.setIntegerOrNull(ps, 29, alert.getRigState());
			SQLHelper.setIntegerOrNull(ps, 30, alert.getFinalRigState());

			ps.setBoolean(31, alert.isOnCreateEventsExecuted());
			ps.setBoolean(32, alert.isSnoozed());
			SQLHelper.setTimestampOrNull(ps, 33, alert.getUnSnoozedAt());
			ps.setString(34, alert.getUnSnoozedBy());
			ps.setString(35, alert.getLastSnoozedBy());
			SQLHelper.setTimestampOrNull(ps, 36, alert.getLastSnoozedAt());

			ps.setString(37, alert.getInvestigateBy());
			SQLHelper.setTimestampOrNull(ps, 38, alert.getInvestigateAt());
			ps.setString(39, alert.getParentClassId());
			ps.setString(40, alert.getParentUuid());

			ps.setString(41, alert.getUuid());

			return ps.executeUpdate() != 0;
		}
	}

	@Override
	public boolean deleteAlert(String id) throws SQLException {
		Objects.requireNonNull(id, "id");
		boolean success = false;
		try (Connection conn = alertsDataSource.getConnection();
			 PreparedStatement deleteAlert = conn.prepareStatement("DELETE FROM alerts WHERE uuid = ?")) {
			deleteAlert.setString(1, id);
			success = deleteAlert.executeUpdate() != 0;
		}
		return success;
	}
	
	@Override
	public List<Alert> getAlerts_TEMP() throws SQLException {
		ArrayList<Alert> results = new ArrayList<>();
		try (Connection conn = alertsDataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement("SELECT * FROM alerts LEFT JOIN snoozedalerts ON alerts.classid = snoozedalerts.alertClassId")) {
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				results.add(getAlert(rs));
		}
		return results;
	}

	@Override
	public List<String> getStaleAlertIds_TEMP(Instant time) throws SQLException {
		ArrayList<String> results = new ArrayList<>();
		try (Connection conn = alertsDataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement("SELECT uuid FROM alerts WHERE status = 2 AND lastStatusChange < ?")) {
			ps.setTimestamp(1, Timestamp.from(time));
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				results.add(rs.getString(1));
		}
		return results;
	}

	@Override
	public SnoozeRecord getSnoozeRecord(String alertClassId, String wellId) throws SQLException {
		String query = "SELECT * FROM snoozedAlerts WHERE alertClassId = ? AND well = ?";
		
		try (Connection conn = alertsDataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, alertClassId);
			ps.setString(2, wellId);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? getSnoozeRecord(rs) : null;
		}
	}

	@Override
	public void createSnoozeRecord(SnoozeRecord record) throws SQLException {
		String query = "INSERT INTO snoozedAlerts (alertClassId, well, snoozedBy, snoozedAt, unSnoozeAt) VALUES (?, ?, ?, ?, ?)";
		String query2 = "UPDATE alerts SET snoozed = true WHERE classid = ? AND wellid = ?";
		
		try (Connection conn = alertsDataSource.getConnection()) {
			try (PreparedStatement ps = conn.prepareStatement(query)) {
				ps.setString(1, record.getClassId());
				ps.setString(2, record.getWellId());
				ps.setString(3, record.getSnoozedBy());
				ps.setTimestamp(4, Timestamp.from(record.getSnoozedAt()));
				ps.setTimestamp(5, Timestamp.from(record.getUnSnoozeAt()));
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = conn.prepareStatement(query2)) {
				ps.setString(1, record.getClassId());
				ps.setString(2, record.getWellId());
				ps.executeUpdate();
			}
		}
	}

	@Override
	public boolean deleteSnoozeRecord(String alertClassId, String wellId) throws SQLException {
		String query = "DELETE FROM snoozedAlerts WHERE alertClassid = ? and well = ?";
		
		try (Connection conn = alertsDataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, alertClassId);
			ps.setString(2, wellId);
			return ps.executeUpdate() != 0;
		}
	}
	
	private SnoozeRecord getSnoozeRecord(ResultSet rs) throws SQLException {
		String alertClassId = rs.getString("alertClassId");
		String well = rs.getString("well");
		Timestamp snoozedAt = rs.getTimestamp("snoozedAt");
		String snoozedBy = rs.getString("snoozedBy");
		Timestamp unSnoozeAt = rs.getTimestamp("unSnoozeAt");
		return new SnoozeRecord(alertClassId, well, snoozedBy, snoozedAt.toInstant(), unSnoozeAt.toInstant());
	}
	
	private AlertImpl getAlert(ResultSet rs) throws SQLException {
		AlertImpl a = (AlertImpl) AlertsFactory.getAlert();
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
	
	private void startH2Service(H2Config config) {
		try {
			
			Class.forName("org.h2.Driver");
			Connection connection = null;
			
			String url = config.getConnectionURL();
			String urlExisting = config.getConnectionURL() + ";IFEXISTS=TRUE";
			String userName = "sa";
			String password = "";

			if (config.isServerEnabled()) {
				logger.info("Validating if H2 service is already active");

				try {
					connection = DriverManager.getConnection(urlExisting, userName, password);
					logger.info("Connecting to existing H2 server");	
				} catch (SQLException e) {
					// Expected Exception
				}

				if (connection == null) {
					try {
						logger.info("Starting H2 Server in this instance");
						server = Server.createTcpServer("-tcpPort",
								                        config.getPort(),
								                        "-tcpAllowOthers",
								                        "-baseDir",
								                        config.getBaseDir());
						server.start();
					} catch (SQLException e) {
						logger.error("Unable to Start H2 Server", e);
					}
				}
			}
			
			if (connection == null) {
				logger.info("Initializing H2 Alert Status Store");
				connection = DriverManager.getConnection(url, userName, password);
			}
			
			// select version from alertStatusVersion where component='global' and version=1.992
			// create table if not exists alertStatusVersion (component varchar(10), version double);
			// insert into alertStatusVersion values ('global', 1.992);
			
			// New version numbers are integers. Due to the way old version numbers were doubles less than 2,
			// they will be truncate on load so updateStore() will receive 0 if nothing exists, 1 if old version system,
			// and 2 or greater if new versioning system.
			
			int version = 0;
			try (Statement s = connection.createStatement()) {
				try (ResultSet rs = s.executeQuery("SELECT version FROM alertStatusVersion WHERE component='global'")) {
					if (rs.first()) {
						double v = rs.getDouble(1);
						// Ensure backwards compatibility with old versioning system
						if (v == 1.992)
							v = 2;
						version = (int) v;
					}
				}
				catch (SQLException e) {
					// alertStatusVersion table does not exist
				}
			}
			
			if (version < CURRENT_VERSION) {
				updateStore(connection, version);
			}
			else if (version > CURRENT_VERSION) {
				throw new SQLException("existing database version greater than current version");
			}
			logger.info("Database version is {}", CURRENT_VERSION);
						
			connection.close();
		} catch (ClassNotFoundException e) {
			logger.error("H2 Driver error", e);
		} catch (SQLException e) {
			logger.error("SQL Exception error", e);
		} catch (IOException e) {
			logger.error("Script execution error", e);
		}
	}
	
	private static void updateStore(Connection conn, int version) throws IOException, SQLException {
		logger.info("Beginning a database update from version {} to version {}", version, CURRENT_VERSION);
		if (version == 0) {
			// database does not exist
			boolean success = executeScript(conn, "sql/AlertStatusCreate.sql");
			if (!success)
				throw new IOException("SQL creation script not found");
			updateVersion(conn, CURRENT_VERSION);
		}
		else {
			for (int next = version + 1; next <= CURRENT_VERSION; next++) {
				boolean success = executeScript(conn, "sql/AlertStatusUpdate-" + next + ".sql");
				if (!success)
					throw new IOException("SQL update script version " + next + " not found");
				updateVersion(conn, next);
			}
		}
	}
	
	private static void updateVersion(Connection conn, int version) throws SQLException {
		try (Statement s = conn.createStatement()) {
			String q = String.format("MERGE INTO alertStatusVersion KEY(component) VALUES('global', %s)", version);
			s.execute(q);
		}
		logger.info("Updated to version {}", version);
	}
	
	private static boolean executeScript(Connection conn, String name) throws IOException, SQLException {
		logger.debug("Executing script: {}", name);
		try (InputStream resource = ResourceHelper.getResourceOrFile(name)) {
			if (resource == null)
				return false;
			BufferedReader script = new BufferedReader(new InputStreamReader(resource));
			try (Statement stmt = conn.createStatement()) {
				String line;
				while ((line = script.readLine()) != null)
					stmt.execute(line);
			}
			return true;
		}
	}
}
