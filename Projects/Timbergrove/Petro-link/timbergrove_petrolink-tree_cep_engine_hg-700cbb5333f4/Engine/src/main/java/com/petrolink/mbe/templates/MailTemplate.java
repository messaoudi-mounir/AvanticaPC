package com.petrolink.mbe.templates;

import java.io.Reader;
import java.io.StringReader;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.setting.EmailSetting;
import com.petrolink.mbe.setting.XmlSettingParser;

/**
 * This class is used as packager for EmailSetting model in Notification template service.
 * Notification Service will use it to know what is category being used in single template (eg BODY, SUBJECT).
 * 
 * @author aristo
 *
 */
public class MailTemplate extends NotificationTemplate {
	private static final Logger logger = LoggerFactory.getLogger(MailTemplate.class);
	private Element modelXmlElement;
	private EmailSetting model;
	
	/**
	* Actual packager class for each category.
	*/
	public class MailTemplateSource extends TemplateSource {
		/**
		 * The Key for Body Template inside MailTemplate.
		 */
		public static final String BODY = "body";
		/**
		 * The Key for Subject Template inside MailTemplate.
		 */
		public static final String SUBJECT = "subject";
		
		String category = BODY;
		
		/**
		 * Constructor.
		 * @param type Type for this template source, see {@link #BODY} and {@link #SUBJECT}
		 */
		public MailTemplateSource(final String type) {
			this.category = type;
		}

		@Override
		public Reader getReader() {
			EmailSetting currentModel = getModel();
			switch (category) {
				case BODY:
					return new StringReader(currentModel.getEmailBody());
				case SUBJECT:
					return new StringReader(currentModel.getEmailSubject());
				default:
					return null;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.templates.NotificationTemplate#load(org.jdom2.Element)
	 */
	@Override
	public void load(Element e) {
		super.load(e);
		
		EmailSetting emailTemplateSetting = XmlSettingParser.parseEmailSetting(e, logger);
		this.setModel(emailTemplateSetting);
		modelXmlElement = e;
	}

	@Override
	public Object getTemplateSource(final String id) {
		return new MailTemplateSource(id);
	}

	
	@Override
	public Element toElement() {
		return modelXmlElement;
	}


	/**
	 * @return the model
	 */
	public final EmailSetting getModel() {
		return model;
	}


	/**
	 * @param newModel the model to set
	 */
	public final void setModel(final EmailSetting newModel) {
		this.model = newModel;
	}	
}
