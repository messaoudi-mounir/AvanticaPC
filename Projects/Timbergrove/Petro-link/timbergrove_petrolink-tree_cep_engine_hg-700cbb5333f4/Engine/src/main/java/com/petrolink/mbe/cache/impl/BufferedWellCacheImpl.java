package com.petrolink.mbe.cache.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.petrolink.mbe.cache.ChannelCache;
import com.petrolink.mbe.cache.WellCache;
import com.petrolink.mbe.model.channel.DataPoint;

/**
 * @author Jose Luis Moya Sobrado
 * @author aristo
 */
public final class BufferedWellCacheImpl extends BufferedCacheResourceImpl implements WellCache {
	private ConcurrentHashMap<UUID, ChannelCache> channels = new ConcurrentHashMap<UUID, ChannelCache>();
	
	/**
	 * @param cache
	 * @param uuid 
	 */
	public BufferedWellCacheImpl(BufferedCacheImpl cache, UUID uuid) {
		super(cache, uuid);
	}

    @Override
	public ChannelCache getChannel(UUID id) {
		return channels.get(id);
	}
	
	@Override
	public ChannelCache getChannel(String id) {
		return channels.get(UUID.fromString(id));
	}
	
	@Override
	public ChannelCache getOrCreateChannel(UUID id) {
		ChannelCache c = channels.get(id);
		if (c == null) {
			// This might be called by multiple threads, so LKVCacheImpl.putOrGetResource() ensures only the first put
			// for a specified ID succeeds and that subsequent puts return the ID of the existing resource.
			// The LKVCache object is the source of truth so the first one to make it into there becomes the
			// value returned.
			c = new BufferedChannelCacheImpl(getCache(), this, id);
			c = (ChannelCache) getCache().putOrGetResource(c);
			channels.put(id, c);
		}
		return c;
	}

	@Override
	public List<ChannelCache> getAllChannels() {
		return new ArrayList<ChannelCache>(channels.values());
	}

	@Override
	public boolean addDataPoint(UUID channelId, DataPoint newDataValue) {
		ChannelCache channel = getChannel(channelId);
		if (channel != null)	{
			channel.addDataPoint(newDataValue);
			return true;
		}
		return false;
	}
}
