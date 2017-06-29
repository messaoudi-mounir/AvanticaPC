package com.petrolink.mbe.alertstatus.impl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * Configuration for Delay alert
 * @author aristo
 *
 */
public class DelayAlertAction {
	private HashMap<String, Long> delaySettingByActionName;
	
	/**
	 * Constructor
	 */
	public DelayAlertAction() {
		delaySettingByActionName = new HashMap<>();
	}
	
	/**
	 * Add delay for specified action
	 * @param actionName
	 * @param delayInMs
	 */
	public void addDelay(String actionName, Long delayInMs) {
		delaySettingByActionName.put(actionName, delayInMs);
	}
	
	/**
	 * Remove delay for specified action
	 * @param actionName
	 */
	public void removeDelay(String actionName) {
		delaySettingByActionName.remove(actionName);
	}
	
	/**
	 * Check whether this setting has no delay being set
	 * @return false if one of action has delay setting, true otherwise
	 */
	public boolean isNoDelay() {
		Collection<Long> settings = delaySettingByActionName.values();
		for (Long setting : settings) {
			if (setting.longValue() > 0L) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether there is delay on create action
	 * @return true if delay larger than 0
	 */
	public boolean isDelayOnCreateActions() {
		return getMilisDelayOnCreateActions() > 0L;
	}
	
	/**
	 * Get delay on create action
	 * @return number of milis in action which should be delayed
	 */
	public long getMilisDelayOnCreateActions() {
		return getMilisDelay("OnCreateActions");
	}
	
	/**
	 * Get delay on Specified action
	 * @param actionName Specified Action Name
	 * @return number of milis in action which should be delayed
	 */
	public long getMilisDelay(String actionName) {
		if(StringUtils.isBlank(actionName)) {
			return 0L;
		}
		
		Long delay = delaySettingByActionName.get(actionName);
		if (delay != null) {
			return delay.longValue();
		}
		return 0L;
	}
	
	/**
	 * 
	 * @param currentTimeInstant 
	 * @param alertImpl
	 * @return Whether create Event should still be delayed
	 */
	public final boolean isCreateEventShouldBeDelayed(Instant currentTimeInstant, long alertCreateTime) {
		long delay = getMilisDelayOnCreateActions();
		if (delay > 0 && currentTimeInstant != null) {
			
			long delayTimeout = alertCreateTime + delay;
			long currentTime = currentTimeInstant.toEpochMilli();
			if (currentTime < delayTimeout) {
				return true;
			}
		}
		
		return false;
	}
}
