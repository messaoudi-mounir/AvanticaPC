package com.petrolink.mbe.engine;

import com.petrolink.mbe.setting.PathSetting;

/**
 * Class for resolving runtime configuration for the engine.
 * @author aristo
 *
 */
public final class RuntimeConfig {
	private RuntimeConfig() {
		
	}
	
	/**
	 * Resolve Location from Path Setting.
	 * @param path PathSetting to be located.
	 * @return Actual location for specified path. 
	 */
	public static final String getLocationFromPathSetting(final PathSetting path) {
		if(path == null) {
			return null;
		}
		
		String location = null;
		if (path.isRelative()) {
			location = getDefaultLocation() + path.getValue();
		} else {
			location = path.getValue();
		}
		return location;
	}
	
	/**
	 * @return a string holding the default location
	 */
	public static final String getDefaultLocation() {
		String location = null;
		if (System.getProperty("smartnow.resources") != null) {
			location = System.getProperty("smartnow.resources").trim();
		} else {
			location = System.getProperty("user.dir").trim();
		}
		return location;
	}
}
