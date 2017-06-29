package com.petrolink.mbe.setting;

/**
 * Enumerate the source of data Being used for Action
 * @author aristo
 *
 */
public enum ActionSources {
	/**
	 * Define that the source of data is unknown
	 */
	UNKNOWN,
	/**
	 * Define that the source of data is from the context
	 */
	CONTEXT,
	/**
	 * Define that the source of data is from body (configured)
	 */
	BODY
}
