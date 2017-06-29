package com.petrolink.mbe.alertstatus.store;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import org.apache.commons.dbcp2.BasicDataSource;

import com.petrolink.mbe.alertstatus.impl.SnoozeRecord;
import com.smartnow.alertstatus.Alert;

/**
 * AlertDataStore interface
 *
 */
public interface AlertDataStore {
	/**
	 * @return a BasicDataStore
	 */
	BasicDataSource getBasicDataSource();
	
	/**
	 * Get the alert with the specified ID
	 * @param alertId
	 * @return A matching alert or null if no alert was found
	 * @throws SQLException 
	 */
	Alert getAlert(String alertId) throws SQLException;
	
	/**
	 * Gets all alerts with status that is not closed, with optional well filter, ordered by creation descending.
	 * @param wells
	 * @return A list of matching alerts
	 * @throws SQLException 
	 */
	List<Alert> getUnclosedAlerts(List<String> wells) throws SQLException;
	
	/**
	 * Get alerts that are snoozed, with an optional well filter.
	 * @param wells
	 * @return A list of matching alerts
	 * @throws SQLException 
	 */
	List<Alert> getSnoozedAlerts(List<String> wells) throws SQLException;
	
	/**
	 * Get the unclosed alerts for a specific class.
	 * @param classId
	 * @return A list of matching alerts
	 * @throws SQLException
	 */
	List<Alert> getUnclosedAlertsByClass(String classId) throws SQLException;
	
	/**
	 * Get the number of unclosed alerts for a specific class.
	 * @param classId
	 * @return A number of matching alerts
	 * @throws SQLException 
	 */
	int getUnclosedAlertCountByClass(String classId) throws SQLException;
	
	/**
	 * Get all alerts with a creation time within the specified range, with an optional well filter,
	 * ordered by creation descending.
	 * @param inclusiveStart
	 * @param inclusiveEnd
	 * @param wellFilter
	 * @param limit
	 * @return A list of matching alerts
	 * @throws SQLException 
	 */
	List<Alert> getAlertsInCreatedRange(Instant inclusiveStart, Instant inclusiveEnd, List<String> wellFilter, int limit) throws SQLException;
	
	/**
	 * Get unclosed alerts with a creation time within the specified range, with an optional well filter,
	 * ordered by creation descending.
	 * @param inclusiveStart
	 * @param inclusiveEnd
	 * @param wellFilter
	 * @param limit
	 * @return A list of matching alerts
	 * @throws SQLException 
	 */
	List<Alert> getUnclosedAlertsInCreatedRange(Instant inclusiveStart, Instant inclusiveEnd, List<String> wellFilter, int limit) throws SQLException;
	
	/**
	 * Get an alert by its status, well, and class ID.
	 * @param status
	 * @param classId
	 * @param wellId
	 * @return A matching alert or null.
	 * @throws SQLException
	 */
	Alert getAlertByStatusClassWell(int status, String classId, String wellId) throws SQLException;
	
	/**
	 * Get alerts by their status.
	 * @param status
	 * @return A list of matching alerts.
	 * @throws SQLException
	 */
	List<Alert> getAlertsByStatus(int status) throws SQLException;
	
	/**
	 * Create an alert
	 * @param alert
	 * @return True if the alert was created without error.
	 * @throws SQLException 
	 */
	boolean createAlert(Alert alert) throws SQLException;
	
	/**
	 * Update an alert with the matching UUID
	 * @param alert
	 * @return True if the alert was created without error.
	 * @throws SQLException 
	 */
	boolean updateAlert(Alert alert) throws SQLException;
	
	/**
	 * Delete an alert with the specified ID and any related alert journal entries.
	 * @param id
	 * @return True if an alert with the specified ID was found and deleted.
	 * @throws SQLException 
	 */
	boolean deleteAlert(String id) throws SQLException;
	
	/**
	 * Get all alerts of any status
	 * @return A list of all alerts
	 * @throws SQLException
	 */
	List<Alert> getAlerts_TEMP() throws SQLException;
	
	/**
	 * Get the ID's of all closed alerts where the last status change was before the specified instant.
	 * @param time
	 * @return A list of matching ID's.
	 * @throws SQLException
	 */
	List<String> getStaleAlertIds_TEMP(Instant time) throws SQLException;
	
	/**
	 * Get a snooze record by alert class ID and well ID
	 * @param alertClassId
	 * @param wellId
	 * @return A matching record or null
	 * @throws SQLException
	 */
	SnoozeRecord getSnoozeRecord(String alertClassId, String wellId) throws SQLException;
	
	/**
	 * Create a snooze record
	 * @param record
	 * @throws SQLException
	 */
	void createSnoozeRecord(SnoozeRecord record) throws SQLException;
	
	/**
	 * Delete a snooze record
	 * @param alertClassId
	 * @param wellId
	 * @return true if the deletion was successful
	 * @throws SQLException
	 */
	boolean deleteSnoozeRecord(String alertClassId, String wellId) throws SQLException;
}
