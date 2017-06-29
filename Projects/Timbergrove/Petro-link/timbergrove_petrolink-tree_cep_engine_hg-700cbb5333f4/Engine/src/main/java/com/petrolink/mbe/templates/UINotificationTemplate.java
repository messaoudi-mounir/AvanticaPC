package com.petrolink.mbe.templates;

import java.io.Reader;
import java.io.StringReader;

import org.jdom2.Element;

/**
 * This class is used as packager for UI notification model in Notification Template Service.
 * @author Joel Lang, Aristo
 *
 */
public final class UINotificationTemplate extends NotificationTemplate {
	/**
	 * The ID of the title template.
	 */
	public static final String TITLE_ID = "title";
	
	/**
	 * The ID of the body template.
	 */
	public static final String BODY_ID = "body";
	
	private String title;
	private String body;
	
	@Override
	public Object getTemplateSource(final String id) {
		return new UINotificationTemplateSource(id);
	}

	@Override
	public Element toElement() {
		Element e = new Element("InlineTemplate");
		e.addContent(new Element("Title").setText(title));
		e.addContent(new Element("Body").setText(body));
		return e;
	}
	
	@Override
	public void load(Element e) {
		super.load(e);
		
		title = e.getChildText("Title", e.getNamespace());
		body = e.getChildText("Body", e.getNamespace());
	}
	
	/**
	* Actual packager class for each category.
	*/
	public final class UINotificationTemplateSource extends TemplateSource {
		private final String id;
		
		UINotificationTemplateSource(String id) {
			this.id = id;
		}
		
		@Override
		public Reader getReader() {
			switch (id) {
				case TITLE_ID:
					return new StringReader(title);
				case BODY_ID:
					return new StringReader(body);
				default:
					return null;
			}
		}
	}
}
