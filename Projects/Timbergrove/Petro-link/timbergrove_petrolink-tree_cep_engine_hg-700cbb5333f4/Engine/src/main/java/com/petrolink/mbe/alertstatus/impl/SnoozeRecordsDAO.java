package com.petrolink.mbe.alertstatus.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.alertstatus.store.AlertDataStore;
import com.smartnow.engine.exceptions.EngineException;

/**
 * DAO for Snooze Records
 * @author paul
 *
 */
public class SnoozeRecordsDAO implements Runnable {
	
    Map<String, SnoozeRecord> snoozeRecords = new ConcurrentHashMap<String, SnoozeRecord>();
    private final AlertDataStore store;
	private final DataSource dataSource;
    private static final Logger logger = LoggerFactory.getLogger(SnoozeRecordsDAO.class);
    private LinkedBlockingQueue<SnoozeRecord> queue = new LinkedBlockingQueue<>();
	private boolean keepRunning = true;
	private Thread thread = null;
	
    /**
     * @param store
     */
    public SnoozeRecordsDAO(AlertDataStore store) {
    	this.store = store;
    	this.dataSource = store.getBasicDataSource();
    	
    	thread = new Thread(this);
    	thread.setDaemon(false);
    	thread.start();
    }

	/**
     * Adds an Snoozed Record
     * @param classId
     * @param wellId
     * @param snoozedBy
     * @param snoozedAt
	 * @param unSnoozeAt 
     * @return true if Snooze was saved correctly
	 * @throws EngineException 
     */
    public boolean snooze(String classId, String wellId, String snoozedBy, Instant snoozedAt, Instant unSnoozeAt) throws EngineException {
    	String key = classId + "/" + wellId;
    	
    	if (!snoozeRecords.containsKey(key)) {
    		SnoozeRecord record = new SnoozeRecord(classId, wellId, snoozedBy, snoozedAt, unSnoozeAt);
    		synchronized (record) {
        		try {
    				queue.put(record);
    			} catch (InterruptedException e) {
    				throw new EngineException("Unable to persist snooze", e);
    			}
        		snoozeRecords.put(key, record);
			}
    		return true;    		
    	} else {
    		// Already Snoozed Log error correctly
    	}
		return false;
    	
    }
    
    /**
     * Adds an UnSnoozed Record
     * @param classId
     * @param wellId
     * @param unSnoozedBy 
     * @param unSnoozedAt
     * @return true if UnSnooze operation was successfull
     * @throws EngineException 
     */
    public boolean unSnooze(String classId, String wellId, String unSnoozedBy, Instant unSnoozedAt) throws EngineException {
    	String key = classId + "/" + wellId;
    	
    	if (snoozeRecords.containsKey(key)) {
    		SnoozeRecord record = snoozeRecords.get(key);
    		
    		synchronized (record) {
        		record.setUnSnoozedBy(unSnoozedBy);
        		record.setUnSnoozedAt(unSnoozedAt);
        		try {
    				queue.put(record);
    			} catch (InterruptedException e) {
    				throw new EngineException("Unable to persist snooze", e);
    			}
        		snoozeRecords.remove(key);
        		return true;    			
    		}
    	} else {
    		return false;
    	}    	
    }

	private DataSource getAlertsDataSource() {
		return dataSource ;
	}
    
	private void persistUnsnooze(SnoozeRecord record) {
		logger.trace("Performing UnSnooze Action persistency tasks for ClassI {} and WellId {}", record.getClassId(), record.getWellId());
		try {
			// TODO Improve how this whole history thing works
            SnoozeRecord existing = store.getSnoozeRecord(record.getClassId(), record.getWellId());
            if (existing != null) {
            	existing.setUnSnoozedAt(record.getUnSnoozedAt());
            }
            store.deleteSnoozeRecord(record.getClassId(), record.getWellId());
        } catch (SQLException e) {
            logger.error("Error un snoozing alert",e);
        }		
	}
	
	
	private void persistSnoozeRecord(SnoozeRecord record) {
		logger.trace("Performing Snooze Action persistency tasks for ClassI {} and WellId {}", record.getClassId(), record.getWellId());
        try {
        	store.createSnoozeRecord(record);
        } catch (SQLException e) {
            logger.error("Error snoozing alert",e);
        }		
	}

	@Override
	public void run() {
		reloadRecords();
		
		while (isKeepRunning()) {
			try {
				SnoozeRecord record = queue.take();
				if (record.getUnSnoozedBy() == null) {
					persistSnoozeRecord(record);
				} else {
					persistUnsnooze(record);
				}
			} catch (Exception e) {
				// This shall never die
				logger.error("Unexpected handled exception while processing Snooze Async Persistence",e);
			}
		}
	}

	private void reloadRecords() {
		try (Connection conn = getAlertsDataSource().getConnection();
				PreparedStatement ps = conn.prepareStatement("select * from snoozedalerts")) {
			ResultSet rs = ps.executeQuery();
			
			List<SnoozeRecord> records = createSnoozeRecordsFromResultSet(rs, false);
			
			for (SnoozeRecord record : records) {
		    	String key = record.getClassId() + "/" + record.getWellId();				
				snoozeRecords.put(key, record);
			}
			
		} catch (SQLException e) {
            logger.error("Error reloading records",e);
		}		
	}

	/**
	 * @return the keepRunning
	 */
	public synchronized boolean isKeepRunning() {
		return keepRunning;
	}

	/**
	 * @param keepRunning the keepRunning to set
	 */
	public synchronized void setKeepRunning(boolean keepRunning) {
		this.keepRunning = keepRunning;
	}

	/**
	 * @param classId
	 * @param wellId
	 * @return the existing SnoozeRecord
	 */
	public SnoozeRecord getSnoozeRecord(String classId, String wellId) {
    	String key = classId + "/" + wellId;
		return snoozeRecords.get(key);
	}
	
	/**
	 * Get all snooze records
	 * @return an array list of SnoozeRecords
	 */
	public List<SnoozeRecord> getSnoozeRecords(){
		if(snoozeRecords != null) {
			return new ArrayList<SnoozeRecord>(snoozeRecords.values());
		}
		return new ArrayList<SnoozeRecord>();
	}
	
	/**
	 * Create alert from ResultSet which contains single record
	 * @param source
	 * @return List of SnoozeRecord
	 * @throws SQLException 
	 */
	public static SnoozeRecord createSnoozeRecordFromResultSet(ResultSet source) throws SQLException {
		SnoozeRecord record = new SnoozeRecord(source.getString("alertClassId"), source.getString("well"), source.getString("snoozedBy"), 
				source.getTimestamp("snoozedAt").toInstant(), source.getTimestamp("unSnoozeAt").toInstant());
		return record;
	} 
	
	/**
	 * Create alert from ResultSet which contains list of record
	 * @param source
	 * @param isSuppressSQLException If true will supress exception when something failed in single record due to SQL Exception
	 * @return List of SnoozeRecord
	 * @throws SQLException 
	 */
	public static List<SnoozeRecord> createSnoozeRecordsFromResultSet(ResultSet source, boolean isSuppressSQLException) throws SQLException {
		ArrayList<SnoozeRecord> records = new ArrayList<SnoozeRecord>();
		
		while (source.next()) {
			try{
				SnoozeRecord record = createSnoozeRecordFromResultSet(source);
				records.add(record);
			} catch (SQLException e) {
				logger.error("Unable to create single Snooze Record ", e);
				if (!isSuppressSQLException) {
					throw new SQLException("Unable to create single Snooze Record ",e);
				} 
			}
		}			
		return records;
	}
}
