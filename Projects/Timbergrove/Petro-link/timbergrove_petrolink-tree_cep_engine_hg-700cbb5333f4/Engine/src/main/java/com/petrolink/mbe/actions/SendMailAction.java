package com.petrolink.mbe.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.Address;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.services.PetroVaultPrincipalService;
import com.petrolink.mbe.setting.EmailSetting;
import com.petrolink.mbe.setting.EmailSetting.GuidAddressMappings;
import com.petrolink.mbe.setting.SMTPConnectionSettings;
import com.petrolink.mbe.setting.SendMailActionSetting;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.petrolink.mbe.templates.MailTemplate;
import com.petrolink.mbe.templates.MailTemplate.MailTemplateSource;
import com.petrolink.mbe.templates.NotificationTemplate;
import com.petrolink.mbe.templates.NotificationTemplateService;
import com.petrolink.mbe.util.EmailHelper;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.engine.Engine;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;
import com.smartnow.engine.settings.EngineSettings;
import com.smartnow.engine.util.NamedValueResource;

import freemarker.template.TemplateException;

/**
 * MBE Action for sending Email.
 * @author aristo
 *
 */
public class SendMailAction extends NotificationAction {

	
	private SendMailActionSetting configuration;
	
	private final Logger mailtestLogger = LoggerFactory.getLogger("SendMailActionTest");
	private static final Logger logger = LoggerFactory.getLogger(SendMailAction.class);
	private long lastEmailtemplateBeingUsed = 0;
	private EmailSetting cachedActiveConfig = null;
	
	/**
	 * Constructor.
	 */
	public SendMailAction() {
		
	
	}
	
	
//	private String templateFilePathString;		
//	/**
//	 * Set Template Name for this Action. Also find actual path for this action 
//	 * @param mailTemplate The name of mail template or null if not used
//	 * @throws EngineException When the actual template file is not in storage
//	 */
//	public final void setTemplateName(final String mailTemplate) throws EngineException {
//		templateName =  mailTemplate;
//		if (StringUtils.isNotBlank(mailTemplate)) {
//			String mailTemplatesDir = getMailTemplatesFolder();
//			templateFilePathString = locateTemplateFile(mailTemplatesDir, templateName);
//			if (templateFilePathString == null) {
//				throw new EngineException("Unable to discover template file " + mailTemplatesDir + templateFilePathString);
//			} else {
//				logger.info("Template Located at {}{}", mailTemplatesDir, templateName);
//			}
//		}
//	}
	
//	/**
//	 * Get Location of Mail templates in the system.
//	 * @return
//	 */
//	private static String getMailTemplatesFolder() {
//		Properties prop = Engine.getInstance().getUserDefinedProperties();
//		String mailTemplateLocation  = (String) prop.get("MailTemplates");
//		String mailTemplateParent = System.getProperty("user.dir") + mailTemplateLocation;
//		return mailTemplateParent;
//	}
	
//	/**
//	 * Look for Mail templates Path.
//	 * @param mailTemplatesPath Parent Folder of mail template
//	 * @param templateName The name of template
//	 * @return location of actual file
//	 */
//	private static String locateTemplateFile(final String mailTemplatesPath, final String templateName) {
//		//Get templates location
//		String cfgFile = null;
//		String mailTemplatePathString = mailTemplatesPath + templateName;
//		Path mailTemplatePath = Paths.get(mailTemplatePathString);
//	
//		
//		if (Files.exists(mailTemplatePath)) {
//			cfgFile = mailTemplatePath.toString();
//		} else if (StringUtils.isBlank(FilenameUtils.getExtension(templateName))) {
//			//Check, user may forgot extension xml
//			String mailTemplatePathExtString = mailTemplatePathString + ".xml";
//			Path mailTemplateExtPath = Paths.get(mailTemplatePathExtString);
//			if (Files.exists(mailTemplateExtPath)) {
//				cfgFile = mailTemplateExtPath.toString();
//			} 
//		} 
//		return cfgFile;
//	}
	
//	/**
//	 * Load Mail Template based on Template File Path.
//	 * @return
//	 */
//	private Element loadMailTemplateXml() {
//		String cfgFile = templateFilePathString;
//				
//		// - Load template mail content
//		logger.info("Loading configuration from file {}", cfgFile);
//		File xmlFile = new File(cfgFile);
//		SAXBuilder builder = new SAXBuilder();
//		Element root = null;
//		try {
//			Document doc = builder.build(xmlFile);
//			root = doc.getRootElement();		
//
//		} catch (JDOMException | IOException e) {
//			logger.error("Failure Loading configuration from file " + cfgFile, e);
//		}
//		return root;
//	} 
	
