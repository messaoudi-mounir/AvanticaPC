package com.petrolink.mbe.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.petrolink.mbe.pvclient.MbeOrchestrationApiClient;
import com.petrolink.mbe.pvclient.MbeOrchestrationApiClient.RigStateInfo;
import com.petrolink.mbe.setting.CacheOption;
import com.petrolink.mbe.setting.HttpClientConnectionSettings;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.petrolink.mbe.util.CacheBuilderHelper;
import com.petrolink.mbe.util.DelayableCacheLoader;
import com.petrolink.mbe.util.NamedThreadFactory;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.engine.exceptions.EngineException;

import Petrolink.MbeOrchestrationApi.NotificationChannelResponse;

/**
 * A service to handle MBE Orchestration.
 * @author aristo
 *
 */
public class PetroVaultMbeOrchestrationService extends HttpClientService {
	private static final int DEFAULT_EXECUTOR_THREAD_NUMBER = 10;
	private static final int DEFAULT_TIMEOUT_IN_SECS = 15;
	
	private int executorThreadNumber = DEFAULT_EXECUTOR_THREAD_NUMBER;
	private CacheOption cacheOption;
	private final Logger logger = LoggerFactory.getLogger(PetroVaultMbeOrchestrationService.class);
	
	private MbeOrchestrationApiClient defaultApiClient = null;
	private ExecutorService cacheExecutor;
	private AsyncLoadingCache<UUID, GetNotificationChannelResult> notificationChannelCache;
	private GetNotificationChannelLoader notificationChannelLoader;
	private AsyncLoadingCache<UUID, Map<String, String>> wellMetadataCache;
	private AsyncLoadingCache<UUID, Map<String, String>> wellboreMetadataCache;
	private AsyncLoadingCache<UUID, List<RigStateInfo>> rigStateCache;
	
	private int timeoutPerRequest = DEFAULT_TIMEOUT_IN_SECS;
	
	/* (non-Javadoc)
	 * @see com.smartnow.engine.services.Service#load(org.jdom2.Element)
	 */
	@Override
	public final void load(final Element e) throws EngineException {
		super.load(e);
		
		HttpClientConnectionSettings hccs = loadConnectionSettings(e.getChild("Connection", e.getNamespace()));
		
		Element cacheElement = e.getChild("CacheOption", e.getNamespace());
		if (cacheElement != null) {
			cacheOption = XmlSettingParser.parseCacheOption(cacheElement);
		} else {
			cacheOption = CacheOption.getDefaultRecommendedSetting(); //Default
		}
		
		setDefaultApiClient(new MbeOrchestrationApiClient(hccs.getURL().toString(), hccs.getAuthentication()));
	}
	
	/* (non-Javadoc)
	 * @see com.smartnow.engine.services.Service#startService()
	 */
	@Override
	public final void startService() {
		initCache();
		setRunning(true);
	}
	
	private void initCache() {
		if (cacheExecutor != null) {
			shutDownExecutor(cacheExecutor);
			cacheExecutor = null;
		}
		cacheExecutor = Executors.newFixedThreadPool(getExecutorThreadNumber(), new NamedThreadFactory("mbe-svc-cache-"));
		if (cacheOption ==  null) {
			cacheOption = CacheOption.getDefaultRecommendedSetting();
		}
		
		GetNotificationChannelLoader loader = new GetNotificationChannelLoader(getDefaultApiClient());
		Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
				.executor(cacheExecutor);
		CacheBuilderHelper.applyCacheOption(cacheOption, cacheBuilder);
		
		AsyncLoadingCache<UUID, GetNotificationChannelResult> cache = cacheBuilder
			    .buildAsync(loader);
		
		notificationChannelLoader = loader;
		notificationChannelCache = cache;	    
		
		rigStateCache = cacheBuilder.buildAsync(k -> defaultApiClient.getRigStateDictionary(k));
		wellMetadataCache = cacheBuilder.buildAsync(k -> defaultApiClient.getWellMetadata(k));
		wellboreMetadataCache = cacheBuilder.buildAsync(k -> defaultApiClient.getWellboreMetadata(k));
	}
	
	/**
	 * Clear cache.
	 */
	public final void clearCache() {
		AsyncLoadingCache<UUID, GetNotificationChannelResult> cache = notificationChannelCache;
		cache.synchronous().invalidateAll();
	}
	
