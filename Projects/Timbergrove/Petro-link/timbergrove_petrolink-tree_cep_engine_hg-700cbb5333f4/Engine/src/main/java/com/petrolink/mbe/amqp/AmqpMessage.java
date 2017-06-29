package com.petrolink.mbe.amqp;

import com.rabbitmq.client.AMQP.BasicProperties;

public class AmqpMessage {
	 private byte[] content;
	 private String routingKey;
	 private BasicProperties properties;
	/**
	 * @return the content
	 */
	public final byte[] getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public final void setContent(byte[] content) {
		this.content = content;
	}
	/**
	 * @return the routingKey
	 */
	public final String getRoutingKey() {
		return routingKey;
	}
	/**
	 * @param routingKey the routingKey to set
	 */
	public final void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}
	/**
	 * @return the properties
	 */
	public final BasicProperties getProperties() {
		return properties;
	}
	/**
	 * @param properties the properties to set
	 */
	public final void setProperties(BasicProperties properties) {
		this.properties = properties;
	}
	 
}
