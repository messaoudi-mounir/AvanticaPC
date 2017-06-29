package com.petrolink.mbe.alertstatus.impl;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import com.smartnow.alertstatus.Alert;

/**
 * Helper class for processing Alert
 * @author aristo
 *
 */
public final class AlertUtils {
	private AlertUtils() {
		// Prevent instatiation
	}
	
	/**
	 * Create a map based on class id of particular alert class
	 * @param results
	 * @return >List containing Affected alert from result
	 */
	public static ArrayListValuedHashMap<String, Alert> getMappedAlertsFromAlerts(Collection<Alert> results){
		if (results == null) {
			return null;
		}
		
		ArrayListValuedHashMap<String, Alert> alertsByClassId = new ArrayListValuedHashMap<>();
		for (Alert affectedAlert : results) {
			if(affectedAlert != null) {
				alertsByClassId.put(affectedAlert.getClassId(), affectedAlert);
			}
		}
		return alertsByClassId;
	}
	
	/**
	 * Extract Affected Alerts from command results
	 * @param results
	 * @return ArrayListValuedHashMap containing Affected alert from result grouped by classId
	 */
	public static ArrayListValuedHashMap<String, Alert> getMappedAlertsFromResults(Collection<AlertRequestResult> results){
		if (results == null) {
			return null;
		}
		
		ArrayListValuedHashMap<String, Alert> alertsByClassId = new ArrayListValuedHashMap<>();
		for (AlertRequestResult alertRequestResult : results) {
			Alert affectedAlert = alertRequestResult.getAffectedAlert();
			if(affectedAlert != null) {
				alertsByClassId.put(affectedAlert.getClassId(), affectedAlert);
			}
		}
		return alertsByClassId;

	}
	
	
}
