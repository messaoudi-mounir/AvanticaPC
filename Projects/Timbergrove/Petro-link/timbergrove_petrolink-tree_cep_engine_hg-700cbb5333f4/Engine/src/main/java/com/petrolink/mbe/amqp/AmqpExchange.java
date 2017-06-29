package com.petrolink.mbe.amqp;

import java.util.Map;

import org.h2.util.StringUtils;

/**
 * Amqp Exchange Configuration
 * @author aristo
 *
 */
public class AmqpExchange {
	
	
	private String name;
	private String type;
	private boolean durable = false;
	private boolean autoDelete = false;
	private boolean internal = false;
	private Map<String,Object> arguments = null;
	
	/**
	 * Create Exchange name and type
	 * @param exchangeName
	 * @param exchangeType
	 */
	public AmqpExchange(String exchangeName,String exchangeType) {
		if (StringUtils.isNullOrEmpty(exchangeName)) {
			throw new IllegalArgumentException("AMQP exchange name may not be null");
		}
		if (StringUtils.isNullOrEmpty(exchangeType)) {
			throw new IllegalArgumentException("AMQP exchange type may not be null");
		}
		name = exchangeName;
		type = exchangeType;
	}
	
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	/**
	 * @param exchangeName the name to set
	 */
	public final void setName(String exchangeName) {
		if (StringUtils.isNullOrEmpty(exchangeName)) {
			throw new IllegalArgumentException("AMQP exchange name may not be null");
		}
		this.name = exchangeName;
	}
	/**
	 * @return the exchange type
	 */
	public final String getType() {
		return type;
	}
	/**
	 * @param exchangeType the exchange type
	 */
	public final void setType(String exchangeType) {
		if (StringUtils.isNullOrEmpty(exchangeType)) {
			throw new IllegalArgumentException("AMQP exchange type may not be null");
		}
		this.type = exchangeType;
	}
	/**
	 * @return true if we are declaring a durable exchange (the exchange will survive a server restart)
	 */
	public final boolean isDurable() {
		return durable;
	}
	/**
	 * @param durable true if we are declaring a durable exchange (the exchange will survive a server restart)
	 */
	public final void setDurable(boolean durable) {
		this.durable = durable;
	}
	/**
	 * @return true if the server should delete the exchange when it is no longer in use
	 */
	public final boolean isAutoDelete() {
		return autoDelete;
	}
	/**
	 * @param autoDelete true if the server should delete the exchange when it is no longer in use
	 */
	public final void setAutoDelete(boolean autoDelete) {
		this.autoDelete = autoDelete;
	}
	/**
	 * @return true if the exchange is internal, i.e. can't be directly published to by a client.
	 */
	public final boolean isInternal() {
		return internal;
	}
	/**
	 * @param internal true if the exchange is internal, i.e. can't be directly published to by a client.
	 */
	public final void setInternal(boolean internal) {
		this.internal = internal;
	}
	/**
	 * @return other properties (construction arguments) for the exchange
	 */
	public final Map<String, Object> getArguments() {
		return arguments;
	}
	/**
	 * @param arguments other properties (construction arguments) for the exchange
	 */
	public final void setArguments(Map<String, Object> arguments) {
		this.arguments = arguments;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("amqr:");
		builder.append("/").append("exchange");
		builder.append("/").append(getName());
		builder.append("?").append("type=").append(getType());
		builder.append("&").append("durable=").append(isDurable());
		builder.append("&").append("autoDelete=").append(isAutoDelete());
		builder.append("&").append("internal=").append(isInternal());
		AmqpUtil.appendArgumentResource(builder, getArguments());
		return builder.toString(); 
	}
}
