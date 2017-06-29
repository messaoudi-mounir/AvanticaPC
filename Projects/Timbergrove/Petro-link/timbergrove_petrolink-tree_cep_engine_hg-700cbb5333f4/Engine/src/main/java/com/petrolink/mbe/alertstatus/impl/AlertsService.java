package com.petrolink.mbe.alertstatus.impl;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.JobKey.jobKey;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import java.util.List;

import javax.sql.DataSource;

import org.jdom2.Document;
import org.jdom2.Element;
import org.json.JSONObject;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.alertstatus.impl.serializers.AlertJSONSerializer;
import com.petrolink.mbe.alertstatus.impl.serializers.AlertStatusResponseJsonSerializer;
import com.petrolink.mbe.alertstatus.impl.serializers.AlertStatusResponseXmlSerializer;
import com.petrolink.mbe.alertstatus.store.AlertDataStore;
import com.petrolink.mbe.alertstatus.store.AlertH2DataStore;
import com.petrolink.mbe.alertstatus.store.H2Config;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.petrolink.mbe.util.XmlConfigUtil;
import com.rabbitmq.client.Channel;
import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.AlertsFactory;
import com.smartnow.alertstatus.serializers.AlertSerializer;
import com.smartnow.alertstatus.serializers.AlertSerializerFactory;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.scheduler.service.SchedulerService;
import com.smartnow.rabbitmq.service.RMQRestfulService;

/**
 * Petrolink specific Alert Service
 * @author paul
 *
 */
public class AlertsService extends RMQRestfulService {	
	
	
	protected class AlertServiceConsumer extends RMQRestfulServiceConsumer {
		private static final String ACTION_INVESTIGATE = "INVESTIGATE";
		
