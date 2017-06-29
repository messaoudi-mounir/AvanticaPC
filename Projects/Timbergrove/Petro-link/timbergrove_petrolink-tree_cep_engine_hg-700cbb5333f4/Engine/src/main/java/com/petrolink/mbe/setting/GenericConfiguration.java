package com.petrolink.mbe.setting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Generic configuration
 * @author aristo
 *
 */
public class GenericConfiguration implements Map<String,Object> {

	private HashMap<String,Object> parameters = new HashMap<>();

	private String name;
	
	@Override
	public int size() {
		return parameters.size();
	}

	@Override
	public boolean isEmpty() {
		return parameters.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return parameters.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return parameters.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return parameters.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return parameters.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return parameters.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		parameters.putAll(m);
	}

	@Override
	public void clear() {
		parameters.clear();
	}

	@Override
	public Set<String> keySet() {
		return parameters.keySet();
	}

	@Override
	public Collection<Object> values() {
		return parameters.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return parameters.entrySet();
	} 
	
	public Object getFromPath(String path) {
		Object source = null;
		if (StringUtils.isNotBlank(path)) {
			traverse(parameters,0,path.split("\\."));
		}
		return source;
	}
	
	/**
	 * Traverse through Cascaded path
	 * @param map
	 * @param level
	 * @param dotPath
	 * @return
	 */
	private static Object traverse(Map<String,Object> map, int level, String[] dotPath) {
        if (map == null) {return null;}
        String currentKey = dotPath[level];
        Object result = map.get(currentKey);

        int childLevel = level+1;
        if (dotPath.length <= childLevel) {
            return result;
        } else if (result instanceof Map){
            @SuppressWarnings("unchecked")
			Map<String,Object> childmap =  (Map<String,Object>) result;
            return traverse(childmap,childLevel,dotPath);
        } else {
            return null;
        }
    }

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}
	
}


