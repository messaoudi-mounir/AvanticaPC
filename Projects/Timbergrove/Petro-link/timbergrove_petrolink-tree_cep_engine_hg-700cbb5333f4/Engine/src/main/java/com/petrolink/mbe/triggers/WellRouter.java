package com.petrolink.mbe.triggers;

import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Well based router assumes the well UUID (String) comes in the message properties headers. It routes using routingKey
 * using a defined value comming on the message properties headers as well.
 * The Router applies only to a defined list of Wells
 * @author paul
 *
 */
public class WellRouter extends Router {
	private static final int KT_UUID = 0;
	private static Logger logger = LoggerFactory.getLogger(WellRouter.class);
	
	HashSet<UUID> wells = new HashSet<UUID>();
	String headerRoutingKey;
	int headerRoutingKeyType;
	String headerKey;
	
	@Override
	public void route(Channel channel, String consumerTag, Envelope envelope, BasicProperties properties, byte[] body) {
		// Header values are instances of LongStringHelper.ByteArrayLongString
		String wellUUIDStr = properties.getHeaders().get(headerKey).toString();
		UUID wellUUID = UUID.fromString(wellUUIDStr);
		
		if (wells.contains(wellUUID)) {
			String routingKey = "";
			if (headerRoutingKey != null) {
				if (headerRoutingKeyType == KT_UUID) {
					routingKey = properties.getHeaders().get(headerKey).toString();
				}
			} else if (this.routingKey != null) {
				routingKey = this.routingKey;
			}

			try {
				channel.exchangeDeclare(this.exchange, this.exchangeType, this.exchangeDurable);
				channel.basicPublish(this.exchange, routingKey, properties, body);
			} catch (IOException e) {
				logger.error("Error publishing message to exchange",e);
			}
		}
	}

	@Override
	public void load(Element e) throws EngineException {
		super.load(e);
		if (e.getChild("Wells") != null) {
			if (e.getChild("Wells").getAttribute("headerKey") != null) {
				this.headerKey = e.getChild("Wells").getAttributeValue("headerKey");
			}
			
			String[] wellsArray = e.getChildText("Wells").split(",");
			for (String uuid : wellsArray) {
				wells.add(UUID.fromString(uuid));
			}
		} else {
			throw new EngineException("Expecting Wells definition");
		}
		
		Element hrk = e.getChild("HeaderRoutingKey");
		if (hrk != null) {
			headerRoutingKey = hrk.getText();
			String typeStr = hrk.getAttributeValue("type");
			if ("uuid".equals(typeStr)) {
				headerRoutingKeyType = KT_UUID;
			}
		}
	}

}
