package com.petrolink.mbe.actions;

import static org.quartz.JobKey.jobKey;

import java.util.Map;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.services.ServiceAccessor;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.scheduler.service.SchedulerService;

/**
 * 
 * @author paul
 *
 */
public class DeleteScheduleInvokeFlow extends MBEAction {
	private static final Logger logger = LoggerFactory.getLogger(DeleteScheduleInvokeFlow.class);
	
	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		SchedulerService service = ServiceAccessor.getSchedulerService();
		
		try {
			service.getScheduler().deleteJob(jobKey(this.getName()+getRuleFlow().getUniqueId(), getRuleFlow().getRuleName()));
		} catch (SchedulerException e) {
			logger.error("unable to unschedule the Job");
		}
		return SUCCESS;
	}

	@Override
	protected int executeTestAction(Map<String, Object> context) throws EngineException {
		// No action required
		return SUCCESS;
	}

	@Override
	public void init(Map<String, Object> context) throws EngineException {
	}

	@Override
	public void finalize(Map<String, Object> context) throws EngineException {
	}

}
