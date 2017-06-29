package com.petrolink.mbe.triggers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.petrolink.mbe.cache.CacheFactory;
import com.petrolink.mbe.cache.WellCache;
import com.petrolink.mbe.directories.WellDirectory;
import com.petrolink.mbe.directories.WellDirectory.WellDefinition;
import com.petrolink.mbe.metrics.MetricSystem;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.parser.MessageConverter;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.util.NamedThreadFactory;
import com.petrolink.mbe.util.UUIDHelper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.TopologyRecoveryException;
import com.smartnow.engine.Engine;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.executiongroups.ExecutionGroup;
import com.smartnow.engine.triggers.Trigger;
import com.smartnow.rabbitmq.util.RMQConnectionSettings;

import Petrolink.WITSML.Datatypes.ChannelDataItem;
import Petrolink.WITSML.Events.ChannelDataAppended;

/**
 * A trigger based on ChannelDataAppended messages received from RabbitMQ.
 * @author paul
 *
 */
public final class ChannelDataAppendedTrigger extends Trigger implements ExceptionHandler {
	private static final String RECEIVE_COUNTER_NAME = "receive-count";
	private static final String RECEIVE_AND_DISPATCH_COUNTER_NAME = "receive-and-dispatch-count";
	private static final Double DOUBLE_ZERO = 0.0;
	private static final String CHANNEL_DATA_APPENDED_NAME = ChannelDataAppended.class.getName();
	private static final Logger logger = LoggerFactory.getLogger(ChannelDataAppendedTrigger.class);
	private static final Counter staticReceiveCounter = MetricSystem.counter(ChannelDataAppendedTrigger.class, "static", RECEIVE_COUNTER_NAME);
	private static final Counter staticReceiveAndDispatchCounter = MetricSystem.counter(ChannelDataAppendedTrigger.class, "static", RECEIVE_AND_DISPATCH_COUNTER_NAME);
	
	private RMQConnectionSettings connectionSettings; 
	private UUID wellUUID;
	private boolean keepRunning = true;
	private ExecutionGroup rulesExecutionGroup;
	protected List<Channel> channels = new ArrayList<Channel>();
	private long recycleMills = 43200000;
	private Instant timestamp = null;
	private ExecutorService executor; // used for both RMQ delivery handling and rule filtering
	private WellCache lkvWellCache;
	private WellCache bufferedWellCache;
	private WellDefinition wellDefinition;
	private ArrayList<ClockChannelState> clocks = new ArrayList<>();
	private boolean shouldDeleteQueue;
	private String receiveCounterName;
	private String receiveAndDispatchCounterName;
	private Counter receiveCounter;
	private Counter receiveAndDispatchCounter;

	/**
	 * Default Constructor
	 */
	public ChannelDataAppendedTrigger() {
		super();
	}
	
	/**
	 * Internal Constructor
	 * @param id Trigger Id
	 * @param settings The RMQ connection settings
	 * @param wellUUID Well UUID
	 */
	public ChannelDataAppendedTrigger(String id, RMQConnectionSettings settings, UUID wellUUID) {
		this.setTriggerId(id);
		this.connectionSettings = Objects.requireNonNull(settings);
		this.wellUUID = Objects.requireNonNull(wellUUID);
	}
	
	@Override
	public void run() {
		logger.info("Running {} with {} concurrent listeners", getTriggerId(), connectionSettings.getConcurrentListeners());
		
		receiveCounterName = MetricSystem.name(ChannelDataAppendedTrigger.class, "inst", wellUUID.toString(), RECEIVE_COUNTER_NAME);
		receiveCounter = MetricSystem.counter(receiveCounterName);
		receiveAndDispatchCounterName = MetricSystem.name(ChannelDataAppendedTrigger.class, "inst", wellUUID.toString(), RECEIVE_AND_DISPATCH_COUNTER_NAME);
		receiveAndDispatchCounter = MetricSystem.counter(receiveAndDispatchCounterName);
		
		// Two for each processor is what RMQ uses by default
		String wellIdPart = wellUUID.toString().substring(0, 8);
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2,
				                                new NamedThreadFactory("rmq-cda-" + wellIdPart + "-"));
		
		lkvWellCache = CacheFactory.getInstance().getLKVCache().getOrCreateWell(this.wellUUID);
		bufferedWellCache = CacheFactory.getInstance().getBufferedCache().getOrCreateWell(this.wellUUID);
		
