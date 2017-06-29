package com.petrolink.mbe.model.channel;

import java.io.StringReader;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Base class for complex data point values that are parsed from strings.
 * @author langj
 *
 */
public abstract class ComplexValue {
	private final ComplexValueType type;
	private final String source;
	private static final XMLInputFactory factory = XMLInputFactory.newInstance();
	
	protected ComplexValue(ComplexValueType type, String source) {
		this.type = type;
		this.source = source;
	}
	
	/**
	 * Gets the type of this ComplexValue, which will be one of the TYPE_* constants.
	 * @return The enumerated type
	 */
	public final ComplexValueType getType() {
		return type;
	}
	
	/**
	 * Get the source string this was parsed from.
	 * @return The source string this was parsed from
	 */
	public final String getSource() {
		return source;
	}
	
	@Override
	public String toString() {
		return source;
	}
	
	/**
	 * Copy the properties of this value into a map. This is used for providing contexts for templates and
	 * JSON serialize
	 * @return a map containing the property name to value entries
	 */
	public abstract Map<String, Object> toMap();
	
	abstract void read(XMLStreamReader reader) throws XMLStreamException;
	
	/**
	 * Try parsing a ComplexValue from a string. Null is returned if the string is null or not any kind of
	 * valid ComplexValue
	 * @param source A source string to parse
	 * @return The parsed ComplexValue or null if source is not a valid ComplexValue.
	 */
	public static ComplexValue tryParse(String source) {
		if (source == null || !source.startsWith("<"))
			return null;

		// PERF: Parsing here uses an XMLStreamReader directly in order to avoid the overhead of building a DOM
		//       that we don't need.
		XMLStreamReader reader;
		try {
			reader = factory.createXMLStreamReader(new StringReader(source));
			reader.next(); // advance to root
			assert reader.isStartElement() : "expected root";
			
			String rootName = reader.getLocalName();
			if (rootName.equals("trajectoryStation")) {
				TrajectoryStationValue ts = new TrajectoryStationValue(source);
				ts.read(reader);
				return ts;
			}
			if (rootName.equals("geologyInterval")) {
				GeologyIntervalValue gi = new GeologyIntervalValue(source);
				gi.read(reader);
				return gi;
			}
		} catch (XMLStreamException e1) {
		}
		
		return null;
	}
	
	/**
	 * If on an element start, move the cursor to the element end of the same level.
	 * @param reader
	 * @return true if the element was detected and skipped
	 * @throws XMLStreamException
	 */
	protected static boolean skipElement(XMLStreamReader reader) throws XMLStreamException {
		if (!reader.isStartElement())
			return false;
		while (!reader.isEndElement()) {
			reader.next();
			// recursively skip any child elements
			if (reader.isStartElement()) {
				skipElement(reader);
				reader.next(); // move to item after end
			}
		}
		return true;
	}
	
	protected static boolean skipTo(XMLStreamReader reader, int eventType) throws XMLStreamException {
		while (reader.getEventType() != eventType) {
			if (!reader.hasNext())
				return false;
			reader.next();
		}
		return true;
	}
	
	/**
	 * Read the first occurance of text in an element without allocating a StringBuilder for multiple occurances.
	 * @param reader
	 * @return
	 * @throws XMLStreamException
	 */
	protected static String readFirstElementText(XMLStreamReader reader) throws XMLStreamException {
		assert reader.isStartElement();
		String text = null;
		while (reader.hasNext()) {
			reader.next();
			if (reader.isEndElement())
				break;
			if (reader.isCharacters() && text == null) {
				text = reader.getText();
			} else if (reader.isStartElement()) {
				skipElement(reader);
			}
		}
		return text;
	}
}
