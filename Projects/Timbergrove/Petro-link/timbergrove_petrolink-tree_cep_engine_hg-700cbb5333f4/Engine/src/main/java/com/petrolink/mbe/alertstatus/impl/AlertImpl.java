package com.petrolink.mbe.alertstatus.impl;

import java.util.Map;
import java.util.UUID;

import com.petrolink.mbe.alertstatus.Alert;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.smartnow.alertstatus.AlertTemplate;

/**
 * @author AndresR
 * Implementation of the class Alert
 */
public class AlertImpl extends com.smartnow.alertstatus.impl.AlertImpl implements Alert {
	private static final long serialVersionUID = 4220037328763558055L;
	protected String createdIndex;
	protected String lastIndex;

	protected Double finalHoleDepth;
	protected Double finalBitDepth;
	protected Double holeDepth;
	protected Double bitDepth;
	protected Integer rigState;
	protected Integer finalRigState;

	protected String wellId;
	private String createdbyRuleId;
	private String updatedByRuleId;
	private String rigName;
	
	private int commentedCount; 
	
	protected String lastSnoozedBy;
	protected long lastSnoozedAt;
	
	protected String parentClassId;
	protected String parentUuid;
	
	/**
	 *  AlertImpl constructor
	 */
	public AlertImpl() {
		this.uuid = UUID.randomUUID().toString();
		this.created = System.currentTimeMillis();
		this.lastStatusChange = this.created;
		this.commentedCount = 0;
		this.setLoaded(true);
	}
	
	/**
	 * create an alert from template
	 * @param template
	 */
	public AlertImpl(AlertTemplate template) {
		com.petrolink.mbe.alertstatus.impl.AlertTemplateImpl _template = (AlertTemplateImpl) template;
		this.wellId = _template.getWellId();
		this.createdIndex = _template.getCreatedIndex();
		this.lastIndex = _template.getCreatedIndex(); //From template , last index should always be there
		this.bitDepth = _template.getBitDepth();
		this.holeDepth = _template.getHoleDepth();
		this.rigState = _template.getRigState();
		this.createdbyRuleId = _template.getCreatedByRuleId();
		this.uuid = UUID.randomUUID().toString();
		this.created = System.currentTimeMillis();
		this.lastStatusChange = this.created;
		this.fromTemplate(template);
	}

	@Override
	public String getCreatedIndex() {
		if (!isLoaded()) {
			load();
		}		
		return createdIndex;
	}
	
	@Override
	public void setCreatedIndex(String createdIndex) {
		if (!isLoaded()) {
			load();
		}
		this.createdIndex = createdIndex;
		this.lastIndex = this.createdIndex;
	}
	
	/**
	 * Get/set principal who has snoozed this alert in the past
	 * @return the lastSnoozedBy
	 */
	public String getLastSnoozedBy() {
		return lastSnoozedBy;
	}
	
	/**
	 * Get/set epochTimeMilis last time this alert in the past
	 * @return the lastSnoozedBy
	 */
	public long getLastSnoozedAt() {
		return lastSnoozedAt;
	}
	
	@Override
	public String getLastIndex() {
		if (!isLoaded()) {
			load();
		}		
		return lastIndex;
	}
	@Override
	public void setLastIndex(String lastIndex) {
		if (!isLoaded()) {
			load();
		}
		this.lastIndex = lastIndex;
	}
	
	/* (non-Javadoc)
	 * @see com.smartnow.alertstatus.impl.AlertImpl#deduplicate(java.util.Map)
	 */
	@Override
	public void deduplicate(Map<String, Object> context) {
		if (!isLoaded()) {
			load();
		}
		this.incrementTally();
		this.lastOccurrence = System.currentTimeMillis();
		this.setLastIndex(context.get(RuleFlow.INDEX_CTX_VARIABLE).toString());
	}
	
