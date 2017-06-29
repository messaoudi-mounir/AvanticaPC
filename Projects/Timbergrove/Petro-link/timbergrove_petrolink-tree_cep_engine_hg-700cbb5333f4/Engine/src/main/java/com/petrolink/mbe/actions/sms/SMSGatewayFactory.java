package com.petrolink.mbe.actions.sms;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.petrolink.mbe.actions.SendSMSAction;
import com.smartnow.engine.Factory;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Factory for SMS Gateway concrete implementations
 * @author Josue
 *
 */
public class SMSGatewayFactory extends Factory {
	private static Logger logger = LoggerFactory.getLogger(SMSGatewayFactory.class);
	
	protected static SMSGatewayFactory _instance;

	/**
	 * @param action
	 * @param type
	 * @param e
	 * @return the concrete SMS Gateway instance
	 * @throws EngineException
	 */
	public static SMSGateway getSMSGateway(SendSMSAction action ,String type, Element e) throws EngineException {
		SMSGateway var = null;

		var = getInstance().getBean(type, SMSGateway.class);

		if (var != null) {
			var.load(action, e);
		} else {
			logger.error("Unknown SMSGateway " + type);
			throw new EngineException("Unknown SMSGateway " + type);
		}

		return var;		
	}
	
	private SMSGatewayFactory() {
		String cpathString = "classpath*:**/petrolink-smsgateways-ctx.xml";
		ClassPathXmlApplicationContext cpath = new ClassPathXmlApplicationContext(cpathString);
		registerRootContext(cpath);

		if (cpath.getBeanDefinitionCount() <= 0) {
			logger.error("Unable to load Bean Definition for SMSGatewayFactory in {}", cpathString);
		} else {
			logger.info("SMSGatewayFactory Definitions Loaded: {} = {}", cpathString, cpath.getBeanDefinitionNames());
			logger.info("SMSGateways root context initialized successfully");
		}
	}

	/**
	 * @return the SMSGateway Factory Instance
	 */
	public static synchronized SMSGatewayFactory getInstance() {
		if (_instance == null)
			_instance = new SMSGatewayFactory();
		return _instance;
	}	
}
