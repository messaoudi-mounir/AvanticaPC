package com.petrolink.mbe.actions.rmq;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import com.petrolink.mbe.amqp.AmqpMessage;
import com.petrolink.mbe.codec.AmqpJsonCodec;
import com.petrolink.mbe.codec.SmartNowCodec;
import com.petrolink.mbe.model.message.AlertCepEvent;
import com.petrolink.mbe.model.message.AlertDefinition;
import com.petrolink.mbe.model.message.AlertSnapshotSummary;
import com.petrolink.mbe.setting.GenericConfiguration;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.smartnow.alertstatus.AlertJournal;

/**
 * Codec for AlertJournal to and from AmqpMessage
 * @author aristo
 *
 */
public class AlertCepEventAmqpCodec implements IAmqpDecoder, IAmqpEncoder {
	private final char ROUTING_SEPARATOR ='.';
	private BasicProperties properties;
	private AmqpJsonCodec codec;
	
	/**
	 * Constructor
	 */
	public AlertCepEventAmqpCodec() {
		codec = new AmqpJsonCodec(); 
		properties = updateProperties();
	}
	
	/**
	 * Message type
	 * @return Message Type
	 */
	public String getMessageType() {
		return "AlertJournal";
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
	
	protected String getRoutingKey(AlertCepEvent cepEvent) {
		assert cepEvent != null : "journal is null";
		

		
		String journalType = cepEvent.getType();
		
		StringBuilder routingBuilder = new StringBuilder();
		routingBuilder.append(journalType);
		
		AlertSnapshotSummary alert = cepEvent.getAlert();
		AlertDefinition alertDefinition = alert.getDefinition(); 
		
		if (alert != null) {
			String alertClassId = null;
			if (alertDefinition != null) {
				alertClassId = alertDefinition.getClassId();
			} 
			
			//Alert class section
			routingBuilder.append(ROUTING_SEPARATOR);
			if (StringUtils.isNotBlank(alertClassId)) {
				routingBuilder.append(alertClassId);
			} else {
				routingBuilder.append("noalertclass");
			}
			
			//Alert Id section
			routingBuilder.append(ROUTING_SEPARATOR);
			String alertId = alert.getInstanceUuid();
			if (StringUtils.isNotBlank(alertId)) {
				routingBuilder.append(alertId);
			} else {
				routingBuilder.append("noalert");
			}
		}
		
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
	public AmqpMessage encode(Object alertJournalObject) throws Exception{
		AlertCepEvent cepEvent  = null;
		if (alertJournalObject instanceof AlertJournal) {
			AlertJournal alertJournal = (AlertJournal)alertJournalObject;
			cepEvent = SmartNowCodec.toAlertCepEvent(alertJournal);
		} else if (alertJournalObject instanceof AlertCepEvent){
			cepEvent = (AlertCepEvent)alertJournalObject;
		}
		
		if(cepEvent == null) return null;
		
		AmqpMessage message = new AmqpMessage();
		message.setProperties(properties);
		message.setRoutingKey(getRoutingKey(cepEvent));
		message.setContent(codec.encodeToBytes(cepEvent));
		return message;
	}
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.actions.rmq.IAmqpDecoder#decode(com.petrolink.mbe.amqp.AmqpMessage)
	 */
	@Override
	public Object decode(AmqpMessage message) throws Exception {
		if(message == null) return null;
		return decodeAsAlertCepEvent(message);
	}
	
	/**
	 * Decode the message as Alert CEP Event
	 * @param message
	 * @return AlertCepEvent
	 * @throws Exception
	 */
	public AlertCepEvent decodeAsAlertCepEvent(AmqpMessage message) throws Exception {
		if(message == null) return null;
		return codec.decode(message, AlertCepEvent.class);
	}
	
}
