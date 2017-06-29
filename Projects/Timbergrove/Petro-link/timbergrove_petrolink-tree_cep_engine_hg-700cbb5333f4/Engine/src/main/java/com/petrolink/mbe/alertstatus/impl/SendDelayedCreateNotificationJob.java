package com.petrolink.mbe.alertstatus.impl;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.services.ServiceAccessor;
import com.smartnow.alertstatus.Alert;

/**
 * Automatic send delayed notification
 * @author aristo
 *
 */
public class SendDelayedCreateNotificationJob implements Job {

		private AlertsDAO dao = null;
	private AlertsService alertService = null;
	private static Logger logger = LoggerFactory.getLogger(SendDelayedCreateNotificationJob.class);
	
	/**
	 * Constructor
	 */
	public SendDelayedCreateNotificationJob() {
		alertService = ServiceAccessor.getAlertsService(); 
		setAlertServiceDao((alertService != null) ? alertService.getAlertsDAO() : null);
	}

	@Override
	public void execute(JobExecutionContext job) throws JobExecutionException {
		dao.checkDelayedAlertCreateAction();
	}
	
	/**
	 * @return the dao
	 */
	public final AlertsDAO getAlertServiceDao() {
		return dao;
	}

	/**
	 * @param dao the dao to set
	 */
	public final void setAlertServiceDao(AlertsDAO dao) {
		this.dao = dao;
	}
}
