package com.petrolink.mbe.services;

import java.util.List;
import java.util.Properties;

import com.smartnow.engine.IFlow;
import com.smartnow.engine.xflow.Flow;

/**
 * Provides a facade for the Engine that can be replaced for unit testing.
 * @author langj
 *
 */
public interface EngineService {
	/**
	 * Get all existing flows.
	 * @return A list of flows.
	 */
	List<IFlow> getAllFlows();
	
	/**
	 * Publish a single flow
	 * @param flow
	 */
	void publishFlow(Flow flow);
	
	/**
	 * Gets the user defined properties
	 * @return a properties map
	 */
	Properties getUserProperties();
}