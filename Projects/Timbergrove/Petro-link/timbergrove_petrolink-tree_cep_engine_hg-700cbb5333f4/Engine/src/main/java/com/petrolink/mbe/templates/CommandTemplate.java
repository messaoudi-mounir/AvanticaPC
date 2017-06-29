package com.petrolink.mbe.templates;

import java.io.Reader;
import java.io.StringReader;

import org.jdom2.Element;

/**
 * A template for commands to be executed on the local system.
 * @author langj
 *
 */
public final class CommandTemplate extends NotificationTemplate {
	/**
	 * The ID of the command text.
	 */
	public static final String COMMAND_ID = "command";
	
	private String command;
	
	@Override
	public void load(Element e) {
		super.load(e);
		command = e.getTextTrim();
	}
	
	@Override
	public Object getTemplateSource(String id) {
		return new CommandTemplateSource(id);
	}

	@Override
	public Element toElement() {
		Element e = new Element("CommandTemplate");
		e.addContent(command);
		return e;
	}
	
	@SuppressWarnings("javadoc")
	public final class CommandTemplateSource extends TemplateSource {
		private final String id;
		
		public CommandTemplateSource(String id) {
			this.id = id;
		}

		@Override
		public Reader getReader() {
			if (id.equals(COMMAND_ID))
				return new StringReader(command);
			return null;
		}
		
	}
}
