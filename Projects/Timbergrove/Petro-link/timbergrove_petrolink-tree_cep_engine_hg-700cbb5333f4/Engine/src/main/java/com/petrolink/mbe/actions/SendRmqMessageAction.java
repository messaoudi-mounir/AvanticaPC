package com.petrolink.mbe.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.actions.rmq.AmqpCodecRegistry;
import com.petrolink.mbe.actions.rmq.IAmqpEncoder;
import com.petrolink.mbe.amqp.AmqpExchange;
import com.petrolink.mbe.amqp.AmqpMessage;
import com.petrolink.mbe.amqp.AmqpPublisher;
import com.petrolink.mbe.amqp.AmqpQueue;
import com.petrolink.mbe.amqp.AmqpResourceReference;
import com.petrolink.mbe.services.RabbitMQChannelService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.setting.ActionSource;
import com.petrolink.mbe.setting.GenericConfiguration;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;
import com.smartnow.engine.nodes.actions.Action;

/**
 * The common base class for actions sending through AMQP.
 * @author aristo
 *
 */
public class SendRmqMessageAction extends MBEAction {

	private static Logger logger = LoggerFactory.getLogger(SendRmqMessageAction.class);
	private RabbitMQChannelService amqpService;
	protected ArrayList<AmqpPublisher> publishers;
	private IAmqpEncoder encoder;
	private ActionSource messageSource;
	
	/**
	 * Send REMQ message
	 */
	public SendRmqMessageAction() {
		
	}
	
	@Override
	public void load(Element e, Node parent) throws EngineException {
		super.load(e, parent);
		
		amqpService = ServiceAccessor.getAmqpChannelService();
		Namespace ns = e.getNamespace();
		
		ArrayList<AmqpPublisher> pubs = new ArrayList<AmqpPublisher>();
		String actionName = this.toString()+" seq "+this.getSequence();
		
		//Load queue
		List<Element> amqpQueueElementsList = e.getChildren("AmqpQueue", ns);
		if (amqpQueueElementsList != null) {
			for (int i = 0; i < amqpQueueElementsList.size(); i++) {
				Element amqpElem = amqpQueueElementsList.get(i);
				AmqpQueue que = XmlSettingParser.parseAmqpQueue(amqpElem);
				try {
					AmqpPublisher pub = getAmqpService().getOrCreatePublisher(que);
					if (pub == null) {
						throw new EngineException(actionName + " is unable to initialize queue writer number "+ i + ", name=" + que.toString());
					}
					pubs.add(pub);
				} catch(IOException ioex) {
					throw new EngineException(actionName + " is unable to initialize queue number "+ i + ", name=" + que.toString(),ioex);
				}
			}
		}
		
		//Load exchange
		List<Element> amqpExchangeElementsList = e.getChildren("AmqpExchange", ns);
		if (amqpExchangeElementsList != null) {
			for (int i = 0; i < amqpExchangeElementsList.size(); i++) {
				Element amqpElem = amqpExchangeElementsList.get(i);
				AmqpExchange exc = XmlSettingParser.parseAmqpExchange(amqpElem);
				try {
					AmqpPublisher pub = getAmqpService().getOrCreatePublisher(exc);
					if (pub == null) {
						throw new EngineException(actionName + " is unable to initialize exchange writer number "+ i + ", name=" + exc.toString());
					}
					pubs.add(pub);
				} catch(IOException ioex) {
					throw new EngineException(actionName + " is unable to initialize exchange number "+ i + ", name=" + exc.toString(),ioex);
				}
			}
		}
		
		//load ref
		List<Element> amqpReferenceElementsList = e.getChildren("AmqpResourceReference", ns);
		if (amqpReferenceElementsList != null) {
			for (int i = 0; i < amqpReferenceElementsList.size(); i++) {
				Element amqpElem = amqpReferenceElementsList.get(i);
				AmqpResourceReference ref = XmlSettingParser.parseAmqpChannelReference(amqpElem);
				try {
					AmqpPublisher pub = getAmqpService().getOrCreatePublisher(ref);
					if (pub == null) {
						throw new EngineException(actionName + " is unable to initialize ref writer number "+ i + ", name=" + ref.toString());
					}
					pubs.add(pub);
				} catch(IOException ioex) {
					throw new EngineException(actionName + " is unable to initialize reference number "+ i + ", name=" + ref.toString(),ioex);
				}
			}
		}
		
		//Verify availability of publisher
		if (pubs.isEmpty()) {
			throw new EngineException(actionName + " has no publisher available");
		} else {
			publishers = pubs;
		}
		
		//Parsing Source
		Element messageElement = e.getChild("Source", ns);
		if (messageElement == null) {
			throw new EngineException(actionName + " has no Source defined");
		}
		messageSource = XmlSettingParser.parseActionSource(messageElement);
		
		//Parsing Output configuration
		Element outputElement = e.getChild("Output", ns);
		if (outputElement == null) {
			throw new EngineException(actionName + " has no Output defined");
		}
		
		String outputEncoder = outputElement.getAttributeValue("encoder");
		if (StringUtils.isNotBlank(outputEncoder)) {
			IAmqpEncoder newEncoder = AmqpCodecRegistry.getInstance().getEncoder(outputEncoder);
			if (newEncoder == null) {
				throw new EngineException(actionName + " has no encoder defined with name "+outputEncoder);
			}
			encoder = newEncoder;
		} else {
			throw new EngineException(actionName + " has no encoder defined");
		}
		
		Element encoderConfigElement = outputElement.getChild("EncoderConfig", ns);
		if (encoderConfigElement != null) {
			GenericConfiguration config = XmlSettingParser.parseGenericConfiguration(encoderConfigElement);
			encoder.load(config);
		}
	}
	
	
	
