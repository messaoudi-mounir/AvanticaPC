package com.petrolink.mbe.templates;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.setting.XmlSettingParser;
import com.rabbitmq.client.Channel;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.rabbitmq.service.RMQRestfulService;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Service which provide Notification template
 * @author aristo
 *
 */
public class NotificationTemplateService extends RMQRestfulService {
	private static Logger logger = LoggerFactory.getLogger(NotificationTemplateService.class);
	
	protected Configuration cfg = null;
	protected NotificationTemplateDAO dao;
		
	protected class NotificationTemplateConsumer extends RMQRestfulServiceConsumer {
		public NotificationTemplateConsumer(Channel channel) {
			super(channel);
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.GenericRestfulService.GenericRestfulServiceConsumer#doDelete(java.util.Map, org.jdom2.Element)
		 */
		@Override
		protected Document doDelete(Map<String, Object> map, Element root) {
			Element replyRoot = new Element("Templates");
			Document replyDocument = new Document(replyRoot);
			for (Element templateElement : root.getChildren()) {
				UUID uuid = UUID.fromString(templateElement.getAttributeValue("uuid"));
				
				NotificationTemplate deletedElement = dao.delete(uuid);
				if (deletedElement != null) {
					Element elem = deletedElement.toElement();
					if (elem != null) {
						replyRoot.addContent(elem);
					}
				}
			}
			return replyDocument;
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.GenericRestfulService.GenericRestfulServiceConsumer#doPost(java.util.Map, org.jdom2.Element)
		 */
		@Override
		protected Document doPost(Map<String, Object> map, Element root) {
			Element replyRoot = new Element("Templates");
			Document replyDocument = new Document(replyRoot);
			for (Element templateElement : root.getChildren()) {
				try {
					Element replyElement = new Element(templateElement.getName());
					NotificationTemplate template = NotificationTemplateFactory.parseTemplate(templateElement);
					
					// Add template loader
					dao.createOrUpdate(template);
					
					replyElement.setAttribute("uuid", template.getUUID().toString());
					replyRoot.addContent(replyElement);
					
					// Saving to file system
					dao.storeTemplate(template.getUUID(), templateElement);
				} catch (EngineException e) {
					logger.error("NotificationTemplateService Exception loading notification template", e);
				} catch (Exception e) {
					logger.error("NotificationTemplateService Exception loading notification template", e);
				}
			}
			return replyDocument;
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.GenericRestfulService.GenericRestfulServiceConsumer#doGet(java.util.Map, org.jdom2.Element)
		 */
		@Override
		protected Document doGet(Map<String, Object> map, Element root) {
			Element replyRoot = new Element("Templates");
			Document replyDocument = new Document(replyRoot);
			if (root.getName().equals("TemplatesGetAll")) {
				for (NotificationTemplate template : dao.retrieveAll().values()) {
					replyRoot.addContent(template.toElement());					
				}
			} else {
				for (Element templateElement : root.getChildren()) {
					UUID uuid = UUID.fromString(templateElement.getAttributeValue("uuid"));
					
					NotificationTemplate t  = dao.retrieve(uuid);
					if (t != null) {
						replyRoot.addContent(t.toElement());
					}
				}
				
			}
			return replyDocument;
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.RMQRestfulService.RMQRestfulServiceConsumer#doPut(java.util.Map, org.jdom2.Element)
		 */
		@Override
		protected Document doPut(Map<String, Object> map, Element root) {
			return doPost(map, root);
		}
	}
	
	@Override
	public void startService() throws EngineException {
		logger.info("NotificationTemplateService Starting");
		startServiceFromInternalSetting();

		super.startService();
		logger.info("NotificationTemplateService Started");
	}
	
	/**
	 * Start Service but doesn't load superclass's setting (which conatin rmq for example). Differ to call to startService() as it doesn't call superclass.
	 * Useful for testing.
	 */
	public final void startServiceFromInternalSetting() {
		cfg = new Configuration(Configuration.VERSION_2_3_23);
		NotificationTemplateLoader tl = new NotificationTemplateLoader(dao);
		//Aristo: Missing parameter Error in runtime should be suppressed most of the time
		//cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER); 		
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
		cfg.setTemplateLoader(tl);
		
		if (dao instanceof NotificationTemplateDAOInMemory) {
			NotificationTemplateDAOInMemory daoEngine = (NotificationTemplateDAOInMemory) dao;
			daoEngine.initialize();
		}
	}
	
	@Override
	public void stopService() {
		logger.info("NotificationTemplateService Stopping");
		super.stopService();
		
		if (dao instanceof NotificationTemplateDAOInMemory) {
			NotificationTemplateDAOInMemory daoEngine = (NotificationTemplateDAOInMemory)dao;
			daoEngine.cleanup();
		}
		
		logger.info("NotificationTemplateService Stopped");
	}

	@Override
	protected GenericRPCServiceConsumer getConsumer(Channel channel) {
		return new NotificationTemplateConsumer(channel);
	}

	/**
	 * @return the templates
	 */
	public Map<UUID, NotificationTemplate> getTemplates() {
		return dao.retrieveAll();
	}
	
	/**
	 * Retrieve a Template.
	 * @param uuid The UUID of the template to retrieve
	 * @return NotificationTemplate which has the specified UUID
	 */
	public NotificationTemplate getTemplate(UUID uuid) {
		return dao.retrieve(uuid);
	}
	
	/**
	 * Delete a Template.
	 * @param uuid The UUID of the template to delete
	 * @return NotificationTemplate which has the specified UUID
	 */
	public NotificationTemplate deleteTemplate(UUID uuid) {
		return dao.delete(uuid);
	}
	
	/**
	 * Store (Create or Update) a Template.
	 * @param template Template to store.
	 * @return UUID which is used to store the template.
	 */
	public UUID storeTemplate(NotificationTemplate template) {
		return dao.createOrUpdate(template);
	}

	/* (non-Javadoc)
	 * @see com.smartnow.rabbitmq.service.RMQRPCService#load(org.jdom2.Element)
	 */
	@Override
	public void load(Element e) throws EngineException {
		logger.info("NotificationTemplateService Initializing");
		super.load(e);
		loadSetting(e);
		logger.info("NotificationTemplateService Initialized");
	}

	/**
	 * Load from setting. Differ to call to load() as it doesn't call superclass.
	 * Useful for testing.
	 * @param e
	 */
	public final void loadSetting(final Element e) {
		NotificationTemplateDAOInMemory daoEngine = new NotificationTemplateDAOInMemory();
		
		if (e != null) {
			Element pathElement = e.getChild("Path", e.getNamespace());
			daoEngine.setPath(XmlSettingParser.parsePathSetting(pathElement));
		}
		dao = daoEngine;
	}

	/**
	 * Run a FreeMaker template.
	 * @param templateName template's full name which contain UUID and category
	 * @param dataModel The data model to use for the template
	 * @return processed template 
	 * @throws TemplateException
	 * @throws IOException
	 */
	public final String processTemplate(final String templateName, final Object dataModel) throws TemplateException, IOException {
		StringWriter out = new StringWriter();	
		Template t = cfg.getTemplate(templateName);
		t.process(dataModel, out);
		return out.toString();
	}
	
	/**
	 * Run a FreeMaker template. By specifying templateId and category.
	 * @param templateId
	 * @param templateCategory
	 * @param dataModel 
	 * @return The processing result
	 * @throws TemplateException
	 * @throws IOException
	 */
	public final String processTemplate(final UUID templateId, final String templateCategory, final Object dataModel) throws TemplateException, IOException {
		String templateName = makeName(templateId, templateCategory);
		return processTemplate(templateName, dataModel);
	}
	
	/**
	 * Make a template full name template's full name which contain UUID and category.
	 * @param templateId
	 * @param templateCategory
	 * @return The full name string
	 */
	public static String makeName(final UUID templateId, final String templateCategory) {
		return templateId.toString() + "." + templateCategory;
	}
}
