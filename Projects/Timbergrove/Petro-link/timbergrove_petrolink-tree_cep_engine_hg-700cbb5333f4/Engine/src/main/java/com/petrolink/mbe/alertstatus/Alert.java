package com.petrolink.mbe.alertstatus;

import org.apache.commons.lang3.StringUtils;

/**
 * Petrolink specific alert status externsion
 * @author paul, Aristo
 *
 */
public interface Alert extends com.smartnow.alertstatus.Alert {
	/**
	 * Status Name for Alert.ACTIVE
	 * {@link #getStatusName} and {@link #getStatusName} 
	 */
	public static final String ALERT_STATUS_NAME_ACTIVE = "ACTIVE";
	
	/**
	 * Status Name for Alert.INACTIVE
	 * {@link #getStatusName} and {@link #getStatusName} 
	 */
	public static final String ALERT_STATUS_NAME_INACTIVE = "INACTIVE";
	
	/**
	 * Status Name for Alert.CLOSED
	 * {@link #getStatusName} and {@link #getStatusName} 
	 */
	public static final String ALERT_STATUS_NAME_CLOSED = "CLOSED";
	
	/**
	 * Status Name for Alert.COMP
	 * {@link #getStatusName} and {@link #getStatusName} 
	 */
	public static final String ALERT_STATUS_NAME_COMP = "COMPLETED";
	
	/**
	 * @return the Rule Id that created the Alert
	 */
	public String getCreatedByRule();
	/**
	 * @param ruleId the Rule Id
	 */
	public void setCreatedByRule(String ruleId);
	/**
	 * @return the Rule Id that updated the Alert
	 */
	public String getUpdatedByRule();
	/**
	 * @param ruleId the Rule Id
	 */
	public void setUpdatedByRule(String ruleId);
	/**
	 * @return the createdIndex
	 */
	public String getCreatedIndex();
	/**
	 * @param createdIndex the createdIndex to set
	 */
	public void setCreatedIndex(String createdIndex);
	/**
	 * @return the lastIndex
	 */
	public String getLastIndex();
	/**
	 * @param lastIndex the lastIndex to set
	 */
	public void setLastIndex(String lastIndex);
	
	/**
	 * @return the Well Id
	 */
	public String getWellId();
	
	/**
	 * @param wellId
	 */
	public void setWellId(String wellId);
	
	/**
	 * @return the rigStatus
	 */
	public Integer getRigState();

	/**
	 * @param rigState the rigState to set
	 */
	public void setRigState(Integer rigState);

	/**
	 * @return the rigStatus
	 */
	public Integer getFinalRigState();

	/**
	 * @param rigStatus the rigStatus to set
	 */
	public void setFinalRigState(Integer rigStatus);
	
	/**
     * @return the holeDepth
     */
    public Double getFinalHoleDepth();

    /**
     * @param holeDepth the holeDepth to set
     */
    public void setFinalHoleDepth(Double holeDepth);
	 /**
     * @return the holeDepth
     */
    public Double getHoleDepth();

    /**
     * @param holeDepth the holeDepth to set
     */
    public void setHoleDepth(Double holeDepth);

    /**
     * @return the bitDepth
     */
    public Double getFinalBitDepth();

    /**
     * @param bitDepth the bitdepth to set
     */
    public void setFinalBitDepth(Double bitDepth);

    /**
     * @return the bitDepth
     */
    public Double getBitDepth();

    /**
     * @param bitDepth the bitdepth to set
     */
    public void setBitDepth(Double bitDepth);
    
	/**
	 * @return the rig name
	 */
	public String getRigName();
	
	/**
	 * Gets the name of an alert status as an uppercase string. To get status code see in {@link #getStatusCode}.
	 * @param status An alert status
	 * @return The name of an alert status
	 */
	public static String getStatusName(int status) {
		switch (status) {
		case Alert.ACTIVE:
			return ALERT_STATUS_NAME_ACTIVE;
		case Alert.INACTIVE:
			return ALERT_STATUS_NAME_INACTIVE;
		case Alert.CLOSED:
			return ALERT_STATUS_NAME_CLOSED;
		case Alert.COMP:
			return ALERT_STATUS_NAME_COMP;
		default:
			throw new IllegalArgumentException("unknown status: " + status);
		}
	}
	
	
	
	/**
	 * Gets the status code from a alert status (will ignore case).To get setatus name see {@link #getStatusName}.
	 * @param statusName The name of an alert status
	 * @return  Integer representing status code in An alert status
	 */
	public static int getStatusCode(String statusName) {
		if (StringUtils.equalsIgnoreCase(ALERT_STATUS_NAME_ACTIVE, statusName)) {
			return Alert.ACTIVE;
		} else if (StringUtils.equalsIgnoreCase(ALERT_STATUS_NAME_INACTIVE, statusName)) {
			return Alert.INACTIVE;
		} else if (StringUtils.equalsIgnoreCase(ALERT_STATUS_NAME_CLOSED, statusName)) {
			return Alert.CLOSED;
		} else if (StringUtils.equalsIgnoreCase(ALERT_STATUS_NAME_COMP, statusName)) {
			return Alert.COMP;
		} else {
			throw new IllegalArgumentException("unknown status name: " + statusName);
		}
	}
	
	/**
	 * @return the commentedCount
	 */
	public int getCommentedCount();

	/**
	 * @param commentedCount the commentedCount to set
	 */
	public void setCommentedCount(int commentedCount);
}
