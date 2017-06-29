package com.petrolink.mbe.model.message;

public class WellParametersSnapshot {

	private String wellId;
	private Double latestHoleDepth;
	private Double initialHoleDepth;
	private Double initialBitDepth;
	private Double latestBitDepth;
	private Integer initialRigState;
	private Integer latestRigState;
	/**
	 * @return the wellId
	 */
	public final String getWellId() {
		return wellId;
	}
	/**
	 * @param wellId the wellId to set
	 */
	public final void setWellId(String wellId) {
		this.wellId = wellId;
	}
	/**
	 * @return the finalHoleDepth
	 */
	public final Double getLatestHoleDepth() {
		return latestHoleDepth;
	}
	/**
	 * @param finalHoleDepth the finalHoleDepth to set
	 */
	public final void setLatestHoleDepth(Double finalHoleDepth) {
		this.latestHoleDepth = finalHoleDepth;
	}
	/**
	 * @return the initialHoleDepth
	 */
	public final Double getInitialHoleDepth() {
		return initialHoleDepth;
	}
	/**
	 * @param initialHoleDepth the initialHoleDepth to set
	 */
	public final void setInitialHoleDepth(Double initialHoleDepth) {
		this.initialHoleDepth = initialHoleDepth;
	}
	/**
	 * @return the initialBitDepth
	 */
	public final Double getInitialBitDepth() {
		return initialBitDepth;
	}
	/**
	 * @param initialBitDepth the initialBitDepth to set
	 */
	public final void setInitialBitDepth(Double initialBitDepth) {
		this.initialBitDepth = initialBitDepth;
	}
	/**
	 * @return the finalBitDepth
	 */
	public final Double getLatestBitDepth() {
		return latestBitDepth;
	}
	/**
	 * @param finalBitDepth the finalBitDepth to set
	 */
	public final void setLatestBitDepth(Double finalBitDepth) {
		this.latestBitDepth = finalBitDepth;
	}
	/**
	 * @return the initialRigstate
	 */
	public final Integer getInitialRigState() {
		return initialRigState;
	}
	/**
	 * @param rigState the initialRigstate to set
	 */
	public final void setInitialRigState(Integer rigState) {
		this.initialRigState = rigState;
	}
	/**
	 * @return the finalRigstate
	 */
	public final Integer getLatestRigState() {
		return latestRigState;
	}
	/**
	 * @param rigState the finalRigstate to set
	 */
	public final void setLatestRigState(Integer rigState) {
		this.latestRigState = rigState;
	}
	
}
