package com.petrolink.mbe.util;

/**
 * Entry point helper class to print the JAR's version string.
 * @author langj
 *
 */
public final class PrintVersionString {
	/**
	 * Prints the version string.
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(ResourceHelper.getVersionString());
	}
}
