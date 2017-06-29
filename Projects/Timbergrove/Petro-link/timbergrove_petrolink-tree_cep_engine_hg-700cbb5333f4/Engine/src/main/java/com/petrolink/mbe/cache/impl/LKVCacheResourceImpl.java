package com.petrolink.mbe.cache.impl;

import java.util.Objects;
import java.util.UUID;

/**
 * Last Known Value Cache Resource Implementation
 * @author paul
 *
 */
public class LKVCacheResourceImpl extends CacheResourceImpl {
	private final LKVCacheImpl cache;	

	/**
	 * Initialize a local cache
	 * @param uuid
	 */
	public LKVCacheResourceImpl(UUID uuid) {
		super(uuid);
		this.cache = null;
	}
	
	/**
	 * @param cache
	 * @param uuid
	 */
	public LKVCacheResourceImpl(LKVCacheImpl cache, UUID uuid) {
		super(uuid);
		this.cache = Objects.requireNonNull(cache);
	}

	/**
	 * @return the Last Known Value Cache
	 */
	public LKVCacheImpl getCache() {
		return this.cache;
	}
}
