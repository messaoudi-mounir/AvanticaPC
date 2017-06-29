package com.petrolink.mbe.amqp;

import java.util.Map;

import org.h2.util.StringUtils;

/**
 * AmqpQueue Configuration
 * @author aristo
 *
 */
public class AmqpQueue {

	private String name;
	private boolean durable = false;
	private boolean autoDelete = true;
	private Map<String,Object> arguments = null;
	private boolean exclusive = false;
	
	/**
	 * Define server-named exclusive, autodelete, non-durable queue
	 */
	public AmqpQueue() {
		
	}
	
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	/**
	 * @param queueName the name to set
	 */
	public final void setName(String queueName) {
		if (StringUtils.isNullOrEmpty(queueName)) {
			throw new IllegalArgumentException("AMQP queue name may not be null");
		}
		this.name = queueName;
	}
	
	/**
	 * @return true if we are declaring a durable queue (the queue will survive a server restart)
	 */
	public final boolean isDurable() {
		return durable;
	}
	/**
	 * @param durable true if we are declaring a durable queue (the queue will survive a server restart)
	 */
	public final void setDurable(boolean durable) {
		this.durable = durable;
	}
	/**
	 * @return true if the server should delete the queue when it is no longer in use
	 */
	public final boolean isAutoDelete() {
		return autoDelete;
	}
	/**
	 * @param autoDelete true if the server should delete the queue when it is no longer in use
	 */
	public final void setAutoDelete(boolean autoDelete) {
		this.autoDelete = autoDelete;
	}
	
	/**
	 * @return other properties (construction arguments) for the queue
	 */
	public final Map<String, Object> getArguments() {
		return arguments;
	}
	/**
	 * @param arguments other properties (construction arguments) for the queue
	 */
	public final void setArguments(Map<String, Object> arguments) {
		this.arguments = arguments;
	}
	
	/**
	 * @return true if the queue is exclusive, i.e. restricted to connection
	 */
	public final boolean isExclusive() {
		return exclusive;
	}
	/**
	 * @param exclusiveMode true if the queue is exclusive, i.e. restricted to connection
	 */
	public final void setExclusive(boolean exclusiveMode) {
		this.exclusive = exclusiveMode;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("amqr:");
		builder.append("/").append("queue");
		builder.append("/").append(getName());
		builder.append("?").append("durable=").append(isDurable());
		builder.append("&").append("autoDelete=").append(isAutoDelete());
		builder.append("&").append("exclusive=").append(isExclusive());
		AmqpUtil.appendArgumentResource(builder, getArguments());
		return builder.toString(); 
	}
}
