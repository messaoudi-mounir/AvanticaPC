package com.petrolink.mbe.templates;

import java.io.Reader;
import java.util.UUID;

import com.petrolink.mbe.templates.NotificationTemplate.TemplateSource;
import com.petrolink.mbe.util.UUIDHelper;

import freemarker.cache.TemplateLoader;

/**
 * Implementation of FreeMarker Template Loader.
 * @author Paul Solano 
 * @author Aristo
 *
 */
public class NotificationTemplateLoader implements TemplateLoader {
	private NotificationTemplateDAO dao = null;
	
	/**
	 * Constructor based on existing Template DAO
	 * @param newDao
	 */
	public NotificationTemplateLoader(NotificationTemplateDAO newDao) {
		dao =  newDao;
	}
	
    /**
     * Puts a template into the loader. 
     * @param uuid the name of the template.
     * @param template the source of the template.
     */
    public void putTemplate(UUID uuid, NotificationTemplate template) {
    	dao.createOrUpdate(uuid,template);
    }

    
    public void closeTemplateSource(Object templateSource) {
    }
    
    /**
     * Expects a UUID.tag format. The UUID will identify the Notification Template and the tab
     * will identify the field or template string within the notification template. Eg.
     * 81549d90-7a99-11e6-bdf4-0800200c9a66.body
     */
    public Object findTemplateSource(String name) {
    	NotificationTemplate template = null;
    	String[] parts = name.split("\\.");
    	if (parts.length == 2 ) {
    		String[] nameParts = parts[0].split("_");
    		if (nameParts.length > 0) {
	    		String actualName = nameParts[0];
	    		//ignore locale in other part
	    		template = dao.retrieve(UUIDHelper.fromStringFast(actualName));
    		}
    	}
    	
        if (template != null) {
        	return template.getTemplateSource(parts[1]);
        } else {
        	return null;
        }
    }
    
    public long getLastModified(Object templateSource) {
        return ((TemplateSource) templateSource).getLastModified();
    }
    
    public Reader getReader(Object templateSource, String encoding) {
        return ((TemplateSource) templateSource).getReader();
    }
}
