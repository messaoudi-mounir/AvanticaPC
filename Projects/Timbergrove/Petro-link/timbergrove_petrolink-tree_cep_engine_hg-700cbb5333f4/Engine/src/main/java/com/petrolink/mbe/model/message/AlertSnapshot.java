package com.petrolink.mbe.model.message;

import java.time.Instant;

/**
 * A snapshot of current state of Alert.
 * this should be reflective of Alert Database record at a time
 * @author aristo
 *
 */
public class AlertSnapshot {

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
	private AlertOpInvestigate latestInvestigate;
	
	private WellParametersSnapshot parameters;
	private ContentContainer metadata;
	private ContentContainer contextDetail ;
	
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
	 * @return the details
	 */
	public final ContentContainer getContextDetail() {
		return contextDetail;
	}
	/**
	 * @param details the details to set
	 */
	public final void setContextDetail(ContentContainer details) {
		this.contextDetail = details;
	}
	/**
	 * @return the metadata
	 */
	public final ContentContainer getMetadata() {
		return metadata;
	}
	/**
	 * @param content the metadata to set
	 */
	public final void setMetadata(ContentContainer content) {
		this.metadata = content;
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
	 * @return the parameters
	 */
	public final WellParametersSnapshot getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public final void setParameters(WellParametersSnapshot parameters) {
		this.parameters = parameters;
	}
	/**
	 * @return the latestAcknowledge
	 */
	public final AlertOpAcknowledge getLatestAcknowledgement() {
		return latestAcknowledgement;
	}
	/**
	 * @param latestAcknowledge the latestAcknowledge to set
	 */
	public final void setLatestAcknowledgement(AlertOpAcknowledge latestAcknowledge) {
		this.latestAcknowledgement = latestAcknowledge;
	}
	/**
	 * @return the alertUuid
	 */
	public final String getInstanceUuid() {
		return instanceUuid;
	}
	/**
	 * @param alertUuid the alertUuid to set
	 */
	public final void setInstanceUuid(String alertUuid) {
		this.instanceUuid = alertUuid;
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
	 * @return the alertCreated
	 */
	public final Instant getCreatedTimestamp() {
		return createdTimestamp;
	}
	/**
	 * @param alertCreated the alertCreated to set
	 */
	public final void setCreatedTimestamp(Instant alertCreated) {
		this.createdTimestamp = alertCreated;
	}
	/**
	 * @return the latestOccurrence
	 */
	public final Instant getLatestOccurrence() {
		return latestOccurrence;
	}
	/**
	 * @param occurrence the latestOccurrence to set
	 */
	public final void setLatestOccurrence(Instant occurrence) {
		this.latestOccurrence = occurrence;
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
	 * Set latest Investigate
	 * @param extractLastInvestigate
	 */
	public void setLatestInvestigation(AlertOpInvestigate extractLastInvestigate) {
		latestInvestigate = extractLastInvestigate;
	}
	/**
	 * @return the latestInvestigate
	 */
	public final AlertOpInvestigate getLatestInvestigation() {
		return latestInvestigate;
	}
	
	
	
}
