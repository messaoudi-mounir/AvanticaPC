package com.petrolink.mbe.actions.rmq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jms.IllegalStateException;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.collections4.map.HashedMap;

import com.petrolink.mbe.amqp.AmqpMessage;
import com.petrolink.mbe.model.channel.ChannelData;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.model.operation.DataPointsAppended;
import com.petrolink.mbe.parser.MessageConverter;
import com.petrolink.mbe.setting.GenericConfiguration;
import com.petrolink.mbe.util.UUIDHelper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;

import Petrolink.WITSML.Events.ChannelDataAppended;

/**
 * Converting Petrolink ChannelData Appended Codec to/from AMQPMessage 
 * @author Aristo
 *
 */
public class ChannelDataAppendedAmqpCodec implements IAmqpEncoder, IAmqpDecoder {

	

	private static DatumWriter<ChannelDataAppended> cdaWriter =
			new SpecificDatumWriter<ChannelDataAppended>(ChannelDataAppended.getClassSchema());
	private static SpecificDatumReader<ChannelDataAppended> cdaReader =	
			new SpecificDatumReader<ChannelDataAppended>(ChannelDataAppended.getClassSchema());
	private UUID wellUuid;
	private UUID channelUuid;
	private String routingKey;
	private BasicProperties properties;
	private BinaryDecoder decoder;
	private ChannelDataAppended cda;
	
	/**
	 * Constructor
	 */
	public ChannelDataAppendedAmqpCodec() {
		
	}
	
	/**
	 * Constructor
	 * @param newWellUuid UUID of the well
	 * @param newChannelUuid UUID of the channel
	 */
	public ChannelDataAppendedAmqpCodec(UUID newWellUuid, UUID newChannelUuid) {
		wellUuid = newWellUuid;
		channelUuid = newChannelUuid;
		onChannelOrWellIdUpdate(); 
	}
	
	/**
	 * Constructor
	 * @param newWellId UUID String of the well
	 * @param newChannelUuid UUID Stringof the channel
	 */
	public ChannelDataAppendedAmqpCodec(String newWellId, String newChannelUuid) {
		wellUuid = UUIDHelper.fromStringFast(newWellId);
		channelUuid = UUIDHelper.fromStringFast(newChannelUuid);
		onChannelOrWellIdUpdate(); 
	}
	
	@Override
	public void load(GenericConfiguration config) {
		// No Configuration
		
	}
	
	/***
	 * Encode specified object to AMQPMessage, if specified object known to this encoder
	 * @return Null if object is not known to this encoder, otherwise will return constructed message
	 */
	@Override
	public AmqpMessage encode(Object object) throws Exception {
		List<DataPoint> serializationTarget = null;
		if (object instanceof DataPoint) {
			ArrayList<DataPoint> container = new ArrayList<DataPoint>(1);
			container.add((DataPoint)object);
			serializationTarget = container;
		} else if (object instanceof Collection<?>) {
			ArrayList<DataPoint> container = new ArrayList<DataPoint>();
			@SuppressWarnings("unchecked")
			Collection<Object> unknownObjectList =  ((Collection<Object>)object);
			for (Object unknownObject : unknownObjectList) {
				if (unknownObject instanceof DataPoint) {
					container.add((DataPoint)unknownObject);
				}
			}
			serializationTarget = container;
		}
		
		if(serializationTarget == null) return null;
		return encodeDataPoints(serializationTarget);
	}
	
	/**
	 * Encode data Points to AMQP message
	 * @param dps
	 * @return AmqpMessage containing the DataPoins
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public AmqpMessage encodeDataPoints(List<DataPoint> dps) throws IllegalStateException, IOException {
		if (dps == null) { 
			return null;
		}
		if ((properties == null) || (routingKey == null)){
			throw new IllegalStateException("WellId or ChannelId has not been initialized correctly");
		}
		byte[] content = encodeToBytes(getChannelUuid(),dps);
		
		AmqpMessage message = new AmqpMessage();
		message.setProperties(properties);
		message.setRoutingKey(routingKey);
		message.setContent(content);
		return message;
	}
	
	
	/**
	 * Encode specific channelData from DataPoints Appended
	 * @param dpa
	 * @return AmqpMessage
	 * @throws IllegalStateException
	 * @throws IOException 
	 */
	public AmqpMessage encodeChannelData(DataPointsAppended dpa) throws IllegalStateException, IOException {
		if (dpa == null) { 
			return null;
		}
		if ((properties == null) || (routingKey == null)){
			throw new IllegalStateException("WellId or ChannelId has not been initialized correctly");
		}
		ArrayList<DataPoint> dps = new ArrayList<DataPoint>();
		UUID targetChannelUuid = getChannelUuid();
		for(ChannelData cd :dpa.getAllChannelData()) {
			if (targetChannelUuid.equals(cd.getChannelId())) {
				dps.addAll(cd.getDataPoints());
			}
		}
		return encodeDataPoints(dps);
	}
	
