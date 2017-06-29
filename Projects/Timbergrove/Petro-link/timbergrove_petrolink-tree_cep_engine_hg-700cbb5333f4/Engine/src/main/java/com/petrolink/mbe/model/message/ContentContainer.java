package com.petrolink.mbe.model.message;

import java.util.Map;

/**
 * Container for alert detail
 * @author aristo
 *
 */
public class ContentContainer {
	/**
	 * Default container key
	 */
	public static final String DEFAULT_VALUE_KEY ="value";
	/**
	 * Default container key for list
	 */
	public static final String DEFAULT_VALUELIST_KEY ="values";
	private String contentType;
	private Map<String,Object> content;

	/**
	 * @return the contentType
	 */
	public final String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public final void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * @return the content
	 */
	public final Map<String,Object> getContent() {
		return content;
	}

	/**
	 * @param map the content to set
	 */
	public final void setContent(Map<String,Object> map) {
		this.content = map;
	}
	
}