	/**
	 * This will merge context from EmailSetting and parent caller of this action.
	 * This will allow Param inside Email setting to be used in the template.
	 * @param currentConfig
	 * @param context
	 * @return
	 */
	private Map<String, Object> mergeMailContexes(final EmailSetting currentConfig, final Map<String, Object> context) {
		//Make new Contexes for putting params
		Map<String, Object> ctx = null;
		
		if(context != null) {
			ctx = createTemplateContext(context);
		} else {
			logger.error("Send email action has null context!");
			ctx = new HashMap<String, Object>();
		}
				
		// Putting params in the mail context
		if (currentConfig != null) {
			ctx.putAll(currentConfig.getActionParameters());
		}
		return ctx;
	}
	
	@Override
	protected int executeAction(final Map<String, Object> context) throws EngineException {	
		try {
			SendMailAction.sendEmailAutoRetry(this, context);
		}catch(EmailException em) {
			logger.error("SendMailAction fail to send email", em);
		}
		return SUCCESS;
	}
	
	/**
	 * 
	 * @param action Action instance
	 * @param context Email context to generate mail
	 * @return List of Email actually send (if no failure, there will be only one sent
	 * @throws EmailException
	 */
	public static List<Email> sendEmailAutoRetry(SendMailAction action, final Map<String, Object> context) throws EmailException {
		Address[] validUnsent = null;
		Logger logger = SendMailAction.logger;
		ArrayList<Email> emails = new ArrayList<Email>();
		do {
			Email email = action.generateEmail(context, validUnsent);
			try {
				validUnsent = null;
				email.send();
				emails.add(email);
				logger.info("Email sent with subject '{}' ", email.getSubject());
			} catch (EmailException em) {
				Throwable cause = em.getCause();
				boolean handled = false;
				if (cause instanceof SendFailedException) {
					SendFailedException sfe = (SendFailedException)cause;
					if ( ArrayUtils.isNotEmpty(sfe.getInvalidAddresses())){
						if(logger.isErrorEnabled()) {
							String csvInvalidAddr = StringUtils.join(sfe.getInvalidAddresses(), ",");
							logger.error("Failure to send email with subject '{}' due to {}, invalid address {}",email.getSubject(), sfe.getLocalizedMessage(),csvInvalidAddr);
						}
					} 
					if (ArrayUtils.isNotEmpty(sfe.getValidUnsentAddresses())) {
						validUnsent = sfe.getValidUnsentAddresses();
						if (logger.isInfoEnabled()) {
							String resendAddr = StringUtils.join(sfe.getValidUnsentAddresses(), ",");
							logger.info("Retry sending email with subject '{}' for {} ", email.getSubject(), resendAddr);
						}
						handled = true;
					}
				}
				
				if (!handled) {
					if (logger.isDebugEnabled()) {
						String emailString = EmailHelper.getRfc822Message(email);
						logger.error("Failure sending email {}", emailString, em);
					} else {
						logger.error("Failure sending email", em);
					}
				}
			}
		} 
		while(validUnsent != null);
		return emails;
	}
	
	/**
	 * Filter the Internet Address Collection based on valid Address. Null valid address means it is not filtered
	 * @param addresses Address to be filtered
	 * @param validAddress
	 * @return the Addresses instance if Valid Address is Null, otherwise new list containing address in addresses which also available in valid Address
	 */
	private static List<InternetAddress> filterAddress(List<InternetAddress> addresses, Address[] validAddress) {
		if (addresses == null) return null;
		if (validAddress == null ) return addresses;
		
		ArrayList<InternetAddress> arrayList = new ArrayList<InternetAddress>();
		for(InternetAddress address: addresses) {
			if (ArrayUtils.contains(validAddress, address)){
				arrayList.add(address);
			}
		}
		return arrayList;
	}
		
