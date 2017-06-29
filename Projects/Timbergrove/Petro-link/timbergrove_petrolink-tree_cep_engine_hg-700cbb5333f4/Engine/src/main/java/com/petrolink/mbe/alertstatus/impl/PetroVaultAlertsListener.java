package com.petrolink.mbe.alertstatus.impl;

import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.AlertJournal;
import com.smartnow.alertstatus.AlertListener;

/**
 * PetroVault HD custom Alert Listener
 * @author paul
 *
 */
public class PetroVaultAlertsListener implements AlertListener {

	@Override
	public void onCreate(Alert alert) {
		// TODO Listener on create
		
	}

	@Override
	public void onUpdate(Alert previous, Alert alert) {
		// TODO Listener on Update
		
	}

	@Override
	public void onStatusChange(Alert alert, int previousStatus) {
		// TODO Listener on Status Change
		
	}

	@Override
	public void onAcknowledge(Alert alert) {
		// TODO Listener on Acknowledge
		
	}

	@Override
	public void onComment(Alert alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSnooze(Alert alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnSnooze(Alert alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onJournalEntry(AlertJournal journal) {
		// TODO Auto-generated method stub
		
	}

}
