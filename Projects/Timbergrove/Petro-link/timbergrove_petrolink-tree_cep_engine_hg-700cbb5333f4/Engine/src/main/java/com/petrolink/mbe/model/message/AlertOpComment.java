package com.petrolink.mbe.model.message;
/**
 * Recording A Comment against alert
 * @author aristo
 *
 */
public class AlertOpComment extends AlertOperation implements IAlertOperation {
	String comment;
	
	/**
	 * @return the comment
	 */
	public final String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public final void setComment(String comment) {
		this.comment = comment;
	}
	@Override
	public String getOperationAction() {
		return "comment";
	}
	
}
