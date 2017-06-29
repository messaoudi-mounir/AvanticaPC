package com.petrolink.mbe.services;

/**
 * Base class for adapters that the service accessor uses to get services
 */
public abstract class ServiceAdapter {
	/**
	 * Get a service by its class
	 * @param cls
	 * @return A matching service instance or null if not found
	 */
	public abstract Object getService(Class<?> cls);
	
	/**
	 * Get a service by its name
	 * @param name
	 * @return A matching service instance or null if not found
	 */
	public abstract Object getService(String name);
}