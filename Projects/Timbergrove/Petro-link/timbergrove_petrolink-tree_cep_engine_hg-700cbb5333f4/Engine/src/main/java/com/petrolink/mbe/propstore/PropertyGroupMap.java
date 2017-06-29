package com.petrolink.mbe.propstore;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.map.AbstractHashedMap;

/**
 * Exposes a property group as a map. commit() can be called to commit changes made since the last commit to
 * the database.
 * Values cannot be null. Putting a null value in the map will be treated as a remove for that key.
 * WARNING: The old value for a key remains in memory until changed are committed.
 * @author langj
 *
 */
@SuppressWarnings("javadoc")
public final class PropertyGroupMap extends AbstractHashedMap<String, Object> {
	private final PropertyStore propertyStore;
	private final String groupName;
	private final HashMap<String, Object> changes = new HashMap<>();
	private boolean loading;
	
	/**
	 * Initialize the group map from a property store and group name
	 * @param propertyStore A property store
	 * @param groupName A group name
	 */
	public PropertyGroupMap(PropertyStore propertyStore, String groupName) {
		super(4);
		this.propertyStore = Objects.requireNonNull(propertyStore);
		this.groupName = Objects.requireNonNull(groupName);

		Map<String, Object> values = propertyStore.getValueMap(groupName);
		if (values != null) {
			loading = true;
			putAll(values);
			loading = false;
		}
	}
	
	/**
	 * Get the property store that this map uses
	 * @return The property store
	 */
	public PropertyStore getPropertyStore() {
		return propertyStore;
	}
	
	/**
	 * Get the name of the group this map handles
	 * @return The group name.
	 */
	public String getGroupName() {
		return groupName;
	}
	
	@Override
	public void clear() {
		for (Entry<String, Object> e : entrySet())
			changes.putIfAbsent(e.getKey(), e.getValue());
		super.clear();
	}
	
	@Override
	public Object put(String key, Object value) {
		if (value == null)
			return remove(key);
		if (!loading) {
			PropertyStore.validatePropertyType(value);
			changes.putIfAbsent(key, get(key));
		}
		return super.put(key, value);
	}
	
	@Override
	public Object remove(Object key) {
		Object prev = super.remove(key);
		if (prev != null)
			changes.putIfAbsent((String) key, prev);
		return prev;
	}
	
	public String getString(String key) {
		return (String) get(key);
	}
	
	public Double getDouble(String key) {
		return (Double) get(key);
	}
	
	public Long getLong(String key) {
		return (Long) get(key);
	}
	
	public OffsetDateTime getDateTime(String key) {
		return (OffsetDateTime) get(key);
	}
	
	public void putString(String key, String value) {
		put(key, value);
	}
	
	public void putDouble(String key, Double value) {
		put(key, value);
	}
	
	public void putLong(String key, Long value) {
		put(key, value);
	}
	
	public void putDateTime(String key, OffsetDateTime value) {
		put(key, value);
	}
	
	/**
	 * Commits all changes made to this map since the last commit to the database.
	 * @return the number of changes committed
	 */
	public int commit() {
		if (changes.isEmpty())
			return 0;
		// Previous changes to this map stored the OLD value for a key in the changes map.
		// Now we replace all old values in the changes map with the new values for the keys.
		// The changes map is then used to update the database.
		// This behavior ensures that updates to a value only go to database if the value is different by the time
		// commit is called.
		Iterator<Entry<String, Object>> it = changes.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> e = it.next();
			String key = e.getKey();
			if (containsKey(key)) {
				Object currentValue = get(key);
				if (Objects.equals(currentValue, e.getValue()))
					it.remove(); // no change, or is back to same value, since previous commit
				else
					e.setValue(currentValue);
			}
			else {
				if (e.getValue() == null)
					it.remove(); // value was added then removed before commit
				else
					e.setValue(null);
			}
		}
		if (changes.isEmpty())
			return 0;
		int changeCount = changes.size();
		propertyStore.setValueMap(groupName, changes);
		changes.clear();
		return changeCount;
	}
	
	/**
	 * Reloads the map from all values in the database. Any uncommitted changes will be lost.
	 * @return the new size of the map
	 */
	public int reload() {
		super.clear();
		changes.clear();
		
		Map<String, Object> values = propertyStore.getValueMap(groupName);
		if (values != null) {
			loading = true;
			putAll(values);
			loading = false;
		}
		
		return size();
	}
}