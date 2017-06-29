package com.petrolink.mbe.model.message;

import java.time.Instant;

/**
 * Defines Operation which can be done against alert
 * @author aristo
 *
 */
public class AlertOperation {
	private String by;
	private Instant timestamp;
	
	/**
	 * @return the origin
	 */
	public final String getBy() {
		return by;
	}
	/**
	 * @param origin the origin to set
	 */
	public final void setBy(String origin) {
		this.by = origin;
	}
	/**
	 * @return the timestamp
	 */
	public final Instant getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public final void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
}
