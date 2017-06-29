package com.petrolink.mbe.setting;


import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.DefaultAuthenticator;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;

import com.petrolink.mbe.alertstatus.impl.AutoAlertDismissal;
import com.petrolink.mbe.amqp.AmqpResourceType;
import com.petrolink.mbe.amqp.AmqpExchange;
import com.petrolink.mbe.amqp.AmqpQueue;
import com.petrolink.mbe.amqp.AmqpResourceReference;
import com.petrolink.mbe.setting.EmailSetting.GuidAddressMappings;
import com.petrolink.mbe.util.EnumHelper;
import com.petrolink.mbe.util.UUIDHelper;
import com.petrolink.mbe.util.XmlConfigUtil;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Helper method for Parsing settings.
 * this way is nicer because you separate parsing and the model itself.
 * @author aristo
 *
 */
public class XmlSettingParser {
	private XmlSettingParser() {
		
	}

	/**
	 * Parse CacheOption.
	 * @param cacheElement Element to parse
	 * @return CacheOption
	 */
	public static CacheOption parseCacheOption(final Element cacheElement) {
		if (cacheElement == null) {
			return null;
		}
		
		CacheOption result = new CacheOption();
		String maximumSizeText = cacheElement.getAttributeValue("maximumSize");
		if (StringUtils.isNotBlank(maximumSizeText)) {
			try {
				result.setMaximumSize(Integer.valueOf(maximumSizeText));
			} catch (NumberFormatException nfe) {
				//just ignore
			}
		}
		String refreshSecondsText = cacheElement.getAttributeValue("refreshSeconds");
		if (StringUtils.isNotBlank(refreshSecondsText)) {
			try {
				Duration refreshDuration = Duration.ofSeconds(Long.valueOf(refreshSecondsText));
				result.setRefreshDuration(refreshDuration);
			} catch (NumberFormatException nfe) {
				//just ignore
			}
		}
		
		String expirationSecondsText = cacheElement.getAttributeValue("expirationSeconds");
		if (StringUtils.isNotBlank(expirationSecondsText)) {
			try {
				Duration expirationDuration = Duration.ofSeconds(Long.valueOf(expirationSecondsText));
				result.setExpirationDuration(expirationDuration);
			} catch (NumberFormatException nfe) {
				//just ignore
			}
		}
		
		return result;
	}
	
	/**
	 * Get duration from duration element
	 * @param durationElement
	 * @return Duration specified by element
	 */
	public static Duration parseDuration(final Element durationElement) {
		if (durationElement == null) {
			return null;
		}
		
		String unitText = durationElement.getAttributeValue("unit");
		String valueText = durationElement.getTextNormalize();
		long  durationValue = 0;
		try {
			durationValue = Long.valueOf(valueText);
		} catch (NumberFormatException nfe) {
			//just ignore
		}
		
		if (durationValue == 0) {
			return Duration.ZERO; //Useless duration
		}		
		
		
		if (StringUtils.isBlank(unitText) 
				|| "millis".equalsIgnoreCase(unitText) 
				|| "milliseconds".equalsIgnoreCase(unitText)) {
			return Duration.ofMillis(durationValue); 
		} else if ("seconds".equalsIgnoreCase(unitText)) {
			return Duration.ofSeconds(durationValue);
		} else if ("minutes".equalsIgnoreCase(unitText)) {
			return Duration.ofMinutes(durationValue);
		} else if ("hours".equalsIgnoreCase(unitText)) {
			return Duration.ofHours(durationValue);
		} else  {
			throw new IllegalArgumentException("Duration unit " +unitText+ " is not supported");
		}
	}
	
	/**
	 * Parse Xml configuration for auto alert dismissal
	 * @param elementXml
	 * @return AutoAlertDismissal
	 */
	public static final AutoAlertDismissal parseAutoAlertDismissal(final Element elementXml) {
		if (elementXml == null) {
			return null;
		}
		
		AutoAlertDismissal config = new AutoAlertDismissal();
		
		//Event to processing delta
		Element eventToProcessingDeltaTimeXml = elementXml.getChild("EventToProcessingDeltaTime", elementXml.getNamespace());
		if (eventToProcessingDeltaTimeXml != null) {
			Duration eventToProcessingDeltaTimeDuration = XmlSettingParser.parseDuration(eventToProcessingDeltaTimeXml);
			config.setEventToProcessingDeltaTime(eventToProcessingDeltaTimeDuration);
		}
		return config;
	}
	
