package com.petrolink.mbe.templates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.engine.RuntimeConfig;
import com.petrolink.mbe.setting.PathSetting;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.engine.exceptions.EngineException;



/**
 * Data Access Object For notificaton.
 * This abstraction will help changing storage infrastructure if we need to.
 * @author aristo
 *
 */
public class NotificationTemplateDAOInMemory implements NotificationTemplateDAO {
	private HashMap<UUID, NotificationTemplate> templates = new HashMap<UUID, NotificationTemplate>();
	private PathSetting path;
	private String location;
	private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateDAO.class);
	
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.templates.NotificationTemplateDAO#createOrUpdate(com.petrolink.mbe.templates.NotificationTemplate)
	 */
	@Override
	public UUID createOrUpdate(final NotificationTemplate template) {
		return createOrUpdate(UUIDHelper.EMPTY, template);
	}
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.templates.NotificationTemplateDAO#createOrUpdate(java.util.UUID, com.petrolink.mbe.templates.NotificationTemplate)
	 */
	@Override
	public UUID createOrUpdate(final UUID requestedUUID, final NotificationTemplate template) {
		if (template != null) {
			UUID templateUUID = template.getUUID();

			//Try using requested UUID
			UUID savedUUID = requestedUUID;
			
			//If not available, use Template UUID
			if (UUIDHelper.isNullOrEmpty(savedUUID)) {
				savedUUID = templateUUID;
			}
			
			//If not available, Use new UUID
			if (UUIDHelper.isNullOrEmpty(savedUUID)) {
				savedUUID =  UUID.randomUUID();
			}
			
			//Overwrite template UUID when empty
			if (UUIDHelper.isNullOrEmpty(templateUUID)) {
				template.setUUID(savedUUID);
			}
			
			templates.put(savedUUID, template);
			
			// Store template to Filesystem
//			File f = new File(location + "/" + savedUUID + ".xml");
//			try {
//				FileWriter writer = new FileWriter(f);
//				storeTemplate(template.toElement(), writer);
//				writer.close();
//			} catch (IOException e) {
//				logger.error("Error saving trigger",e);
//			}			
			
			return savedUUID;
		}
		return UUIDHelper.EMPTY;
	}
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.templates.NotificationTemplateDAO#retrieve(java.util.UUID)
	 */
	@Override
	public NotificationTemplate retrieve(UUID uuid) {
		return templates.get(uuid);
	}
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.templates.NotificationTemplateDAO#retrieveAll()
	 */
	@Override
	public Map<UUID, NotificationTemplate> retrieveAll() {
		HashMap<UUID, NotificationTemplate> result = new HashMap<>(templates);
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.templates.NotificationTemplateDAO#delete(java.util.UUID)
	 */
	@Override
	public final NotificationTemplate delete(UUID uuidToDelete) {
		if (templates.containsKey(uuidToDelete)) {
			NotificationTemplate t = templates.get(uuidToDelete);
			
			// Delete from Storage
			try {
				File f = new File(location + t.getUUID() + ".xml");
				FileUtils.forceDelete(f);
			} catch (IOException e) {
				logger.error("Unable to delete configuration file for flow {} ", t.getUUID());
			}			

			templates.remove(uuidToDelete);
			return t;
		}
		
		return null;
	}
	
	/**
	 * @return the path
	 */
	public final PathSetting getPath() {
		return path;
	}

	/**
	 * @param pathSetting the path to set
	 */
	public final void setPath(final PathSetting pathSetting) {
		this.path = pathSetting;
		this.location = RuntimeConfig.getLocationFromPathSetting(pathSetting);
	}
	
	/**
	 * Initialize the content. This may be loading from files or service or whatever.
	 */
	public final void initialize() {
		String currentLocation = this.location;
		if (StringUtils.isBlank(currentLocation)) {
			logger.info("Notification Folder location is not set, load default notification template is skipped");
			return;
		}

		//Create well directory if not available
		File directory = new File(currentLocation);
	    if (!directory.exists()){
	    	logger.info("NotificationTemplate location is not exists. Creating in {} ", currentLocation);
	        directory.mkdirs();
	    }
		
	    //This implementation load files in default in memory
 		SAXBuilder builder = new SAXBuilder();
		File[] files = new File(currentLocation).listFiles();
		if (files != null) {
			for (File file : files) {
				try {
					if (file.getName().endsWith(".xml")) {
						logger.info("NotificationTemplateService Loading notification configuration from " + file.getName());
						Document notificationsDoc = builder.build(file);
						
						Element rootElem = notificationsDoc.getRootElement();
						String rootElemName = rootElem.getName();
						//Multimember
						if ("Templates".equalsIgnoreCase(rootElemName)) {
							List<Element> elements = rootElem.getChildren();
							//Process
							for (Element child : elements) {
								String childName = child.getName();
								try {
									NotificationTemplate template = parseTemplate(child);
									if (template != null) {
										createOrUpdate(template);
									}
								} catch (EngineException e) {
									logger.error("Failure Loading Child="+childName+" config from file " + file.getPath(), e);
								}
							}
						} else {
							//Single Member
							NotificationTemplate notifTemplate = null;
							try {
								notifTemplate = parseTemplate(rootElem);
								createOrUpdate(notifTemplate);
							} catch (EngineException e) {
								logger.error("Failure Loading Element="+rootElemName+" config from file " + file.getPath(), e);
							}
						}
						
					}
				} catch (IOException e) {
					logger.error("Failure Loading configuration from file " + file.getPath(), e);
				} catch (JDOMException e) {
					logger.error("Failure Loading configuration from file " + file.getPath(), e);
				}
			}
		}
	}
	
	/**
	 * Not implemented
	 */
	public void cleanup() {
		
	}
	
	private NotificationTemplate parseTemplate(final Element element) throws EngineException{
//		try {
			return NotificationTemplateFactory.parseTemplate(element);
//		} catch (EngineException e) {
//			logger.error("Failure to parse NotificationTemplate", e);
//		}
//		return null;
	}
	
	public void storeTemplate(UUID uuid, Element template) throws Exception {
		Writer writer = new FileWriter(this.location + "/" + uuid.toString() + ".xml");
		Document doc = new Document(template);
		doc.getRootElement().addContent(template);
		XMLOutputter xmlOutput = new XMLOutputter();

		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(doc, writer);
		writer.flush();
	}	
}