	@Override
	protected int executeTestAction(final Map<String, Object> context) throws EngineException {
		EmailSetting currentConfig = getActiveConfiguration();
		StringBuilder mailBuilder = new StringBuilder();
		mailBuilder.append(currentConfig.getConnectionSetting().toString());
		mailBuilder.append(System.lineSeparator());
		
		// Get Dictionary for UUID to Address 
				Map<String, List<InternetAddress>> latestPrincipalToEmails =  
						getUuidStringToAddressesDictionary(currentConfig.getGuidRecipientsMappingMode(), currentConfig.getGuidRecipientsCombined());
				
		// Preparing Mail Context
		Map<String, Object> ctx = mergeMailContexes(currentConfig, context);
				
		// get template content
		String bodyResult = processTemplate(MailTemplateSource.BODY, ctx);
		String subjectResult = processTemplate(MailTemplateSource.SUBJECT, ctx);
		
		
		List<InternetAddress> toAddresses = currentConfig.getInternetAddressListTO(latestPrincipalToEmails);
		List<InternetAddress> ccAddresses = currentConfig.getInternetAddressListCC(latestPrincipalToEmails);
		List<InternetAddress> bccAddresses = currentConfig.getInternetAddressListBCC(latestPrincipalToEmails);
		
		mailBuilder.append("To: ").append(IterableUtils.toString(toAddresses)).append(System.lineSeparator());
		mailBuilder.append("Cc: ").append(IterableUtils.toString(ccAddresses)).append(System.lineSeparator());
		mailBuilder.append("Bcc: ").append(IterableUtils.toString(bccAddresses)).append(System.lineSeparator());
		
		mailBuilder.append("Subject: ").append(subjectResult).append(System.lineSeparator());
		mailBuilder.append("Body: ").append(bodyResult).append(System.lineSeparator());
		
		mailtestLogger.info(mailBuilder.toString());
		
		return 0;
	}
	
	/**
	 * Construct email based on active setting and with current context.
	 * @param context
	 * @param filter 
	 * @return generated email based on current context and configuration
	 */
	public final Email generateEmail(final Map<String, Object> context, final Address[] filter) {
		//Check Config
		EmailSetting currentConfig = null;
		try {
			currentConfig = getActiveConfiguration();
			//CHECKSTYLE:OFF : Will be processed at next check
		} catch (EngineException e) {
			//CHECKSTYLE:ON
		}
		//Unable to check config
		if (currentConfig == null) {
			logger.error("Can not Process Send Mail as there is no Active Configuration");
			return null; //Nothing you can do
		}
		return generateEmail(currentConfig, context, filter);
	}
	
	/**
	 * Construct email message based on specified configuration.
	 * @param currentConfig
	 * @param context
	 * @param filter 
	 * @return  generated email based on specified context and configuration
	 */
	public final Email generateEmail(final EmailSetting currentConfig, final Map<String, Object> context, final Address[] filter) {
		if (currentConfig == null) {
			logger.error("Can not Process Send Mail as there is no Active Configuration");
			return null; //Nothing you can do
		}
		
		//Email type, default is html as it is more generic
		Email email;
		if (currentConfig.isPlainTextEmail()) {
			email = new SimpleEmail();
		} else {
			email = new HtmlEmail();
		}
		
		//Configure email
		currentConfig.getConnectionSetting().configureEmailConnection(email);
		
		// Preparing Mail Context
		Map<String, Object> ctx = mergeMailContexes(currentConfig, context);
		
		// Get Dictionary for UUID to Address 
		Map<String, List<InternetAddress>> latestPrincipalToEmails =  
				getUuidStringToAddressesDictionary(currentConfig.getGuidRecipientsMappingMode(),currentConfig.getGuidRecipientsCombined());
		// get template content
		String bodyResult;
		String subjectResult;
		try {
			bodyResult = processTemplate(MailTemplateSource.BODY, ctx);
			subjectResult = processTemplate(MailTemplateSource.SUBJECT, ctx);
		} catch (EngineException em) {
			logger.error("Failure processing email's template", em);
			return null;
		}
		
		
		//The following string operations may should be done on loading 
		try {
			email.setFrom(currentConfig.getEmailFrom().toString());

			//Beware to not SET the following address to empty list
			//there is unfixed issue in https://issues.apache.org/jira/browse/EMAIL-115
			List<InternetAddress> toAddresses = filterAddress(currentConfig.getInternetAddressListTO(latestPrincipalToEmails), filter);
			
			
			if ((toAddresses != null) && !(toAddresses.isEmpty())) {
				email.setTo(toAddresses);
			} 
			
			List<InternetAddress> ccAddresses = filterAddress(currentConfig.getInternetAddressListCC(latestPrincipalToEmails),filter); 
			if ((ccAddresses != null) && !(ccAddresses.isEmpty())) {
				email.setCc(ccAddresses);
			} 
			
			List<InternetAddress> bccAddresses = filterAddress(currentConfig.getInternetAddressListBCC(latestPrincipalToEmails),filter); 
			if ((bccAddresses != null) && !(bccAddresses.isEmpty())) {
				email.setBcc(bccAddresses);
			} 
			
			// - Set subject
			email.setSubject(subjectResult);
			
			// - Set Body
			if (email instanceof HtmlEmail) {
				HtmlEmail htmlEmail = (HtmlEmail) email;
				htmlEmail.setHtmlMsg(bodyResult);
			} else {
				email.setMsg(bodyResult);
			}
			
			
			return email;
		} catch (EmailException em) {
			logger.error("Failure constructing email", em);
			return null;
		} 
	}
	
