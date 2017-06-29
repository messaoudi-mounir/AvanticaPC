package com.petrolink.mbe.journal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.petrolink.mbe.codec.SmartNowCodec;
import com.petrolink.mbe.model.message.AlertSimpleMetadata;
import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.journal.impl.AlertJournalImpl;
import com.smartnow.alertstatus.journal.impl.DefaultJournalTypes;

/**
 * Multi Alert Journal implementation for Acknowledgement
 * @author aristo
 *
 */
public class AcknowledgeMultiJournalImpl  extends MultiAlertJournal{

	/**
	 * Journal type for Acknowledgement Single
	 */
	public static final String ACKNOWLEDGE_SINGLE_TYPE = DefaultJournalTypes.ACKNOWLEDGE;
	/**
	 * Journal type for Acknowledgement Multiple
	 */
	public static final String ACKNOWLEDGE_MULTI_TYPE="multiple_acknowledgement";
	
	/**
	 * 
	 */
	public AcknowledgeMultiJournalImpl() {
		super(ACKNOWLEDGE_SINGLE_TYPE);
	}
	
	/**
	 * Set Acknowledgement for specified Alerts
	 * @param alerts
	 * @param principal
	 * @param timestamp
	 */
	public void setAcknowledge(List<Alert> alerts,String principal, Instant timestamp) {
		if (alerts == null || alerts.isEmpty()) {
			throw new IllegalArgumentException("Alerts may not be null or empty in Acknowledgement");
		}
		
		this.principal = principal;
		this.timestamp = timestamp;

		setAlerts(alerts);
		if (isMultipleAlerts()) {
			setType(ACKNOWLEDGE_MULTI_TYPE);
		} else {
			setType(ACKNOWLEDGE_SINGLE_TYPE);
		}
	} 

}
