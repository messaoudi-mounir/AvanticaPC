package com.petrolink.mbe.journal;

import java.util.ArrayList;
import java.util.List;

import org.h2.util.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.petrolink.mbe.codec.SmartNowCodec;
import com.petrolink.mbe.model.message.AlertSimpleMetadata;
import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.journal.impl.AlertJournalImpl;

/**
 * Multi Alert Journal implementation
 * @author aristo
 *
 */
public class MultiAlertJournal extends AlertJournalImpl {
	private JSONObject jsonDetail;
	private boolean multipleAlerts;
	
	/**
	 * Define type for multi Alert journal
	 * @param type
	 */
	public MultiAlertJournal(String type) {
		super(null, type);
		jsonDetail = new JSONObject();
		setDetails(jsonDetail);
	}
	
	@Override
	public void setAlert(Alert alert) {
		ArrayList<AlertSimpleMetadata> simpleAlerts = new ArrayList<>();
		AlertSimpleMetadata simpleAlert = SmartNowCodec.toAlertSimpleMetadata(alert);
		simpleAlerts.add(simpleAlert);

		super.setAlert(alert);
		updateAlertRelatedProperties(simpleAlerts);
	}
	
	/**
	 * Set alerts affected by this journal
	 * @param alerts
	 */
	public void setAlerts(List<Alert> alerts) {
		ArrayList<AlertSimpleMetadata> simpleAlerts = SmartNowCodec.toAlertSimpleMetadata(alerts);
		setAlertSimpleMetadata(simpleAlerts);
		
		if (alerts.size() == 1) {
			Alert singleAlert = alerts.get(0);
			super.setAlert(singleAlert);
		} 
	}
	
	
	/**
	 * Set Simple Alerts
	 * @param simpleAlerts
	 */
	public void setAlertSimpleMetadata(List<AlertSimpleMetadata> simpleAlerts) {
		
		if ((simpleAlerts == null) || simpleAlerts.isEmpty()) {
			if (simpleAlerts == null) {
				jsonDetail.remove("alerts");
			} else {
				jsonDetail.put("alerts", new JSONArray());
			}
			
		} else {
			JSONArray alertArray = new JSONArray(simpleAlerts);
			jsonDetail.put("alerts", alertArray);
		}
		updateAlertRelatedProperties(simpleAlerts);
	}
	
	

	/**
	 * @return the jsonDetail
	 */
	public final JSONObject getJsonDetail() {
		return jsonDetail;
	}

	/**
	 * @return the multipleAlert
	 */
	public final boolean isMultipleAlerts() {
		return multipleAlerts;
	}

	/**
	 * @param multipleAlert Whether the alert has more than one alert
	 */
	private final void setMultipleAlert(boolean multipleAlert) {
		multipleAlerts = multipleAlert;
	}
	
	/**
	 * Update alert related properties
	 * @param simpleAlerts
	 */
	private void updateAlertRelatedProperties(List<AlertSimpleMetadata> simpleAlerts) {
		if ((simpleAlerts == null) || simpleAlerts.isEmpty()) {
			setMultipleAlert(false);
			setAlertClassId(null);
			super.setAlert(null);
		} else if (simpleAlerts.size() == 1) {
			setMultipleAlert(false);
			AlertSimpleMetadata simpleAlert = simpleAlerts.get(0);
			setAlertClassId(simpleAlert.getClassId());
		} else if (simpleAlerts.size() > 1 ){
			setMultipleAlert(true);
			//Set Alert Class if all alert have same alert Class
			AlertSimpleMetadata simpleAlert = simpleAlerts.get(0);
			String cid = simpleAlert.getClassId();
			for (AlertSimpleMetadata alertSimpleMetadata : simpleAlerts) {
				if (!StringUtils.equals(cid, alertSimpleMetadata.getClassId())) {
					cid = null;
					break;
				}
			}
			setAlertClassId(cid);
			super.setAlert(null);
		}
	}
}