	/**
	 * Parse Xml configuration for connection to model object.
	 * @param mailConnectionXml
	 * @return SendMailActionConnection object 
	 */
	public static final SMTPConnectionSettings parseSMTPConnectionSettings(final Element mailConnectionXml) {
		if (mailConnectionXml == null) { 
			return null;
		}
		
		SMTPConnectionSettings result =  new SMTPConnectionSettings();
		
		String sslText = XmlConfigUtil.getAttributeOrChildText(mailConnectionXml, "ssl");
		if (StringUtils.isNotBlank(sslText)) {
			result.setSslEnabled(Boolean.valueOf(sslText));
		}
		
		String sslCheckServerIdentityText = XmlConfigUtil.getAttributeOrChildText(mailConnectionXml, "sslCheckServerIdentity");
		if (StringUtils.isNotBlank(sslCheckServerIdentityText)) {
			result.setSslCheckServerIdentity(Boolean.valueOf(sslCheckServerIdentityText));
		}
		
		Element startTLSXml = mailConnectionXml.getChild("StartTLS", mailConnectionXml.getNamespace());
		if (startTLSXml != null) {
			result.setStartTlsEnabled(Boolean.valueOf(startTLSXml.getTextTrim()));
			
			if (result.isStartTlsEnabled()) {
				result.setStartTlsRequired(Boolean.valueOf(startTLSXml.getAttributeValue("required")));
			}
		}
		
		
		String host = mailConnectionXml.getChildText("Host", mailConnectionXml.getNamespace());
		if (StringUtils.isNotBlank(host)) {
			result.setHostName(host);
		}
		
		String port = mailConnectionXml.getChildText("Port", mailConnectionXml.getNamespace());
		if (StringUtils.isNotBlank(port)) {
			int portNumber = Integer.parseInt(port);
			result.setSmtpPort(portNumber);
		}
		
		String user = mailConnectionXml.getChildText("User", mailConnectionXml.getNamespace());
		String password = mailConnectionXml.getChildText("Password", mailConnectionXml.getNamespace());
		if (StringUtils.isNotBlank(user) && StringUtils.isNotBlank(password)) {
			result.setMailAuthenticator(new DefaultAuthenticator(user, password));
		} else {
			result.setMailAuthenticator(null);
		}
		
		return result;
	}
	
	/**
	 * Parse parameters into hashMap.
	 * @param parametersDefinition element to parse
	 * @param logger Logger to record exception.
	 * @return A hashmap with parameters name as key, with value of the Parameter Value
	 */
	private static HashMap<String, Object> parseActionParameters(final Element parametersDefinition, final Logger logger) {
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		
		if (parametersDefinition == null) { 
			return parameters; 
		}
		
		//Parse
		for (Element param : parametersDefinition.getChildren()) {
			String paramName = param.getAttributeValue("name");
			String paramType = param.getAttributeValue("type");
			String paramStringValue = param.getValue(); 
			if (StringUtils.isNotBlank(paramName) && StringUtils.isNotBlank(paramType)) {
				//In try catch so it can skip bad parameters
				try {
					switch (paramType) {
						case "Double":
							parameters.put(paramName, new Double(paramStringValue));
							break;
						case "Integer":
							parameters.put(paramName, new Integer(paramStringValue));
							break;
						case "String":
							parameters.put(paramName, paramStringValue);
							break;		
						default:
							if (logger != null) {
								logger.warn("UnknownType param {} with value {} as {}. ", paramName, paramStringValue, paramType);
							}
							break;
					}
				} catch (NumberFormatException nfe) {
					if (logger != null) {
						String errorString = String.format("Error while parsing param %s with value %s as %s. ", paramName, paramStringValue, paramType);
						logger.error(errorString, nfe);
					}
				}
			}
		}
		return parameters;
	}
	
