package com.petrolink.mbe.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Provides helper methods for working with dates and times.
 * @author langj
 *
 */
public final class DateTimeHelper {
	/**
	 * The Unix time epoch, required to convert DateTime objects to and from Java's OffsetDateTime.
	 */
	public static final OffsetDateTime UNIX_EPOCH = OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
	
	private DateTimeHelper() {}
	
	/**
	 * Convert a number representing Unix Epoch time in microseconds to OffsetDateTime with ZoneOffset.UTC.
	 * @param value The value to be converted
	 * @return An equivalent OffsetDateTime instance
	 */
	public static OffsetDateTime fromEpochMicros(long value) {
		return UNIX_EPOCH.plus(value, ChronoUnit.MICROS);
	}

	/**
	 * Convert a number representing Unix Epoch time in milliseconds to OffsetDateTime with ZoneOffset.UTC.
	 * @param value The value to be converted
	 * @return An equivalent OffsetDateTime instance
	 */
	public static OffsetDateTime fromEpochMillis(long value) {
		return UNIX_EPOCH.plus(value, ChronoUnit.MILLIS);
	}
		
	/**
	 * Convert OffsetDateTime to a long representing Unix Epoch time in microseconds.
	 * @param value The value to be converted
	 * @return An equivalent long value
	 */
	public static long toEpochMicros(OffsetDateTime value) {
		return ChronoUnit.MICROS.between(UNIX_EPOCH, value);
	}

	/**
	 * Convert OffsetDateTime to a long representing Unix Epoch time in milliseconds.
	 * @param value The value to be converted
	 * @return An equivalent long value
	 */
	public static long toEpochMillis(OffsetDateTime value) {
		return ChronoUnit.MILLIS.between(UNIX_EPOCH, value);
	}
	
	/**
	 * Convert OffsetDateTime to a double representing Unix Epoch time in milliseconds.
	 * @param value The value to be converted
	 * @return An equivalent double value
	 */
	public static double toEpochMillisDouble(OffsetDateTime value) {
		// PERF: This code specifically avoids allocating a new Instant or any other object.
		//       Prefer multiplication instead of division for nanoseconds.
		return ((double) value.toEpochSecond() * 1000.0) + ((double) value.getNano() * 0.000001);
	}
}
