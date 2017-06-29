package com.petrolink.mbe.services;

import java.util.HashMap;

/**
 * A service adapter that allows services to be registered per class and a fallback adapter in case the desired
 * service is not found. This is useful for unit testing.
 * @author langj
 *
 */
public class ServiceCollectionAdapter extends ServiceAdapter {
	private final HashMap<Class<?>, Object> services = new HashMap<>();
	private final ServiceAdapter fallback;
	
	/**
	 * Initialize the adapter with a fallback.
	 * @param fallback A fallback adapter or null.
	 */
	public ServiceCollectionAdapter(ServiceAdapter fallback) {
		this.fallback = fallback;
	}
	
	@Override
	public Object getService(Class<?> cls) {
		Object svc = services.get(cls);
		if (svc != null)
			return svc;
		return fallback != null ? fallback.getService(cls) : null;
	}

	@Override
	public Object getService(String name) {
		return fallback != null ? fallback.getService(name) : null;
	}

	/**
	 * Register a service using the specified class as a key
	 * @param cls A key class
	 * @param inst A service instance
	 */
	public void registerService(Class<?> cls, Object inst) {
		services.put(cls,  inst);
	}
}