		WellDirectory wellDirectory = ServiceAccessor.getWellDirectory();
		if (wellDirectory != null) {
			wellDefinition = wellDirectory.getWell(this.wellUUID);
		}
		
		createChannels();
		
		while (this.isActive()) {

			try {
				Thread.sleep(10000); // sleep time is important for minimum clock interval 
				
			} catch (InterruptedException e) {
				logger.error("Unexcepted interruption of trigger {}", getTriggerId());
			}
			
			Instant nowInstant = Instant.now();

			if (isActive() && (timestamp.toEpochMilli() + recycleMills == nowInstant.toEpochMilli())) {
				logger.info("Recycling {} with {} concurrent listeners", getTriggerId(), connectionSettings.getConcurrentListeners());
				closeChannels();
				createChannels();				
			} else {
				if (!isKeepRunning() || !isActive()) {
					break;
				}
			}
			
			if (!clocks.isEmpty()) {
				for (ClockChannelState c : clocks) {
					// Note that this does not make up for missed time, so if a 15 second interval clock is called once per minute it will not
					// dispatch multiple times
					if (nowInstant.compareTo(c.nextTime) >= 0) {
						DataPoint dp = new DataPoint(OffsetDateTime.ofInstant(nowInstant, ZoneOffset.UTC), DOUBLE_ZERO);
						dispatch(c.channelId, dp);
						c.nextTime = nowInstant.plus(c.interval);
					}
				}
			}
		}
		
		// Closing channels gracefully when Trigger becomes Inactive
		logger.info("Ending run {}", getTriggerId());
		closeChannels();
		
		executor.shutdownNow();
		executor = null;
		
