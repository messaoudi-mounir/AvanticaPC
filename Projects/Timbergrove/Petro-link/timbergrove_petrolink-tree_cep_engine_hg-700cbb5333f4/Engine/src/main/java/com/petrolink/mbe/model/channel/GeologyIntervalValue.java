package com.petrolink.mbe.model.channel;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Represents a geology interval parsed from a datapoint string value.
 * @author langj
 *
 */
@SuppressWarnings("javadoc")
public class GeologyIntervalValue extends ComplexValue {
	private String uid;
	private OffsetDateTime dTim;
	private double mdTop;
	private String mdTopUnit;
	private double mdBottom;
	private String mdBottomUnit;
	
	public GeologyIntervalValue(String source) {
		super(ComplexValueType.GEOLOGY_INTERVAL, source);
	}
	
	public String getUid() {
		return uid;
	}
	
	public OffsetDateTime getDTim() {
		return dTim;
	}
	
	public double getMdTop() {
		return mdTop;
	}
	
	public String getMdTopUnit() {
		return mdTopUnit;
	}
	
	public double getMdBottom() {
		return mdBottom;
	}
	
	public String getMdBottomUnit() {
		return mdBottomUnit;
	}
	
	@Override
	public Map<String, Object> toMap() {
		HashMap<String, Object> m = new HashMap<>();
		m.put("uid", uid);
		if (dTim != null)
			m.put("dTim", dTim.toString());
		m.put("mdTop", mdTop);
		if (mdTopUnit != null)
			m.put("mdTopUnit", mdTopUnit);
		m.put("mdBottom", mdBottom);
		if (mdBottomUnit != null)
			m.put("mdBottomUnit", mdBottomUnit);
		return m;
	}
	
	@Override
	void read(XMLStreamReader reader) throws XMLStreamException {
		assert reader.isStartElement();
		
		uid = reader.getAttributeValue(null, "uid");
		
		while (reader.hasNext()) {
			reader.next();
			if (reader.isEndElement() || !skipTo(reader, START_ELEMENT))
				break;

			switch (reader.getLocalName()) {
			case "dTim":
				dTim = OffsetDateTime.parse(readFirstElementText(reader));
				break;
			case "mdTop":
				mdTopUnit = reader.getAttributeValue(null, "uom");
				mdTop = Double.parseDouble(readFirstElementText(reader));
				break;
			case "mdBottom":
				mdBottomUnit = reader.getAttributeValue(null, "uom");
				mdBottom = Double.parseDouble(readFirstElementText(reader));
				break;
			default:
				skipElement(reader);
				break;
			}
			assert reader.isEndElement();
		}
	}
}
