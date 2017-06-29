package com.petrolink.mbe.model.channel;

/**
 * Channel's Index Type , based on petrolink's general structure.
 * @author aristo
 */
public enum ChannelIndexType {
	/**
	 * Unknown Index Type.
	 */
	Unknown,
	/**
	 * Time Index.
	 */
	Datetime,
	/**
	 * Double based index, usually Depth based index.
	 */
	Double, 
	/**
	 * Long based index.
	 */
	Long;
}
