package com.petrolink.mbe.propstore;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * A service for persistently storing arbitrary properties.
 * @author langj
 *
 */
@SuppressWarnings("javadoc") // temporary
public interface PropertyStore {
	/**
	 * The unspecified type code, used when a type is not provided.
	 */
	public static final byte TYPE_UNSPECIFIED = 0;
	
	/**
	 * The type code for strings.
	 */
	public static final byte TYPE_STRING = 1;
	
	/**
	 * The type code for doubles.
	 */
	public static final byte TYPE_DOUBLE = 2;
	
	/**
	 * The type code for longs.
	 */
	public static final byte TYPE_LONG = 3;
	
	/**
	 * The type code for OffsetDateTime.
	 */
	public static final byte TYPE_DATETIME = 4;
	
	public Object getValue(String groupName, String key);
	
	public void setValue(String groupName, String key, Object value);
	
	public Map<String, Object> getValueMap(String groupName);
	
	public void setValueMap(String groupName, Map<String, Object> values);
	
	public void deleteGroup(String groupName);
	
	public static byte getPropertyType(Object value) {
		if (value != null) {
			if (value instanceof String)
				return TYPE_STRING;
			if (value instanceof Double)
				return TYPE_DOUBLE;
			if (value instanceof Long)
				return TYPE_LONG;
			if (value instanceof OffsetDateTime)
				return TYPE_DATETIME;
		}
		return TYPE_UNSPECIFIED;
	}
	
	public static byte validatePropertyType(Object value) {
		byte type = getPropertyType(value);
		if (value != null && type == TYPE_UNSPECIFIED)
			throw new IllegalArgumentException("unaccepted type: " + value.getClass());
		return type;
	}
}