	/**
	 * Convert DataPoinst appended to multiple amqp message (for each channelData) with routing for current specific well
	 * @param dpa
	 * @return  List of AmqpMessage for each Channel 
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public List<AmqpMessage> encodeDataPointsAppended(DataPointsAppended dpa) throws IllegalStateException, IOException {
		if (dpa == null) { 
			return null;
		}
		ArrayList<AmqpMessage> messages = new ArrayList<AmqpMessage>();
		
		UUID curWellId = getWellUuid();
		BasicProperties curProp = properties; //Basic properties based on well
		for(ChannelData cd :dpa.getAllChannelData()) {
			AmqpMessage message = new AmqpMessage();
			UUID curChId = cd.getChannelId();
			byte[] content = encodeToBytes(curChId,cd.getDataPoints());
			message.setProperties(curProp);
			message.setRoutingKey(getRoutingKey(curWellId, curChId));
			message.setContent(content);
			messages.add(message);
		}
		return messages;
	}
	
	/**
	 * Prepare bytes to be sent to RMQ
	 * @param channelId
	 * @param dps
	 * @return
	 * @throws IOException
	 */
	protected static byte[] encodeToBytes(UUID channelId, List<DataPoint> dps) throws IOException {
		//Data
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);		
		ChannelDataAppended cda = MessageConverter.toMessageModel(channelId, dps);
		cdaWriter.write(cda, encoder);
		encoder.flush();
		out.close();
		return out.toByteArray();
	}
	
	private static String getRoutingKey(UUID specificWellUuid,UUID specificChannelUuid) {
		String wellId = UUIDHelper.toStringFast(specificWellUuid);
		String chId =  UUIDHelper.toStringFast(specificChannelUuid);
		return String.format("'%s'.channel.dataappnded.'%s'",chId, wellId);
	}

	private void onChannelOrWellIdUpdate() {
		if ((wellUuid == null) || (channelUuid == null)) {
			properties = null;
			routingKey = null;
		} else {
			String wellId = UUIDHelper.toStringFast(wellUuid);
			routingKey = getRoutingKey(wellUuid,channelUuid);
			
			Map<String, Object> headers = new HashedMap<>();
			headers.put("uuid", wellId);
					
			Builder channelDataAppendedPropBuilder = new AMQP.BasicProperties.Builder();
			channelDataAppendedPropBuilder.contentType("avro/binary");
			channelDataAppendedPropBuilder.type(ChannelDataAppended.class.getName()); // Petrolink.WITSML.Events.ChannelDataAppended
			channelDataAppendedPropBuilder.headers(headers);
			properties = channelDataAppendedPropBuilder.build();
		}
	}
	
	/**
	 * @return the wellId
	 */
	public final UUID getWellUuid() {
		return wellUuid;
	}

	/**
	 * @param newWellId the wellId to set
	 */
	public final void setWellUuid(UUID newWellId) {
		this.wellUuid = newWellId;
		onChannelOrWellIdUpdate();
	}

	/**
	 * @return the channelUuid
	 */
	public final UUID getChannelUuid() {
		return channelUuid;
	}

	/**
	 * @param channelUuid the channelUuid to set
	 */
	public final void setChannelUuid(UUID channelUuid) {
		this.channelUuid = channelUuid;
		onChannelOrWellIdUpdate();
	}

	/**
	 * Decode message as Object (contains DataPointsAppended)
	 */
	@Override
	public Object decode(AmqpMessage message) throws Exception {
		if(message == null) return null;
		return decodeAsDataPointsAppended(message);
	}
	
	/**
	 * Decode message as DataPoint
	 * @param message 
	 * @return DataPointsAppended
	 * @throws IOException 
	 */
	public DataPointsAppended decodeAsDataPointsAppended(AmqpMessage message) throws IOException {
		if(message == null) return null;
		byte[] body = message.getContent();
		// Reuse binary decoder and ChannelDataAppended objects
		decoder = DecoderFactory.get().binaryDecoder(body, decoder);
		cda = cdaReader.read(cda, decoder);
		
		DataPointsAppended dpa = MessageConverter.toInternalModel(cda);
		return dpa;
	}

	
}
