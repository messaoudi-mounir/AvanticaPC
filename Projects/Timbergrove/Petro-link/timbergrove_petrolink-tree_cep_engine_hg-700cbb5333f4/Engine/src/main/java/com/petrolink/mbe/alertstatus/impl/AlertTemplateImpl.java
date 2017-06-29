package com.petrolink.mbe.alertstatus.impl;

/**
 * The Petrolink Alert Template Implementation
 * @author paul
 *
 */
public class AlertTemplateImpl extends com.smartnow.alertstatus.impl.AlertTemplateImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6160545820193508661L;

	protected String createdIndex;
	protected Double holeDepth;
    protected Double bitDepth;
    protected Integer rigState;
    protected String createdByRuleId;

	protected String wellId;	
	/**
	 * @return the holeDepth
	 */
	public Double getHoleDepth() {
		return holeDepth;
	}
	/**
	 * @param holeDepth the holeDepth to set
	 */
	public void setHoleDepth(Double holeDepth) {
		this.holeDepth = holeDepth;
	}
	/**
	 * @return the bitDepth
	 */
	public Double getBitDepth() {
		return bitDepth;
	}
	/**
	 * @param bitDepth the bitDepth to set
	 */
	public void setBitDepth(Double bitDepth) {
		this.bitDepth = bitDepth;
	}
	/**
	 * @return the wellId
	 */
	public String getWellId() {
		return wellId;
	}
	/**
	 * @param wellId the wellId to set
	 */
	public void setWellId(String wellId) {
		this.wellId = wellId;
	}
	/**
	 * @return the metadata
	 */
	public Object getMetadata() {
		return metadata;
	}
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}
	/**
	 * @return the rigState
	 */
	public Integer getRigState() {
		return rigState;
	}
	/**
	 * @param rigState the rigState to set
	 */
	public void setRigState(Integer rigState) {
		this.rigState = rigState;
	}
	/**
	 * @return the createdIndex
	 */
	public String getCreatedIndex() {
		return createdIndex;
	}
	/**
	 * @param createdIndex the createdIndex to set
	 */
	public void setCreatedIndex(String createdIndex) {
		this.createdIndex = createdIndex;
	}
	/**
	 * @return the createdByRuleId
	 */
	public String getCreatedByRuleId() {
		return createdByRuleId;
	}
	/**
	 * @param createdByRuleId the createdByRuleId to set
	 */
	public void setCreatedByRuleId(String createdByRuleId) {
		this.createdByRuleId = createdByRuleId;
	}	
}
