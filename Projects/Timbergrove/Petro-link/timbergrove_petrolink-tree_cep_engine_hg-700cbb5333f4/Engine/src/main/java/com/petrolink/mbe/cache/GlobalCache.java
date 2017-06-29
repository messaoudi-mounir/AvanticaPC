/**
 * 
 */
package com.petrolink.mbe.cache;

import java.util.UUID;

/**
 * @author Jose Luis Moya Sobrado
 *
 */
public interface GlobalCache  {	
	 /**
	 * @param id of the Resource
	 * @return the Resource for the provided id
	 */
	Resource getResource(String id);
	 
	 /**
	 * @param id
	 * @return the Resource with the given UUID
	 */
	Resource getResource(UUID id);
	
	/**
	 * Get the channel by UUID
	 * @param id
	 * @return the Channel with the given UUID
	 */
	ChannelCache getChannel(UUID id);
	
	/**
	 * Get the Channel by UUID String
	 * @param id
	 * @return the Channel with the given UUID
	 */
	ChannelCache getChannel(String id);
	
	 /**
	 * @param id
	 * @return Well Cache container with the given Id
	 */
	WellCache getWell(String id);

	 /**
	 * @param id
	 * @return Well Cache container with the given Id
	 */
	WellCache getWell(UUID id);
	
	/**
	 * Create a well cache with the specified UUID. Returns an existing cache if it already exists.
	 * @param id A well UUID.
	 * @return A new or existing well cache.
	 */
	WellCache getOrCreateWell(UUID id);
		
	/**
	 * @param id
	 */
	void removeWell(UUID id);
}
