package com.petrolink.mbe.rulesflow.variables;

import static com.smartnow.engine.util.DataTypes.BOOLEAN;
import static com.smartnow.engine.util.DataTypes.DATE;
import static com.smartnow.engine.util.DataTypes.DOUBLE;
import static com.smartnow.engine.util.DataTypes.FLOAT;
import static com.smartnow.engine.util.DataTypes.INTEGER;
import static com.smartnow.engine.util.DataTypes.LONG;
import static com.smartnow.engine.util.DataTypes.STRING;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.rulesflow.RuleFlow;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Static value Variable 
 * @author paul
 *
 */
public class Static extends Variable {
	private static Logger logger = LoggerFactory.getLogger(Static.class);
	private Object value;
	private int type;

	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public Class<?> getValueType() {
		return Object.class;
	}

	/**
	 * Set value and type, by trying to detetc value type from string value itself.
	 * @param valueText
	 */
	private void setValueDetectType(final String valueText) {
		if (StringUtils.isBlank(valueText)) {
			//Default
			this.value = valueText;
			setType(STRING);
			return;
		}
		
		Boolean booleanValue = BooleanUtils.toBooleanObject(valueText);
		if (booleanValue != null) { //Check whether it is boolean
			this.value = booleanValue.booleanValue();
			setType(BOOLEAN);
		} else {
			try { //Check whether it is double
				this.value = Double.parseDouble(valueText);
				setType(DOUBLE);
			} catch (NumberFormatException nfe) {
				try { //Check whether it is date
					SimpleDateFormat df = new SimpleDateFormat("");
					this.value = df.parse(valueText);
					setType(DATE);
				} catch (ParseException e1) {
					//Then it is string
					this.value = valueText;
					setType(STRING);
				}
			}
		}
	}
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.rulesflow.variables.Variable#load(org.jdom2.Element)
	 */
	@Override
	public void load(RuleFlow rule, Element e) throws EngineException {
		super.load(rule, e);
		String attributeValue = e.getAttributeValue("type");
		if (attributeValue == null) {
			//Unknown String Try Parse
			String valueText = e.getTextTrim();
			logger.warn("One Variable with value {}, does not have tyep attribute", valueText);
			setValueDetectType(valueText);
		} else {	
			switch (attributeValue.toLowerCase()) {
				case "string":
					setType(STRING);
					this.value = e.getTextTrim();
					break;
				case "integer":
					setType(INTEGER);
					this.value = Integer.parseInt(e.getTextTrim());
					break;
				case "long":
					setType(LONG);
					this.value = Long.parseLong(e.getTextTrim());
					break;
				case "float":
					setType(FLOAT);
					this.value = Float.parseFloat(e.getTextTrim());
					break;
				case "double":
					setType(DOUBLE);
					this.value = Double.parseDouble(e.getTextTrim());
					break;
				case "date":
					setType(DATE);
					try {
						SimpleDateFormat df = new SimpleDateFormat("");
						this.value = df.parse(e.getTextTrim());
					} catch (ParseException e1) {
						logger.error("Error parsing date", e1);
					}
					break;
				case "boolean":
					setType(BOOLEAN);
					this.value = "true".equals(e.getTextTrim().toLowerCase()) ? true : false;
					break;
				default:
					this.value = e.getTextTrim();
					setType(STRING);
					break;
			}
		}
	}

	
	/**
	 * Returns the data type from the corresponding id
	 * @param dataType
	 * @return the String DataType
	 */
	public String getDataTypeName(int dataType){
		switch(dataType){
		case BOOLEAN:
			return "boolean";
		case DATE:
			return "date";
		case DOUBLE:
			return "double";
		case FLOAT:
			return "float";
		case LONG:
			return "long";
		case INTEGER:
			return "integer";
		case STRING:
			return "string";
		default:
			return "string";
		}
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	@Override
	public Element toElement() {
		Element element = new Element("Variable");
		element.setAttribute("id", alias);
		element.setAttribute("type", getDataTypeName(type));
		element.addContent(value.toString());
		return element;
	}

}
