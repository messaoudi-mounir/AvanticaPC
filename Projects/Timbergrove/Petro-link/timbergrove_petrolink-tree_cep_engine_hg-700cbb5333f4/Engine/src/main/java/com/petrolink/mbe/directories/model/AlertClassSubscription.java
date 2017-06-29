package com.petrolink.mbe.directories.model;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.petrolink.mbe.rulesflow.RuleFlow;

/**
 * Alert Class listener, able to provide indexing to Rule which listen to specific Alert Class
 * @author aristo
 *
 */
public class AlertClassSubscription {

	private ConcurrentHashMap<String, RuleFlow> flows = new ConcurrentHashMap<>();
	private String alertClass;
	/**
	 * Construct alert class subscription for specified alertClass
	 * @param alertClassTarget
	 */
	public AlertClassSubscription (String alertClassTarget) {
		 alertClass = alertClassTarget;
	}
	
	/**
	 * Get currently subscribing flows
	 * @return Collection of rule which currently subscring to Alert Class
	 */
	public Collection<RuleFlow> getSubscribingFlows() {
		return flows.values();
	}
	
	/**
	 * Make Subscription for the rule
	 * @param rule
	 */
	public void register(RuleFlow rule) {
		flows.putIfAbsent(rule.getUniqueId(), rule);
	}
	
	/**
	 * Remove Subscription for the rule
	 * @param rule
	 */
	public void unregister(RuleFlow rule) {
		unregister(rule.getUniqueId());
	}
	
	/**
	 * Remove Subscription for the rule
	 * @param ruleId the Id of the rule
	 */
	public void unregister(String ruleId) {
		flows.remove(ruleId);
	}
	
	/**
	 * Check whether the AlertClassSubscription is currently empty
	 * @return True if the subscription is currently empty
	 */
	public boolean isEmpty() {
		return flows.isEmpty();
	}

	/**
	 * @return the alertClass
	 */
	public final String getAlertClassId() {
		return alertClass;
	}
}
