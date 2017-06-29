package com.petrolink.mbe.actions;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import org.jdom2.Element;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.services.ServiceAccessor;
import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.AlertsFactory;
import com.smartnow.alertstatus.impl.InvokeFlowAlertListener;
import com.smartnow.engine.Engine;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.event.EventStore;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.executiongroups.ExecutionGroup;
import com.smartnow.engine.nodes.Node;
import com.smartnow.engine.scheduler.service.SchedulerService;
import com.smartnow.engine.util.MVELUtil;
import com.smartnow.engine.util.VariableResolverFactoryContext;

/**
 * @author paul
 *
 */
public class ScheduleInvokeFlow extends MBEAction implements Job {
	private static final Logger logger = LoggerFactory.getLogger(ScheduleInvokeFlow.class);
	private String invokeFlowId;
	private String cronTaskExpression;
	private Serializable startAtScript = null;
	private String baseDate;
	
	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		try {
			// Evaluate expression to define 
			OffsetDateTime datetime = null;
			switch (baseDate) {
			case "alert.created":
				datetime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(getAlert(context).getCreated()), ZoneId.of("UTC"));
				break;
			case "alert.lastocurrance":
				datetime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(getAlert(context).getLastOccurrence()), ZoneId.of("UTC"));
				break;
			case "now":
				datetime = OffsetDateTime.now();
				break;
			}
					
			VariableResolverFactoryContext ctx = new VariableResolverFactoryContext(context);
			ctx.put("datetime", datetime);

			MVELUtil.evaluate(startAtScript, ctx, logger);

			datetime = (OffsetDateTime) ctx.get("datetime");
			
			Date startTime = Date.from(datetime.toInstant());

			// Grab the Scheduler instance from the Factory 
			SchedulerService service = ServiceAccessor.getSchedulerService();
	        Scheduler scheduler = service.getScheduler();
	   	        
			// define the job and tie it to our HelloJob class
	        JobDetail job = newJob(ScheduleInvokeFlow.class)
	            .withIdentity(this.getName()+getRuleFlow().getUniqueId(), getRuleFlow().getRuleName())
	            .build();
	        
	        job.getJobDataMap().put("flowId", this.invokeFlowId);
	        job.getJobDataMap().put("alertUUID", getAlert(context).getUuid());

	        // Trigger the job to run now, and then repeat every 40 seconds
	        String triggerId = getRuleFlow().getUniqueId() + "-" + this.invokeFlowId;

	        
	        Trigger trigger = null;
	        if (cronTaskExpression != null) {
		        trigger = newTrigger()
			            .withIdentity(triggerId, getRuleFlow().getRuleName())
			            .startAt(startTime)
			            .withSchedule(cronSchedule(cronTaskExpression))
			            .build();
	        } else {
		        trigger = newTrigger()
			            .withIdentity(triggerId, getRuleFlow().getRuleName())
			            .startAt(startTime)
			            .build();	        	
	        }
	        
	        // Tell quartz to schedule the job using our trigger
	        scheduler.scheduleJob(job, trigger);
	
	        // and start it off
	        scheduler.start();
		} catch (SchedulerException e) {
			logger.error("Scheduler Exception error", e);
		}       
		
		return 0;
	}

	@Override
	protected int executeTestAction(Map<String, Object> context) throws EngineException {
		return 0;
	}

	@Override
	public void init(Map<String, Object> context) throws EngineException {
	}

	@Override
	public void finalize(Map<String, Object> context) throws EngineException {
	}

	/* (non-Javadoc)
	 * @see com.smartnow.engine.nodes.actions.Action#load(org.jdom2.Element, com.smartnow.engine.nodes.Node)
	 */
	@Override
	public void load(Element e, Node parent) throws EngineException {
		super.load(e, parent);
		
		if (e.getChild("CronExpression", e.getNamespace()) != null) {
			this.cronTaskExpression = e.getChildText("CronExpression", e.getNamespace());			
		}
		
		if (e.getChild("StartAt", e.getNamespace()) != null) {
			this.startAtScript = MVELUtil.compileScript(e.getChildText("StartAt", e.getNamespace()));

			this.baseDate = e.getChild("StartAt", e.getNamespace()).getAttributeValue("baseDate");
		} else {
			throw new EngineException("Expecting StartAt parameter");
		}
		
		if (e.getChild("InvokeFlowId", e.getNamespace()) != null) {
			this.invokeFlowId = e.getChildText("InvokeFlowId", e.getNamespace());
		}
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		this.invokeFlowId = dataMap.getString("flowId");
		Alert alert = AlertsFactory.getAlertsDAO().getAlert(dataMap.getString("alertUUID"));
		
		try {
			invokeFlow(this.invokeFlowId, alert, InvokeFlowAlertListener.EVENT_TYPE_ALERT);
		} catch (EngineException e) {
			logger.error("Error invoking flow with id {}", this.invokeFlowId);
		}
	}

	/**
	 * @param eventClassId
	 * @param content
	 * @param contentType
	 * @throws EngineException
	 */
	public void invokeFlow(String eventClassId, Object content, char contentType) throws EngineException {
		logger.trace("Invoking Alert Flow with id {}", eventClassId );

		ExecutionGroup executionGroup = Engine.getInstance().getActiveFlow(eventClassId).getExecGroup();
		EventStore store = executionGroup.getEventStore();
		Event newEvent = new Event(store);
		newEvent.setClassId(eventClassId);
		newEvent.setSource("ALERT_STATUS");

		newEvent.setContent(content, contentType);
			
		executionGroup.submitEvent(newEvent);
	}
}