	/* (non-Javadoc)
	 * @see com.smartnow.alertstatus.impl.AlertImpl#deduplicate(com.smartnow.alertstatus.Alert)
	 */
	@Override
	public void deduplicate(com.smartnow.alertstatus.Alert alert) {
		if (!isLoaded()) {
			load();
		}
		Alert _alert = (Alert) alert;
		this.incrementTally();
		this.lastOccurrence = System.currentTimeMillis();
		this.setDescription(_alert.getDescription()); //When doing scripted update it may actually update the description
		this.setLastIndex(_alert.getLastIndex());
		this.setDetails(_alert.getDetails());
		this.setFinalBitDepth(_alert.getBitDepth());
		this.setFinalHoleDepth(_alert.getHoleDepth());
		this.setFinalRigState(_alert.getFinalRigState());
		this.setUpdatedByRule(_alert.getCreatedByRule());
	}

	/**
	 * @return the wellId
	 */
	public String getWellId() {
		return wellId;
	}
	
	/**
	 * @return the rigName
	 */
	public String getRigName() {
		return rigName;
	}
	
	/**
	 * @return the notificationsSent
	 */
	public boolean isOnCreateEventsExecuted() {
		return onCreateEventsExecuted;
	}
	
	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @param lastStatusChange the lastStatusChange to set
	 */
	public void setLastStatusChange(long lastStatusChange) {
		this.lastStatusChange = lastStatusChange;
	}
	/**
	 * @param created the created to set
	 */
	public void setCreated(long created) {
		this.created = created;
	}
	/**
	 * @param acknowledgeBy the acknowledgeBy to set
	 */
	public void setAcknowledgeBy(String acknowledgeBy) {
		this.acknowledgeBy = acknowledgeBy;
	}
	/**
	 * @param acknowledgeAt the acknowledgeAt to set
	 */
	public void setAcknowledgeAt(long acknowledgeAt) {
		this.acknowledgeAt = acknowledgeAt;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @param commentedBy the commentedBy to set
	 */
	public void setCommentedBy(String commentedBy) {
		this.commentedBy = commentedBy;
	}
	/**
	 * @param commentedAt the commentedAt to set
	 */
	public void setCommentedAt(long commentedAt) {
		this.commentedAt = commentedAt;
	}

	/**
	 * 
	 * @param snoozed
	 */
	public void setSnoozed(boolean snoozed){
		this.snoozed = snoozed;
	}
	
	/**
	 * 
	 * @param snoozedBy
	 */
	public void setSnoozedBy(String snoozedBy){
		this.snoozedBy = snoozedBy;
	}
	
	/**
	 * Get/set principal who has snoozed this alert in the past
	 * @param principal
	 */
	public void setLastSnoozedBy(String principal) {
		lastSnoozedBy = principal;
	}
	
	/**
	 * Get/set epochTimeMilis last time this alert in the past
	 * @param epochTimeMilis
	 */
	public void setLastSnoozedAt(long epochTimeMilis) {
		lastSnoozedAt = epochTimeMilis;
	}
	
	/**
	 * @param snoozedAt
	 */
	public void setSnoozedAt(long snoozedAt){
		this.snoozedAt = snoozedAt;
	}
	
	/**
	 * @param wellId
	 */
	public void setWellId(String wellId){
		this.wellId = wellId;
	}
	
	/**
	 * @param rigName
	 */
	protected void setRigName(String rigName){
		this.rigName = rigName;
	}
	
	/**
	 * @param onCreateEventsExecuted the notificationsSent to set
	 */
	public void setOnCreateEventsExecuted(boolean onCreateEventsExecuted) {
		this.onCreateEventsExecuted = onCreateEventsExecuted;
	}
	/**
	 * Deduplicates a new occurance of the event, including 
	 * - Increment of the Tally
	 * - Update the LastOccurrance Timestamp
	 * @param context
	 */
	
    /**
     * @return the holeDepth
     */
    public Double getHoleDepth() {
		if (!isLoaded()) {
			load();
		}

        return holeDepth;
    }

    /**
     * @param holeDepth the holeDepth to set
     */
    public void setHoleDepth(Double holeDepth) {
        this.holeDepth = holeDepth;
        setFinalHoleDepth(holeDepth);
    }

    /**
     * @return the bitDepth
     */
    public Double getBitDepth() {
		if (!isLoaded()) {
			load();
		}

        return bitDepth;
    }

    /**
     * @param bitDepth the bitdepth to set
     */
    public void setBitDepth(Double bitDepth) {
        this.bitDepth = bitDepth;
        setFinalBitDepth(bitDepth);
    }

	/**
	 * @return the rigState
	 */
	@Override
	public Integer getRigState() {
		if (!isLoaded()) {
			load();
		}

		return rigState;
	}

	/**
	 * @param rigState the rigState to set
	 */
	@Override
	public void setRigState(Integer rigState) {
		this.rigState = rigState;
	}

	@Override
	public Integer getFinalRigState() {
		return finalRigState;
	}

	@Override
	public void setFinalRigState(Integer rigState) {
		this.finalRigState = rigState;
	}

	@Override
	public String getCreatedByRule() {
		if (!isLoaded()) {
			load();
		}
		return this.createdbyRuleId;
	}

	@Override
	public void setCreatedByRule(String ruleId) {
		this.createdbyRuleId = ruleId;
		this.updatedByRuleId = ruleId;
	}

	@Override
	public String getUpdatedByRule() {
		if (!isLoaded()) {
			load();
		}		
		return this.updatedByRuleId;
	}

	@Override
	public void setUpdatedByRule(String ruleId) {
		this.updatedByRuleId = ruleId;
	}
	
	/**
	 * @return the commentedCount
	 */
	@Override
	public int getCommentedCount() {
		return commentedCount;
	}

	/**
	 * @param commentedCount the commentedCount to set
	 */
	@Override
	public void setCommentedCount(int commentedCount) {
		this.commentedCount = commentedCount;
	}
	
	/**
	 * when adding a comment this method increments the count by 1
	 */
	public void incrementCommentedCount(){
		this.commentedCount++;
	}

	/**
	 * Get/Set parent alert's classId for this alert (when acknowledged by other alert)
	 * @return the parentClassId
	 */
	public final String getParentClassId() {
		return parentClassId;
	}

	/**
	 * Get/Set parent alert's classId for this alert (when acknowledged by other alert)
	 * @param classId the parentClassId to set
	 */
	public final void setParentClassId(String classId) {
		this.parentClassId = classId;
	}

	/**
	 * Get/Set parent alert's uuid for this alert (when acknowledged by other alert)
	 * @return the parentUuid
	 */
	public final String getParentUuid() {
		return parentUuid;
	}

	/**
	 * Get/Set parent alert's uuid for this alert (when acknowledged by other alert)
	 * @param alertUuid the parentUuid to set
	 */
	public final void setParentUuid(String alertUuid) {
		this.parentUuid = alertUuid;
	}
	
	/**
	 * Copies the provided alert contents to this alert.
	 * @param alert
	 */
	public void copyFrom(com.smartnow.alertstatus.Alert alert) {
		AlertImpl _alert = (AlertImpl) alert;
		this.classId = _alert.getClassId();
		this.classification = _alert.getClassification();
		this.description = _alert.getDescription();
		this.details = _alert.getDetails();
		this.detailsContentType = _alert.getDetailsContentType();
		this.domain = _alert.getDomain();
		this.acknowledgeAt = _alert.getAcknowledgeAt();
		this.acknowledgeBy = _alert.getAcknowledgeBy();
		this.comment = _alert.getComment();
		this.commentedBy = _alert.getCommentedBy();
		this.commentedAt = _alert.getCommentedAt();
		this.created = _alert.getCreated();
		this.bitDepth = _alert.getBitDepth();
		this.finalBitDepth = _alert.finalBitDepth;
		this.holeDepth = _alert.getHoleDepth();
		this.finalBitDepth = _alert.finalBitDepth;
		this.createdbyRuleId = _alert.getCreatedByRule();
		this.updatedByRuleId = _alert.getUpdatedByRule();
		this.createdIndex = _alert.getCreatedIndex();
		this.lastIndex = _alert.getLastIndex();
		this.lastOccurrence = _alert.getLastOccurrence();
		this.lastStatusChange = _alert.getLastStatusChange();
		this.name = _alert.getName();
		this.rigName = _alert.getRigName();
		this.rigState = _alert.getRigState();
		this.finalRigState = _alert.getFinalRigState();
		this.onCreateEventsExecuted = _alert.onCreateEventsExecuted;
		this.snoozed = _alert.isSnoozed();
		this.snoozedBy = _alert.getSnoozedBy();
		this.snoozedAt = _alert.getSnoozedAt();
		this.lastSnoozedBy = _alert.getLastSnoozedBy();
		this.lastSnoozedAt = _alert.getLastSnoozedAt();
		this.status = _alert.status;
		this.tally = _alert.tally;
		this.wellId = _alert.wellId;
		this.metadata = _alert.metadata;
		this.parentClassId = _alert.parentClassId;
		this.parentUuid = _alert.parentUuid;
		this.investigateAt = _alert.getInvestigateAt();
		this.investigateBy = _alert.getInvestigateBy();
		this.setLoaded(true);
	}

	@Override
	public Double getFinalHoleDepth() {
		return finalHoleDepth;
	}

	@Override
	public void setFinalHoleDepth(Double holeDepth) {
		this.finalHoleDepth = holeDepth;
	}

	@Override
	public Double getFinalBitDepth() {
		return finalBitDepth;
	}

	@Override
	public void setFinalBitDepth(Double bitDepth) {
		this.finalBitDepth = bitDepth;
	}

	/* (non-Javadoc)
	 * @see com.smartnow.alertstatus.impl.AlertImpl#fromTemplate(com.smartnow.alertstatus.AlertTemplate)
	 */
	@Override
	public void fromTemplate(AlertTemplate template) {
		super.fromTemplate(template);
		
		AlertTemplateImpl templateImpl = (AlertTemplateImpl) template;
		
		this.commentedCount = 0;
		this.createdIndex = templateImpl.getCreatedIndex();
		this.createdbyRuleId = templateImpl.getCreatedByRuleId();
		this.lastIndex = templateImpl.getCreatedIndex();
		this.bitDepth = templateImpl.getBitDepth();
		this.finalBitDepth = templateImpl.getBitDepth();
		this.holeDepth = templateImpl.getHoleDepth();
		this.finalHoleDepth = templateImpl.getHoleDepth();
		this.rigState = templateImpl.getRigState();
		this.finalRigState = templateImpl.getRigState();
		this.wellId = templateImpl.getWellId();
	}
	
	protected String investigateBy;
	protected long investigateAt;
	
	/**
	 * Whether this alert is under investigation
	 * @return true if investigateBy has some value
	 */
	public boolean isInvestigating() {
		if (!this.loaded) {
			this.load();
		}

		return this.investigateBy != null;
	}

	/**
	 * @return the investigateBy
	 */
	public final String getInvestigateBy() {
		return investigateBy;
	}

	/**
	 * @param investigateBy the investigateBy to set
	 */
	public final void setInvestigateBy(String investigateBy) {
		this.investigateBy = investigateBy;
	}

	/**
	 * @return the investigateAt
	 */
	public final long getInvestigateAt() {
		return investigateAt;
	}

	/**
	 * @param investigateAt the investigateAt to set
	 */
	public final void setInvestigateAt(long investigateAt) {
		this.investigateAt = investigateAt;
	}

	
}
