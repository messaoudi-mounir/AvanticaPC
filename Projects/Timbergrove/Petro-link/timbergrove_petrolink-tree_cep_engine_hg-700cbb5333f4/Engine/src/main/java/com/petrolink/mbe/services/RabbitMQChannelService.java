package com.petrolink.mbe.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.amqp.AmqpExchange;
import com.petrolink.mbe.amqp.AmqpPublisher;
import com.petrolink.mbe.amqp.AmqpQueue;
import com.petrolink.mbe.amqp.AmqpResourceReference;
import com.petrolink.mbe.amqp.AmqpResourceType;
import com.petrolink.mbe.amqp.AmqpSubscriber;
import com.petrolink.mbe.amqp.AmqpUtil;
import com.petrolink.mbe.amqp.IAmqpPubSub;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.services.Service;
import com.smartnow.rabbitmq.util.RMQConnectionSettings;

/**
 * A service to allow direct publishing to rmq
 * @author Aristo
 *
 */
public class RabbitMQChannelService extends Service{
	private static Logger logger = LoggerFactory.getLogger(RabbitMQChannelService.class);
	private RMQConnectionSettings connectionSettings;
	private Connection rmqConnection;
	private ConcurrentHashMap<String, AmqpPublisher> pubChannels;
	private ConcurrentHashMap<String, AmqpSubscriber> subChannels;
	private ConcurrentHashMap<String, AmqpExchange> exchanges;
	private ConcurrentHashMap<String, AmqpQueue> queues;
	private static Object pubChannelLock = new Object();
	private XMLOutputter xmlPrettyOutput;
	
	/**
	 * Constructor
	 */
	public RabbitMQChannelService() {
		xmlPrettyOutput = new XMLOutputter();
		xmlPrettyOutput.setFormat(Format.getPrettyFormat());
	}
	
	@Override
	public void load(Element e) throws EngineException {
		super.load(e);
		
		Element connectionElem = e.getChild("DefaultSetting", e.getNamespace());
		connectionSettings = new RMQConnectionSettings();
		connectionSettings.load(connectionElem);
		
		ConcurrentHashMap<String, AmqpExchange> newExchanges = new  ConcurrentHashMap<String, AmqpExchange>();
		
		Element exchangesElem = e.getChild("Exchanges", e.getNamespace());
		List<Element> amqpExchangesList = exchangesElem.getChildren("AmqpExchange", e.getNamespace());
		for(Element amqpElem: amqpExchangesList) {
			try {
				AmqpExchange exc = XmlSettingParser.parseAmqpExchange(amqpElem);
				if(exc != null) {
					String key = createKey(AmqpResourceType.EXCHANGE,exc.getName());
					newExchanges.put(key, exc);
				}
			} catch (Exception ex) {
				if (logger.isErrorEnabled()) {
					logger.error("Unable to load exchange {}", xmlPrettyOutput.outputString(amqpElem),ex);
				}
			}
		}
		exchanges = newExchanges;
		
		ConcurrentHashMap<String, AmqpQueue> newQueues = new  ConcurrentHashMap<String, AmqpQueue>();
		
		Element queuesElem = e.getChild("Queues", e.getNamespace());
		List<Element> amqpQueuesList = queuesElem.getChildren("AmqpQueue", e.getNamespace());
		for(Element amqpElem: amqpQueuesList) {
			try {
				AmqpQueue que = XmlSettingParser.parseAmqpQueue(amqpElem);
				if(que != null) {
					String qName = que.getName();
					if (StringUtils.isNotBlank(qName)) {
						String key = createKey(AmqpResourceType.QUEUE,qName);
						newQueues.put(key, que);
					} else {
						logger.error("Unable to load a server named queue in initialization");
					}
				}
			} catch (Exception ex) {
				if (logger.isErrorEnabled()) {
					logger.error("Unable to load queue {}", xmlPrettyOutput.outputString(amqpElem),ex);
				}
			}
		}
		
		
		queues = newQueues;
	}
	
	/**
	 * Load exchange configuration into current exchange list
	 * @param exc Queue specification
	 */
	public void putExchangeIfAbsent(AmqpExchange exc) {
		String key = createKey(AmqpResourceType.EXCHANGE,exc.getName());
		exchanges.putIfAbsent(key, exc);
	}
	
	/**
	 * Load queue configuration into current queue list
	 * @param que Queue specification
	 */
	public void putQueueIfAbsent(AmqpQueue que) {
		String key = createKey(AmqpResourceType.QUEUE,que.getName());
		queues.putIfAbsent(key, que);
	}
	
	/**
	 * Create key for searching specific publisher
	 * @param type
	 * @param name
	 * @return
	 */
	public static String createKey(AmqpResourceType type,String name) {
		if (AmqpResourceType.EXCHANGE == type) {
			return String.format("X_%s", name);
		} else if (AmqpResourceType.QUEUE == type) {
			return String.format("Q_%s", name);
		} else {
			return String.format("U_%s", name);
		}
	}
	
	/**
	 * Get Or Create Publisher for the specified exchange
	 * @param exchange Exchange specification 
	 * @return Publisher which can be used for publishing to specific exchange, or null if exchange is not yet loaded into service
	 * @throws IOException If there is failure in creating publisher or AMQP declare
	 */
	public AmqpPublisher getOrCreatePublisher(AmqpExchange exchange)  throws IOException{
		putExchangeIfAbsent(exchange);
		return getOrCreatePublisher(AmqpResourceType.EXCHANGE,exchange.getName());
	}
	
