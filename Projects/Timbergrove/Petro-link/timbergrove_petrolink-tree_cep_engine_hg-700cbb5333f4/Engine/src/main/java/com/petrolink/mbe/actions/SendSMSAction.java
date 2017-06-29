package com.petrolink.mbe.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.actions.sms.SMSGateway;
import com.petrolink.mbe.actions.sms.SMSGatewayFactory;
import com.petrolink.mbe.templates.NotificationTemplate;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;

/**
 * Send SMS Action 
 * @author Josue
 *
 */

public class SendSMSAction extends NotificationAction {
	private final Logger logger = LoggerFactory.getLogger(SendSMSAction.class);
	protected String subject;
	protected String message;
	private NotificationTemplate template;
	private HashSet<UUID> principals;
	private SMSGateway gateway;

	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		this.gateway.sendSMS(principals, subject, message);
		return SUCCESS;
	}

	@Override
	protected int executeTestAction(Map<String, Object> context) throws EngineException {
		// TODO Write to File SMS send parameters
		return 0;
	}

	@Override
	public void init(Map<String, Object> context) throws EngineException {
		// No initialization required		
	}

	@Override
	public void finalize(Map<String, Object> context) throws EngineException {
		// No finalization required			
	}

	@Override
	public void load(final Element e, final Node parent) throws EngineException {
		super.load(e, parent);

		if (e.getAttribute("gateway") != null) {
			this.gateway = SMSGatewayFactory.getSMSGateway(this, e.getAttributeValue("gateway"), e);
		} else {
			throw new EngineException("gateway Attribute is required");
		}
		
		if (e.getAttribute("template") != null) {
			UUID templateUUID = UUID.fromString(e.getAttributeValue("template"));
			this.template = getNotificationTemplateService().getTemplate(templateUUID);
		}
		
		if (e.getChild("Principals", e.getNamespace()) != null) {
			String[] principalStrs = e.getChildText("Principals",e.getNamespace()).split(",");
			
			Arrays.asList(principalStrs).forEach(p -> { try { 
					this.principals.add(UUID.fromString(p)); 
				} catch (Exception ex) {
					logger.error("Exception while loading principal {}",p);
				} });
		}
		
		if (e.getChild("Subject",e.getNamespace()) != null) {
			this.subject = e.getChildText("Subject",e.getNamespace());
		} else if (template == null) {
			throw new EngineException("Subject must be specified");
		}
		
		if (e.getChild("Message",e.getNamespace()) != null) {
			this.message = e.getChildText("Message",e.getNamespace());
		} else if (template == null) {
			throw new EngineException("Subject must be specified");
		}
	}

}
