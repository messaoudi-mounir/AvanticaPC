package com.petrolink.mbe.actions;

import java.io.BufferedWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.EngineService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.scheduler.service.SchedulerService;

/**
 * Evaluation Log generation
 * Expects the evaluation result, index and value of the primary channel on the context as
 * 	- result (Evaluation Result boolean)
 * 	- index (The Channel Index)
 * 	- value (The Channel Value)
 * @author paul
 * @author langj
 */

public class EvaluationLog extends ChannelLogAction {
	private static Logger logger = LoggerFactory.getLogger(EvaluationLog.class);
	private static final Double BOXED_ONE = 1.0;
	private static final Double BOXED_ZERO = 0.0;
	private static final Duration DEFAULT_BATCH_TIME = Duration.ofSeconds(10);
	
	private Duration batchTime = DEFAULT_BATCH_TIME;
	private final SchedulerService schedulerService;
	private final ArrayList<DataPoint> batchedData = new ArrayList<>(); // access to this must be synchronized due to quartz job
	private JobDetail updateJobDetail;
	private int updateJobDetailCounter;
	
	/**
	 * Constructor.
	 */
	public EvaluationLog() {
		//operationMode = 3; // HACK force test mode for now so we don't bombard the PV API for every use case
		schedulerService = ServiceAccessor.getSchedulerService();
		
		EngineService engineService = ServiceAccessor.getEngineService();
		if (engineService != null) {
			String batchTimeString = engineService.getUserProperties().getProperty("EvaluationLogBatchTime");
			if (batchTimeString != null)
				batchTime = Duration.ofMillis(Long.parseLong(batchTimeString));
		}
	}
	
	@Override
	protected String getChannelLogType() {
		return "EvaluationLog";
	}
	
	@Override
	protected String getDirectoryPropertyName() {
		return "EvaluationLogs";
	}
	
	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		// PERF: Evaluation logging is a potential hot path. Minimize allocations as much as possible.
		
		Object index = getIndex(context);
		Double resultObject = getEvaluationResult(context) ? BOXED_ONE : BOXED_ZERO;
		
		if (schedulerService != null) {
			Scheduler scheduler = schedulerService.getScheduler();
			
			synchronized (batchedData) {
				batchedData.add(new DataPoint(index, resultObject));
				
				// Schedule a job that will execute in the future to update channels with all batched data that has accumulated
				// between now and the start time
				if (updateJobDetail == null) {
					RuleFlow flow = getRuleFlow();
					String group = "EvaluationLog-" + getSequence() + "-" + flow.getUniqueId();
					Instant startTime = Instant.now().plus(batchTime);

					logger.trace("Scheduling batch update for {} at {}", flow.getUniqueId(), startTime);
					
					int number = updateJobDetailCounter++;
					
					JobDataMap dataMap = new JobDataMap();
					dataMap.put("action", this);
					
					updateJobDetail = JobBuilder.newJob(BatchUpdateJob.class)
							.storeDurably(false)
							.usingJobData(dataMap)
				            .withIdentity("BatchUpdateJob" + number, group)
							.build();
					
					Trigger trigger = TriggerBuilder.newTrigger()
							.forJob(updateJobDetail)
				            .withIdentity("BatchUpdateTrigger" + number, group)
							.startAt(Date.from(startTime))
							.build();
					
					try {
						scheduler.scheduleJob(updateJobDetail, trigger);
					} catch (SchedulerException e) {
						throw new EngineException("update job schedule failed", e);
					}
				}
			}
		}
		else {
			updateChannelsAsync(index, resultObject);
		}
		
		return SUCCESS;
	}
	
	@Override
	protected void writeTestLogHeader(BufferedWriter output) throws Exception {
		output.write("Timestamp");
		output.write(',');
		output.write("Index");
		output.write(',');
		output.write("Result");
		output.newLine();
	}
	
	@Override
	protected void writeTestLog(Map<String, Object> context, BufferedWriter output) throws Exception {
		boolean result = getEvaluationResult(context);
		Object index = getIndex(context);
		//Object value = getValue(context);
		
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

		output.write(now.toString());		
		output.write(',');
		output.write(index.toString());		
		output.write(',');
		output.write(result ? '1' : '0');
		output.newLine();
	}

	private void executeUpdate(JobDetail jobDetail) {
		logger.trace("Executing scheduled update");
		
		ArrayList<DataPoint> dataCopy;
		synchronized (batchedData) {
			dataCopy = new ArrayList<>(batchedData);
			batchedData.clear();

			// UpdateJobDetail must be cleared to allow future jobs to be scheduled
			if (updateJobDetail != null) {
				assert updateJobDetail.getKey().equals(jobDetail.getKey());
				updateJobDetail = null;
				// Job will be deleted from quartz automatically because they're not durable
			}
		}
		
		updateChannelsAsync(dataCopy);
	}
	
	/**
	 * Job containing Batch Update for the Evaluation log
	 * @author aristo
	 *
	 */
	public static class BatchUpdateJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			EvaluationLog action = (EvaluationLog) context.getJobDetail().getJobDataMap().get("action");
			action.executeUpdate(context.getJobDetail());
		}
	}
}
