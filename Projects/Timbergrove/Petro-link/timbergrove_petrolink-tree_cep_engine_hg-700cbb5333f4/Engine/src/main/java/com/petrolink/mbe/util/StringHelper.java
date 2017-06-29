package com.petrolink.mbe.util;

/**
 * A helper class for working with strings
 * @author langj
 *
 */
public final class StringHelper {
	/**
	 * Join a sequence of objects by using String.valueOf(...) on each
	 * @param delimiter
	 * @param elements
	 * @return A joined string
	 */
	public static String join(CharSequence delimiter, Object... elements) {
		String[] strs = new String[elements.length];
		for (int i = 0; i < elements.length; i++)
			strs[i] = String.valueOf(elements[i]);
		return String.join(delimiter, strs);
	}
}
