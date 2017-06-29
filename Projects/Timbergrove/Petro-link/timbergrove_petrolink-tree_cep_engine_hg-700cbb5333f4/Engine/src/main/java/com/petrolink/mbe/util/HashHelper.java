package com.petrolink.mbe.util;

/**
 * Contains helper methods for implementing the Object.hashCode() method and any other class that uses simple hashing.
 * @author langj
 *
 */
public final class HashHelper {
	/**
	 * A byte-sized prime number.
	 */
	public static final byte PRIME_BYTE = (byte) 31;
	
	/**
	 * A short-sized prime number.
	 */
	public static final short PRIME_SHORT = (short) 27427;
	
	/**
	 * An integer-sized prime number.
	 */
	public static final int PRIME_INTEGER = 15486893;
	
	/**
	 * Combine two hash codes
	 * @param h1
	 * @param h2
	 * @return Combined hash code
	 */
	public static int combineHashCodes(int h1, int h2) {
		return (((h1 << 5) + h1) ^ h2);
	}
	
	/**
	 * Combine three hash codes
	 * @param h1
	 * @param h2
	 * @param h3
	 * @return Combined hash code
	 */
	public static int combineHashCodes(int h1, int h2, int h3) {
		return (((h1 << 5) + h1) ^ (((h2 << 5) + h2) ^ h3));
	}
	
	/**
	 * Combine four hash codes
	 * @param h1
	 * @param h2
	 * @param h3
	 * @param h4
	 * @return Combined hash code
	 */
	public static int combineHashCodes(int h1, int h2, int h3, int h4) {
		h1 = (((h1 << 5) + h1) ^ h3);
		h2 = (((h2 << 5) + h2) ^ h4);
		return (((h1 << 5) + h1) ^ h2);
	}
}