	/* (non-Javadoc)
	 * @see com.smartnow.engine.services.Service#stopService()
	 */
	@Override
	public final void stopService() {
		if (cacheExecutor != null) {
			shutDownExecutor(cacheExecutor);
			cacheExecutor = null;
		}
		setRunning(false);
	}

	/**
	 * @return the timeout
	 */
	public final int getTimeoutPerRequest() {
		return timeoutPerRequest;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public final void setTimeoutPerRequest(final int timeout) {
		this.timeoutPerRequest = timeout;
	}
	
	/**
	 * @return the defaultApiClient
	 */
	public final MbeOrchestrationApiClient getDefaultApiClient() {
		return defaultApiClient;
	}

	/**
	 * @param newDefaultApiClient the defaultApiClient to set
	 */
	public final void setDefaultApiClient(final MbeOrchestrationApiClient newDefaultApiClient) {
		this.defaultApiClient = newDefaultApiClient;
		
		if (newDefaultApiClient != null) {
			logger.info("MbeOrchestration Service API set to {} with {}", defaultApiClient.getBaseUri(), defaultApiClient.getAuthentication().toString());
		} else {
			logger.info("MbeOrchestration Service API set to NULL");
		}
	}
	
	

	/**
	 * @return the executorThreadNumber
	 */
	public final int getExecutorThreadNumber() {
		return executorThreadNumber;
	}

	/**
	 * @param executorThreadNumber the executorThreadNumber to set
	 */
	public final void setExecutorThreadNumber(final int executorThreadNumber) {
		if (executorThreadNumber > 0) {
			this.executorThreadNumber = executorThreadNumber;
		}
	}
	
	private void shutDownExecutor(final ExecutorService anExecutor) {
		try {
			logger.info("Shutting down executor");
		    anExecutor.shutdown();
		    anExecutor.awaitTermination(15, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("Shutting down executor failed, interrupted", e);
		} finally {
		    if (!anExecutor.isTerminated()) {
		    	logger.info("Shutting down executor, forced. Canceling unfinished task.");
		    }
		    anExecutor.shutdownNow();
		    logger.info("Shutting down executor finished.");
		}
	}
	
	/**
	 * @return the debugDelayMilis
	 */
	public final long getDebugDelayMilis() {
		DelayableCacheLoader loader = notificationChannelLoader;
		if (loader != null) {
			return loader.getDebugDelayMilis();
		}
		return 0L;
	}

	/**
	 * For debugging when testing code. DO NOT SET on production!
	 * @param aDebugDelayMilis the debugDelayMilis to set in milisceonds. 0 Means no delay
	 */
	public final void setDebugDelayMilis(final long aDebugDelayMilis) {
		DelayableCacheLoader loader = notificationChannelLoader;
		if (loader != null) {
			loader.setDebugDelayMilis(aDebugDelayMilis);
		}
	}
	
	/**
	 * Get Notification Channel for requested principalUuids.
	 * @param actionID Action Id needing this instance, used for cache key
	 * @param principalUuids
	 * @return Notification Channels's UUID
	 */
	public HashSet<UUID> getNotificationChannelIds(final UUID actionID, final Iterable<UUID> principalUuids) {
		if (principalUuids == null) {
			return new HashSet<UUID>();
		}

		
		//Prevent duplicate and make structure which can be copied from iterable
		HashSet<UUID> requestUUIDs = new HashSet<UUID>();
		for (UUID uuid : principalUuids) {
			requestUUIDs.add(uuid);
		}
		
		if (UUIDHelper.isNullOrEmpty(actionID)) {
			//If there is no cache
			try	{
				GetNotificationChannelTask task = new GetNotificationChannelTask(getDefaultApiClient(), requestUUIDs);
				return task.call().getChannelIds();
			} catch (IOException eite) {
				logger.error("Failure to getNotificationChannelIds (NoCache), server not responding ", eite);
			}
		} else {
			GetNotificationChannelLoader loader = notificationChannelLoader;
			boolean isparameterUpdated = loader.setActionParameters(actionID, requestUUIDs); //Set loader to tell parameters needed
			if (isparameterUpdated) {
				notificationChannelCache.synchronous().invalidate(actionID);
			}
			
			CompletableFuture<GetNotificationChannelResult> future = notificationChannelCache.get(actionID);
			try	{
				GetNotificationChannelResult result = future.get(getTimeoutPerRequest(), TimeUnit.SECONDS);
				return result.getChannelIds();
			} catch (ExecutionException ee) {
				logger.error("Failure to getNotificationChannelIds ", ee);
			} catch (InterruptedException | TimeoutException eite) {
				logger.error("Failure to getNotificationChannelIds, server not responding ", eite);
			}
		}
		return new HashSet<UUID>();
	}
	
	/**
	 * Gets a future for the metadata of a well, or null if the ID does not match a well. The result will be cached.
	 * @param id A well resource ID
	 * @return A future
	 */
	public CompletableFuture<Map<String, String>> getWellMetadataAsync(UUID id) {
		return wellMetadataCache.get(id);
	}
	
	/**
	 * Gets a future for the metadata of a wellbore, or null if the ID does not match a well. The result will be cached.
	 * @param id A wellbore resource ID
	 * @return A future
	 */
	public CompletableFuture<Map<String, String>> getWellboreMetadataAsync(UUID id) {
		return wellboreMetadataCache.get(id);
	}

	/**
	 * Gets a future for the matching rig state dictionary, or null if the ID does not match. The result will be cached.
	 * @param id A rig state dictionary resource ID
	 * @return A future
	 */
	public CompletableFuture<List<RigStateInfo>> getRigStateDictionaryAsync(UUID id) {
		return rigStateCache.get(id);
	}
	
	/**
	 * Class for Cache loader in GetNotificationChannel.
	 * Much neater/controllable than using lambda.
	 * @author aristo
	 *
	 */
	class GetNotificationChannelLoader extends DelayableCacheLoader implements CacheLoader<UUID, GetNotificationChannelResult> {
		private MbeOrchestrationApiClient client;
		private HashMap<UUID, HashSet<UUID>> actionToPrincipalList;
		
		/**
		 * Constructor.
		 * @param apiClient the SecurityApiClient to call to service
		 */
		GetNotificationChannelLoader(final MbeOrchestrationApiClient apiClient) {
			client =  apiClient;
			actionToPrincipalList = new HashMap<UUID, HashSet<UUID>>();
		}
		
		/**
		 * Set Current Request for specified Action Id
		 * @param actionId
		 * @param currentRequest
		 * @return true if there is update compared to currentRequest, false otherwise
		 */
		public boolean setActionParameters(final UUID actionId, final HashSet<UUID> currentRequest) {
			HashSet<UUID> previouslyRequested = actionToPrincipalList.get(actionId);
			
			if (!currentRequest.equals(previouslyRequested)) {
				actionToPrincipalList.put(actionId, currentRequest);
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public GetNotificationChannelResult load(final UUID actionInstanceId) throws Exception {
			if (getDebugDelayMilis() > 0) {
				Thread.sleep(getDebugDelayMilis());
			}
			HashSet<UUID> principalList = actionToPrincipalList.get(actionInstanceId);
			return new GetNotificationChannelTask(client, principalList).call();
		}

	}
	
	/**
	 * The result holder for GetNotificationChannel. 
	 * @author aristo
	 */
	class GetNotificationChannelResult {
		private NotificationChannelResponse response;
		private HashSet<UUID> channelIds;
		
		/**
		 * Constructor.
		 * @param serverResponse
		 */
		public GetNotificationChannelResult(final NotificationChannelResponse serverResponse) {
			response = serverResponse;
			channelIds = new HashSet<UUID>(response.getChannels());
		}
		
		/**
		 * get list of user notification Channel Id from Requested Principals.
		 * @return List of user notification Channel Id.
		 */
		public HashSet<UUID> getChannelIds() {
			return channelIds;
		}
	}
	
	/**
	 * The main Task logic for GetNotificationChannel. 
	 * @author aristo
	 */
	class GetNotificationChannelTask implements Callable<GetNotificationChannelResult> {
		private MbeOrchestrationApiClient client;
		private HashSet<UUID> requestedPrincipals;
		
		GetNotificationChannelTask(final MbeOrchestrationApiClient apiClient, final HashSet<UUID> principals) {
			client =  apiClient;
			requestedPrincipals = principals;
		}
		
		@Override
		public GetNotificationChannelResult call() throws IOException {
			NotificationChannelResponse response =	client.getNotificationChannel(requestedPrincipals);
			return new GetNotificationChannelResult(response);
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
