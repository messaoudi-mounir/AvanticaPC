package com.petrolink.mbe.alertstatus.impl;

import java.time.OffsetDateTime;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnow.alertstatus.AlertsFactory;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Automatic Unsnooze on Timer
 * @author paul
 *
 */
public class UnSnoozeCronJob implements Job {

	/**
	 * Job parameter in datamap to indicate principal used to unsnooze
	 */
	public final static String UNSNOOZE_PRINCIPAL_JOB_DATAMAP_KEY = "unsnoozePrincipal";
	
	/**
	 * Job parameter in datamap to indicate class id to unsnooze
	 */
	public final static String UNSNOOZE_CLASSID_JOB_DATAMAP_KEY = "classId";
	
	/**
	 * Job parameter in datamap to indicate well id to unsnooze
	 */
	public final static String UNSNOOZE_WELLID_JOB_DATAMAP_KEY = "wellId";
	
	private static final Logger logger = LoggerFactory.getLogger(UnSnoozeCronJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.debug("Executing");
		try {
			JobDataMap dataMap = context.getJobDetail().getJobDataMap();
			String classId = (String) dataMap.get(UNSNOOZE_CLASSID_JOB_DATAMAP_KEY);
			String wellId = (String) dataMap.get(UNSNOOZE_WELLID_JOB_DATAMAP_KEY);
			String principal = (String)dataMap.get(UNSNOOZE_PRINCIPAL_JOB_DATAMAP_KEY);
			if (principal == null) {
				principal = "automatic";
			}
			
			AlertsDAO dao = (AlertsDAO) AlertsFactory.getAlertsDAO();
			
			try {
				dao.unSnooze(classId, wellId, OffsetDateTime.now(), principal);
			} catch (EngineException e) {
				//Should already logged but just in case
				logger.error("Failure executing unsnooze automatically", e);
			}
		} catch (Exception e) {
			logger.error("exception during execution", e);
			throw new JobExecutionException(e);
		}
		logger.debug("Finished executing");
	}
}
