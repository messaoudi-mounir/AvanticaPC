package com.petrolink.mbe.util;

public class EnumHelper {

	/**
	 * Search Enum value of Enum ignorecase 
	 * @param enumeration Enumeration class 
	 * @param enumString
	 * @return Null if matching enum not found, the Enum value otherwise
	 */
	public static <T extends Enum<?>> T valueOfIgnoreCase(Class<T> enumeration,
	        String enumString) {
	    for (T each : enumeration.getEnumConstants()) {
	        if (each.name().compareToIgnoreCase(enumString) == 0) {
	            return each;
	        }
	    }
	    return null;
	}
}
