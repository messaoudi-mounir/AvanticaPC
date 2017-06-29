package com.petrolink.mbe.model.message;

/**
 * Snooze Action Record
 * @author aristo
 *
 */
public class AlertOpSnooze  extends AlertOperation implements IAlertOperation {
	
	@Override
	public String getOperationAction() {
		return "snooze";
	}
	
}