	/**
	 * Parse EmailSetting.
	 * @param template Element to parse
	 * @param logger Logger to record exception.
	 * @return EmailSetting
	 */
	public static final EmailSetting parseEmailSetting(final Element template, final Logger logger) {
		if (template == null) {
			return null;
		}
		EmailSetting result = new EmailSetting();
		
		//Loading Connections
		final String connectionElementName = "Connection";
		Element  mailConnectionXml = template.getChild(connectionElementName, template.getNamespace());
		
		if (mailConnectionXml == null) {
			result.setConnectionSetting(null);
		} else {
			String connectionId = mailConnectionXml.getAttributeValue("id");
			if (StringUtils.isBlank(connectionId)) {
				SMTPConnectionSettings conf = XmlSettingParser.parseSMTPConnectionSettings(mailConnectionXml);
				result.setConnectionSetting(conf);
				result.setConnectionSettingRef(null);
			} else {
				result.setConnectionSetting(null);
				result.setConnectionSettingRef(connectionId);
			}
		}
		
		//Guid Mapping Mode
		final String guidMappingModeElementName = "GuidMapping";
		String mappingModeString = XmlConfigUtil.getAttributeOrChildText(template, guidMappingModeElementName);
		if (StringUtils.isBlank(mappingModeString)) {
			result.setGuidRecipientsMappingMode(GuidAddressMappings.UNKNOWN);
		} else if ("Email".equalsIgnoreCase(mappingModeString)) {
			result.setGuidRecipientsMappingMode(GuidAddressMappings.EMAIL);
		} else if ("SmsPhone".equalsIgnoreCase(mappingModeString)) {
			result.setGuidRecipientsMappingMode(GuidAddressMappings.SMS_PHONE_EMAIL);
		} else {
			result.setGuidRecipientsMappingMode(GuidAddressMappings.UNKNOWN);
		}
				
		//ContentType
		final String contentTypeElementName = "ContentType";
		String contentType = XmlConfigUtil.getAttributeOrChildText(template, contentTypeElementName);
		if (StringUtils.isBlank(contentType)) {
			result.setContentType(null);
		} else {
			result.setContentType(contentType);
		}
		
		
		
				
		//Body
		final String bodyElementName = "Body";
		String body = XmlConfigUtil.getAttributeOrChildText(template, bodyElementName);
		if (StringUtils.isBlank(body)) {
			result.setEmailBody(StringUtils.EMPTY);
		} else {
			result.setEmailBody(body);
		}
		
		//Subject
		final String subjectElementName = "Subject";
		final String subject = XmlConfigUtil.getAttributeOrChildText(template, subjectElementName);
		if (StringUtils.isBlank(subject)) {
			result.setEmailSubject(StringUtils.EMPTY);
		} else {
			result.setEmailSubject(subject);
		}
		
		// Loading default params from Template
		String actionParametersName = "Params";
		HashMap<String, Object> newActionParameters = new HashMap<String, Object>();
		
		Element templateParameters = template.getChild(actionParametersName, template.getNamespace());
		if (templateParameters != null) {
			newActionParameters.putAll(parseActionParameters(templateParameters, logger));
		}
		result.setActionParameters(newActionParameters);
		
		//Sender processing
		String from = XmlConfigUtil.getAttributeOrChildText(template, "From");
		if (StringUtils.isNotBlank(from)) {
			try	{
				InternetAddress fromAddress = new InternetAddress(from);
				result.setEmailFrom(fromAddress);
			} catch (AddressException ae) {
				if (logger != null) {
					logger.error("Unable to parse address as email : " + from, ae);
				}
				result.setEmailFrom(null);
			}
		} 
		
		//Loading Recipients
		String csvTo = XmlConfigUtil.getAttributeOrChildText(template, "To");
		String csvCc = XmlConfigUtil.getAttributeOrChildText(template, "CC");
		String csvBcc = XmlConfigUtil.getAttributeOrChildText(template, "BCC");
		result.setCsvRecipientsTO(csvTo);
		result.setCsvRecipientsCC(csvCc);
		result.setCsvRecipientsBCC(csvBcc);
				
		return result;
	}
	
	/**
	 * Parse SendMailActionSetting
	 * @param e Element to parse.
	 * @param logger Logger to record exception.
	 * @return SendMailActionSetting
	 */
	public static final SendMailActionSetting parseSendMailActionSetting(final Element e, final Logger logger) {
		if (e == null) {
			return null;
		}
		
		SendMailActionSetting result = new SendMailActionSetting();
		String templateName = e.getAttributeValue("template");
		UUID templateId = UUIDHelper.fromStringFast(templateName, true);
		result.setTemplateId(templateId);
		result.setTemplateName(templateName);
		result.setEmailSetting(XmlSettingParser.parseEmailSetting(e, logger));
		return result;
	}
	
	/**
	 * Parse PathSetting
	 * @param pathElement Element to parse.
	 * @return PathSetting
	 */
	public static final PathSetting parsePathSetting(final Element pathElement) {
		if (pathElement == null) {
			return null;
		}
		
		PathSetting result = new PathSetting();
		String relative = pathElement.getAttributeValue("relative");
		if (StringUtils.isNotBlank(relative)) {
			result.setRelative(Boolean.valueOf(relative));
		}
		result.setValue(pathElement.getTextTrim());
		return result;
	}
	
