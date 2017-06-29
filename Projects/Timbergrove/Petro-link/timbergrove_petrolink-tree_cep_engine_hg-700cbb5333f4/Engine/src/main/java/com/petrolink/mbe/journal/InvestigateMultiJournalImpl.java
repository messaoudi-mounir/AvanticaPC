package com.petrolink.mbe.journal;

import java.time.Instant;
import java.util.List;

import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.journal.impl.AlertJournalImpl;

/**
 * Alert Journal implementation
 * @author aristo
 *
 */
public class InvestigateMultiJournalImpl extends MultiAlertJournal {

	/**
	 * Journal type for Investigate Single
	 */
	public static final String INVESTIGATE_SINGLE_TYPE="investigation";
	
	/**
	 * Journal type for Investigate Multiple
	 */
	public static final String INVESTIGATE_MULTI_TYPE="multiple_investigation";
	
	/**
	 * Constructor
	 * @param alert
	 */
	public InvestigateMultiJournalImpl() {
		super(INVESTIGATE_SINGLE_TYPE);
	}
	
	/**
	 * Set investigate record
	 * @param alerts 
	 * @param principal
	 * @param timestamp
	 */
	public void setInvestigate(List<Alert> alerts, String principal, Instant timestamp) {
		if (alerts == null || alerts.isEmpty()) {
			throw new IllegalArgumentException("Alerts may not be null or empty in Acknowledgement");
		}
		
		this.principal = principal;
		this.timestamp = timestamp;
		
		setAlerts(alerts);
		if (isMultipleAlerts()) {
			setType(INVESTIGATE_MULTI_TYPE);
		} else {
			setType(INVESTIGATE_SINGLE_TYPE);
		}
	}

}
