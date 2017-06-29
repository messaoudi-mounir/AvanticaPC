package com.petrolink.mbe.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.petrolink.mbe.setting.CacheOption;

/**
 * Cache building helper.
 * @author aristo
 *
 */
public final class CacheBuilderHelper {
	
	private CacheBuilderHelper() {
		
	}
	
	/**
	 * Apply Cache option for specified Caffeine CacheBuilder .
	 * @param option
	 * @param cacheBuilder
	 */
	public static void applyCacheOption(final CacheOption option, final Caffeine<Object, Object> cacheBuilder ) {
		if (option == null) {
			return;
		}
		
		if (cacheBuilder != null) {
			cacheBuilder.maximumSize(option.getMaximumSize());
			
			Duration expireDuration = option.getExpirationDuration();
			if (expireDuration != null) {
				long expireDurationSeconds = expireDuration.getSeconds();
				cacheBuilder.expireAfterWrite(expireDurationSeconds, TimeUnit.SECONDS);
			}
			
			Duration refeshDuration = option.getRefreshDuration();
			if (refeshDuration != null) {
				long refreshDurationSeconds = refeshDuration.getSeconds();
				cacheBuilder.refreshAfterWrite(refreshDurationSeconds, TimeUnit.SECONDS);
			}
		}
		
		
	}
}
