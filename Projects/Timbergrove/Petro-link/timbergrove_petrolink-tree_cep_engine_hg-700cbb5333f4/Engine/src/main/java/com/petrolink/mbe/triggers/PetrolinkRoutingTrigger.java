package com.petrolink.mbe.triggers;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.smartnow.engine.Engine;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.triggers.ConditionalTrigger;
import com.smartnow.engine.triggers.Trigger;

/**
 * Trigger for Petrolink data
 * @author Paul Solano
 * @author aristo
 */
public final class PetrolinkRoutingTrigger extends Trigger implements ConditionalTrigger {
	private static Logger logger = LoggerFactory.getLogger(PetrolinkRoutingTrigger.class);
	private String uri;
	private String exchange;
	private String queue;
	private String exchangeType = "direct";
	private boolean exchangeDurable = true;
	private String routingKey = "";
	private int concurrentListeners;
	protected Serializable condition;
	protected List<Channel> channels = new ArrayList<Channel>();
	protected List<Router> routers = new ArrayList<Router>();

	/**
	 * Run method, under runnable implementation (used by thread to run)
	 */
	@Override
	public void run() {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setUri(uri);
			
			Connection connection = factory.newConnection();
			
			for (int i=0; i< concurrentListeners;i++) {
				Channel channel = connection.createChannel();
				
				channel.queueDeclare(queue, true, false, false, null);
				channel.exchangeDeclare(exchange, exchangeType, exchangeDurable);
			    channel.queueBind(queue, exchange, routingKey);
			    channel.basicQos(1);
			    
				channel.basicConsume(queue, true, new Consumer(channel));
				channels.add(channel);
			}			
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e) {
			logger.error("Exception while setting up RabbitMQ channel consumers",e);			
		} catch (IOException e) {
			logger.error("IO Exception while setting up RabbitMQ channel consumers",e);
		} catch (TimeoutException e) {
			logger.error("Timeout while setting up RabbitMQ channel consumers",e);
		}
	}

	/**
	 * Stop this routing
	 */
	@Override
	public void stop() {
		for (Channel c : channels) {
			try {
				c.close();
			} catch (IOException | TimeoutException e) {
				logger.error("Error closing channel", e);
			}
		}
	}


	/**
	 * Load the configuration based on Xml element.
	 * @param e Xml Configuration
	 * @throws EngineException
	 */
	@Override
	public void load(Element e, Engine settings) throws EngineException {
		super.load(e, settings);
		if (e.getChild("Connection") != null) {
			this.uri = e.getChildText("Connection").trim();
		} else {
			throw new EngineException("Expecting Connection definition for RabbitMQ");
		}
		
		if (e.getChild("Exchange") != null) {
			this.exchange = e.getChildText("Exchange").trim();
			
			if (e.getChild("Exchange").getAttribute("type") != null) {
				this.exchangeType = e.getChild("Exchange").getAttributeValue("type");
			}

			if (e.getChild("Exchange").getAttribute("durable") != null) {
				this.exchangeDurable = "true".compareTo(e.getChild("Exchange").getAttributeValue("durable").toLowerCase()) == 0 ? true : false;
			}
		} else {
			throw new EngineException("Expecting Exchange definition for RabbitMQ");
		}
		
		if (e.getChild("RoutingKey") != null) {
			this.routingKey = e.getChildText("RoutingKey");
		}
		
		if (e.getChild("Queue") != null) {
			this.queue = e.getChildText("Queue").trim();
		} else {
			throw new EngineException("Expecting Queue definition for RabbitMQ");
		}

		if (e.getChild("ConcurrentListeners") != null) {
			this.concurrentListeners = Integer.parseInt(e.getChildText("ConcurrentListeners").trim());
		} else {
			this.concurrentListeners = 1;
		}
		
		List<Element> routes = e.getChild("Routes").getChildren();
		for (Element route : routes) {
			Router router = RouterFactory.getRouter(route, this);
			routers.add(router);
		}
	}

	/**
	 * Uri for this routing.
	 * @return Uri for this routing
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * AMQP Connection Uri for this routing.
	 * @param uri AMQP Connection Uri for this routing
	 */
	public void setUri(final String uri) {
		this.uri = uri;
	}

	/**
	 * Get AMQP Exchange for this routing. 
	 * @return  AMQP Exchange for this routing
	 */
	public String getExchange() {
		return exchange;
	}

	/**
	 * AMQP Exchange for this routing.
	 * @param amqpExchange AMQP Exchange for this routing
	 */
	public void setExchange(final String amqpExchange) {
		this.exchange = amqpExchange;
	}

	/**
	 * AMQP Queue id for this routing.
	 * @return AMQP Queue id 
	 */
	public String getQueue() {
		return queue;
	}

	/**
	 *  AMQP Queue id for this routing.
	 * @param amqpQueue AMQP Queue id
	 */
	public void setQueue(final String amqpQueue) {
		this.queue = amqpQueue;
	}

	/**
	 * Number of concurrent listener.
	 * @return the Number of Concurrent Listeners
	 */
	public int getConcurrentListeners() {
		return concurrentListeners;
	}

	/**
	 * Number of concurrent listener.
	 * @param concurrentListenerCount
	 */
	public void setConcurrentListeners(final int concurrentListenerCount) {
		this.concurrentListeners = concurrentListenerCount;
	}

	/**
	 * Evaluate condition for specified event.
	 * @event Event to evaluate
	 */
	@Override
	public boolean evaluateCondition(Event ev) {
		return standardConditionEvaluator(this.condition, ev);
	}

	/**
	 * Whether this routing is Daemon. Always return False for this class.
	 */
	@Override
	public boolean isDaemon() {
		return false;
	}
	
	/**
	 * Channel Consumer class for this routing
	 * @author aristo
	 *
	 */
	private final class Consumer extends DefaultConsumer  {
		/**
		 * Constructor
		 * @param channel
		 */
		public Consumer(Channel channel) {
			super(channel);
		}

		/**
		 * Handle Delivert for this message
		 */
		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
				throws IOException {
			for (Router router : routers) {
				router.route(getChannel(), consumerTag, envelope, properties, body);
			}
		}
	}
}
