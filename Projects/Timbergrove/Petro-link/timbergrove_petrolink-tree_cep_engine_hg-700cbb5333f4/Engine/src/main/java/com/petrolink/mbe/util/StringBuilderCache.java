package com.petrolink.mbe.util;

import java.util.Objects;

/**
 * Provides a thread-local cached StringBuilder. Allocated StringBuilders should be freed after use.
 * @author langj
 */
public final class StringBuilderCache {
	private static final int DEFAULT_CAPACITY = 16;
	private static final int MAX_CAPACITY = 360;
	
	private static final ThreadLocal<StringBuilder> cachedInstance = new ThreadLocal<>();
	
	private StringBuilderCache() {}
	
	/**
	 * Allocate a StringBuilder with the default capacity.
	 * @return A StringBuilder instance.
	 */
	public static StringBuilder allocate() {
		return allocate(DEFAULT_CAPACITY);
	}
	
	/**
	 * Allocate a StringBuilder with the specified capacity.
	 * @param capacity
	 * @return A StringBuilder instance with at least the specified capacity.
	 */
	public static StringBuilder allocate(int capacity) {
		if (capacity <= MAX_CAPACITY) {
			StringBuilder sb = cachedInstance.get();
			if (sb != null && capacity < sb.capacity()) {
				cachedInstance.set(null);
				sb.setLength(0);
				return sb;
			}
		}
		return new StringBuilder(capacity);
	}
	
	/**
	 * Free a StringBuilder instance back into the cache.
	 * @param sb
	 */
	public static void free(StringBuilder sb) {
		Objects.requireNonNull(sb);
		if (sb.capacity() <= MAX_CAPACITY)
			cachedInstance.set(sb);
	}
	
	/**
	 * Free a StringBuilder instance back into the cache and return its string value.
	 * @param sb
	 * @return The StringBuilder's string.
	 */
	public static String toStringAndFree(StringBuilder sb) {
		Objects.requireNonNull(sb);
		String s = sb.toString();
		if (sb.capacity() <= MAX_CAPACITY)
			cachedInstance.set(sb);
		return s;
	}
}