	/**
	 * Get uuid to Email Address dictionary.
	 * @return
	 */
	private Map<String, List<InternetAddress>> getUuidStringToAddressesDictionary(final GuidAddressMappings resolutionMode, final HashSet<UUID> guidRecipientsAll) {
		if (guidRecipientsAll == null) { 
			return null;
		}
		if (guidRecipientsAll.size() <= 0) {
			return null;
		}
		
		PetroVaultPrincipalService currentService = getPrincipalService();
		
		if (currentService != null) {
			
			if (resolutionMode == GuidAddressMappings.SMS_PHONE_EMAIL) {
				return currentService.getSmsPhoneInternetAddresses(guidRecipientsAll);
			} else {
				return currentService.getInternetAddresses(guidRecipientsAll);
			}
		} else {
			return null;
		}
	}
	
	
	/**
	 * Merge Mail action setting with template, if available.
	 * @return 
	 * @throws EngineException
	 */
	private EmailSetting getActiveConfiguration() throws EngineException {
		SendMailActionSetting actionSetting = this.configuration;
		
		EmailSetting activeConfiguration;
		
		EmailSetting actionEmailSetting = actionSetting.getEmailSetting();
		EmailSetting emailTemplateSetting = null;
		MailTemplate emailTemplate = loadEmailTemplate();
		long lastTemplateUpdated = 0;
		if (emailTemplate != null) {
			emailTemplateSetting = emailTemplate.getModel();
			lastTemplateUpdated = emailTemplate.getLastModified();

			if ((cachedActiveConfig == null) 
					|| (lastTemplateUpdated > lastEmailtemplateBeingUsed)
					) {
				//Merged Config need to be recreated because either of:
				//1. No previous active configuration
				//2. Template is now updated
				
				activeConfiguration = new EmailSetting();
				if (emailTemplateSetting == null) {
					activeConfiguration = actionEmailSetting;
				} else	if (actionEmailSetting == null) {
					activeConfiguration = emailTemplateSetting;
				} else {
					//Merge
					activeConfiguration = 
							actionEmailSetting.mergeWithTemplate(emailTemplateSetting);
				}
				cachedActiveConfig = activeConfiguration;
				lastEmailtemplateBeingUsed = emailTemplate.getLastModified();
			} else {
				//Use cached value
				activeConfiguration = cachedActiveConfig;
			}			

			//Some missing parameters
			List<String> missingParameters = activeConfiguration.getMissingSettings();
			if (!missingParameters.isEmpty()) {
				throw new EngineException("Missing Parameters when loading definition: " + missingParameters.toString());
			}
			return activeConfiguration;
		}
		
		return null;
	}
	
	
	/**
	 * Load attributes.
	 */
	@Override
	public void load(final Element e, final Node parent) throws EngineException {
		super.load(e, parent);
		SendMailActionSetting actionSetting = XmlSettingParser.parseSendMailActionSetting(e, logger);
		
		loadFromSetting(actionSetting);
	}
	
