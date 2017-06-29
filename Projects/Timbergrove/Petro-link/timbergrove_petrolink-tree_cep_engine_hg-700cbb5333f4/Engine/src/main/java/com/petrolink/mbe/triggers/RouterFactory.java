package com.petrolink.mbe.triggers;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.smartnow.engine.Factory;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Message Routing Implementation Factory
 * @author paul
 *
 */
public class RouterFactory extends Factory {
	private static Logger logger = LoggerFactory.getLogger(RouterFactory.class);
	protected static RouterFactory _instance;

	/**
	 * @param e
	 * @param trigger
	 * @return the concrete Router instance
	 * @throws EngineException
	 */
	public static Router getRouter(Element e, PetrolinkRoutingTrigger trigger) throws EngineException {
		Router router = null;
		String type = e.getName();

		router = getInstance().getBean(type, Router.class);
		
		if (router != null) {
			router.load(e);
		} else {
			logger.error("Unknown Router " + type);
			throw new EngineException("Unknown Router " + type);
		}

		return router;
	}

	private RouterFactory() {
		registerRootContext(new ClassPathXmlApplicationContext("classpath*:**/petrolink-routers-ctx.xml"));

		logger.info("Routers root context initialized successfully");
	}

	/**
	 * @return the Router Factory Instance
	 */
	public static synchronized RouterFactory getInstance() {
		if (_instance == null)
			_instance = new RouterFactory();
		return _instance;
	}
}
