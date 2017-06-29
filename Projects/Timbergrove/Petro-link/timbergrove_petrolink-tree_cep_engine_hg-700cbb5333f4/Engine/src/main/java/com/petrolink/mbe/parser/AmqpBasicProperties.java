package com.petrolink.mbe.parser;

import java.util.Map;

/**
 * Helper Class For resolving Data Content of Basic Properties.
 * @author aristo
 */
public final class AmqpBasicProperties {

	private AmqpBasicProperties() {
		//Hide Utility class
	}

	/**
	 * Key parameter of getStringValue() to get Message Type.
	 */
	public static final String SOURCE_TYPE_CONTEXT_KEY = "type";
	
	/**
	 * Key parameter of getStringValue() to get Routing Key.
	 */
	public static final String ROUTINGKEY_KEY = "routingKey";
	
	/**
	 * Key parameter of getStringValue() to get Content Type.
	 */
	public static final String CONTENT_TYPE_KEY = "contentType";
	
	/**
	 * Key parameter of getStringValue() to get Content Encoding.
	 */
	public static final String CONTENT_ENCODING_KEY = "contentEncoding";
	
	/**
	 * Key parameter of getStringValue() to get Correlation Id.
	 */
	public static final String CORRELATION_ID_KEY = "correlationId";
	
	/**
	 * Get String Value from Properties Map.
	 * @param properties The properties Map to be searched
	 * @param key The Key to retrieve object from property map
	 * @return String value of such properties, null if not available
	 */
	public static String getStringValue(final Map<String, Object> properties, final String key) {
		if (properties == null) {
			return null;
		}
		
		//Check Content
		String propertyString = null;
		Object propertyObject = properties.get(key);
		if (propertyObject == null) {
			return null;
		} else if (propertyObject instanceof String) {
			propertyString = (String) propertyObject;
		} else {
			propertyString = propertyObject.toString();
		}
		
		return propertyString;
	}
	
	/**
	 * get Type from properties using {@link #getStringValue(Map, String)} with key {@link #SOURCE_TYPE_CONTEXT_KEY}.
	 * @param properties Properties container
	 * @return Type of the message
	 */
	public static String getType(final Map<String, Object> properties)	{
		return getStringValue(properties, SOURCE_TYPE_CONTEXT_KEY);
	}
	
	/**
	 * get RoutingKey from properties using {@link #getStringValue(Map, String)} with key {@link #ROUTINGKEY_KEY}.
	 * @param properties Properties container
	 * @return Routing Key of the message
	 */
	public static String getRoutingKey(final Map<String, Object> properties)	{
		return getStringValue(properties, ROUTINGKEY_KEY);
	}
	
	/**
	 * get Content Type from properties using {@link #getStringValue(Map, String)} with key {@link #CONTENT_TYPE_KEY}.
	 * @param properties Properties container
	 * @return Content Type of the message
	 */
	public static String getContentType(final Map<String, Object> properties)	{
		return getStringValue(properties, CONTENT_TYPE_KEY);
	}
	
	/**
	 * get Content Encoding from properties using {@link #getStringValue(Map, String)} with key {@link #CONTENT_ENCODING_KEY}.
	 * @param properties Properties container
	 * @return Content Encoding of the message
	 */
	public static String getContentEncoding(final Map<String, Object> properties)	{
		return getStringValue(properties, CONTENT_ENCODING_KEY);
	}
	
	/**
	 * get Correlation ID from properties using {@link #getStringValue(Map, String)} with key {@link #CORRELATION_ID_KEY}.
	 * @param properties Properties container
	 * @return Correlation ID  of the message
	 */
	public static String getCorrelationId(final Map<String, Object> properties)	{
		return getStringValue(properties, CORRELATION_ID_KEY);
	}
}
