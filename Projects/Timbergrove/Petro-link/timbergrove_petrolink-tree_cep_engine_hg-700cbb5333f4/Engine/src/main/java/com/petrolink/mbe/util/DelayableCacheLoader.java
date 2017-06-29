package com.petrolink.mbe.util;

/**
 * Utility class to setup for Cache loader which can be delayed (easy for testing)
 * @author aristo
 *
 */
public class DelayableCacheLoader {
	private long debugDelayMilis = 0;
	
	/**
	 * @return the debugDelayMilis
	 */
	public final long getDebugDelayMilis() {
		return debugDelayMilis;
	}

	/**
	 * For debugging when testing code. DO NOT SET on production!
	 * @param aDebugDelayMilis the debugDelayMilis to set in milisceonds. 0 Means no delay
	 */
	public final void setDebugDelayMilis(final long aDebugDelayMilis) {
		this.debugDelayMilis = aDebugDelayMilis;
	}
}
