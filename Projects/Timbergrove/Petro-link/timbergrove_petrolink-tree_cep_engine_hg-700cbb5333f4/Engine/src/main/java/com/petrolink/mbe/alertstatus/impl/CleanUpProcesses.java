package com.petrolink.mbe.alertstatus.impl;

import java.time.Instant;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnow.alertstatus.AlertsFactory;

/**
 * Process to clean up closed alerts
 * @author paul
 *
 */
public class CleanUpProcesses implements Job {
	// Register the Clean up Job in the Quartz Scheduler. 
	// This task shall run every X minutes and clean all CLOSED alert that have been closed over X time
	// Laststatuschange + timetolive < time -> delete
	private static AlertsService service;
	private static Logger logger = LoggerFactory.getLogger(CleanUpProcesses.class);
	
	/**
	 * Closed Alert's DATAMAP key for Time To Live
	 */
	public static final String CLOSED_ALERT_TTL_JOB_DATAMAP = "jdmttl";
	
	
	@Override
	public void execute(JobExecutionContext job) throws JobExecutionException {
		JobDataMap dataMap = job.getJobDetail().getJobDataMap();
		
		long ttl = Long.parseLong((String)dataMap.get(CLOSED_ALERT_TTL_JOB_DATAMAP));
		Instant expirationInstant = Instant.now().minusMillis(ttl);
		clearClosedAlertsAndJournalOlderThan(expirationInstant);
	}
	
	/**
	 * Clear Alerts and Journal older than specified time
	 * @param maximumTimeToBeCleared Time expiration
	 */
	public void clearClosedAlertsAndJournalOlderThan(Instant maximumTimeToBeCleared) {
		AlertsDAO dao = (AlertsDAO) AlertsFactory.getAlertsDAO();
		dao.clearClosedAlertsAndJournalOlderThan(maximumTimeToBeCleared);
	}
	
	
	/**
	 * @return the service
	 */
	public static AlertsService getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public static void setService(AlertsService service) {
		CleanUpProcesses.service = service;
	}
}
