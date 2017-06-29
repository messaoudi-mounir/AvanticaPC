package com.petrolink.mbe.propstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of PropertyStore using a SQL DataSource.
 * @author langj
 *
 */
@SuppressWarnings("javadoc") // temporary
public final class DataSourcePropertyStore implements PropertyStore {
	private static final Logger logger = LoggerFactory.getLogger(DataSourcePropertyStore.class);
	
	private final DataSource dataSource;
	
	public DataSourcePropertyStore(DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource);
	}

	@Override
	public Object getValue(String group, String key) {
		return getValueCore(group, key, TYPE_UNSPECIFIED);
	}

	@Override
	public void setValue(String group, String key, Object value) {
		if (value != null) {
			byte type = PropertyStore.validatePropertyType(value);
			setValueCore(group, key, value, type);
		} else {
			setValueCore(group, key, null, TYPE_UNSPECIFIED);
		}
	}

	@Override
	public Map<String, Object> getValueMap(String group) {
		return getValueMapCore(group);
	}

	@Override
	public void setValueMap(String group, Map<String, Object> values) {
		for (Entry<String, Object> e : values.entrySet()) {
			PropertyStore.validatePropertyType(e.getValue());
		}
		setValueMapCore(group, values);
	}
	
	@Override
	public void deleteGroup(String groupName) {
		deleteGroupCore(Objects.requireNonNull(groupName));
	}
	
	private Object getValueCore(String group, String key, byte type) {
		try (Connection conn = dataSource.getConnection()) {
			EntryInfo e = executeGetEntry(conn, group, key);
			if (e == null)
				return null;
			if (type != TYPE_UNSPECIFIED && e.type != type)
				throw new ClassCastException("existing data type mismatch");
			return toRequestedType(type, e.doubleValue, e.stringValue);
		} catch (SQLException e) {
			logger.error("Exception while getting property", e);
			return null;
		}
	}
	
	private HashMap<String, Object> getValueMapCore(String group) {
		try (Connection conn = dataSource.getConnection()) {
			ArrayList<EntryInfo> entries = executeGetEntries(conn, group);
			if (entries == null)
				return null;
			HashMap<String, Object> result = new HashMap<>(entries.size());
			for (EntryInfo e : entries)
				result.put(e.key, toRequestedType(e.type, e.doubleValue, e.stringValue));
			return result;
		} catch (SQLException e) {
			logger.error("Exception while getting map", e);
			return null;
		}
	}
	
	private void setValueCore(String group, String key, Object value, byte type) {
		try (Connection conn = dataSource.getConnection()) {
			if (value == null) {
				executeDeleteEntry(conn, group, key);
			} else {
				assert type != TYPE_UNSPECIFIED;
				Double dv = toDoubleValue(type, value);
				String sv = toStringValue(type, value);
				executeSetEntry(conn, group, key, type, dv, sv);
			}
		} catch (SQLException e) {
			logger.error("Exception while setting string", e);
		}
	}
	
	private void setValueMapCore(String group, Map<String, Object> values) {
		try (Connection conn = dataSource.getConnection()) {
			conn.setAutoCommit(false);
			try {
				for (Entry<String, Object> mapEntry : values.entrySet()) {
					String key = mapEntry.getKey();
					Object value = mapEntry.getValue();
					if (value == null) {
						executeDeleteEntry(conn, group, key);
					} else {
						byte type = PropertyStore.getPropertyType(value);
						assert type != TYPE_UNSPECIFIED : "type should already be validated";
						Double dv = toDoubleValue(type, value);
						String sv = toStringValue(type, value);
						executeSetEntry(conn, group, key, type, dv, sv);
					}
				}
				conn.commit();
			}
			catch (Exception e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			logger.error("Exception while setting string", e);
		}
	}
	
	private void deleteGroupCore(String group) {
		try (Connection conn = dataSource.getConnection()) {
			executeDeleteGroup(conn, group);
		} catch (SQLException e) {
			logger.error("Exception while deleting group", e);
		}
	}
	
	private EntryInfo executeGetEntry(Connection conn, String group, String key) throws SQLException {
		final String query = "SELECT type, dv, sv FROM propertyEntry WHERE grp = ? AND key = ?";

		try (PreparedStatement st = conn.prepareStatement(query)) {
			st.setString(1, group);
			st.setString(2, key);
			try (ResultSet rs = st.executeQuery()) {
				if (!rs.next())
					return null;
				byte type = rs.getByte(1);
				double dv = rs.getDouble(2);
				Double dvo = rs.wasNull() ? null : dv;
				String sv = rs.getString(3);
				return new EntryInfo(key, type, dvo, sv);
			}
		}
	}
	
	private ArrayList<EntryInfo> executeGetEntries(Connection conn, String group) throws SQLException {
		final String query = "SELECT key, type, dv, sv FROM propertyEntry WHERE grp = ?";
		
		ArrayList<EntryInfo> results = null;
		try (PreparedStatement st = conn.prepareStatement(query)) {
			st.setString(1, group);
			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					if (results == null)
						results = new ArrayList<>();
					String key = rs.getString(1);
					byte type = rs.getByte(2);
					double dv = rs.getDouble(3);
					Double dvo = rs.wasNull() ? null : dv;
					String sv = rs.getString(4);
					results.add(new EntryInfo(key, type, dvo, sv));
				}
			}
		}
		return results;
	}
	
	private void executeDeleteEntry(Connection conn, String group, String key) throws SQLException {
		final String query = "DELETE FROM propertyEntry WHERE grp = ? AND key = ?";
		
		try (PreparedStatement st = conn.prepareStatement(query)) {
			st.setString(1, group);
			st.setString(2, key);
			st.executeUpdate();
		}
	}
	
	private void executeSetEntry(Connection conn, String group, String key, byte type, Double dv, String sv) throws SQLException {
		final String query = "MERGE INTO propertyEntry (grp, key, type, dv, sv) KEY (grp, key) VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement st = conn.prepareStatement(query)) {
			st.setString(1, group);
			st.setString(2, key);
			st.setByte(3, type);
			if (dv != null)
				st.setDouble(4, dv);
			else
				st.setNull(4, Types.DOUBLE);
			if (sv != null)
				st.setString(5, sv);
			else
				st.setNull(5, Types.VARCHAR);
			st.executeUpdate();
		}
	}
	
	private void executeDeleteGroup(Connection conn, String group) throws SQLException {
		final String query = "DELETE FROM propertyEntry WHERE grp = ?";
		
		try (PreparedStatement st = conn.prepareStatement(query)) {
			st.setString(1, group);
			st.executeUpdate();
		}
	}
	
	private static Object toRequestedType(byte type, Double doubleValue, String stringValue) {
		switch (type) {
		case TYPE_DOUBLE:
			return doubleValue;
		case TYPE_LONG:
			return doubleValue != null ? (Long) doubleValue.longValue() : null;
		case TYPE_STRING:
			return stringValue;
		case TYPE_DATETIME:
			return stringValue != null ? OffsetDateTime.parse(stringValue) : null;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private static Double toDoubleValue(byte type, Object value) {
		switch (type) {
		case TYPE_DOUBLE:
			return (Double) value;
		case TYPE_LONG:
			return value != null ? (Double) ((Long) value).doubleValue() : null;
		case TYPE_STRING:
			return null;
		case TYPE_DATETIME:
			return null;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private static String toStringValue(byte type, Object value) {
		switch (type) {
		case TYPE_DOUBLE:
			return null;
		case TYPE_LONG:
			return null;
		case TYPE_STRING:
			return (String) value;
		case TYPE_DATETIME:
			return value != null ? ((OffsetDateTime) value).toString() : null;
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private static class EntryInfo {
		public final String key;
		public final byte type;
		public final Double doubleValue;
		public final String stringValue;
		
		EntryInfo(String key, byte type, Double doubleValue, String stringValue) {
			this.key = key;
			this.type = type;
			this.doubleValue = doubleValue;
			this.stringValue = stringValue;
		}
	}
}
