package com.petrolink.mbe.model.channel;

/**
 * Channel's Data Type , based on petrolink's general structure
 * @author Aristo
 */
public enum ChannelDataType {
	/**
	 * Double Channel Data
	 */
	Double, 
	/**
	 * Long Channel Data
	 */
	Long, 
	/**
	 * String Channel Data
	 */
	String, 
	/**
	 * DateTimeOffset Channel Data
	 */
	DateTimeOffset,
	/**
	 * Custom Channel Data
	 */
	Custom;
}
