package com.petrolink.mbe.cache.impl;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.petrolink.mbe.cache.ChannelCache;
import com.petrolink.mbe.cache.GlobalCache;
import com.petrolink.mbe.cache.Resource;
import com.petrolink.mbe.cache.WellCache;

/**
 * 
 * @author Jose Luis Moya Sobrado
 */
public final class LKVCacheImpl implements GlobalCache {
	private ConcurrentHashMap<UUID, Resource> resources;
	private ConcurrentHashMap<UUID, WellCache> wells;
	
	/**
	 * Constructor
	 */
	public LKVCacheImpl() {
		resources = new ConcurrentHashMap<UUID, Resource>();
		wells = new ConcurrentHashMap<UUID, WellCache>();
	}

	@Override
	public Resource getResource(String uuid) {
		return resources.get(UUID.fromString(uuid));
	}

	@Override
	public Resource getResource(UUID uuid) {
		return resources.get(uuid);
	}
	
	@Override
	public ChannelCache getChannel(String id) {
		return getChannel(UUID.fromString(id));
	}
	
	@Override
	public ChannelCache getChannel(UUID id) {
		Resource r = resources.get(id);
		return r instanceof ChannelCache ? (ChannelCache) r : null;
	}

	@Override
	public WellCache getWell(String uuid) {
		return wells.get(UUID.fromString(uuid));
	}

	@Override
	public WellCache getWell(UUID uuid) {
		return wells.get(uuid);
	}
	
	@Override
	public WellCache getOrCreateWell(UUID uuid) {
		WellCache well = wells.get(uuid);
		if (well == null) {
			well = new WellCacheImpl(this, uuid);
			well = (WellCache) putOrGetResource(well);
			wells.put(uuid, well); // The resources map is the source of truth, so don't need to use putIfAbsent() here.
		}
		return well;
	}

	@Override
	public void removeWell(UUID uuid) {
		resources.remove(uuid);
		wells.remove(uuid);
	}

	/**
	 * Put a resource into the cache. Returns the existing resource if one with the same ID is already in the cache.
	 * @param resource
	 * @return
	 */
	protected Resource putOrGetResource(Resource resource) {
		Resource existing = resources.putIfAbsent(resource.getId(), resource);
		return existing != null ? existing : resource;
	}
	
	protected void removeResource(String uuid) {
		resources.remove(uuid);
	}
}
