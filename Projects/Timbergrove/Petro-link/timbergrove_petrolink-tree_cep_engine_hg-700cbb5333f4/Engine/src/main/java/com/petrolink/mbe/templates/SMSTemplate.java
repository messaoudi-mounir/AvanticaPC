package com.petrolink.mbe.templates;

import java.io.Reader;
import java.io.StringReader;

import org.jdom2.Element;

import com.petrolink.mbe.setting.SMTPConnectionSettings;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.petrolink.mbe.util.XmlConfigUtil;

/**
 * This class is used as packager for EmailSetting model in Notification template service.
 * Notification Service will use it to know what is category being used in single template (eg BODY, SUBJECT).
 * 
 * @author aristo
 *
 */
public class SMSTemplate extends NotificationTemplate {
	private String body, subject;
	private SMTPConnectionSettings connectionSettings;
	
	/**
	* Actual packager class for each category.
	*/
	public class SMSTemplateSource extends TemplateSource {
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
		public SMSTemplateSource(final String type) {
			this.category = type;
		}

		@Override
		public Reader getReader() {
			switch (category) {
				case BODY:
					return new StringReader(body);
				case SUBJECT:
					return new StringReader(subject);
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

		final String connectionElementName = "Connection";
		Element mailConnectionXml = e.getChild(connectionElementName, e.getNamespace());

		if (mailConnectionXml != null) {
			if (mailConnectionXml.getAttribute("id") == null) {
				this.setConnectionSettings(XmlSettingParser.parseSMTPConnectionSettings(mailConnectionXml));				
			} else {
				
			}
		}
		
		this.body = XmlConfigUtil.getAttributeOrChildText(e, "Body");
		this.subject = XmlConfigUtil.getAttributeOrChildText(e, "Subject");
	}

	@Override
	public Object getTemplateSource(final String id) {
		return new SMSTemplateSource(id);
	}

	
	@Override
	public Element toElement() {
		Element e = new Element("SMSNotification");
		e.addContent(new Element("Body").setText(this.body));
		e.addContent(new Element("Subject").setText(this.subject));
		return e;
	}

	/**
	 * @return the connectionSettings
	 */
	public SMTPConnectionSettings getConnectionSettings() {
		return connectionSettings;
	}

	/**
	 * @param connectionSettings the connectionSettings to set
	 */
	public void setConnectionSettings(SMTPConnectionSettings connectionSettings) {
		this.connectionSettings = connectionSettings;
	}
}