		public AlertServiceConsumer(Channel channel) {
			super(channel);
		}
		

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.GenericRestfulService.GenericRestfulServiceConsumer#doGet(java.util.Map, org.jdom2.Element)
		 */
		@Override
		protected Document doGet(Map<String, Object> map, Element root) {
			Document reply = null;
			String classId = null;
			String wellId = null;
			String where = null;
			
			switch (root.getChildText("Action", root.getNamespace()).toUpperCase()) {
			case "QUERY":
				String uuid  = root.getChildText("UUID", root.getNamespace()).trim();
				reply = (Document) ((AlertsService) trigger).dao.getSerializedAlert(uuid, "XML");
				break;
			case "QUERYALL":
				reply = (Document) ((AlertsService) trigger).dao.getSerializedAlerts("XML");
				break;
			case "QUERYACTIVE":
				if (root.getChild("ClassId") != null) {
					classId = root.getChildText("ClassId");
				}
				
				if (root.getChild("WellId") != null) {
					wellId = root.getChildText("WellId");
				}

				if ((wellId == null) && (classId == null)) {
					String whereActive = "where status = " + Alert.ACTIVE;
					reply = (Document) ((AlertsService) trigger).dao.getSerializedAlerts(whereActive, "XML");					
				} else if (wellId == null) {
					reply = (Document) ((AlertsService) trigger).dao.getActiveAlertByWellClassId(wellId, classId, "XML");
				} else {
					reply = (Document) ((AlertsService) trigger).dao.getActiveAlertsByClassId(classId, "XML");
				}
				
				break;
			case "QUERYWHERE":
				reply = (Document) ((AlertsService) trigger).dao.getSerializedAlerts(root.getChildText("Where", root.getNamespace()), "XML");
				break;
			case "QUERYSNOOZEDCLASSES": 
				if(root.getChild("Well", root.getNamespace()) == null){
					reply = (Document) ((AlertsService) trigger).dao.getSnoozedClasses("XML");
				} else if(root.getChild("Class", root.getNamespace()) == null){
					reply = (Document) ((AlertsService) trigger).dao.getSnoozedClassesByWell("XML", root.getChildText("Well", root.getNamespace()));
				} else {
					reply = (Document) ((AlertsService) trigger).dao.getSnoozedClassesByWellClass("XML", 
							root.getChildText("Well", root.getNamespace()), root.getChildText("Class", root.getNamespace()));
				}
				
				break;
			case "QUERYCOUNTWHERE":
				where = root.getChildText("Where", root.getNamespace());
				
				int result = ((AlertsService) trigger).dao.getAlertCountWhere(where);
				
				reply = AlertStatusResponseXmlSerializer.serializeAlertCountAsDocument(result);
				break;
			case "UPDATESTATUS":
			case "ACKNOWLEDGE":
			case "ACKNOWLEDGEWHERE":
			case "COMMENT":
			case "SNOOZE":
			case "UNSNOOZE":
			case ACTION_INVESTIGATE:	
				reply = new Document(new Element("Error"));
				reply.getRootElement().setText("Unsupported operation on method get");
				break;
			}
				
			
			return reply;
		}

		
		
		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.RMQRestfulService.RMQRestfulServiceConsumer#doPost(java.util.Map, org.jdom2.Element)
		 */
		@Override
		protected Document doPost(Map<String, Object> map, Element root) {
			Document reply = null;
			String s = root.getChildText("Action", root.getNamespace()).toUpperCase();
			AlertsDAO alertDao =  ((AlertsService) trigger).dao;
			List<Alert> affectedAlerts ;
			
			switch (s) {
			case "UPDATESTATUS":
				reply = (Document) ((AlertsService) trigger).dao.changeAlertStatus("XML", root.getChildText("UUID", root.getNamespace()),
						Integer.parseInt(root.getChildText("NewStatus", root.getNamespace())));
				break;
			case ACTION_INVESTIGATE:
				affectedAlerts = alertDao.investigateAlert( 
						XmlConfigUtil.getChildrenText(root, "UUID")
						, XmlConfigUtil.getChildrenText(root, "ClassId")
						, root.getChildText("By", root.getNamespace())
						, OffsetDateTime.parse(root.getChildText("Timestamp", root.getNamespace()))
						, false //Do not replace investigator
						);
				reply = AlertStatusResponseXmlSerializer.serializeAlertsAsDocument(affectedAlerts);
				break;
			case "ACKNOWLEDGE":
				affectedAlerts = AlertRequestResult.getAlertsFromResults(alertDao.acknowledgeAlerts(
						XmlConfigUtil.getChildrenText(root, "UUID")
						, root.getChildText("By", root.getNamespace())
						, OffsetDateTime.parse(root.getChildText("Timestamp", root.getNamespace()))
						, null
						, null
						)); 
				reply = AlertStatusResponseXmlSerializer.serializeAlertsAsDocument(affectedAlerts);
				break;
			case "ACKNOWLEDGEWHERE":
				affectedAlerts = alertDao.acknowledgeWhere(
						root.getChildText("Where", root.getNamespace())
						, root.getChildText("By", root.getNamespace())
						, OffsetDateTime.parse(root.getChildText("Timestamp", root.getNamespace()))
						); 
				reply = AlertStatusResponseXmlSerializer.serializeAlertsAsDocument(affectedAlerts);
				break;
			case "COMMENT":
				reply = (Document) alertDao.commentAlert("XML", root.getChildText("UUID", root.getNamespace()),
						root.getChildText("Comment", root.getNamespace()), root.getChildText("By", root.getNamespace()),
						OffsetDateTime.parse(root.getChildText("Timestamp", root.getNamespace())));
				break;
			case "SNOOZE":
				try {
					if(root.getChild("UUID", root.getNamespace()) != null){
						reply = (Document) ((AlertsService) trigger).dao.snoozeAlert("XML", 
								root.getChildText("UUID", root.getNamespace()), 
								root.getChildText("By", root.getNamespace()), 
								OffsetDateTime.parse(root.getChildText("Timestamp", root.getNamespace())),
								OffsetDateTime.parse(root.getChildText("ExpireAt", root.getNamespace())));
					} else {
						reply = (Document) ((AlertsService) trigger).dao.snoozeClass("XML", 
								root.getChildText("Class", root.getNamespace()),
								root.getChildText("Well", root.getNamespace()),
								root.getChildText("By", root.getNamespace()), 
								OffsetDateTime.parse(root.getChildText("Timestamp", root.getNamespace())),
								OffsetDateTime.parse(root.getChildText("ExpireAt", root.getNamespace())));
					}
				} catch (EngineException e) {
					reply = buildExceptionDocument(e);
				}
				break;
			case "UNSNOOZE":
				try {
					Alert activeAlert = null;
					
					if(root.getChild("UUID", root.getNamespace()) != null){
						//Unsnoozing based on single alert
						activeAlert = alertDao.unSnoozeAlert("XML", 
								root.getChildText("UUID", root.getNamespace()),
								OffsetDateTime.parse(root.getChildText("Timestamp", root.getNamespace())),
								root.getChildText("By", root.getNamespace()));
					} else {
						//Unsnoozing based on class directly
						activeAlert =
							alertDao.unSnoozeAlert("XML", 
								root.getChildText("Class", root.getNamespace()),
								root.getChildText("Well", root.getNamespace()),
								OffsetDateTime.parse(root.getChildText("Timestamp", root.getNamespace())),
								root.getChildText("By", root.getNamespace()));
					}
					
					if(activeAlert instanceof com.petrolink.mbe.alertstatus.Alert) {
						reply = AlertStatusResponseXmlSerializer.serializeAlertAsDocument((com.petrolink.mbe.alertstatus.Alert)activeAlert, true);
					} else{
						reply = AlertStatusResponseXmlSerializer.serializeAlertAsDocument(null, true);
					}
				} catch (EngineException e) {
					reply = buildExceptionDocument(e);
				}
				break;
			}
			
			return reply;
		}
		
