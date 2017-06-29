package com.petrolink.mbe.services;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.petrolink.mbe.pvclient.ResourceApiClient;
import com.petrolink.mbe.setting.CacheOption;
import com.petrolink.mbe.setting.HttpClientConnectionSettings;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.petrolink.mbe.util.CacheBuilderHelper;
import com.petrolink.mbe.util.DelayableCacheLoader;
import com.petrolink.mbe.util.NamedThreadFactory;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.engine.exceptions.EngineException;

import Petrolink.ResourceApi.Resource;

/**
 * Service to query things related to PV Resource.
 * @author aristo
 *
 */
public class PetroVaultResourceService extends HttpClientService {
	private static final int DEFAULT_EXECUTOR_THREAD_NUMBER = 10;
	private static final int DEFAULT_TIMEOUT_IN_SECS = 15;
	
	private int executorThreadNumber = DEFAULT_EXECUTOR_THREAD_NUMBER;
	private CacheOption cacheOption;
	private final Logger logger = LoggerFactory.getLogger(PetroVaultResourceService.class);
	private ResourceApiClient defaultApiClient = null;
	private ExecutorService cacheExecutor;
	private AsyncLoadingCache<String, GetResourceByUriResult> resourceByUriCache;
	private GetResourceByUriLoader resourceByUriCacheLoader;
	
	private int timeoutPerRequest = DEFAULT_TIMEOUT_IN_SECS;
	
	
	/**
	 * Get resource data from specified URI.
	 * @param uri Petrolink URI like structure for the resource
	 * @return Resource contained under the URI
	 */
	public final Resource getResourceByUri(final String uri) {
		CompletableFuture<GetResourceByUriResult> future = resourceByUriCache.get(uri);
		try	{
			GetResourceByUriResult result = future.get(getTimeoutPerRequest(), TimeUnit.SECONDS);
			//resourceByUriCache.synchronous()
			if (result != null) {
				return result.getResource();
			}
		} catch (ExecutionException ee) {
			logger.error("Failure to getPrincipalUnder ", ee);
		} catch (InterruptedException | TimeoutException eite) {
			logger.error("Failure to getPrincipalUnder, server not responding ", eite);
		}
		return null;
	}
	
	/**
	 * Get Resource UUID from specified URI.
	 * @param uri
	 * @return UUID of the resource or UUIDHelper.EMPTY if not available
	 */
	public final UUID getResourceIdByUri(final String uri) {
		Resource res = getResourceByUri(uri);
		if (res != null && res.getMetadata() != null) {
			String resId = res.getMetadata().getId();
			if (StringUtils.isNotBlank(resId)) {
				return UUIDHelper.fromStringFast(resId, true);
			}
		}
		return UUIDHelper.EMPTY;
	}
	
//	/**
//	 * Eg fd12ed7c-8ace-4891-8814-f5e729aab501
//	 * @return
//	 */
//	public final String getWitsmlWellById(final UUID id){
//		
//	}
	
	/* (non-Javadoc)
	 * @see com.smartnow.engine.services.Service#load(org.jdom2.Element)
	 */
	@Override
	public final void load(final Element e) throws EngineException {
		super.load(e);
		
		HttpClientConnectionSettings cs = loadConnectionSettings(e.getChild("Connection", e.getNamespace()));
		
		Element cacheElement = e.getChild("CacheOption", e.getNamespace());
		if (cacheElement != null) {
			cacheOption = XmlSettingParser.parseCacheOption(cacheElement);
		} else {
			cacheOption = CacheOption.getDefaultRecommendedSetting(); //Default
		}
		
		setDefaultApiClient(new ResourceApiClient(cs.getURL().toString(), cs.getAuthentication()));
	}
	/* (non-Javadoc)
	 * @see com.smartnow.engine.services.Service#startService()
	 */
	@Override
	public final void startService() {
		if (cacheExecutor != null) {
			shutDownExecutor(cacheExecutor);
			cacheExecutor = null;
		}
		
		cacheExecutor = Executors.newFixedThreadPool(getExecutorThreadNumber(), new NamedThreadFactory("resource-svc-cache-"));
		
		initResourceByUriCache();
		setRunning(true);
	}
	
