package com.petrolink.mbe.templates;

import java.util.Map;
import java.util.UUID;

import org.jdom2.Element;

/**
 * Notification Template DAO interface
 * @author paul
 *
 */
public interface NotificationTemplateDAO {

	/**
	 * @param template
	 * @return the UUID the data will be stored, or Empty when can't store
	 */
	UUID createOrUpdate(NotificationTemplate template);

	/**
	 * 
	 * @param uuid
	 * @param template
	 * @return the UUID the data will be stored, or Empty when can't store
	 */
	UUID createOrUpdate(UUID uuid, NotificationTemplate template);

	/**
	 * 
	 * @param uuid
	 * @return the Notification template for a given UUID
	 */
	NotificationTemplate retrieve(UUID uuid);

	/**
	 * 
	 * @return the Map of all existing Notification Templates
	 */
	Map<UUID, NotificationTemplate> retrieveAll();

	/**
	 * 
	 * @param uuidToDelete
	 * @return the deleted Notification Template with the given UUID
	 */
	NotificationTemplate delete(UUID uuidToDelete);

	/**
	 * Stores the template to the underlying persistent storage
	 * @param uuid 
	 * @param template
	 * @throws Exception 
	 */
	public void storeTemplate(UUID uuid, Element template) throws Exception;
}