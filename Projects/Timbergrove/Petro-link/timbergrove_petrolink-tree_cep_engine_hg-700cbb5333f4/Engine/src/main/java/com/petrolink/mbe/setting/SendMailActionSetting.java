package com.petrolink.mbe.setting;

import java.util.UUID;

/**
 * Setting for SendEmailAction
 * @author aristo
 *
 */
public class SendMailActionSetting {
	private EmailSetting emailSetting;
	private String templateName;
	private UUID templateId;
	/**
	 * @return the emailSetting
	 */
	public final EmailSetting getEmailSetting() {
		return emailSetting;
	}
	/**
	 * @param anEmailSetting the emailSetting to set
	 */
	public final void setEmailSetting(final EmailSetting anEmailSetting) {
		this.emailSetting = anEmailSetting;
	}
	/**
	 * @return the template
	 */
	public final String getTemplateName() {
		return templateName;
	}
	/**
	 * @param aTemplate the template to set
	 */
	public final void setTemplateName(final String aTemplate) {
		this.templateName = aTemplate;
	}
	
	/**
	 * @return the template
	 */
	public final UUID getTemplateId() {
		return templateId;
	}
	/**
	 * @param aTemplate the template to set
	 */
	public final void setTemplateId(final UUID aTemplate) {
		this.templateId = aTemplate;
	}
}