		/**
		 * 
		 */
		@Override
		protected JSONObject doGet(Map<String, Object> map, JSONObject json){
			JSONObject reply = new JSONObject();
			String classId = null;
			String wellId = null;
			String where = null;
			
			switch (json.getString("action").toUpperCase()) {
			case "QUERY":
				where = "where uuid = '" + json.getString("uuid") + "'";
				reply.put("results", ((AlertsService) trigger).dao.getSerializedAlerts(where, "JSON"));
				break;
			case "QUERYALL":
				reply.put("results", ((AlertsService) trigger).dao.getSerializedAlerts("JSON"));
				break;
			case "QUERYACTIVE":
				String whereActive = "where status = " + Alert.ACTIVE;
				reply.put("results", ((AlertsService) trigger).dao.getSerializedAlerts(whereActive, "JSON"));
				break;
			case "QUERYWHERE":
				classId = json.getString("ClassId");				
				wellId = json.getString("WellId");

				if ((wellId == null) && (classId == null)) {
					whereActive = "where status = " + Alert.ACTIVE;
					reply.put("results",((AlertsService) trigger).dao.getSerializedAlerts(whereActive, "JSON"));
				} else if (wellId == null) {
					reply.put("results",((AlertsService) trigger).dao.getActiveAlertByWellClassId(wellId, classId, "JSON"));
				} else {
					reply.put("results",((AlertsService) trigger).dao.getActiveAlertsByClassId(classId, "JSON"));
				}
				
				
				reply.put("results", ((AlertsService) trigger).dao.getSerializedAlerts(json.getString("where"), "JSON"));
				break;
			case "QUERYCOUNTWHERE":
				where = json.getString("Where");
				
				int result = ((AlertsService) trigger).dao.getAlertCountWhere(where);
				
				reply.put("count", result);
				break;
			case "UPDATESTATUS":
			case "ACKNOWLEDGE":
			case "ACKNOWLEDGEWHERE":
			case "COMMENT":
			case "SNOOZE":
			case "UNSNOOZE":
				reply.put("results", "Unsupported operation on method get");
			}
			
			return reply;
		}
		
