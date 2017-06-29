package com.petrolink.mbe.amqp;

import org.apache.commons.lang3.StringUtils;

/**
 * AMQP Reference to Channel Queue or Exchange
 * @author aristo
 *
 */
public class AmqpResourceReference {
	private String name;
	private AmqpResourceType type;
	
	/**
	 * @param name
	 * @param type
	 */
	public AmqpResourceReference(String name, AmqpResourceType type) {
		setName(name);
		setType(type);
	}
	
	
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * @param inputName the name to set
	 */
	public final void setName(String inputName) {
		if (StringUtils.isBlank(inputName)) {
			throw new IllegalArgumentException("Reference require name");
		}
		this.name = inputName;
	}
	
	/**
	 * @return the type
	 */
	public final AmqpResourceType getType() {
		return type;
	}
	
	/**
	 * @param inputType the type to set
	 */
	public final void setType(AmqpResourceType inputType) {
		if (inputType == null) {
			throw new IllegalArgumentException("Type Can not be null");
		}
		this.type = inputType;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("amqr:");
		builder.append("/").append("reference");
		builder.append("/").append(getName());
		builder.append("?").append("type=").append(getType());
		return builder.toString(); 
	}
}
