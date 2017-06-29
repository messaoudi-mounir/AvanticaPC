package com.petrolink.mbe.alertstatus.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.smartnow.alertstatus.Alert;

/**
 * Request Result, from DAO
 * @author aristo
 *
 */
public class AlertRequestResult {

	/**
	 * Constructor
	 */
	public AlertRequestResult() {
		
	}

	private Alert affectedAlert;
	private int originalStatus;

	/**
	 * Set original Properties from Alert, useful for comparing
	 * @param alert
	 */
	public void setOriginalProperties(Alert alert) {
		originalStatus = alert.getStatus();
	}

	/**
	 * @return the affectedAlert
	 */
	public final Alert getAffectedAlert() {
		return affectedAlert;
	}

	/**
	 * @param affectedAlert the affectedAlert to set
	 */
	public final void setAffectedAlert(Alert affectedAlert) {
		this.affectedAlert = affectedAlert;
	}

	/**
	 * @return the originalStatus
	 */
	public final int getOriginalStatus() {
		return originalStatus;
	}
	

	/**
	 * Extract Affected Alerts from command results
	 * @param results
	 * @return >List containing Affected alert from result
	 */
	public static ArrayList<Alert> getAlertsFromResults(Collection<AlertRequestResult> results){
		ArrayList<Alert> updatedAlerts = new ArrayList<>();
		for (AlertRequestResult alertCommandResult : results) {
			updatedAlerts.add(alertCommandResult.getAffectedAlert());
		}
		return updatedAlerts;	
	}
	
	
}
