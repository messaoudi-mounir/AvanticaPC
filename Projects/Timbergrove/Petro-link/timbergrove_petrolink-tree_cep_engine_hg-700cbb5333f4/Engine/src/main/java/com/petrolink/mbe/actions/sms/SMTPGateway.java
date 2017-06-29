package com.petrolink.mbe.actions.sms;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.actions.SendSMSAction;
import com.petrolink.mbe.services.PetroVaultPrincipalService;
import com.petrolink.mbe.setting.SMTPConnectionSettings;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.smartnow.engine.Engine;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.settings.EngineSettings;
import com.smartnow.engine.util.NamedValueResource;

/**
 * SMS Gateway through SMTP (EMail) Protocol
 * 
 * @author Josue
 *
 */
public class SMTPGateway extends SMSGateway {
	private SMTPConnectionSettings connectionSettings;
	private Logger logger = LoggerFactory.getLogger(SMTPGateway.class);
	private String addressPattern;
	private String[] staticTargets;

	@Override
	public boolean sendSMS(final HashSet<UUID> targets, final String subject, final String message) throws EngineException {
		Map<String, List<String>> toAddresses = null;

		PetroVaultPrincipalService petroVaultService = getPrincipalService();
		if (petroVaultService != null) {
			toAddresses = petroVaultService.getMobiles(targets);	

			try {
				Email email = new SimpleEmail();
				connectionSettings.configureEmailConnection(email);
				
				for (List<String> addresses : toAddresses.values()) {
					for (String a : addresses) {
						String address = String.format(addressPattern, a);
						email.addTo(address);
					}
				}
				
				if (staticTargets.length > 0) {
					for (String t : staticTargets) {
						String address = String.format(addressPattern, t);
						email.addTo(address);
					}
				}
				
				email.setSubject(subject);
				email.setMsg(message);
			} catch (EmailException e) {
				logger.error("Unable to format mail address", e);
				throw new EngineException(e);
			}
		} else {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.petrolink.mbe.actions.sms.SMSGateway#load(com.petrolink.mbe.actions.
	 * SendSMSAction, org.jdom2.Element)
	 */
	@Override
	public void load(SendSMSAction action, Element e) {
		super.load(action, e);

		final String connectionElementName = "Connection";
		Element mailConnectionXml = e.getChild(connectionElementName, e.getNamespace());

		if (mailConnectionXml != null) {
			if (mailConnectionXml.getAttribute("id") != null) {
				// Obtaining the Connection from the Connections Set on the Engine
				
				NamedValueResource connectionSettings = (NamedValueResource) Engine.getInstance().getSetEntry(EngineSettings.CONNECTIONS_SET, mailConnectionXml.getAttributeValue("id"));
				if (connectionSettings != null) {
					this.setConnectionSettings(new SMTPConnectionSettings(connectionSettings));					
				} else {
					logger.error("Unable to load connection settings");
				}
			} else {
				this.setConnectionSettings(XmlSettingParser.parseSMTPConnectionSettings(mailConnectionXml));				
			}
		}

		if (e.getChild("MailAddressPattern", e.getNamespace()) != null)
			this.addressPattern = e.getChildText("MailAddressPattern", e.getNamespace());
		
		if (e.getChild("Targets", e.getNamespace()) != null)
			this.staticTargets = e.getChildText("Targets", e.getNamespace()).split(",");
	}

	/**
	 * @return the Connection Settings
	 */
	public SMTPConnectionSettings getConnectionSettings() {
		return connectionSettings;
	}

	/**
	 * @param connectionSettings
	 *            the connectionSettings to set
	 */
	public void setConnectionSettings(SMTPConnectionSettings connectionSettings) {
		this.connectionSettings = connectionSettings;
	}
		
}
