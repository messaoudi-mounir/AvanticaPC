package com.petrolink.mbe.util;

import java.util.Map;

import org.json.JSONObject;

/**
 * Contains helper methods for working with JSON objects.
 * @author langj
 *
 */
public final class JSONHelper {
	private JSONHelper() {}
	
	/**
	 * Updates a JSON object with properties from another. Values on the target are overwritten if they already exist.
	 * @param target
	 * @param source
	 */
	public static void update(JSONObject target, JSONObject source) {
		for (String key : source.keySet())
			target.put(key, source.get(key));
	}
	
	/**
	 * Updates a string map with properties from a JSONObject. Values are converted to strings. Keys with a
	 * JSONObject.NULL value are removed from the target map.
	 * @param target
	 * @param source 
	 */
	public static void update(Map<String, String> target, JSONObject source) {
		for (String key : source.keySet()) {
			Object value = source.get(key);
			if (value != null) {
				if (value != JSONObject.NULL)
					target.put(key, value.toString());
				else
					target.remove(key);
			}
		}
	}
	
	/**
	 * Updates a JSONObject with entries from a string map. Null values, if allowed in the source, are not included.
	 * @param target
	 * @param source
	 */
	public static void update(JSONObject target, Map<String, String> source) {
		for (String key : source.keySet()) {
			String value = source.get(key);
			if (value != null)
				target.put(key, value);
		}
	}
}
