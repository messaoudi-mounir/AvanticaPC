package com.petrolink.mbe.services;

import java.util.Objects;

import com.petrolink.mbe.alertstatus.impl.AlertsService;
import com.petrolink.mbe.directories.AlertProcessorDirectory;
import com.petrolink.mbe.directories.WellDirectory;
import com.petrolink.mbe.templates.NotificationTemplateService;
import com.smartnow.engine.scheduler.service.SchedulerService;

/**
 * Provides strongly typed access to engine services, and allows unit tests to intercept service access.
 * @author langj
 *
 */
public final class ServiceAccessor {
	private static final NullServiceAdapter NULL_ADAPTER = new NullServiceAdapter();
	private static ServiceAdapter adapter = NULL_ADAPTER;
	
	private ServiceAccessor() {}
	
	/**
	 * Initialize with an adapter.
	 * This should be used on engine startup or at the beginning of a unit test.
	 * @param newAdapter
	 */
	public static void initialize(ServiceAdapter newAdapter) {
		if (adapter != NULL_ADAPTER)
			throw new IllegalStateException("already initialized");
		adapter = Objects.requireNonNull(newAdapter);
	}
	
	/**
	 * @return the engine service
	 */
	public static final EngineService getEngineService() {
		return (EngineService) adapter.getService(EngineService.class);
	}
	
	/**
	 * @return the alerts service
	 */
	public static final AlertsService getAlertsService() {
		return (AlertsService) adapter.getService(AlertsService.class);
	}
	
	/**
	 * @return the well directory service
	 */
	public static final WellDirectory getWellDirectory() {
		return (WellDirectory) adapter.getService(WellDirectory.class);
	}
	
	/**
	 * @return the well directory service
	 */
	public static final AlertProcessorDirectory getAlertProcessorDirectory() {
		return (AlertProcessorDirectory) adapter.getService(AlertProcessorDirectory.class);
	}
	
	/**
	 * @return the PV MBE service
	 */
	public static final RabbitMQChannelService getAmqpChannelService() {
		return (RabbitMQChannelService) adapter.getService(RabbitMQChannelService.class);
	}
	
	/**
	 * @return the PV MBE service
	 */
	public static final PetroVaultMbeOrchestrationService getPVMBEService() {
		return (PetroVaultMbeOrchestrationService) adapter.getService(PetroVaultMbeOrchestrationService.class);
	}
	
	/**
	 * @return the PV principal service
	 */
	public static final PetroVaultPrincipalService getPVPrincipalService() {
		return (PetroVaultPrincipalService) adapter.getService(PetroVaultPrincipalService.class);
	}
	
	/**
	 * @return the PV resource service
	 */
	public static final PetroVaultResourceService getPVResourceService() {
		return (PetroVaultResourceService) adapter.getService(PetroVaultResourceService.class);
	}
	
	/**
	 * @return the chronos client service
	 */
	public static final ChronosClientService getChronosClientService() {
		return (ChronosClientService) adapter.getService(ChronosClientService.class);
	}
	
	/**
	 * @return the scheduler service
	 */
	public static final SchedulerService getSchedulerService() {
		return (SchedulerService) adapter.getService(SchedulerService.class);
	}
	
	/**
	 * @return the notification template service
	 */
	public static final NotificationTemplateService getNotificationTemplateService() {
		return (NotificationTemplateService) adapter.getService(NotificationTemplateService.class);
	}
	
	/**
	 * @return the property store service
	 */
	public static final PropertyStoreService getPropertyStoreService() {
		return (PropertyStoreService) adapter.getService(PropertyStoreService.class);
	}
	
	/**
	 * Default adapter that returns null
	 */
	public static final class NullServiceAdapter extends ServiceAdapter {
		@Override
		public Object getService(Class<?> cls) {
			return null;
		}

		@Override
		public Object getService(String name) {
			return null;
		}
	}
}
