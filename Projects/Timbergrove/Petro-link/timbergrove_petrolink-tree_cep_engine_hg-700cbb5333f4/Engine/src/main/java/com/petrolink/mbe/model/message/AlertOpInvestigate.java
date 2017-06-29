package com.petrolink.mbe.model.message;

/**
 * Investigate alert record
 * @author aristo
 *
 */
public class AlertOpInvestigate extends AlertOperation implements IAlertOperation {

	@Override
	public String getOperationAction() {
		return "investigate";
	}

}
