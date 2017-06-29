package com.petrolink.mbe.model.channel;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import com.petrolink.mbe.util.DateTimeHelper;
import com.petrolink.mbe.util.HashHelper;

/**
 * An immutable index and value pair. A DataPoint is comparable to others by index if both have the same index type.
 * @author Jose Luis Moya Sobrado
 * @author langj
 */
public final class DataPoint implements Comparable<DataPoint> {
	private final Object index;
	private final Object value;
	
	/**
	 * Construct a DataPoint from a given index and value
	 * @param index
	 * @param value
	 */
	public DataPoint(Object index, Object value) {
		this.index = Objects.requireNonNull(index, "index must not be null");
		this.value = Objects.requireNonNull(value, "value must not be null");
		
		assert isValidIndexType(this.index) : "not a valid index type";
		assert isValidValueType(this.value) : "not a valid value type";
	}
	
	/**
	 * Gets the index of the data point.
	 * Must be one of: OffsetDateTime, Double, or Long. 
	 * @return Index of Data Value
	 */
	public Object getIndex() {
		return index;
	}
	
	/**
	 * Gets the index of the data point in its numeric form. For OffsetDateTime this is the epoch time in milliseconds.
	 * @return The equivalent numeric value.
	 */
	public double getIndexNumber() {
		return numericValue(index);
	}

	/**
	 * Gets the value of the data point. This supports scripting like datavalue.value = 3
	 * Must be one of: Long, Double, String, OffsetDateTime, byte[], double[], or String[]
	 * @return existing value or first value on the array
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * @return the value as a Number if it is one, otherwise zero.
	 */
	public Number getValueAsNumber() {
		if (value instanceof Number)
			return (Number) value;
		return 0L;
	}
	
	@Override
	public int hashCode() {
		return HashHelper.combineHashCodes(index.hashCode(), value.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataPoint) {
			DataPoint other = (DataPoint) obj;
			return index.equals(other.index) && value.equals(other.value);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "(" + index.toString() + ", " + value.toString() + ")";
	}

	/**
	 * Compares the index of the current data point with another data point. The value is ignored.
	 */
	@Override
	public int compareTo(DataPoint o) {
		return (int) numericSubtract(index, o.index);
		// TODO: compareTo should be similar to equals(), use a custom comparator to compare only the indices
		//int ii = DataPoint.compareIndices(index, o.index);
		//if (ii != 0)
		//	return ii;
		//if (value instanceof Comparable && value.getClass().isInstance(o.value))
		//	return ((Comparable) value).compareTo(o.value);
		//return -1;
	}

	/**
	 * Try parsing an index from a string, and return null of failure.
	 * First attempts to parse an Long, Double, then OffsetDateTime.
	 * 
	 * This method is relatively slow and should not be used for high frequency processing.
	 * @param value
	 * @return A Long, Double, OffsetDateTime, or null if the string is not a valid index type.
	 */
	public static Object tryParseIndex(String value) {
		Objects.requireNonNull(value, "value must not be null");
		
		if (!value.contains("T")) {
			if (!value.contains(".")) {
				try {
					return Long.parseLong(value);
				} catch (NumberFormatException ex) {
				}
			}
			
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException ex) {
			}
		}
		
		try {
			return OffsetDateTime.parse(value);
		} catch (DateTimeParseException ex) {
		}
		
		return null;
	}
	
	/**
	 * Parse an index from a string, and throw on failure.
	 * First attempts to parse an Long, Double, then OffsetDateTime.
	 * 
	 * This method is relatively slow and should not be used for high frequency processing.
	 * @param value
	 * @return A Long, Double, OffsetDateTime, or null if the string is not a valid index type.
	 * @throws NumberFormatException parsing failed
	 */
	public static Object parseIndex(String value) {
		Object index = tryParseIndex(value);
		if (index == null)
			throw new NumberFormatException("could not parse index: " + value);
		return index;
	}
	
	/**
	 * Try converting an object to a valid index type (Long, Double, or OffsetDateTime).
	 * 
	 * If the value is a Number, it is either returned as-is or widened into a Double or Long.
	 * If the value is a String, it is parsed as a Long, Double, or OffsetDateTime.
	 * If the value is an OffsetDateTime, it is returned as-is.
	 * Otherwise, null is returned.
	 * @param value
	 * @return The converted value, or null.
	 */
	public static Object tryConvertIndex(Object value) {
		if (value instanceof Number) {
			if (value instanceof Double)
				return value;
			if (value instanceof Long)
				return value;
			if (value instanceof Integer)
				return (long) ((Integer) value);
			if (value instanceof Float)
				return (double) ((Float) value);
			return ((Number) value).longValue();
		}
		if (value instanceof String)
			return tryParseIndex((String) value);
		if (value instanceof OffsetDateTime)
			return value;
		return null;
	}
	
	/**
	 * Compares two indices
	 * @param left
	 * @param right
	 * @return A number less than zero if left is less than right, a number greater than zero if left is greater than right, or zero if the two are equal.
	 */
	public static int compareIndices(Object left, Object right) {
		double r = numericSubtract(left, right);
		return r < 0 ? -1 : (r > 0 ? 1 : 0);
	}
	
	/**
	 * Gets the difference between two indices. For OffsetDateTime, it is the difference in milliseconds.
	 * @param left
	 * @param right
	 * @return The difference between two indices.
	 */
	public static double numericSubtract(Object left, Object right) {
		if (left instanceof OffsetDateTime)
			return DateTimeHelper.toEpochMillisDouble((OffsetDateTime) left) - DateTimeHelper.toEpochMillisDouble((OffsetDateTime) right);
		if (left instanceof Double)
			return ((Double) left) - ((Double) right);
		if (left instanceof Long)
			return ((Long) left) - ((Long) right);
		throw new IllegalArgumentException("left index was not a valid index type");
	}
	
	/**
	 * Gets the index as a numeric value. For OffsetDateTime it is the value as unix epoch milliseconds.
	 * @param value
	 * @return The index as a numeric value.
	 */
	public static double numericValue(Object value) {
		if (value instanceof OffsetDateTime)
			return DateTimeHelper.toEpochMillisDouble((OffsetDateTime) value);
		if (value instanceof Double)
			return (Double) value;
		if (value instanceof Long)
			return (Long) value;
		throw new IllegalArgumentException("index was not a valid index type");
	}
	
	/**
	 * Check if an object is of a valid index type
	 * @param index
	 * @return True if the object is of a valid index type
	 */
	public static boolean isValidIndexType(Object index) {
		return index instanceof OffsetDateTime
				|| index instanceof Double
				|| index instanceof Long;
	}
	
	/**
	 * Check if an object is of a valid value type
	 * @param value
	 * @return True if the object is of a valid value type
	 */
	public static boolean isValidValueType(Object value) {
		return value instanceof Double
				|| value instanceof Long
				|| value instanceof String
				|| value instanceof ComplexValue
				|| value instanceof byte[]
				|| value instanceof double[]
				|| value instanceof String[]
				|| value instanceof OffsetDateTime;
	}
}