	private void initResourceByUriCache() {
		if (cacheOption ==  null) {
			cacheOption = CacheOption.getDefaultRecommendedSetting();
		}
		
		GetResourceByUriLoader loader = new GetResourceByUriLoader(getDefaultApiClient());
		Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
				.executor(cacheExecutor);
		CacheBuilderHelper.applyCacheOption(cacheOption, cacheBuilder);
		
		AsyncLoadingCache<String, GetResourceByUriResult> cache = cacheBuilder
				.buildAsync(loader);
			    
		resourceByUriCacheLoader = loader;
		resourceByUriCache = cache;	    
			    
	}
	
	/**
	 * Clear cache.
	 */
	public final void clearCache() {
		AsyncLoadingCache<String, GetResourceByUriResult> cache = resourceByUriCache;
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
	public final ResourceApiClient getDefaultApiClient() {
		return defaultApiClient;
	}

	/**
	 * @param newDefaultApiClient the defaultApiClient to set
	 */
	public final void setDefaultApiClient(final ResourceApiClient newDefaultApiClient) {
		this.defaultApiClient = newDefaultApiClient;
		
		if (newDefaultApiClient != null) {
			logger.info("Resource API set to {} with {}", defaultApiClient.getBaseUri(), defaultApiClient.getAuthentication().toString());
		} else {
			logger.info("Resource API set to NULL");
		}
	}

	/**
	 * @return the executorThreadNumber
	 */
	public final int getExecutorThreadNumber() {
		return executorThreadNumber;
	}

	/**
	 * @param threadNumber the executorThreadNumber to set
	 */
	public final void setExecutorThreadNumber(final int threadNumber) {
		if (executorThreadNumber > 0) {
			this.executorThreadNumber = threadNumber;
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
		GetResourceByUriLoader loader = resourceByUriCacheLoader;
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
		GetResourceByUriLoader loader = resourceByUriCacheLoader;
		if (loader != null) {
			loader.setDebugDelayMilis(aDebugDelayMilis);
		}
	}
	
	/**
	 * Class for Cache loader in GetUserNotificationChannel.
	 * Much neater/controllable than using lambda.
	 * @author aristo
	 *
	 */
	class GetResourceByUriLoader extends DelayableCacheLoader implements CacheLoader<String, GetResourceByUriResult> {
		private ResourceApiClient client;
		
		/**
		 * Constructor.
		 * @param apiClient the SecurityApiClient to call to service
		 */
		GetResourceByUriLoader(final ResourceApiClient apiClient) {
			client =  apiClient;
		}
		
		@Override
		public GetResourceByUriResult load(final String resourceUri) throws Exception {
			if (getDebugDelayMilis() > 0) {
				Thread.sleep(getDebugDelayMilis());
			}
			return new GetResourceByUriTask(client, resourceUri).call();
		}
	}
	
	class GetResourceByUriTask implements Callable<GetResourceByUriResult> {

		private ResourceApiClient client;
		private String uri;
		
		GetResourceByUriTask(final ResourceApiClient apiClient, final String resourceUri) {
			client =  apiClient;
			uri = resourceUri;
		}
		
		@Override
		public GetResourceByUriResult call() throws Exception {
			Resource resource = client.getResourceByUri(uri);
			
			if (resource != null) {
				GetResourceByUriResult result = new GetResourceByUriResult(resource);
				result.setRequestUri(uri);
				return result;
			}
			return null;
		}
		
	}
	
	class GetResourceByUriResult {
		private String requestUri;
		private Resource resource;
		public GetResourceByUriResult(Resource requestedResource) {
			resource = requestedResource;
		}
		
		public UUID getChannelId() {
			return UUIDHelper.fromStringFast(resource.getMetadata().getId());
		}

		/**
		 * @return the channelResource
		 */
		public final Resource getResource() {
			return resource;
		}

		/**
		 * @return the requestUri
		 */
		public final String getRequestUri() {
			return requestUri;
		}

		/**
		 * @param request the requestUri to set
		 */
		public final void setRequestUri(final String request) {
			this.requestUri = request;
		}
		
		
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