		@Override
		protected JSONObject doPost(Map<String, Object> map, JSONObject json){
			JSONObject reply = new JSONObject();
			AlertsDAO alertDao =((AlertsService) trigger).dao;
			List<Alert> affectedAlerts;
			switch (json.getString("action").toUpperCase()) {
			case "UPDATESTATUS":
				reply.put("results", alertDao.changeAlertStatus("JSON", json.getString("uuid"), json.getInt("newStatus")));
				break;
			case "ACKNOWLEDGE":
				affectedAlerts = alertDao.acknowledgeAlert(
						json.getString("uuid")
						, json.getString("acknowledgeBy")
						, OffsetDateTime.parse(json.getString("timestamp"))
						,null
						,null
						); 
				reply.put("results", AlertStatusResponseJsonSerializer.serializeAlertsAsJSONArray(affectedAlerts));
				break;
			case "ACKNOWLEDGEWHERE":
				affectedAlerts = alertDao.acknowledgeWhere(
						json.getString("where")
						, json.getString("acknowledgeBy")
						, OffsetDateTime.parse(json.getString("timestamp"))
						); 
				reply.put("results", AlertStatusResponseJsonSerializer.serializeAlertsAsJSONArray(affectedAlerts));
				break;
			case "COMMENT":
				reply.put("results", alertDao.commentAlert("JSON", json.getString("uuid"), json.getString("comment"), json.getString("commentedBy"), 
					OffsetDateTime.parse(json.getString("timestamp"))));
				break;
			case "SNOOZEALERT":
				reply.put("results", alertDao.snoozeAlert("JSON", json.getString("uuid"), json.getString("snoozedBy"), 
					OffsetDateTime.parse(json.getString("timestamp")), OffsetDateTime.parse(json.getString("unSnoozeAt"))));
				break;
			case "SNOOZECLASS":
				try{
					reply.put("results", alertDao.snoozeClass("JSON", json.getString("ClassId"), json.getString("WellId"), json.getString("snoozedBy"), 
						OffsetDateTime.parse(json.getString("timestamp")), OffsetDateTime.parse(json.getString("unSnoozeAt"))));
				} catch (EngineException ex) {
					reply.put("error", AlertJSONSerializer.serializeExceptionAsAlertError(ex));
				}
				break;
			case "UNSNOOZECLASS":
				try {
					reply.put("results", alertDao.unSnoozeAlert("JSON", json.getString("classId"), json.getString("wellId"), OffsetDateTime.parse(json.getString("timestamp")), json.getString("By")));
				} catch (EngineException ex) {
					reply.put("error", AlertJSONSerializer.serializeExceptionAsAlertError(ex));
				}
				break;
			case "UNSNOOZEALERT":
				try {
					reply.put("results", alertDao.unSnoozeAlert("JSON", json.getString("classId"), json.getString("wellId"), OffsetDateTime.parse(json.getString("timestamp")), json.getString("By")));
				} catch (EngineException ex) {
					reply.put("error", AlertJSONSerializer.serializeExceptionAsAlertError(ex));
				}
				break;
			}
			
			return reply;
		}


		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.RMQRestfulService.RMQRestfulServiceConsumer#genericXMLError(java.lang.Exception)
		 */
		@Override
		protected Document buildExceptionDocument(Exception e) {
			return AlertStatusResponseXmlSerializer.serializeExceptionAsAlertError(e);
		}
	}
	
	private AlertDataStore dataStore;
	
	protected AlertsDAO dao = null;
	private SchedulerService schedulerService;
	private Scheduler jobScheduler;
	private H2Config databaseConfig;
	
	private static final Duration CLEANUP_SCHEDULE_DELAY = Duration.ofMinutes(2); //Minimum Duration
	private static final Duration MINIMUM_CLEANUP_PERIOD = Duration.ofSeconds(30); //Minimum Duration
	private static final Duration DEFAULT_CLEANUP_PERIOD = Duration.ofMinutes(10); //Default Duration
	private Duration cleanupPeriod = DEFAULT_CLEANUP_PERIOD;
	private static final Duration DEFAULT_CLOSED_ALERT_TTL = Duration.ofSeconds(60); //Default Closed Alert TTL
	
