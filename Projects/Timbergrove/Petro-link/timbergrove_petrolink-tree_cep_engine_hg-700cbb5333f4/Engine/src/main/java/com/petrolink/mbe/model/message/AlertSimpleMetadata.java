package com.petrolink.mbe.model.message;

/**
 * Simplified Reference to alert instance
 * @author aristo
 *
 */
public class AlertSimpleMetadata {

	/**
	 * Constructor.
	 */
	public AlertSimpleMetadata() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructor.
	 * @param uuid UUID of the alert
	 * @param classId Class ID of the alert
	 */
	public AlertSimpleMetadata(String uuid, String classId) {
		super();
		this.uuid = uuid;
		this.classIid = classId;
	}
	
	private String uuid;
	private String classIid;
	
	/**
	 * @return the uuid
	 */
	public final String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public final void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the classIid
	 */
	public final String getClassId() {
		return classIid;
	}

	/**
	 * @param classIid the classIid to set
	 */
	public final void setClassIid(String classIid) {
		this.classIid = classIid;
	}

}
