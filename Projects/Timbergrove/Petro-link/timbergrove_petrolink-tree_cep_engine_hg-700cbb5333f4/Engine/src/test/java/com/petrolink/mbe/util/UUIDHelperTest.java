package com.petrolink.mbe.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

/**
 * This class tests the UUIDHelper. 
 *
 */
public class UUIDHelperTest {
	private final String UUID1_STR         = "ec95b573-0739-4b80-95cc-b60b9567eace";
	private final String UUID1_STR_INVALID = "73b595ec-3907-804b-95cc-b60b9567eace";
	
	/**
	 * Test that bytes are correctly being swapped when converting to Java-endian (big-endian)
	 * to .NET-endian (little-endian + big-endian)
	 */
	@Test
	public void testDotNetConversion() {
		UUID valid = UUID.fromString(UUID1_STR);
		UUID invalid = UUID.fromString(UUID1_STR_INVALID);
		
		byte[] validBytes = UUIDHelper.toBytesDotNet(valid);
		
		UUID invalid2 = UUIDHelper.fromBytes(validBytes);
		
		Assert.assertEquals(invalid, invalid2);
		
		byte[] invalidBytes = UUIDHelper.toBytes(invalid);
		
		UUID valid2 = UUIDHelper.fromBytesDotNet(invalidBytes);
		
		Assert.assertEquals(valid,  valid2);
	}
	
	/**
	 * Test ensuring UUID fromStringFast return UUID must be empty when exception is supressed
	 */
	@Test
	public void testFromStringFastWhenExceptionIsSupressed() {
		//For suppress exception
		UUID mustBeEmpty1 = UUIDHelper.fromStringFast(null, true);
		UUID mustBeEmpty2 = UUIDHelper.fromStringFast("", true);
		UUID mustBeEmpty3 = UUIDHelper.fromStringFast("asadljfsdlcfvljsd", true);
		
		
		
		assertEquals("Null string must return EMPTY UUID", mustBeEmpty1, UUIDHelper.EMPTY);
		assertEquals("Empty string must return EMPTY UUID", mustBeEmpty2, UUIDHelper.EMPTY);
		assertEquals("Random string must return EMPTY UUID", mustBeEmpty3, UUIDHelper.EMPTY);
		
		UUID refernceImpl = UUID.fromString(UUID1_STR);
		UUID mustBeCorrect = UUIDHelper.fromStringFast(UUID1_STR, true);
		
		assertEquals("Incorrect UUID Implementation",refernceImpl, mustBeCorrect);
	}
	
	/**
	 * Test ensuring UUID fromStringFast throw correct exception
	 */
	@Test
	public void testFromStringFastWhenExceptionIsThrown() {
		//For null
		boolean illegalArgumentThrown = false;
		try {
			@SuppressWarnings("unused")
			UUID mustBeExc = UUIDHelper.fromStringFast(null);
		} catch(IllegalArgumentException ae) {
			illegalArgumentThrown = true;
		}
		assertTrue("Null string must throw exception", illegalArgumentThrown);
		
		//For empty string
		illegalArgumentThrown = false;
		try {
			@SuppressWarnings("unused")
			UUID mustBeExc = UUIDHelper.fromStringFast("");
		} catch(IllegalArgumentException ae) {
			illegalArgumentThrown = true;
		}
		assertTrue("Empty string must throw exception", illegalArgumentThrown);
		
		//For random string
		illegalArgumentThrown = false;
		try {
			@SuppressWarnings("unused")
			UUID mustBeExc = UUIDHelper.fromStringFast("asadljfsdlcfvljsd");
		} catch(IllegalArgumentException ae) {
			illegalArgumentThrown = true;
		}
		
		assertTrue("Random string must throw exception", illegalArgumentThrown);
		
		//Must not throw exception when handling is correct
		UUID refernceImpl = UUID.fromString(UUID1_STR);
		UUID mustBeCorrect = UUIDHelper.fromStringFast(UUID1_STR, false);
		
		assertEquals("Incorrect UUID Implementation",refernceImpl, mustBeCorrect);
	}
}