	private static final Duration SEND_CREATE_EVENT_SCHEDULE_DELAY = Duration.ofMinutes(2); //Minimum Duration
	private static final Duration MINIMUM_SEND_CREATE_EVENT_SCHEDULE_PERIOD = Duration.ofSeconds(10); //Minimum Duration
	private static final Duration DEFAULT_SEND_CREATE_EVENT_SCHEDULE_PERIOD = Duration.ofSeconds(30); //Default Duration
	private Duration memberAlertCreatedDelayCheckPeriod = DEFAULT_SEND_CREATE_EVENT_SCHEDULE_PERIOD;
		
	private Duration closedAlertTimeToLive = DEFAULT_CLOSED_ALERT_TTL;
	
	private static Logger logger = LoggerFactory.getLogger(AlertsService.class);
	private static String CLEANUP_CLOSED_ALERT_JOB_ID = "cleanupJob";
	private static String SEND_DELAYED_ALERT_CREATE_JOB_ID = "sendDelayedCreateJob";
	private static String SEND_DELAYED_ALERT_CREATE_TRIGGER_ID = "sendDelayedCreateTrigger";
	
	/**
	 * Name of alert listener as specified by bean name
	 */
	public static final String ALERT_LISTENER_NAME = "ruleFlowAlertListener";
	
	/**
	 * @return the alertsDataSource
	 */
	public DataSource getAlertsDataSource() {
		if (dataStore == null) {
			setupDataStore();
		}
		
		return dataStore.getBasicDataSource();
	}
	
	/**
	 * @return the alertsDataStore
	 */
	public AlertDataStore getAlertsDataStore() {
		if (dataStore == null) {
			setupDataStore();
		}
		
		return dataStore;
	}
	
	/**
	 * @param newDataStore 
	 */
	public void setAlertsDataStore(AlertDataStore newDataStore) {
		this.dataStore = newDataStore;
	}

	
	
	@Override
	public void startService() throws EngineException {
		this.getAlertsDataStore();
		
		dao = (AlertsDAO) AlertsFactory.getAlertsDAO();
		dao.start();
		dao.registerListener(AlertsFactory.getAlertListener(ALERT_LISTENER_NAME));
				
		// Register the Clean up Job in the Quartz Scheduler. 
		// This task shall run every X minutes and clean all CLOSED alert that have been closed over X time
		// Laststatuschange + timetolive < time -> delete			
		startCleanUpJob();
		startSendDelayedCreateJob();
		//scheduleCloseInactiveAcknowledged();
		
		// Creating Listeners for RabbitMQ Alert Status Services
		super.startService();
		
	}
	
	/**
	 * Get current DAO
	 * @return AlertsDAO
	 */
	public AlertsDAO getAlertsDAO() {
		return dao;
	}

	
	private synchronized void setupDataStore() {
		AlertH2DataStore h2Store = new AlertH2DataStore();
		h2Store.init(databaseConfig);
		dataStore = h2Store;
	}
	
	@Override
	public void stopService() {
		stopSendDelayedCreateJob();
		stopCleanUpJob();
		
		super.stopService();
	}

	
	
