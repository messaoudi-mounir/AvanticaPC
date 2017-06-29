package com.petrolink.mbe.amqp;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

/**
 * Publisher for AmqpMessage to one or multiple Channel
 * @author aristo
 *
 */
public class AmqpPublisher implements IAmqpPubSub {

	private String name;
	private Channel amqpChannel;
	private AmqpResourceType channelType;
	private String exchangeName;
	private String defaultRoutingKey;
	
	/**
	 * Create publisher to exchange
	 * @param pubChan
	 * @param exchange
	 */
	public AmqpPublisher(Channel pubChan, AmqpExchange exchange) {
		channelType =  AmqpResourceType.EXCHANGE;
		setAmqpChannel(pubChan);
		exchangeName = exchange.getName();
	}
	
	/**
	 * Create publisher to queue
	 * @param pubChan
	 * @param queue
	 */
	public AmqpPublisher(Channel pubChan, AmqpQueue queue) {
		channelType =  AmqpResourceType.QUEUE;
		setAmqpChannel(pubChan);
		exchangeName = StringUtils.EMPTY;
		defaultRoutingKey = queue.getName();
	}
	
	/**
	 * Publish a message.
	 * @param serializedBytes the message body
	 * @throws IOException
	 */
	public void publish(byte[] serializedBytes) throws IOException {
		publish(null, serializedBytes);
	}
	
	/**
	 * Publish a message.
	 * @param prop
	 * @param serializedBytes the message body
	 * @throws IOException
	 */
	public void publish(BasicProperties prop, byte[] serializedBytes) throws IOException {
		publish(null, prop, serializedBytes);
	}
	
	/**
	 * Publish a message.
	 * @param routingKey
	 * @param prop
	 * @param serializedBytes the message body
	 * @throws IOException
	 */
	public void publish(String routingKey, BasicProperties prop, byte[] serializedBytes) throws IOException {
		if (AmqpResourceType.EXCHANGE == channelType) {
			amqpChannel.basicPublish(exchangeName, routingKey, prop, serializedBytes);
		} else if (AmqpResourceType.QUEUE == channelType) {
			//Do not use normal routing key in Queue
			amqpChannel.basicPublish(exchangeName, defaultRoutingKey, prop, serializedBytes);
		} else {
			throw new java.lang.IllegalStateException("Only able to publish to Exchange or queue");
		}
	}
	
	/**
	 * Publish a message
	 * @param message
	 * @throws IOException
	 */
	public void publish(AmqpMessage message) throws IOException {
		if (message == null) return;
		publish(message.getRoutingKey(), message.getProperties(), message.getContent());
	}
		
	/**
	 * @return the amqpChannel
	 */
	public final Channel getAmqpChannel() {
		return amqpChannel;
	}

	/**
	 * @param channel the amqpChannel to set
	 */
	public final void setAmqpChannel(Channel channel) {
		amqpChannel = channel;
	}
		
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String newName) {
		name = newName;
	}
	
	@Override
	public void close() throws IOException, TimeoutException {
		amqpChannel.close();
	}

}

