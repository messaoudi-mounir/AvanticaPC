package com.petrolink.mbe.model.message;

/**
 * For storing generic definition of alert.
 * This should rarely change, and generally can be used to determine duplication
 * @author aristo
 *
 */
public class AlertDefinition {
	private String name;
	private String classId; 
	private String domain;
	private String classification;
	
	/**
	 * Clone this instance
	 * @return clone of AlertDefinition
	 */
	public AlertDefinition cloneAsAlertDefinition() {
		AlertDefinition clone = new AlertDefinition();
		clone.name = name;
		clone.classId = classId;
		clone.domain = domain;
		clone.classification = classification;
		return clone;
	}
		
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the classId
	 */
	public final String getClassId() {
		return classId;
	}
	/**
	 * @param classId the classId to set
	 */
	public final void setClassId(String classId) {
		this.classId = classId;
	}
	
	/**
	 * @return the domain
	 */
	public final String getDomain() {
		return domain;
	}
	/**
	 * @param domain the domain to set
	 */
	public final void setDomain(String domain) {
		this.domain = domain;
	}
	/**
	 * @return the classification
	 */
	public final String getClassification() {
		return classification;
	}
	/**
	 * @param classification the classification to set
	 */
	public final void setClassification(String classification) {
		this.classification = classification;
	}
}
