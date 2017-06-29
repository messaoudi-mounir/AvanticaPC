package com.petrolink.mbe.directories.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Class for defining what AlertClassSubscription being active with a subscriber
 * @author aristo
 */
public class AlertClassSubscriber {

	private String subscriberId;
	private HashSet<AlertClassSubscription> subs = new HashSet<>();
	/**
	 * Constructor
	 */
	public AlertClassSubscriber() {
		
	}
	
	/**
	 * Get Subscription set for this instance of subscriber
	 * @return copy of Subscription
	 */
	public Set<AlertClassSubscription> getCopySubscriptions() {
		HashSet<AlertClassSubscription> currentSub = subs;
		HashSet<AlertClassSubscription> copy = new HashSet<>();
		copy.addAll(currentSub);
		return copy;
	}

	/**
	 * Add subscription for the alert class
	 * @param subscription
	 */
	public void addSubscription(AlertClassSubscription subscription) {
		if (subscription == null) {return;}
		subs.add(subscription);
	}
	
	/**
	 * Removes the specified Subscription from this subscriber if it is present. 
	 * More formally, removes an element e such that (o==null ? e==null : o.equals(e)), if this subscriber contains such a subscription. 
	 * Returns true if this subscriber contained the element (or equivalently, if this subscriber changed as a result of the call). 
	 * (This set will not contain the element once the call returns.)
	 * @param subscription Subscription to be removed
	 * @return true if the subscriber contained the specified subscription
	 */
	public boolean removeSubscription(AlertClassSubscription subscription) {
		if (subscription == null) { return false; }
		return subs.remove(subscription);
	}
	
	/**
	 * @return the subscriberId
	 */
	public final String getSubscriberId() {
		return subscriberId;
	}
	
	/**
	 * @param subscriberId the subscriberId to set
	 */
	public final void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}
}
