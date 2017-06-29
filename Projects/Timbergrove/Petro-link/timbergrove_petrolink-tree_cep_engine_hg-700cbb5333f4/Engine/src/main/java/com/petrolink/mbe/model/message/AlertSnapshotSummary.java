package com.petrolink.mbe.model.message;

import java.time.Instant;

/**
 * Alert Summary. 
 * Do not put too many fields here as it is intended to give updates to other components.
 * If needed the other component can query the alert itself (eg through DB or other mean).
 * Available fields here are only intended for fields which may trigger other automatic rule processing.
 * @author aristo
 *
 */
public class AlertSnapshotSummary {
	private String instanceUuid;
	private AlertDefinition definition;
	private String description;
	
	private int status; 
	private int severity;
	private int priority;
	
	private Instant createdTimestamp;
	private String createdIndex;
	
	private String latestIndex;
	private Instant latestStatusChange;
	private Instant latestOccurrence;

	private int commentedCount = 0;
	private int tally = 0;
	private boolean onCreateEventsExecuted = false;
	private boolean snoozed = false;
	
	private AlertOpAcknowledge latestAcknowledgement;
	private AlertOpComment latestComment;
	private AlertOpSnooze latestSnoozed;
	
	/**
	 * Extract AlertSnapshotSummary from AlertSnapshot
	 * @param alert
	 * @return AlertSnapshotSummary
	 */
	public static AlertSnapshotSummary from(AlertSnapshot alert) {
		AlertSnapshotSummary summary = new AlertSnapshotSummary();
		
		summary.setInstanceUuid(alert.getInstanceUuid());
		summary.setDefinition(alert.getDefinition().cloneAsAlertDefinition());
		summary.setDescription(alert.getDescription());
		summary.setStatus(alert.getStatus());
		summary.setSeverity(alert.getSeverity());
		summary.setPriority(alert.getPriority());
		
		summary.setCreatedTimestamp(alert.getCreatedTimestamp());
		summary.setCreatedIndex(alert.getCreatedIndex());
		
		summary.setLatestIndex(alert.getLatestIndex());
		summary.setLatestStatusChange(alert.getLatestStatusChange());
		summary.setLatestOccurrence(alert.getLatestOccurrence());
		
		summary.setCommentedCount(alert.getCommentedCount());
		summary.setTally(alert.getTally());
		summary.setOnCreateEventsExecuted(alert.isOnCreateEventsExecuted());
		summary.setSnoozed(alert.isSnoozed());
		
		summary.setLatestAcknowledgement(alert.getLatestAcknowledgement());
		summary.setLatestComment(alert.getLatestComment());
		summary.setLatestSnoozed(alert.getLatestSnoozed());
		return summary;
	}

	/**
	 * @return the instanceUuid
	 */
	public final String getInstanceUuid() {
		return instanceUuid;
	}

	/**
	 * @param instanceUuid the instanceUuid to set
	 */
	public final void setInstanceUuid(String instanceUuid) {
		this.instanceUuid = instanceUuid;
	}

	/**
	 * @return the definition
	 */
	public final AlertDefinition getDefinition() {
		return definition;
	}

	/**
	 * @param definition the definition to set
	 */
	public final void setDefinition(AlertDefinition definition) {
		this.definition = definition;
	}

	/**
	 * @return the status
	 */
	public final int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public final void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the severity
	 */
	public final int getSeverity() {
		return severity;
	}

	/**
	 * @param severity the severity to set
	 */
	public final void setSeverity(int severity) {
		this.severity = severity;
	}

	/**
	 * @return the priority
	 */
	public final int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public final void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the createdTimestamp
	 */
	public final Instant getCreatedTimestamp() {
		return createdTimestamp;
	}

	/**
	 * @param createdTimestamp the createdTimestamp to set
	 */
	public final void setCreatedTimestamp(Instant createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	/**
	 * @return the latestIndex
	 */
	public final String getLatestIndex() {
		return latestIndex;
	}

	/**
	 * @param latestIndex the latestIndex to set
	 */
	public final void setLatestIndex(String latestIndex) {
		this.latestIndex = latestIndex;
	}

	/**
	 * @return the latestStatusChange
	 */
	public final Instant getLatestStatusChange() {
		return latestStatusChange;
	}

	/**
	 * @param latestStatusChange the latestStatusChange to set
	 */
	public final void setLatestStatusChange(Instant latestStatusChange) {
		this.latestStatusChange = latestStatusChange;
	}

	/**
	 * @return the latestOccurrence
	 */
	public final Instant getLatestOccurrence() {
		return latestOccurrence;
	}

	/**
	 * @param latestOccurrence the latestOccurrence to set
	 */
	public final void setLatestOccurrence(Instant latestOccurrence) {
		this.latestOccurrence = latestOccurrence;
	}

	/**
	 * @return the commentedCount
	 */
	public final int getCommentedCount() {
		return commentedCount;
	}

	/**
	 * @param commentedCount the commentedCount to set
	 */
	public final void setCommentedCount(int commentedCount) {
		this.commentedCount = commentedCount;
	}

	/**
	 * @return the tally
	 */
	public final int getTally() {
		return tally;
	}

	/**
	 * @param tally the tally to set
	 */
	public final void setTally(int tally) {
		this.tally = tally;
	}

	/**
	 * @return the onCreateEventsExecuted
	 */
	public final boolean isOnCreateEventsExecuted() {
		return onCreateEventsExecuted;
	}

	/**
	 * @param onCreateEventsExecuted the onCreateEventsExecuted to set
	 */
	public final void setOnCreateEventsExecuted(boolean onCreateEventsExecuted) {
		this.onCreateEventsExecuted = onCreateEventsExecuted;
	}

	/**
	 * @return the snoozed
	 */
	public final boolean isSnoozed() {
		return snoozed;
	}

	/**
	 * @param snoozed the snoozed to set
	 */
	public final void setSnoozed(boolean snoozed) {
		this.snoozed = snoozed;
	}

	/**
	 * @return the latestAcknowledgement
	 */
	public final AlertOpAcknowledge getLatestAcknowledgement() {
		return latestAcknowledgement;
	}

	/**
	 * @param latestAcknowledgement the latestAcknowledgement to set
	 */
	public final void setLatestAcknowledgement(AlertOpAcknowledge latestAcknowledgement) {
		this.latestAcknowledgement = latestAcknowledgement;
	}

	/**
	 * @return the latestComment
	 */
	public final AlertOpComment getLatestComment() {
		return latestComment;
	}

	/**
	 * @param latestComment the latestComment to set
	 */
	public final void setLatestComment(AlertOpComment latestComment) {
		this.latestComment = latestComment;
	}

	/**
	 * @return the latestSnoozed
	 */
	public final AlertOpSnooze getLatestSnoozed() {
		return latestSnoozed;
	}

	/**
	 * @param latestSnoozed the latestSnoozed to set
	 */
	public final void setLatestSnoozed(AlertOpSnooze latestSnoozed) {
		this.latestSnoozed = latestSnoozed;
	}

	/**
	 * @return the createdIndex
	 */
	public final String getCreatedIndex() {
		return createdIndex;
	}

	/**
	 * @param createdIndex the createdIndex to set
	 */
	public final void setCreatedIndex(String createdIndex) {
		this.createdIndex = createdIndex;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public final void setDescription(String description) {
		this.description = description;
	}
}
