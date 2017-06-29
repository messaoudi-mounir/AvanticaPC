package com.petrolink.mbe.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Provides helper methods for working with SQL objects.
 * @author langj
 *
 */
public class SQLHelper {
	/**
	 * Get a Double object from a ResultSet.
	 * @param rs
	 * @param columnLabel
	 * @return The double value from the column, or null if the column was null.
	 * @throws SQLException
	 */
	public static Double getDoubleOrNull(ResultSet rs, String columnLabel) throws SQLException {
		double v = rs.getDouble(columnLabel);
		return rs.wasNull() ? null : v;
	}
	
	/**
	 * Set a double parameter on a prepared statement. If the value is null the parameter is set to null.
	 * @param ps
	 * @param parameterIndex
	 * @param value
	 * @throws SQLException
	 */
	public static void setDoubleOrNull(PreparedStatement ps, int parameterIndex, Double value) throws SQLException {
		if (value != null)
			ps.setDouble(parameterIndex, value);
		else
			ps.setNull(parameterIndex, Types.DOUBLE);
	}
	
	/**
	 * Get an Integer object from a ResultSet.
	 * @param rs
	 * @param columnLabel
	 * @return The integer value from the column, or null if the column was null.
	 * @throws SQLException
	 */
	public static Integer getIntegerOrNull(ResultSet rs, String columnLabel) throws SQLException {
		int v = rs.getInt(columnLabel);
		return rs.wasNull() ? null : v;
	}
	
	/**
	 * Set an integer parameter on a prepared statement. If the value is null the parameter is set to null.
	 * @param ps
	 * @param parameterIndex
	 * @param value
	 * @throws SQLException
	 */
	public static void setIntegerOrNull(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
		if (value != null)
			ps.setInt(parameterIndex, value);
		else
			ps.setNull(parameterIndex, Types.INTEGER);
	}
	
	/**
	 * Get a timestamp as a long representing the number of milliseconds since the epoch.
	 * @param rs
	 * @param columnLabel
	 * @return A time in milliseconds
	 * @throws SQLException
	 */
	public static long getTimestampAsTime(ResultSet rs, String columnLabel) throws SQLException {
		Timestamp v = rs.getTimestamp(columnLabel);
		return v != null ? v.getTime() : 0;
	}
	
	/**
	 * Set a timestamp in a prepared statement to a time in milliseconds or null if the value is 0
	 * @param ps
	 * @param parameterIndex
	 * @param value
	 * @throws SQLException
	 */
	public static void setTimestampOrNull(PreparedStatement ps, int parameterIndex, long value) throws SQLException {
		if (value != 0)
			ps.setTimestamp(parameterIndex, new Timestamp(value));
		else
			ps.setTimestamp(parameterIndex, null);
	}
}
