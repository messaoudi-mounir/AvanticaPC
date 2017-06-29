package com.petrolink.mbe.rulesflow.variables;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.petrolink.mbe.rulesflow.RuleFlow;
import com.smartnow.engine.Factory;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Variable Factory
 * @author paul
 *
 */
public class VariableFactory extends Factory {
	private static Logger logger = LoggerFactory.getLogger(VariableFactory.class);
	
	protected static VariableFactory _instance;

	/**
	 * Get a Variable concrete implementation instance
	 * @param rule the associated Rule
	 * @param e the Variable declaration element
	 * @return an instance of the variable loaded from the provided element
	 * @throws EngineException
	 */
	public static Variable getVariable(RuleFlow rule, Element e) throws EngineException {
		String type = e.getName();

		return getVariable(type, rule, e);
	}
	
	/**
	 * Get a ChannelAlias concrete implementation instance
	 * @param rule the associated Rule
	 * @param e the Variable declaration element
	 * @param defaultType 
	 * @return an instance of the variable loaded from the provided element
	 * @throws EngineException
	 */
	public static ChannelAlias getChannel(RuleFlow rule, Element e, String defaultType) throws EngineException {
		String type;
		if (e.getAttribute("type") != null) {
			type = "channel_" + e.getAttributeValue("type").toLowerCase();
		} else {
			type = "channel_" + defaultType;			
		}

		return (ChannelAlias) getVariable(type, rule, e);		
	}

	/**
	 * Get a Variable concrete implementation instance
	 * @param type 
	 * @param rule the associated Rule
	 * @param e the Variable declaration element
	 * @return an instance of the variable loaded from the provided element
	 * @throws EngineException
	 */
	public static Variable getVariable(String type, RuleFlow rule, Element e) throws EngineException {
		Variable var = null;
		if (StringUtils.isBlank(type)) {
			throw new EngineException("Variable type should not be null!");
		}
		
		VariableFactory factoryInstance = getInstance();
		String beanTypeString = type.toLowerCase();
		
		var = factoryInstance.getBean(beanTypeString, Variable.class);
		
		if (var != null) {
			var.load(rule, e);
		} else {
			logger.error("Unknown Variable " + type);
			throw new EngineException("Unknown Variable " + type);
		}

		return var;
	}	
	
	private VariableFactory() {
		super(new ClassPathXmlApplicationContext("classpath*:**/petrolink-variables-ctx.xml"));
	}

	/**
	 * @return the Variable Factory Instance
	 */
	public static synchronized VariableFactory getInstance() {
		if (_instance == null)
			_instance = new VariableFactory();
		return _instance;
	}
}
