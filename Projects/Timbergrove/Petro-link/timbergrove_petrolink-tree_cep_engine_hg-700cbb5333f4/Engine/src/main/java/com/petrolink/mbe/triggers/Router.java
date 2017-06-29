package com.petrolink.mbe.triggers;

import org.jdom2.Element;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Router base Implementation 
 * @author Paul Solano
 * @author aristo
  */
public abstract class Router {
	protected String uri;
	protected String exchange;
	protected String queue;
	protected String exchangeType = "direct";
	protected boolean exchangeDurable = true;
	protected String routingKey = "";
	
	/**
	 * Route a message into specified channel.
	 * @param channel Channel to route to
	 * @param consumerTag Consumer Tag (from RMQ)
	 * @param envelope Envelope (from RMQ)
	 * @param properties Message propertiesg (from RMQ)
	 * @param body Byte array Message to be routed
	 */
	public abstract void route(Channel channel, String consumerTag, Envelope envelope, BasicProperties properties, byte[] body);
	
	/**
	 * Load the configuration based on Xml element.
	 * @param e Xml Configuration
	 * @throws EngineException
	 */
	public void load(Element e) throws EngineException {
//		if (e.getChild("Connection") != null) {
//			this.uri = e.getChildText("Connection").trim();
//		} else {
//			throw new EngineException("Expecting Connection definition for RabbitMQ");
//		}
		
		if (e.getChild("Exchange") != null) {
			this.exchange = e.getChildText("Exchange").trim();
			
			if (e.getChild("Exchange").getAttribute("type") != null) {
				this.exchangeType = e.getChild("Exchange").getAttributeValue("type");
			}

			if (e.getChild("Exchange").getAttribute("durable") != null) {
				this.exchangeDurable = "true".equals(e.getChild("Exchange").getAttributeValue("durable").toLowerCase()) ? true : false;
			}
		} else {
			throw new EngineException("Expecting Exchange definition for RabbitMQ");
		}
		
		if (e.getChild("RoutingKey") != null) {
			this.routingKey = e.getChildText("RoutingKey");
		}
	}
}
