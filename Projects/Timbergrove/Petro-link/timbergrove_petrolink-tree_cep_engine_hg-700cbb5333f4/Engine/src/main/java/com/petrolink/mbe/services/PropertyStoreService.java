package com.petrolink.mbe.services;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.propstore.DataSourcePropertyStore;
import com.petrolink.mbe.propstore.PropertyStore;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.services.Service;

/**
 * Manages the lifetime of a PropertyStore
 * @author langj
 *
 */
public final class PropertyStoreService extends Service {
	private static final Logger logger = LoggerFactory.getLogger(PropertyStoreService.class);
	
	private DataSourcePropertyStore propertyStore;

	@Override
	public void load(Element e) throws EngineException {
		super.load(e);
	}
	
	@Override
	public void startService() throws EngineException {
		logger.info("Starting PropertyStoreService");
		try {
			propertyStore = new DataSourcePropertyStore(ServiceAccessor.getAlertsService().getAlertsDataSource());
			logger.info("Started PropertyStoreService");
		} catch(Exception error) {
			logger.error("Failed to start PropertyStoreService correctly", error);
		}
		
	}

	@Override
	public void stopService() {
		logger.info("Stopping PropertyStoreService");
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Get the PropertyStore instance
	 * @return A PropertyStore
	 */
	public PropertyStore getPropertyStore() {
		return propertyStore;
	}
}