	/**
	 * Publish to message
	 * @param message
	 * @throws IOException
	 */
	protected void publishToChannel(AmqpMessage message) throws IOException {
		for (AmqpPublisher pub : publishers) {
			pub.publish(message);
		}
	}
	
	
	
	/**
	 * Executed when receiving event with ACTIVE Engine Status and ACTIVE Action 
	 */
	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		Object source = null;
		if (messageSource != null) {
			source = messageSource.getSource(context);
		}
		
		if (source != null) {
			AmqpMessage message;
			try {
				message = encoder.encode(source);
			} catch (Exception e) {
				logger.error("Unable to encode correctly with {} and source {}", encoder, source, e);
				return Action.RECOVERABLE_FAIL;
			}
			
			if(message != null) {
				try {
					publishToChannel(message);
				} catch (Exception e) {
					logger.error("Unable to encode correctly with {} and source {}", encoder, source, e);
					return Action.RECOVERABLE_FAIL;
				}
			} else {
				logger.warn("Trying to publish null message");
			}
		}
		return Action.SUCCESS;
	}

	/**
	 * Executed when receiving event with 
	 * a. ACTIVE Action with PASSIVE Engine status
	 * b. TEST Action with PASSIVE Engine Status
	 * c. PASSIVE Action with Any Engine Status
	 */
	@Override
	protected int executePasiveAction(Map<String, Object> context) throws EngineException {
		// TODO Auto-generated method stub
		return Action.SUCCESS;
	}
	
	/**
	 * Executed when receiving event with  
	 * a. ACTIVE Action with TEST Engine status
	 * b. TEST Action with NON Passive Engine status
	 */
	@Override
	protected int executeTestAction(Map<String, Object> arg0) throws EngineException {
		// TODO Auto-generated method stub
		return Action.SUCCESS;
	}
	
	
	/**
	 * Executed when receiving event with Active Engine and Test Action 
	 */
	@Override
	public void init(Map<String, Object> arg0) throws EngineException {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalize(Map<String, Object> arg0) throws EngineException {
		// TODO Auto-generated method stub

	}

	/**
	 * Cleaning up which can be called by Ruleflow when ruleflow doing deprovision
	 */
	@Override
	public void deprovision() {
		
	}

	/**
	 * @return the amqpService
	 */
	public final RabbitMQChannelService getAmqpService() {
		return amqpService;
	}

	/**
	 * @param service the amqpService to set
	 */
	public final void setAmqpService(RabbitMQChannelService service) {
		this.amqpService = service;
	}
}