	/**
	 * Load from setting. Differ to call to load() as it doesn't call superclass.
	 * Useful for testing.
	 * @param actionSetting 
	 */
	public final void loadFromSetting(final SendMailActionSetting actionSetting) {
		
		if (actionSetting != null && actionSetting.getEmailSetting() != null) {
			String connRef = actionSetting.getEmailSetting().getConnectionSettingRef();
			
			Engine eng = Engine.getInstance();
			if (StringUtils.isNotBlank(connRef) && eng != null) {
				NamedValueResource settings = (NamedValueResource) Engine.getInstance().getSetEntry(EngineSettings.CONNECTIONS_SET, connRef);
				SMTPConnectionSettings smtpSetting = new SMTPConnectionSettings(settings);
				actionSetting.getEmailSetting().setConnectionSetting(smtpSetting);
			}
		}
		
		this.configuration = actionSetting;
		this.cachedActiveConfig = null;
				
		//Store this action setting as mail template (for runtime freemarker processing)
		EmailSetting actionEmailSetting = actionSetting.getEmailSetting();
		if (actionEmailSetting.hasEmailBodyOrSubject()) {
			MailTemplate actionTemplate = new MailTemplate();
			actionTemplate.setUUID(getInstanceId());
			actionTemplate.setModel(actionEmailSetting);
			
			getNotificationTemplateService().storeTemplate(actionTemplate);
		}
	}
	
	/**
	 * Load Email Template from server, should be called every time action is processed by design.
	 * This to ensure latest update in service is taken
	 * @return
	 */
	private MailTemplate loadEmailTemplate() {
		SendMailActionSetting actionSetting = this.configuration;
		if (actionSetting ==  null) { 
			return null;
		}
		
		UUID templateUUID = actionSetting.getTemplateId();
		//If template being used
		if (!UUIDHelper.isNullOrEmpty(templateUUID)) {
			NotificationTemplate template = getNotificationTemplateService().getTemplate(templateUUID);
			if (template instanceof MailTemplate) {
				MailTemplate mailTemplate = (MailTemplate) template;
				return mailTemplate;
			}
		}
		return null;
	}
	
	/**
	 * Run a FreeMaker template.
	 * @param template
	 * @param context
	 * @return processed template 
	 */
	private String processTemplate(final String category, final Map<String, Object> context) throws EngineException {
		UUID targetUUID = UUIDHelper.EMPTY;
		SendMailActionSetting currentActionSetting = this.configuration;
		EmailSetting currentSetting = currentActionSetting.getEmailSetting();
		
		if (MailTemplateSource.BODY.equalsIgnoreCase(category)) {
			if (currentSetting.hasEmailBody()) {
				targetUUID = getInstanceId();
			}
		} else if (MailTemplateSource.SUBJECT.equalsIgnoreCase(category)) {
			if (currentSetting.hasEmailSubject()) {
				targetUUID = getInstanceId();
			}
		}
		
		//Not available in main Setting, use template
		if (UUIDHelper.isNullOrEmpty(targetUUID)) {
			targetUUID = currentActionSetting.getTemplateId();
		}
		
		//Actual call to service
		if (!UUIDHelper.isNullOrEmpty(targetUUID)) {
			try {
				return getNotificationTemplateService().processTemplate(targetUUID, category, context);
			} catch (TemplateException | IOException e) {
				throw new EngineException("Failure to load Template: " + NotificationTemplateService.makeName(targetUUID, category), e);
			}
		} else {
			//Basically can't process anything
			return StringUtils.EMPTY;
		}
	}
	
	@Override
	public void init(final Map<String, Object> context) throws EngineException {
			
	}
	
	@Override
	public final void finalize(final Map<String, Object> context) throws EngineException {
		logger.info("Rule {} email action, finalized", this.getRuleFlow().getRuleId());
		
	}
		
	/**
	 * Cleaning up which can be called by Ruleflow when ruleflow doing cleanup
	 */
	@Override
	public final void deprovision() {
		logger.info("Rule {} email action, deprovision", this.getRuleFlow().getRuleId());
		//Remove Notification template
		getNotificationTemplateService().deleteTemplate(getInstanceId());
	}
}