		MetricSystem.remove(receiveCounterName);
		MetricSystem.remove(receiveAndDispatchCounterName);
	}

	private void closeChannels() {
		if (shouldDeleteQueue && !channels.isEmpty()) {
			String queue = connectionSettings.getQueue();
			try {
				channels.get(0).queueDelete(queue);
			} catch (IOException e) {
				logger.error("Error deleting RMQ queue {}", queue, e);
			}
			shouldDeleteQueue = false;
		}
		
		for (Channel c : channels) {
			try {
				c.close();
			} catch (IOException | TimeoutException e) {
				logger.error("Error closing channel", e);
			}
		}
		channels.clear();
	}

	private void createChannels() {
		try {
			String exchange = connectionSettings.getExchange();
			String exchangeType = connectionSettings.getExchangeType();
			String queue = connectionSettings.getQueue();
			String routingKey = connectionSettings.getRoutingKey();
			boolean queueDurable = connectionSettings.isDurable();
			// This is a temporary workaround for PVHD-732 where PVHD uses wrong routing key
			// The queue will bind to both. The routing key is swapped as impact to change non-log data is much smaller
			String oldRoutingKey = StringUtils.replace(routingKey, ".dataappnded.", ".dataappended.");
			
			ConnectionFactory factory = connectionSettings.getConnectionFactory();			
			Connection connection = factory.newConnection(executor);
			
			for (int i=0; i<getConnectionSettings().getConcurrentListeners();i++) {
				logger.info("Starting consumer on exchange {} ({}) with routing key: {}", exchange, exchangeType, routingKey);
				
				Channel channel = connection.createChannel();
				
				channel.queueDeclare(queue, true, false, false, null);
				channel.exchangeDeclare(exchange, exchangeType, queueDurable);
			    channel.queueBind(queue, exchange, routingKey);
			    if (oldRoutingKey != routingKey)
			    	channel.queueBind(queue, exchange, oldRoutingKey);
			    channel.basicQos(1);
			    
			    Consumer consumer = new Consumer(channel);
				channel.basicConsume(queue, true, consumer);
				channels.add(channel);
			}
			
			timestamp = Instant.now();
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e) {
			logger.error("Exception while setting up RabbitMQ channel consumers",e);			
		} catch (IOException e) {
			logger.error("IO Exception while setting up RabbitMQ channel consumers",e);
		} catch (TimeoutException e) {
			logger.error("Timeout while setting up RabbitMQ channel consumers",e);
		}		
	}
	
	@Override
	public void stop() {
		setActive(false);
	}
	
	/**
	 * Sets whether the RMQ queue should be deleted on shutdown.
	 * @param value
	 */
	public void setShouldDeleteQueue(boolean value) {
		shouldDeleteQueue = value;
	}

	@Override
	public void load(Element e, Engine settings) throws EngineException {
		super.load(e, settings);

		if (e.getAttribute("recycleTimeInMills") != null) {
			this.recycleMills  = Long.parseLong(e.getAttributeValue("recycleTimeInMills"));
		}
		
		connectionSettings = new RMQConnectionSettings();
		connectionSettings.setRoutingKey("#");
		connectionSettings.load(e.getChild("RabbitMQRestfulServices", e.getNamespace()));
		
		if (e.getChild("WellUUID") != null) {
			this.wellUUID = UUID.fromString(e.getChildText("WellUUID"));
		}
		
		Element clockChannelSettings = e.getChild("ClockChannelSettings", e.getNamespace());
		if (clockChannelSettings != null)
			loadClockChannelSettings(clockChannelSettings);
	}
	
	/**
	 * Load clock channel settings from an XML element
	 * @param e
	 */
	public void loadClockChannelSettings(Element e) {
		clocks.clear();
		for (Element cc : e.getChildren("ClockChannel", e.getNamespace())) {
			UUID channelId = UUID.fromString(cc.getAttributeValue("id"));
			long interval = Long.parseLong(cc.getAttributeValue("interval"));
			clocks.add(new ClockChannelState(channelId, interval));
		}
	}

	@Override
	public boolean isDaemon() {
		return true;
	}

	/**
	 * @return the rulesExecutionGroup
	 */
	public ExecutionGroup getRulesExecutionGroup() {
		return rulesExecutionGroup;
	}
	
	/**
	 * @return the connectionSettings
	 */
	public RMQConnectionSettings getConnectionSettings() {
		return connectionSettings;
	}
	
	// Entry point for receiving data after it has been decoded from Avro.
	// The clock channel generators also invoke this.
	private void dispatch(UUID channelId, DataPoint dp) {
		lkvWellCache.addDataPoint(channelId, dp);
		bufferedWellCache.addDataPoint(channelId, dp);
		
		// TODO Move this decision-making into RuleFlow?
		Collection<RuleFlow> updateRules = wellDefinition.getRulesDirectory().getRulesUpdated(channelId);
		if (updateRules != null) {
			for (RuleFlow r: updateRules) {
				r.updateLocalCache(channelId, dp);
			}
		}
		
		Collection<RuleFlow> executeRules = wellDefinition.getRulesDirectory().getRulesExecuted(channelId);
		if (executeRules != null) {
			for (RuleFlow r: executeRules) {
				r.submitExecuteEvent(channelId, dp);
			}
		}
	}

	/**
	 * Handles consumption of RabbitMQ messages. Each instance is one concurrent listener.
	 * @author paul
	 * @author langj
	 *
	 */
	private final class Consumer extends DefaultConsumer {
		private final Logger logger = LoggerFactory.getLogger(Consumer.class);
		
		private BinaryDecoder decoder;
		private SpecificDatumReader<ChannelDataAppended> channelAppendedReader;
		private ChannelDataAppended cda;
		
		public Consumer(Channel channel) {
			super(channel);
			reloadSchema();
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
				throws IOException {

			receiveCounter.inc();
			staticReceiveCounter.inc();
			
			if (!properties.getType().equals(CHANNEL_DATA_APPENDED_NAME) || !active)
				return;
			
			// Rather than deserializing the body, extract the channel ID from the routing key and check it quickly
			String routingKey = envelope.getRoutingKey();
			assert routingKey.charAt(0) == '\'' && routingKey.charAt(37) == '\'' : "channel uuid expected to be first quoted element of routing key";
			UUID channelId = UUIDHelper.fromStringFast(routingKey.substring(1, 37));
			
			if (!wellDefinition.isChannelRegistered(channelId)) {
				//logger.trace("Unknown channel {} for well {}", channelId, wellUUID);
				return;
			}
			
			//logger.trace("Decoding data for well {} on channel {}", wellUUID, channelId);
			
			// Reuse binary decoder and ChannelDataAppended objects
			decoder = DecoderFactory.get().binaryDecoder(body, decoder);
			cda = channelAppendedReader.read(cda, decoder);

			for (ChannelDataItem cdi : cda.getData()) {
				assert UUIDHelper.fromBytes(cdi.getId().bytes()).equals(channelId) : "all data should be for same channel now";
				DataPoint dp = MessageConverter.toInternalModel(cdi);
				logger.trace("Received datapoint {} on channel {} for well {}", dp, channelId, wellUUID);
				dispatch(channelId, dp);
				receiveAndDispatchCounter.inc();
				staticReceiveAndDispatchCounter.inc();
			}
		}

		public void reloadSchema() {
			logger.info("Reloading Avro schema for {}", ChannelDataAppended.class);
			// Create Avro datum reader for the type
			channelAppendedReader = new SpecificDatumReader<ChannelDataAppended>(ChannelDataAppended.getClassSchema());
		}
	}
	
	@Override
	public void handleUnexpectedConnectionDriverException(Connection conn, Throwable exception) {
		try {
			logger.error("Handled error in RabbitMQ connection", exception);			
			setKeepRunning(false);
		} catch (Exception e) {
			logger.error("Error stopping the Service due to a RabbitMQ exception", exception);			
		}
	}

	@Override
	public void handleReturnListenerException(Channel channel, Throwable exception) {
		try {
			logger.error("Handled error in RabbitMQ connection", exception);			
			setKeepRunning(false);
		} catch (Exception e) {
			logger.error("Error stopping the Service due to a RabbitMQ exception", exception);			
		}
	}

	@Override
	public void handleFlowListenerException(Channel channel, Throwable exception) {
		try {
			logger.error("Handled error in RabbitMQ connection", exception);			
			setKeepRunning(false);
		} catch (Exception e) {
			logger.error("Error stopping the Service due to a RabbitMQ exception", exception);			
		}
	}

	@Override
	public void handleConfirmListenerException(Channel channel, Throwable exception) {
		try {
			logger.error("Handled error in RabbitMQ connection", exception);			
			setKeepRunning(false);
		} catch (Exception e) {
			logger.error("Error stopping the Service due to a RabbitMQ exception", exception);			
		}
	}

	@Override
	public void handleBlockedListenerException(Connection connection, Throwable exception) {
		try {
			logger.error("Handled error in RabbitMQ connection", exception);			
			setKeepRunning(false);
		} catch (Exception e) {
			logger.error("Error stopping the Service due to a RabbitMQ exception", exception);			
		}
	}

	@Override
	public void handleConnectionRecoveryException(Connection conn, Throwable exception) {
		try {
			logger.error("Handled error in RabbitMQ connection", exception);			
			setKeepRunning(false);
		} catch (Exception e) {
			logger.error("Error stopping the Service due to a RabbitMQ exception", exception);			
		}
	}

	@Override
	public void handleChannelRecoveryException(Channel ch, Throwable exception) {
		try {
			logger.error("Handled error in RabbitMQ connection", exception);			
			setKeepRunning(false);
		} catch (Exception e) {
			logger.error("Error stopping the Service due to a RabbitMQ exception", exception);			
		}
	}

	@Override
	public void handleConsumerException(Channel channel, Throwable exception, com.rabbitmq.client.Consumer consumer, String arg3,
			String arg4) {
		try {
			logger.error("Handled error in RabbitMQ connection", exception);			
			setKeepRunning(false);
		} catch (Exception e) {
			logger.error("Error stopping the Service due to a RabbitMQ exception", exception);			
		}
	}

	@Override
	public void handleTopologyRecoveryException(Connection conn, Channel ch, TopologyRecoveryException exception) {
		try {
			logger.error("Handled error in RabbitMQ connection", exception);			
			setKeepRunning(false);
		} catch (Exception e) {
			logger.error("Error stopping the Service due to a RabbitMQ exception", exception);			
		}
	}

	/**
	 * @return the keepRunning
	 */
	private synchronized boolean isKeepRunning() {
		return keepRunning;
	}

	/**
	 * @param keepRunning the keepRunning to set
	 */
	private synchronized void setKeepRunning(boolean keepRunning) {
		this.keepRunning = keepRunning;
	}
	
	private final class ClockChannelState {
		public final UUID channelId;
		public final Duration interval;
		public Instant nextTime;
		
		ClockChannelState(UUID channelId, long intervalMillis) {
			this.channelId = channelId;
			this.interval = Duration.ofMillis(intervalMillis);
			this.nextTime = Instant.MIN;
		}
	}
}
