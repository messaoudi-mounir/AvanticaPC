package com.petrolink.mbe.actions.rmq;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.petrolink.mbe.alertstatus.impl.AlertImpl;
import com.petrolink.mbe.amqp.AmqpMessage;
import com.petrolink.mbe.codec.AmqpJsonCodec;
import com.petrolink.mbe.codec.SmartNowCodec;
import com.petrolink.mbe.model.message.AlertSnapshot;
import com.petrolink.mbe.setting.GenericConfiguration;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.smartnow.alertstatus.Alert;

/**
 * Codec for Alert to and from AmqpMessage
 * @author aristo
 *
 */
public class AlertAmqpCodec implements IAmqpDecoder, IAmqpEncoder {
	private final char ROUTING_SEPARATOR ='.';
	private BasicProperties properties;
	private AmqpJsonCodec codec;
	private String messageType = AlertSnapshot.class.getName();
	
	/**
	 * Constructor
	 */
	public AlertAmqpCodec() {
		codec = new AmqpJsonCodec(); 
		properties = updateProperties();
	}
	
	/**
	 * Message Type
	 * @return Message type
	 */
	public String getMessageType() {
		return messageType;
	}
	
	protected BasicProperties updateProperties() {
		//Properties			
		Map<String, Object> headers = new HashedMap<>();
		BasicProperties props = generateBasicProperties(headers);
		return props;
	} 
	
	private BasicProperties generateBasicProperties(Map<String, Object> headers) {
		Builder channelDataAppendedPropBuilder = new AMQP.BasicProperties.Builder();
		channelDataAppendedPropBuilder.deliveryMode(2); //Persistent
		channelDataAppendedPropBuilder.contentType(codec.getEncodeContentType().toString());
		channelDataAppendedPropBuilder.type(getMessageType()); // Petrolink.WITSML.Events.ChannelDataAppended
		channelDataAppendedPropBuilder.headers(headers);
		return channelDataAppendedPropBuilder.build();
	}
	
	protected String getRoutingKey(AlertSnapshot snapshot) {
		assert snapshot != null : "snapshot is null";
		
		//Format: alertClass.alertuuid (if any).domain
		String alertClassId = snapshot.getDefinition().getClassId();
		
		StringBuilder routingBuilder = new StringBuilder();
		routingBuilder.append("alert");
		routingBuilder.append(ROUTING_SEPARATOR).append(alertClassId);
		
		String alertId = snapshot.getInstanceUuid();
		routingBuilder.append(ROUTING_SEPARATOR).append(alertId);
		routingBuilder.append(ROUTING_SEPARATOR).append(snapshot.getDefinition().getDomain());
		return routingBuilder.toString();
	}
	
	@Override
	public void load(GenericConfiguration config) {
		// No Configuration
		
	}
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.actions.rmq.IAmqpEncoder#encode(java.lang.Object)
	 */
	@Override
	public AmqpMessage encode(Object targetObject) throws Exception{
		AlertSnapshot serializationTarget = null;
		if (targetObject instanceof AlertImpl) {
			serializationTarget = SmartNowCodec.toAlertSnapshot((AlertImpl)targetObject);
		}
		
		if(serializationTarget == null) return null;
		
		AmqpMessage message = new AmqpMessage();
		message.setProperties(properties);
		message.setRoutingKey(getRoutingKey(serializationTarget));
		message.setContent(codec.encodeToBytes(serializationTarget));
		return message;
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.actions.rmq.IAmqpDecoder#decode(com.petrolink.mbe.amqp.AmqpMessage)
	 */
	@Override
	public Object decode(AmqpMessage message) throws Exception {
		if(message == null) return null;
		return decodeAsAlertSnapshot(message);
	}
	
	/**
	 * Decode message as alert
	 * @param message
	 * @return AlertSnapshot
	 * @throws Exception
	 */
	public AlertSnapshot decodeAsAlertSnapshot(AmqpMessage message) throws Exception {
		if(message == null) return null;
		return codec.decode(message, AlertSnapshot.class);
	}
	
	public AlertImpl decodeAsAlertImpl(AmqpMessage message) throws Exception {
		AlertSnapshot snapshot = decodeAsAlertSnapshot(message);
		return SmartNowCodec.toPetrolinkAlertImpl(snapshot);
	}

	
}
