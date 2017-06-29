package com.petrolink.mbe.setting;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 * Generic handler for Action extraction
 * @author aristo
 *
 */
public class ActionSource {
	private ActionSources sourceType;
	private String sourceKey;
	private String body;
	
	/**
	 * Get Source form context
	 * @param context
	 * @return Source Object from specified config
	 */
	public Object getSource(Map<String, Object> context) {
		Object source = null;
		if (ActionSources.CONTEXT == sourceType) {
			if (StringUtils.isBlank(sourceKey)) {
				source = context;
			} else{
				source = traverse(context,0,sourceKey.split("\\."));
			}
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
        } else if (result instanceof JSONObject){
        	JSONObject json = (JSONObject)result;
        	Map<String,Object> childmap = json.toMap();
        	return traverse(childmap,childLevel,dotPath);
        } else {
            return null;
        }
    }

	/**
	 * @return the sourceKey
	 */
	public final String getSourceKey() {
		return sourceKey;
	}

	/**
	 * @param sourceKey the sourceKey to set
	 */
	public final void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	/**
	 * @return the sourceType
	 */
	public final ActionSources getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType the sourceType to set
	 */
	public final void setSourceType(ActionSources sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @return the body
	 */
	public final String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public final void setBody(String body) {
		this.body = body;
	}
}
