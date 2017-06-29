package com.petrolink.mbe.setting;

import java.time.Duration;

/**
 * Cache Option setting for the services.
 * @author aristo
 *
 */
public class CacheOption {
	/**
	 * Default Cache size.
	 */
	public static final int DEFAULT_CACHE_MAX_SIZE = 10_000;
	/**
	 * Default duration of cache before it is expired (ie removed from cache).
	 */
	public static final long DEFAULT_CACHE_EXPIRATION_MINUTES = 15;
	/**
	 * Default duration of cache before it needs to be refreshed.
	 */
	public static final long DEFAULT_CACHE_REFRESH_MINUTES = 10;
	//private long cacheExpirationMinutes = DEFAULT_CACHE_EXPIRATION_MINUTES;
	private int maximumCacheSize = DEFAULT_CACHE_MAX_SIZE;
	private Duration expirationDuration = null;
	private Duration refreshDuration = null;
	
	/**
	 * @return the maximumCacheSize
	 */
	public final int getMaximumSize() {
		return maximumCacheSize;
	}
	/**
	 * @param aMaximumCacheSize the maximumCacheSize to set
	 */
	public final void setMaximumSize(final int aMaximumCacheSize) {
		this.maximumCacheSize = aMaximumCacheSize;
	}
	
	/**
	 * Refresh duration. While cache being refreshed, it will return currently hold value and trying to refresh it, until it is expired.
	 * @return Duration of cache before it needs to be refreshed
	 */
	public final Duration getRefreshDuration() {
		return refreshDuration;
	}
	
	/**
	 * Refresh duration. While cache being refreshed, it will return currently hold value and trying to refresh it, until it is expired.
	 * @param newDuration Duration of cache before it is expired.
	 */
	public final void setRefreshDuration(final Duration newDuration) {
		refreshDuration = newDuration;
	}
	
	/**
	 * Duration of cache before it is expired (ie removed from cache).
	 * @return Duration of cache before it is expired.
	 */
	public final Duration getExpirationDuration() {
		return expirationDuration;
	}
	
	/**
	 * Duration of cache before it is expired (ie removed from cache).
	 * @param newDuration Duration of cache before it is expired.
	 */
	public final void setExpirationDuration(final Duration newDuration) {
		expirationDuration = newDuration;
	}
	
	/**
	 * Default setting for Cache Option.
	 * @return A new Cacheoption which has Default setting for Cache Option
	 */
	public static CacheOption getDefaultRecommendedSetting() {
		CacheOption result = new CacheOption();
		Duration expirationDuration = Duration.ofMinutes(DEFAULT_CACHE_EXPIRATION_MINUTES);
		Duration refreshDuration = Duration.ofMinutes(DEFAULT_CACHE_REFRESH_MINUTES);
		
		result.setExpirationDuration(expirationDuration);
		result.setRefreshDuration(refreshDuration);
		return result;
	}
}
