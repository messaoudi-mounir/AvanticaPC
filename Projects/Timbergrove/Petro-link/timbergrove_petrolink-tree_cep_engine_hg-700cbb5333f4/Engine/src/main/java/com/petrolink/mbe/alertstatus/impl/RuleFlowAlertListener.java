package com.petrolink.mbe.alertstatus.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnow.alertstatus.impl.InvokeFlowAlertListener;
import com.smartnow.engine.exceptions.EngineException;

/**
 * RuleFlowAlertListener extension for InvokeFlow
 * @author aristo
 *
 */
public class RuleFlowAlertListener extends InvokeFlowAlertListener {

	ConcurrentHashMap<String, Set<String>> onInvestigationDirectory = new ConcurrentHashMap<String, Set<String>>();
	Logger logger = LoggerFactory.getLogger(com.smartnow.alertstatus.Alert.class); //Same logger as parent

	/**
	 * Called when Investigate Ack is called
	 * @param alert
	 */
	public void onInvestigate(AlertImpl alert) {
		if (this.onInvestigationDirectory.containsKey(alert.getClassId())) {
			Set<String> events = this.onInvestigationDirectory.get(alert.getClassId());
			for (String eventClassId : events) {
				try {
					logger.trace("Invoking alert flow " + alert.getClassId() + " for event On Investigate");
					this.invokeFlow(eventClassId, alert, 'A');
				} catch (EngineException arg4) {
					this.logger.error("Error invoking flow on Investigate", arg4);
				}
			}
		}

	}
	
	/**
	 * Alert Class id
	 * @param alertClassId
	 * @param eventClassId
	 */
	public void registerOnInvestigate(String alertClassId, String eventClassId) {
		Set<String> events = this.onInvestigationDirectory.get(alertClassId);
		if (events == null) {
			events = new HashSet<String>();
			this.onInvestigationDirectory.put(alertClassId, events);
		}

		synchronized (events) {
			events.add(eventClassId);
		}
	}
}
