package com.petrolink.mbe.parser;

import java.util.Map;

import com.smartnow.engine.event.Event;
import com.smartnow.engine.triggers.Trigger;

/**
 * Utility class to help resolve parser context items.
 * @author aristo
 *
 */
public final class ParserContext {

	private static final String EVENT_KEY = "event";
	private static final String PROPERTIES_KEY = "properties";
	private static final String TRIGGER_KEY = "trigger";
	
	private ParserContext() {
		//Hide utility class
	}
	
	/**
	 * Get Event from the context dictionary. Equals to context.get({@link #EVENT_KEY})
	 * @param context Context dictionary
	 * @return Event object
	 */
	public static Event getEvent(final Map<String, Object> context) {
		return (Event) context.get(EVENT_KEY);
	}
	
	/**
	 * Get AMQP Basic Properties from the context dictionary. Equals to context.get({@link #PROPERTIES_KEY})
	 * @param context Context dictionary
	 * @return Dictionary object containing  AMQP Basic Properties
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAmqpBasicProperties(final Map<String, Object> context) {
		return (Map<String, Object>) context.get(PROPERTIES_KEY);
	}

	/**
	 * Get Trigger from the context dictionary. Equals to context.get({@link #TRIGGER_KEY})
	 * @param context Context dictionary
	 * @return Trigger object
	 */
	public static Trigger getTrigger(final Map<String, Object> context) {
		return (Trigger) context.get(TRIGGER_KEY);
	}
	
}
