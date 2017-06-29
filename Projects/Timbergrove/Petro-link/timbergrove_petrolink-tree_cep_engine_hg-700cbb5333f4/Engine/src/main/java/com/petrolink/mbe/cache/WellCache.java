/**
 * 
 */
package com.petrolink.mbe.cache;

import java.util.List;
import java.util.UUID;

import com.petrolink.mbe.model.channel.DataPoint;

/**
 * @author Jose Luis Moya Sobrado
 * @author Aristo
 */
public interface WellCache extends Resource {
	/**
	 * @param id
	 * @return the Channel Cache
	 */
	ChannelCache getChannel(String id);
	
	/**
	 * Get a channel by its ID.
	 * @param id
	 * @return the Channel Cache
	 */
	ChannelCache getChannel(UUID id);
	
	/**
	 * Creates a channel cache, or gets an existing cache.
	 * @param id The channel UUID.
	 * @return A new or existing channel cache.
	 */
	ChannelCache getOrCreateChannel(UUID id);
	
	/**
	 * @return the List of Channel Caches
	 */
	List<ChannelCache> getAllChannels();

	/**
	 * A quick way to put DataValue inside specific channel cache in this Cache.
	 * @param channelId The identifier for the Channel to be inserted 
	 * @param newDataValue the new DataValue to be inserted
	 * @return True if ChannelCache is available, false otherwise
	 */
	boolean addDataPoint(UUID channelId, DataPoint newDataValue);
}
