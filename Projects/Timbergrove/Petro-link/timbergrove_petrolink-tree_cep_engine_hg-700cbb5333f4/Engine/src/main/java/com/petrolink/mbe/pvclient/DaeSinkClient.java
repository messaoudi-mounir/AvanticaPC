package com.petrolink.mbe.pvclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.parser.DaeModelConverter;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.smartnow.rabbitmq.util.RMQConnectionSettings;

import Petrolink.Microservice.DAE.ChannelDataValues;
/**
 * Client for Connecting to DAE Sink Service.
 * @author aristo
 * @deprecated By request of Mujeeb, code left here for reference
 */
public class DaeSinkClient {

	private static final String IDENTITY_CONTENT_ENCODING = "identity";

	protected static final String AVRO_BINARY_CONTENT_TYPE = "avro/binary";

	public static final String CHANNEL_DATA_VALUES_TYPE = "Petrolink.Microservice.DAE.ChannelDataValues";

	private RMQConnectionSettings rmqSettings; 
	
	private final Logger logger = LoggerFactory.getLogger(DaeSinkClient.class);
	private BinaryEncoder encoder;
	private SpecificDatumWriter<ChannelDataValues> cdvWriter;
	private Channel publishChannel;
		
	private String DEFAULT_EXCHANGE_NAME = "Petrolink.Microservice.DAE.Output";
	
	public DaeSinkClient(RMQConnectionSettings setting) {
		rmqSettings = setting;
		reloadSchema();
	}
	/**
	 * Internal Constructor
	 * @param uri Connection URI
	 * @param exchange Exchange
	 * @param queue Queue
	 * @param exchangeType type of Exchange
	 * @param exchangeDurable is Exchange Durable?
	 * @param routingKey Routing Key
	 * @param concurrentListeners Quantity of Concurrent Listeners
	 */
	public DaeSinkClient(
			String uri,
			String exchange,
			String queue, 
			String exchangeType,
			boolean exchangeDurable,
			String routingKey, 
			int concurrentListeners
			){
		
		RMQConnectionSettings connectionSettings = new RMQConnectionSettings();
		connectionSettings.setConnectionURI(uri);
		
		if (StringUtils.isBlank(exchange)){
			connectionSettings.setExchange(DEFAULT_EXCHANGE_NAME);
		} else {
			connectionSettings.setExchange(exchange);
		}
		
		connectionSettings.setQueue(queue);
		connectionSettings.setExchangeType(exchangeType);
		connectionSettings.setDurable(exchangeDurable);
		connectionSettings.setRoutingKey(routingKey);
		connectionSettings.setConcurrentListeners(concurrentListeners);
		
		rmqSettings = connectionSettings;
		reloadSchema();
	}
	
	private void createChannels() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, TimeoutException {
		ConnectionFactory factory = rmqSettings.getConnectionFactory();			
		Connection connection = factory.newConnection();
		
		String exchange = rmqSettings.getExchange();
		String exchangeType = rmqSettings.getExchangeType();
		boolean queueDurable = rmqSettings.isDurable();
		
		try {
			publishChannel = connection.createChannel();
			publishChannel.exchangeDeclare(exchange, exchangeType, queueDurable);
		} catch (IOException e) {
			logger.error("IO Exception while setting up DaeSinkClient channel producer",e);
		}
	}
	
	private void closeChannels() {
		Channel c = publishChannel;
		publishChannel = null;
		try {
			c.close();
		} catch (IOException | TimeoutException e) {
			logger.error("Error closing DAE Sink channel", e);
		}
		
	}
	
	public void reloadSchema(){
		logger.info("Reloading Avro schema for {}", ChannelDataValues.class);
		// Create Avro datum writer for the type
		cdvWriter = new SpecificDatumWriter<ChannelDataValues>(ChannelDataValues.getClassSchema());
	}
	
	/**
	 * Update a channel with a single value.
	 * @param channelId The channel ID
	 * @param index The index to update
	 * @param value The value to update with
	 * @throws IOException
	 */
	public void updateChannel(UUID channelId, Object index, Object value) throws IOException {
	
		DataPoint dp = new DataPoint(index,value);
		ArrayList<DataPoint> dpList = new ArrayList<DataPoint>();
		dpList.add(dp);
		updateChannel(channelId, dpList);
	}
	
	/**
	 * Update a channel with a series of data points.
	 * @param channelId The channel ID
	 * @param data A collection of data points.
	 * @throws IOException
	 */
	public void updateChannel(UUID channelId, Collection<DataPoint> data) throws IOException {
		ChannelDataValues datum = new ChannelDataValues();
		datum = DaeModelConverter.toChannelDataValues(channelId, data);
		
		updateChannel(datum);
	}
	
	/**
	 * Update a channel with a ChannelDataValues
	 * @param datum
	 * @throws IOException
	 */
	public void updateChannel(ChannelDataValues datum) throws IOException {
		byte[] result = avroEncode(datum);
		
		String routingKey = StringUtils.EMPTY;
			
		publishChannel.basicPublish(rmqSettings.getExchange(), routingKey
				, new AMQP.BasicProperties.Builder()
					.type(CHANNEL_DATA_VALUES_TYPE)	
					.contentEncoding(IDENTITY_CONTENT_ENCODING)
					.contentType(AVRO_BINARY_CONTENT_TYPE)
					.build()
				,  
				result);
	}
	
	public byte[] avroEncode(ChannelDataValues datum) throws IOException  {
		//TODO :  this is may not be optimum as creating stream multiple time each time of serialization
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		encoder = EncoderFactory.get().binaryEncoder(out, encoder);
		cdvWriter.write(datum, encoder);
		
		encoder.flush();
		out.close();
		
		return out.toByteArray();
	}
}
