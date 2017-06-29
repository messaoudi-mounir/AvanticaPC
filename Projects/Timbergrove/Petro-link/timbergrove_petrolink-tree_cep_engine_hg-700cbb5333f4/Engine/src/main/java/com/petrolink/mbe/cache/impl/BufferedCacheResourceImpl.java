package com.petrolink.mbe.cache.impl;

import java.util.Objects;
import java.util.UUID;

/**
 * Last Known Value Cache Resource Implementation
 * @author paul
 *
 */
public class BufferedCacheResourceImpl extends CacheResourceImpl {
	private final BufferedCacheImpl cache;

	/**
	 * @param cache
	 * @param uuid
	 */
	public BufferedCacheResourceImpl(BufferedCacheImpl cache, UUID uuid) {
		super(uuid);
		this.cache = Objects.requireNonNull(cache);
	}

	/**
	 * Constructor for not global Buffered Caches
	 * @param uuid
	 */
	public BufferedCacheResourceImpl(UUID uuid) {
		super(uuid);
		this.cache = null;
	}

	/**
	 * @return the Last Known Value Cache
	 */
	public BufferedCacheImpl getCache() {
		return this.cache;
	}
}