	/**
	 * Parse AmqpExchange
	 * @param amqpExchangeElement Element to parse.
	 * @return AmqpExchange
	 */
	public static final AmqpExchange parseAmqpExchange(final Element amqpExchangeElement) {
		if (amqpExchangeElement == null) {
			return null;
		}
		
		String exchangeName = amqpExchangeElement.getAttributeValue("exchange");
		String exchangeType = amqpExchangeElement.getAttributeValue("type");
		AmqpExchange result = new AmqpExchange(exchangeName, exchangeType);
		
		String durable = amqpExchangeElement.getAttributeValue("durable");
		if (durable != null) {
			result.setDurable(Boolean.parseBoolean(durable));
		}
		
		String autoDelete = amqpExchangeElement.getAttributeValue("autoDelete");
		if (autoDelete != null) {
			result.setAutoDelete(Boolean.parseBoolean(autoDelete));
		}
		
		String internal = amqpExchangeElement.getAttributeValue("internal");
		if (internal != null) {
			result.setInternal(Boolean.parseBoolean(internal));
		}
		return result;
	}

	/**
	 * Parse AmqpExchange
	 * @param amqpQueueElement Element to parse.
	 * @return AmqpExchange
	 */
	public static final AmqpQueue parseAmqpQueue(final Element amqpQueueElement) {
		if (amqpQueueElement == null) {
			return null;
		}
		
		String queueName = amqpQueueElement.getAttributeValue("queue");
		AmqpQueue result = new AmqpQueue();
		result.setName(queueName);
		
		String durable = amqpQueueElement.getAttributeValue("durable");
		if (durable != null) {
			result.setDurable(Boolean.parseBoolean(durable));
		}
		
		String autoDelete = amqpQueueElement.getAttributeValue("autoDelete");
		if (autoDelete != null) {
			result.setAutoDelete(Boolean.parseBoolean(autoDelete));
		}
		
		String exclusive = amqpQueueElement.getAttributeValue("exclusive");
		if (exclusive != null) {
			result.setExclusive(Boolean.parseBoolean(exclusive));
		}
		return result;
	}
	
	/**
	 * Parse AmqpChannelReference
	 * @param amqpReferenceElement Element to parse.
	 * @return AmqpChannelReference
	 */
	public static final AmqpResourceReference parseAmqpChannelReference(final Element amqpReferenceElement) {
		if (amqpReferenceElement == null) {
			return null;
		}
		
		String refName = amqpReferenceElement.getAttributeValue("name");
		String refTypeName =  amqpReferenceElement.getAttributeValue("type");
		AmqpResourceType refType = EnumHelper.valueOfIgnoreCase(AmqpResourceType.class, refTypeName);
		AmqpResourceReference result = new AmqpResourceReference(refName,refType);
		return result;
	}
	
	/**
	 * Parse ActionSource
	 * @param actionSourceElement
	 * @return ActionSource
	 */
	public static final ActionSource parseActionSource(final Element actionSourceElement) {
		if (actionSourceElement == null) {
			return null;
		}
		Namespace ns = actionSourceElement.getNamespace();
		
		ActionSource sourceSetting = new ActionSource();
		String sourceString = actionSourceElement.getAttributeValue("source");
		ActionSources sourceType = EnumHelper.valueOfIgnoreCase(ActionSources.class, sourceString);
		if (sourceType == null) {
			sourceType = ActionSources.UNKNOWN;
		}
		sourceSetting.setSourceType(sourceType);
		sourceSetting.setSourceKey(actionSourceElement.getAttributeValue("sourceKey"));
		sourceSetting.setBody(actionSourceElement.getChildText("Body", ns));
		
		return sourceSetting;
	}

	/**
	 * Parse GenericConfiguration
	 * @param configElement
	 * @return GenericConfiguration
	 */
	public static GenericConfiguration parseGenericConfiguration(Element configElement) {
		if (configElement == null) {
			return null;
		}
		Namespace ns = configElement.getNamespace();
		GenericConfiguration config =  new GenericConfiguration();
		
		parseGenericConfiguration(config,configElement,ns);
		return config;
	}
	
	private static void parseGenericConfiguration(GenericConfiguration parent, Element element, Namespace ns) {
		if (parent == null) {
			return;
		}
		if (element == null) {
			return;
		}
		
		String configName = element.getAttributeValue("name");
		String configValueString = element.getAttributeValue("value");
		if (StringUtils.isNotBlank(configValueString)) {
			parent.put(configName, configValueString);
		} else {
			List<Element> configValueChildren = element.getChildren("Config", ns);
			if (configValueChildren != null) {
				GenericConfiguration childrenConfig =  new GenericConfiguration();
				for (Element childElement : configValueChildren) {
					parseGenericConfiguration(childrenConfig,childElement,ns);
				}
				parent.put(configName, childrenConfig);
			}
		}
		
		
	}
}