	/* (non-Javadoc)
	 * @see com.smartnow.engine.services.Service#load(org.jdom2.Element)
	 */
	@Override
	public void load(Element e) throws EngineException {
		super.load(e);
		
		H2Config dbConfig = new H2Config();
		Element h2ConfigElement = e.getChild("H2Server", e.getNamespace());
		if (h2ConfigElement != null) {
			dbConfig.setServerEnabled(true);
			dbConfig.setPort(h2ConfigElement.getAttributeValue("port"));
			dbConfig.setBaseDir(h2ConfigElement.getTextTrim());
		}
		
		dbConfig.setConnectionURL(e.getChildText("ConnectionURL", e.getNamespace()));
		dbConfig.setCacheSize(e.getChildText("CacheSize", e.getNamespace()));
		databaseConfig = dbConfig;
		
		Element ttlElement = e.getChild("TimeToLive", e.getNamespace());
		if (ttlElement != null) {
			Duration duration = XmlSettingParser.parseDuration(ttlElement);
			setClosedAlertLiveTime(duration);
		}
		
		Element cleanupPeriodElement = e.getChild("CleanUpPeriod", e.getNamespace());
		if (cleanupPeriodElement != null) {
			Duration duration = XmlSettingParser.parseDuration(cleanupPeriodElement);
			setCleanupPeriod(duration);
		}
				
		Element memberAlertCreatedDelayCheckPeriodElement = e.getChild("AlertCreatedActionDelayCheckPeriod", e.getNamespace());
		if (cleanupPeriodElement != null) {
			Duration memberAlertCreatedDelayCheckPeriod = XmlSettingParser.parseDuration(memberAlertCreatedDelayCheckPeriodElement);
			setMemberAlertCreatedDelayCheckPeriod(memberAlertCreatedDelayCheckPeriod);
		}
	}
	
	
	
	protected void stopCleanUpJob(){
		try {
			jobScheduler.deleteJob(jobKey(CLEANUP_CLOSED_ALERT_JOB_ID, this.getName()));
		} catch (SchedulerException e) {
			logger.error("Failure stopping cleanup job for {} and group {} ", CLEANUP_CLOSED_ALERT_JOB_ID, this.getName(), e);
		}
	}
	
	protected void startCleanUpJob(){
		try {
			// Grab the Scheduler instance from the Factory 	   
	        CleanUpProcesses.setService(this);
	        
			// define the job and tie it to our HelloJob class
	        JobDetail job = newJob(CleanUpProcesses.class)
	            .withIdentity(CLEANUP_CLOSED_ALERT_JOB_ID, this.getName())
	            .build();
	        
	        long closedAlertTtlMilis = getClosedAlertTimeToLive().toMillis();
	        long cleanUpPeriodMilis = getCleanupPeriod().toMillis();
	        
	        job.getJobDataMap().put(CleanUpProcesses.CLOSED_ALERT_TTL_JOB_DATAMAP, String.valueOf(closedAlertTtlMilis));
	        
	        // Trigger the job to run now, and then repeat every cleanupPeriod
	        Date startTime = new Date(System.currentTimeMillis() + CLEANUP_SCHEDULE_DELAY.toMillis()); 
	        Trigger trigger = newTrigger()
	            .withIdentity("trigger1", "group1")
	            .startAt(startTime)
	            .withSchedule(simpleSchedule()
	            		.withIntervalInMilliseconds(cleanUpPeriodMilis)
	                    .repeatForever())            
	            .build();
	
	        // Tell quartz to schedule the job using our trigger
	        jobScheduler.scheduleJob(job, trigger);	
	        
	        logger.info("Scheduled cleanup every {} ms with TTL = {} ms begin at {}", cleanUpPeriodMilis, closedAlertTtlMilis, startTime);
		} catch (SchedulerException e) {
			logger.error("Scheduler Exception error", e);
		}       
	}
	
	protected void startSendDelayedCreateJob(){
		try {
			// define the job and tie it to our HelloJob class
	        JobDetail job = newJob(SendDelayedCreateNotificationJob.class)
	            .withIdentity(SEND_DELAYED_ALERT_CREATE_JOB_ID, this.getName())
	            .build();
	        
	        long sendDelayedCreateEventPeriodMilis = getMemberAlertCreatedDelayCheckPeriod().toMillis();
	        
	        // Trigger the job to run now, and then repeat every cleanupPeriod
	        Date startTime = new Date(System.currentTimeMillis() + SEND_CREATE_EVENT_SCHEDULE_DELAY.toMillis()); 
	        Trigger trigger = newTrigger()
	            .withIdentity(SEND_DELAYED_ALERT_CREATE_TRIGGER_ID, this.getName())
	            .startAt(startTime)
	            .withSchedule(simpleSchedule()
	            		.withIntervalInMilliseconds(sendDelayedCreateEventPeriodMilis)
	                    .repeatForever())            
	            .build();
	
	        // Tell quartz to schedule the job using our trigger
	        jobScheduler.scheduleJob(job, trigger);	
	        
	        logger.info("Scheduled create event sending every {} ms  begin at {}", sendDelayedCreateEventPeriodMilis, startTime);
		} catch (SchedulerException e) {
			logger.error("Scheduler create event sending Exception error", e);
		}
	}
	