	/**
	 *  Get Or Create Publisher for the specified queue
	 * @param queue Queue specification
	 * @return Publisher which can be used for publishing to specific queue, or null if queue is not yet loaded into service
	 * @throws IOException If there is failure in creating publisher or AMQP declare
	 */
	public AmqpPublisher getOrCreatePublisher(AmqpQueue queue) throws IOException{
		String exchangeOrQueueName = queue.getName();
		if (StringUtils.isNotBlank(exchangeOrQueueName)) {
			putQueueIfAbsent(queue);
		}
		return getOrCreatePublisher(AmqpResourceType.QUEUE, exchangeOrQueueName);
	}
	
	/**
	 *  Get Or Create Publisher for the specified reference
	 * @param ref Queue/Exchange reference 
	 * @return Publisher which can be used for publishing to specific exchange or queue, or null if exchange/queue is not yet loaded into service
	 * @throws IOException If there is failure in creating publisher or AMQP declare
	 */
	public AmqpPublisher getOrCreatePublisher(AmqpResourceReference ref) throws IOException{
		String name = ref.getName();
		AmqpResourceType type = ref.getType();
		return getOrCreatePublisher(type,name);
	}
	
	/**
	 * Get Or Create Publisher for the specified exchange
	 * @param type Whether this should be queue publisher or exchange publisher
	 * @param exchangeOrQueueName Exchange or Queue name to be published to
	 * @return Publisher which can be used for publishing to specific exchange or queue, or null if exchange/queue is not yet loaded into service
	 * @throws IOException If there is failure in creating publisher or AMQP declare
	 */
	protected AmqpPublisher getOrCreatePublisher(AmqpResourceType type,String exchangeOrQueueName) throws IOException {
		if(StringUtils.isBlank(exchangeOrQueueName) && (type == AmqpResourceType.EXCHANGE) ) {
			throw new IllegalArgumentException("exchange Name may not be null when getOrCreatePublisher");
		}
		
		String key =  createKey(type,exchangeOrQueueName);
		AmqpPublisher writer = pubChannels.getOrDefault(key, null);
		
		if (writer == null) {
			AmqpQueue queueDef = null;
			AmqpExchange exchangeDef  = null;
			if (AmqpResourceType.EXCHANGE == type) {
				exchangeDef = exchanges.get(key);
				if (exchangeDef == null) {
					return null;
				}
			} else if ((AmqpResourceType.QUEUE == type) && (StringUtils.isNotBlank(exchangeOrQueueName))) {
				queueDef = queues.get(key);
				if (queueDef == null) {
					return null;
				}
			}
			
			synchronized (pubChannelLock) {
				writer = pubChannels.getOrDefault(key, null);
				if (writer == null) {
					Channel channel = rmqConnection.createChannel();
					
					if (AmqpResourceType.EXCHANGE == type) {
						AmqpUtil.declare(channel, exchangeDef);
						AmqpPublisher publisher = new AmqpPublisher(channel,exchangeDef);
						publisher.setName(key);
						pubChannels.putIfAbsent(key, publisher);
					} else if (AmqpResourceType.QUEUE == type) {
						AmqpUtil.declare(channel, queueDef);
						AmqpPublisher publisher = new AmqpPublisher(channel,queueDef);
						publisher.setName(key);
						
						if (!StringUtils.equals(exchangeOrQueueName, queueDef.getName())) {
							//This happens when queue get named by server
							key = createKey(type, queueDef.getName());
							putQueueIfAbsent(queueDef);
						} 
						pubChannels.putIfAbsent(key, publisher);
					}
					
					writer = pubChannels.get(key);
				}
				
			}
		}
		
		
		return writer;
	}
	
	/**
	 * Start service. This connects to rmq.
	 */
	@Override
	public void startService() {
		try {
			pubChannels = new ConcurrentHashMap<String, AmqpPublisher>();
			subChannels = new ConcurrentHashMap<String, AmqpSubscriber>();
			ConnectionFactory factory = connectionSettings.getConnectionFactory();
			rmqConnection = factory.newConnection();
			
		} catch (Exception e) {
			logger.error("error while starting service", e);	
		}
	}
	
	/**
	 * Stop service. This close the channel and rmq connection.
	 */
	@Override
	public void stopService() {
		try {
			stopChannels();
			Connection curConnection = rmqConnection;
			curConnection.close();
		} catch (Exception e) {
			logger.error("error while stopping service", e);
		}
	}
	
	/**
	 * Stop all channels
	 */
	private void stopChannels() {
		ConcurrentHashMap<String, AmqpPublisher> pChannels = pubChannels;
		ConcurrentHashMap<String, AmqpSubscriber> sChannels = subChannels;
		ArrayList<Exception> excs = new ArrayList<Exception> ();
		excs.addAll(stopChannels(pChannels.values()));
		excs.addAll(stopChannels(sChannels.values()));
		if (!excs.isEmpty()) {
			String lnSeparator = System.getProperty("line.separator");
			StringBuilder errBuild = new StringBuilder();
			errBuild.append("Failed stopping ").append(excs.size()).append(" channels").append(lnSeparator);
			for(Exception exc: excs) {
				errBuild.append(ExceptionUtils.getStackTrace(exc)).append(lnSeparator);
			}
			logger.error(errBuild.toString());
		}
	}
	
	/**
	 * Stop all channels in the specified hashMap
	 * @param channels channels to stop
	 * @return Exception which happens when stopping for each channel
	 */
	private <T extends IAmqpPubSub> ArrayList<Exception> stopChannels(Collection<T> entries) {
		ArrayList<Exception> excs = new ArrayList<Exception> ();
		if (entries == null) {
			return excs;
		};
		
		for (IAmqpPubSub pubsub : entries) {
			try {
				pubsub.close();
			} catch (Exception e) {
				Exception wex = new Exception("Failed to close channel "+pubsub.getName(), e);
				excs.add(wex);
			}
		}
		return excs;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
