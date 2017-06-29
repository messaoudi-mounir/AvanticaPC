package com.petrolink.mbe.directories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.directories.model.AlertClassSubscriber;
import com.petrolink.mbe.directories.model.AlertClassSubscription;
import com.petrolink.mbe.model.message.AlertCepEvent;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.services.Service;

/**
 * Directory for Alert Processor Rules
 * @author aristo
 *
 */
public class AlertProcessorDirectory extends Service{

	private static final Object DIR_LOCK = new Object();
	
	private static Logger logger = LoggerFactory.getLogger(AlertProcessorDirectory.class);
	
	private final ConcurrentHashMap<String, AlertClassSubscriber> allRules = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, AlertClassSubscription> alertClassSubscriptions = new ConcurrentHashMap<>();
	
	@Override
	public void startService() throws EngineException {
		logger.info("Started AlertProcessorDirectory: {}", getName());
	}

	@Override
	public void stopService() {
		
		logger.info("Stopped AlertProcessorDirectory: {}", getName());
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Create listener when needed
	 * @param inputAlertClassId
	 * @return
	 */
	private AlertClassSubscription createAlertClassListener(String inputAlertClassId) {
		String alertClassId = inputAlertClassId.toLowerCase();
		AlertClassSubscription listener = new AlertClassSubscription(alertClassId);
		return listener;
	}
	
		
	/**
	 * Register a rule with its directory
	 * @param rule
	 */
	public void registerRule(RuleFlow rule) {
		if (rule == null) return;
		String id = rule.getUniqueId();
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("when registering rule, rule must have id");
		}
		synchronized (DIR_LOCK) {
			//Unregister first
			AlertClassSubscriber oldRule = allRules.get(id);
			if (oldRule != null) {
				logger.debug("found older rule with same id, removing older rule");
				unregisterRule(id);
			}
			
			//Reregister
			AlertClassSubscriber register = new AlertClassSubscriber();
			register.setSubscriberId(rule.getUniqueId());
			Set<String> alertClassIds = rule.getAlertClassDependencies().keySet();
			for (String dependentAlertClassId : alertClassIds) {
				String alertClassId = dependentAlertClassId.toLowerCase();
				AlertClassSubscription listener = alertClassSubscriptions.computeIfAbsent(alertClassId, cid -> createAlertClassListener(cid));
				listener.register(rule);
				register.addSubscription(listener);
			}

			//Register to all rule
			allRules.put(id, register);
		}
	}
	
	/**
	 * Clear a rule by removing it from its directory. Also deletes a well if it no longer has any rules
	 * @param rule
	 */
	public void unregisterRule(RuleFlow rule) {
		if (rule == null) return;
		String id = rule.getUniqueId();
		unregisterRule(id);
	}
	
	/**
	 * Clear a rule by removing it from its directory. Also deletes a well if it no longer has any rules
	 * @param id Unique id of the rule
	 */
	public void unregisterRule(String id) {
		if (StringUtils.isBlank(id)) {
			throw new IllegalArgumentException("when unregistering rule, rule must have id");
		}
		synchronized (DIR_LOCK) {
			AlertClassSubscriber register = allRules.get(id);
			
			Set<AlertClassSubscription> listeners = register.getCopySubscriptions();
			for (AlertClassSubscription listener : listeners) {
				listener.unregister(id);
			}
			
			for (AlertClassSubscription listener : listeners) {
				if (listener.isEmpty()) {
					alertClassSubscriptions.remove(listener.getAlertClassId());
				}
			}
			
			
			allRules.remove(id);
		}
	}
	
	
	/**
	 * Temporary mechanism to dispatch cepEvent to all listener of the event
	 * @param cepEvent
	 */
	public void dispatch(AlertCepEvent cepEvent) {
		
		String alertClassId = cepEvent.getAlertClassId();
		if (StringUtils.isBlank(alertClassId)) {
			logger.debug("Dispatching blank {}", cepEvent);
			return;
		}
		
		int receiverCount =0;
		Collection<RuleFlow> flows = getSubscribingFlows(alertClassId);
		if(flows != null) {
			for(RuleFlow flow: flows) {
				flow.submitExecuteEvent(cepEvent);
				receiverCount++;
			}
		}
		
		logger.debug("Dispatched {} to {}", cepEvent, receiverCount);
	}
	
	/**
	 * Get collection of flow which subscribes specified alert Class
	 * @param alertClassIdTarget
	 * @return Collection of flow which subscribes specified alert Class. Empty list if no listener found.
	 */
	public Collection<RuleFlow> getSubscribingFlows(String alertClassIdTarget) {
		if (StringUtils.isNotBlank(alertClassIdTarget)) {
			String alertClassId = alertClassIdTarget.toLowerCase();
			AlertClassSubscription listener =	alertClassSubscriptions.get(alertClassId.toLowerCase());
			if (listener != null) {
				return listener.getSubscribingFlows();
			} 
		}
		return new ArrayList<RuleFlow>();
		
	}
	
}