	protected void stopSendDelayedCreateJob() {
		try {
			jobScheduler.deleteJob(jobKey(SEND_DELAYED_ALERT_CREATE_JOB_ID, this.getName()));
		} catch (SchedulerException e) {
			logger.error("Failure stopping SendDelayedCreate job for {} and group {} ", SEND_DELAYED_ALERT_CREATE_JOB_ID, this.getName(), e);
		}
	}

	@Override
	protected GenericRPCServiceConsumer getConsumer(Channel channel) {
		return new AlertServiceConsumer(channel);
	}
	
	public Logger getLogger() {
		return logger;
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
	 * @return the cleanupPeriod
	 */
	public final Duration getCleanupPeriod() {
		return cleanupPeriod;
	}

	/**
	 * Set cleanup period. Must not be less than MINIMUM_CLEANUP_PERIOD, otherwise will be set as DEFAULT_CLEANUP_PERIOD
	 * @param duration 
	 */
	public final void setCleanupPeriod(Duration duration) {
		if (duration == null) {
			logger.warn("Trying to set cleanup period as NULL");
			cleanupPeriod = DEFAULT_CLEANUP_PERIOD;
		} else if (duration.abs().compareTo(MINIMUM_CLEANUP_PERIOD) > 0) {
			cleanupPeriod = duration.abs();
		} else {
			logger.warn("Trying to set cleanup period less than minimum, requested is {} ms", duration.toMillis());
			cleanupPeriod = DEFAULT_CLEANUP_PERIOD;
		}
	}	

	/**
	 * @return the closedAlertTimeToLive
	 */
	public final Duration getClosedAlertTimeToLive() {
		return closedAlertTimeToLive;
	}
	
	/**
	 * Set length of time beofre closed alert can be cleaned from memory
	 * @param duration
	 */
	private final void setClosedAlertLiveTime(Duration duration) {
		if (duration == null) {
			logger.warn("Trying to set closed alert TTL as NULL");
			closedAlertTimeToLive = DEFAULT_CLOSED_ALERT_TTL;
		} else {
			closedAlertTimeToLive = duration.abs();
		}
	}
	
	
	/**
	 * @return the cleanupPeriod
	 */
	public final Duration getMemberAlertCreatedDelayCheckPeriod() {
		return memberAlertCreatedDelayCheckPeriod;
	}

	/**
	 * Set cleanup period. Must not be less than MINIMUM_CLEANUP_PERIOD, otherwise will be set as DEFAULT_CLEANUP_PERIOD
	 * @param duration 
	 */
	public final void setMemberAlertCreatedDelayCheckPeriod(Duration duration) {
		if (duration == null) {
			logger.warn("Trying to set cleanup period as NULL");
			memberAlertCreatedDelayCheckPeriod = DEFAULT_SEND_CREATE_EVENT_SCHEDULE_PERIOD;
		} else if (duration.abs().compareTo(MINIMUM_SEND_CREATE_EVENT_SCHEDULE_PERIOD) > 0) {
			memberAlertCreatedDelayCheckPeriod = duration.abs();
		} else {
			logger.warn("Trying to set cleanup period less than minimum, requested is {} ms", duration.toMillis());
			memberAlertCreatedDelayCheckPeriod = DEFAULT_SEND_CREATE_EVENT_SCHEDULE_PERIOD;
		}
	}	
}
