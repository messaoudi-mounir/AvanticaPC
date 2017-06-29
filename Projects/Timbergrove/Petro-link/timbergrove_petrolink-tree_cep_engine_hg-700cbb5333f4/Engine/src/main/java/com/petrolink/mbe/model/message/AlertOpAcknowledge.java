package com.petrolink.mbe.model.message;

/**
 * Snooze Action Record
 * @author aristo
 *
 */
public class AlertOpAcknowledge  extends AlertOperation implements IAlertOperation {
	
	@Override
	public String getOperationAction() {
		return "acknowledge";
	}
	
}
