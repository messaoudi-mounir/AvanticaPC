package com.petrolink.mbe.templates;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.smartnow.engine.Factory;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Factory for Notification Template, this will read engine configuration to allow parsing of such template.
 */
public class NotificationTemplateFactory extends Factory {
	private static Logger logger = LoggerFactory.getLogger(NotificationTemplateFactory.class);
	
	protected static NotificationTemplateFactory _instance;

	/**
	 * Parse Notification Template
	 * @param e 
	 * @return the Notification Template instance from the XML Element
	 * @throws EngineException 
	 *  
	 */
	public static NotificationTemplate parseTemplate(Element e) throws EngineException {
		NotificationTemplate template = null;
		String type = e.getName();
		
		template = getInstance().getBean(type, NotificationTemplate.class);

		if (template != null) {
			template.load(e);
		} else {
			logger.error("Unknown Notification Template " + type);
			throw new EngineException("Unknown Notification Template " + type);
		}

		return template;
	}

	private NotificationTemplateFactory() {
		String cpathString = "classpath*:**/petrolink-templates-ctx.xml";
		ClassPathXmlApplicationContext cpath = new ClassPathXmlApplicationContext(cpathString);
		registerRootContext(cpath);
		if (cpath.getBeanDefinitionCount() <= 0) {
			logger.error("Unable to load Bean Definition for NotificationTemplateFactory in {}", cpathString);
		} else {
			logger.info("NotificationTemplateFactory Definitions Loaded: {} = {}", cpathString, cpath.getBeanDefinitionNames());
			logger.info("NotificationTemplateFactory root context initialized successfully");
		}
	}

	/**
	 * Notification Template Factory
	 * @return the NotificationTemplate Factory singleton instance
	 */
	public static synchronized NotificationTemplateFactory getInstance() {
		if (_instance == null)
			_instance = new NotificationTemplateFactory();
		return _instance;
	}
}
