package com.petrolink.mbe.actions;

import com.petrolink.mbe.services.PetroVaultPrincipalService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.templates.NotificationTemplateService;

/**
 * Abstract Notification Action to provide common methods for all Notifications
 * @author paul
 *
 */
public abstract class NotificationAction extends MBEAction {
	private NotificationTemplateService notificationTemplateService;
	private PetroVaultPrincipalService principalService;

	/**
	 * Get template service which will be used to retrieve template, store template from current setting, and to process with freemarker.
	 * @return
	 */
	protected NotificationTemplateService getNotificationTemplateService() {
		if (notificationTemplateService == null) {
			notificationTemplateService = ServiceAccessor.getNotificationTemplateService();
		}
		return notificationTemplateService;
	}
	
	/**
	 * Directly sets the Notification Template Service
	 * @param notificationTemplateService 
	 */
	public void setNotificationTemplateService(NotificationTemplateService notificationTemplateService) {
		this.notificationTemplateService = notificationTemplateService;
	}	

	/**
	 * Get principal service which main use case is to resolve from guid to email. 
	 * @return
	 */
	protected PetroVaultPrincipalService getPrincipalService() {
		if (principalService == null) {
			principalService = ServiceAccessor.getPVPrincipalService();
		}
		return principalService;
	}
}
