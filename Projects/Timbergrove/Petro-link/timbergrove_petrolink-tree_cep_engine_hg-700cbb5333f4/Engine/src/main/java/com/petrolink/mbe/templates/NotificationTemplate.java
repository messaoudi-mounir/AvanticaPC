package com.petrolink.mbe.templates;

import java.io.Reader;
import java.util.UUID;

import org.jdom2.Element;

/**
 * Base Class for Notification Templates
 * @author paul
 *
 */
public abstract class NotificationTemplate {
	private UUID uuid;
	private long lastModified = System.currentTimeMillis();
	
	/**
	 * Template Source base implementation.
	 * @author aristo
	 *
	 */
	public abstract class TemplateSource {
		/**
		 * Get time when template last modified.
		 * @return Milis time based on Java Epoch.
		 */
		public long getLastModified() {
			return lastModified;
		}
		
		/**
		 * Get reader for the templatesource
		 * @return reader for this template.
		 */
		public abstract Reader getReader();
	}

	/**
	 * @return the uuid
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * Load the configuration based on Xml element.
	 * @param e Xml Configuration
	 */
	public void load(Element e) {
		String uuidString = e.getAttributeValue("uuid");
		if (uuidString != null)
			this.uuid = UUID.fromString(uuidString);
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * Get Template Source with specified ID
	 * @param id
	 * @return TemplateSource Object.
	 */
	public abstract Object getTemplateSource(String id);

	/**
	 * Convert current Notification template to element
	 * @return XmlElement representing this notification template.
	 */
	public abstract Element toElement();
}
