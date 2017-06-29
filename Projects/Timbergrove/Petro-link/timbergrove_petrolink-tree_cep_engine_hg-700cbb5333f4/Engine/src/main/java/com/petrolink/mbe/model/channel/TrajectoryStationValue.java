package com.petrolink.mbe.model.channel;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * Represents a trajectory station value parsed from a datapoint string value.
 * @author langj
 *
 */
@SuppressWarnings("javadoc")
public final class TrajectoryStationValue extends ComplexValue {
	private String uid;
	private OffsetDateTime dTimStn;
	private double md;
	private String mdUnit;
	private double tvd;
	private String tvdUnit;
	private double incl;
	private String inclUnit;
	private double azi;
	private String aziUnit;
	private double mtf;
	private String mtfUnit;
	private double gtf;
	private String gtfUnit;
	private double dispNs;
	private String dispNsUnit;
	private double dispEw;
	private String dispEwUnit;
	private double vertSect;
	private String vertSectUnit;
	private double dls;
	private String dlsUnit;
	
	public TrajectoryStationValue(String source) {
		super(ComplexValueType.TRAJECTORY_STATION, source);
	}
	
	public String getUid() {
		return uid;
	}
	
	public OffsetDateTime getDTimStn() {
		return dTimStn;
	}
	
	public double getMd() {
		return md;
	}
	
	public String getMdUnit() {
		return mdUnit;
	}
	
	public double getTvd() {
		return tvd;
	}
	
	public String getTvdUnit() {
		return tvdUnit;
	}
	
	public double getIncl() {
		return incl;
	}
	
	public String getInclUnit() {
		return inclUnit;
	}
	
	public double getAzi() {
		return azi;
	}
	
	public String getAziUnit() {
		return aziUnit;
	}
	
	public double getMtf() {
		return mtf;
	}
	
	public String getMtfUnit() {
		return mtfUnit;
	}
	
	public double getGtf() {
		return gtf;
	}
	
	public String getGtfUnit() {
		return gtfUnit;
	}
	
	public double getDispNs() {
		return dispNs;
	}
	
	public String getDispNsUnit() {
		return dispNsUnit;
	}
	
	public double getDispEw() {
		return dispEw;
	}
	
	public String getDispEwUnit() {
		return dispEwUnit;
	}
	
	public double getVertSect() {
		return vertSect;
	}
	
	public String getVertSectUnit() {
		return vertSectUnit;
	}
	
	public double getDls() {
		return dls;
	}
	
	public String getDlsUnit() {
		return dlsUnit;
	}

	@Override
	public Map<String, Object> toMap() {
		HashMap<String, Object> m = new HashMap<>();
		if (uid != null)
			m.put("uid", uid);
		if (dTimStn != null)
			m.put("dTimStn", dTimStn);
		m.put("md", md);
		if (mdUnit != null)
			m.put("mdUnit", mdUnit);
		m.put("tvd", tvd);
		if (tvdUnit != null)
			m.put("tvdUnit", tvdUnit);
		m.put("incl", incl);
		if (inclUnit != null)
			m.put("inclUnit", inclUnit);
		m.put("azi", azi);
		if (aziUnit != null)
			m.put("aziUnit", aziUnit);
		m.put("mtf", mtf);
		if (mtfUnit != null)
			m.put("mtfUnit", mtfUnit);
		m.put("gtf", gtf);
		if (gtfUnit != null)
			m.put("gtfUnit", gtfUnit);
		m.put("dispNs", dispNs);
		if (dispNsUnit != null)
			m.put("dispNsUnit", dispNsUnit);
		m.put("dispEw", dispEw);
		if (dispEwUnit != null)
			m.put("dispEwUnit", dispEwUnit);
		m.put("vertSect", vertSect);
		if (vertSectUnit != null)
			m.put("vertSectUnit", vertSectUnit);
		m.put("dls", dls);
		if (dlsUnit != null)
			m.put("dlsUnit", dlsUnit);
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
			case "dTimStn":
				dTimStn = OffsetDateTime.parse(readFirstElementText(reader));
				break;
			case "md":
				mdUnit = reader.getAttributeValue(null, "uom");
				md = Double.parseDouble(readFirstElementText(reader));
				break;
			case "tvd":
				tvdUnit = reader.getAttributeValue(null, "uom");
				tvd = Double.parseDouble(readFirstElementText(reader));
				break;
			case "incl":
				inclUnit = reader.getAttributeValue(null, "uom");
				incl = Double.parseDouble(readFirstElementText(reader));
				break;
			case "azi":
				aziUnit = reader.getAttributeValue(null, "uom");
				azi = Double.parseDouble(readFirstElementText(reader));
				break;
			case "mtf":
				mtfUnit = reader.getAttributeValue(null, "uom");
				mtf = Double.parseDouble(readFirstElementText(reader));
				break;
			case "gtf":
				gtfUnit = reader.getAttributeValue(null, "uom");
				gtf = Double.parseDouble(readFirstElementText(reader));
				break;
			case "dispNs":
				dispNsUnit = reader.getAttributeValue(null, "uom");
				dispNs = Double.parseDouble(readFirstElementText(reader));
				break;
			case "dispEw":
				dispEwUnit = reader.getAttributeValue(null, "uom");
				dispEw = Double.parseDouble(readFirstElementText(reader));
				break;
			case "vertSect":
				vertSectUnit = reader.getAttributeValue(null, "uom");
				vertSect = Double.parseDouble(readFirstElementText(reader));
				break;
			case "dls":
				dlsUnit = reader.getAttributeValue(null, "uom");
				dls = Double.parseDouble(readFirstElementText(reader));
				break;
			default:
				skipElement(reader);
				break;
			}
			assert reader.isEndElement();
		}
	}
}
