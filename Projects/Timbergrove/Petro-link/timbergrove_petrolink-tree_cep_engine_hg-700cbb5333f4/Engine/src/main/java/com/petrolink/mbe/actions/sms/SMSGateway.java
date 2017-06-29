package com.petrolink.mbe.actions.sms;

import java.util.HashSet;
import java.util.UUID;

import org.jdom2.Element;

import com.petrolink.mbe.actions.SendSMSAction;
import com.petrolink.mbe.services.PetroVaultPrincipalService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Abstract SMS Gateway class. Each concrete implementation shall provide a way to send SMS messages through different transports 
 * or messaging services
 * @author Josue
 *
 */
public abstract class SMSGateway {
	private SendSMSAction parent;

	/**
	 * Executes the actual send message using the specific protocol or messaging service
	 * @param targets
	 * @param subject 
	 * @param message
	 * @return true if Success
	 * @throws EngineException
	 */
	public abstract boolean sendSMS(final HashSet<UUID> targets, final String subject, final String message) throws EngineException;
	
	/**
	 * Load specific configuration settings
	 * @param action
	 * @param e
	 */
	public void load(SendSMSAction action, Element e) {
		this.setParent(action);
	}

	/**
	 * @return the parent
	 */
	public SendSMSAction getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(SendSMSAction parent) {
		this.parent = parent;
	}
	
	/**
	 * Get principal service which main use case is to resolve from guid to email. 
	 * @return
	 */
	protected PetroVaultPrincipalService getPrincipalService() {
		return ServiceAccessor.getPVPrincipalService();
	}
}
