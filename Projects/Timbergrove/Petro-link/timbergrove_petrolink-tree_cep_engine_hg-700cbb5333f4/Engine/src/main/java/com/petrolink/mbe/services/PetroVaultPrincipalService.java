package com.petrolink.mbe.services;

import java.io.IOException;
import java.util.ArrayList;
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

import javax.mail.internet.InternetAddress;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.petrolink.mbe.pvclient.SecurityApiClient;
import com.petrolink.mbe.setting.CacheOption;
import com.petrolink.mbe.setting.HttpClientConnectionSettings;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.petrolink.mbe.util.CacheBuilderHelper;
import com.petrolink.mbe.util.DelayableCacheLoader;
import com.petrolink.mbe.util.NamedThreadFactory;
import com.petrolink.mbe.util.PrincipalDetailsUtil;
import com.smartnow.engine.exceptions.EngineException;

import Petrolink.SecurityApi.Principal;
import Petrolink.SecurityApi.PrincipalDetails;

/**
 * Service to query things related to Principal.
 * @author aristo
 *
 */
public class PetroVaultPrincipalService extends HttpClientService {

	private static final int DEFAULT_EXECUTOR_THREAD_NUMBER = 10;
	private static final int DEFAULT_TIMEOUT_IN_SECS = 15;
	
	private int executorThreadNumber = DEFAULT_EXECUTOR_THREAD_NUMBER;
	private CacheOption cacheOption;
	private final Logger logger = LoggerFactory.getLogger(PetroVaultPrincipalService.class);
	private SecurityApiClient defaultApiClient = null;
	private ExecutorService cacheExecutor;
	private AsyncLoadingCache<UUID, GetMembersDeepTaskResult> principalUnderCache;
	private GetPrincipalUnderLoader principalUnderLoader;
	
	private int timeoutPerRequest = DEFAULT_TIMEOUT_IN_SECS;
	
	
	
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
				
		setDefaultApiClient(new SecurityApiClient(cs.getURL().toString(), cs.getAuthentication()));
	}
	
	
	/**
	 * Get cascaded members of specified Guid recipient.
	 * (non-Javadoc)
	 * @param principalUuid Principal UUID whose members needs to be retreived.
	 * @see SecurityApiClient#getMemberDeep(String)
	 * @return null if specified principal is not exists 
	 * @throws IOException 
	 */
	public final List<PrincipalDetails> getMembersDeep(final UUID principalUuid) throws IOException  {
		
				
		CompletableFuture<GetMembersDeepTaskResult> future = principalUnderCache.get(principalUuid);
		
		try	{
			GetMembersDeepTaskResult result = future.get(getTimeoutPerRequest(), TimeUnit.SECONDS);
			return result.getPrincipalChildren();
		} catch (ExecutionException ee) {
			logger.error("Failure to getMembersDeep ", ee);
		} catch (InterruptedException | TimeoutException eite) {
			logger.error("Failure to getMembersDeep, server not responding ", eite);
		}
		return new ArrayList<PrincipalDetails>();
	}
	
	/**
	 * Get cascaded members of specified Principal UUID.
	 * @param principalIds Iterable Principal UUID whose members needs to be retrieved.
	 * @return A map which describe relationship between a single requested UUID to its member.
	 */
	public final Map<UUID, GetMembersDeepTaskResult> getMembersDeep(final Iterable<UUID> principalIds) {
		
		
		// Lookup and asynchronously compute entries that are absent
		CompletableFuture<Map<UUID, GetMembersDeepTaskResult>> cacheFuture = principalUnderCache.getAll(principalIds);
		Map<UUID, GetMembersDeepTaskResult> cacheMap = null;
		try {
			cacheMap = cacheFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Processing for getPrincipalsUnder interrupted ", e);
		}
		return cacheMap;
		
	}
	
	/**
	 * Get cascaded InternetAddress from members of specified Principal UUID.
	 * @param principalIds HashSet Principal UUID whose members needs to be retrieved.
	 * @return A map which describe relationship between a single requested UUID to its InternetAddress list.
	 */
	public final Map<String, List<InternetAddress>> getInternetAddresses(final HashSet<UUID> principalIds) {
		HashMap<String, List<InternetAddress>> result = new HashMap<String, List<InternetAddress>>();
		Map<UUID, GetMembersDeepTaskResult> principalMap = getMembersDeep(principalIds);
		
		if (principalMap != null) {
			for (Map.Entry<UUID, GetMembersDeepTaskResult> entry : principalMap.entrySet()) {
				GetMembersDeepTaskResult queryResult =	entry.getValue();
				List<InternetAddress> principalsAddresses = queryResult.getPrincipalEmails();
			    result.put(queryResult.getPrincipalUuid().toString(), principalsAddresses);
			}
		}
		return result;
	}
	
	/**
	 * Get cascaded InternetAddress from SMS address members of specified Principal UUID.
	 * @param principalIds HashSet Principal UUID whose members needs to be retrieved.
	 * @return A map which describe relationship between a single requested UUID to its SMS address InternetAddress list.
	 */
	public final Map<String, List<InternetAddress>> getSmsPhoneInternetAddresses(final HashSet<UUID> principalIds) {
		HashMap<String, List<InternetAddress>> result = new HashMap<String, List<InternetAddress>>();
		Map<UUID, GetMembersDeepTaskResult> principalMap = getMembersDeep(principalIds);
		
		if (principalMap != null) {
			for (Map.Entry<UUID, GetMembersDeepTaskResult> entry : principalMap.entrySet()) {
				GetMembersDeepTaskResult queryResult =	entry.getValue();
				List<InternetAddress> principalsAddresses = queryResult.getPrincipalSmsPhoneEmails();
			    result.put(queryResult.getPrincipalUuid().toString(), principalsAddresses);
			}
		}
		return result;
	}

	/**
	 * Get cascaded InternetAddress from Mobile address members of specified Principal UUID.
	 * Different with getSmsPhoneInternetAddresses() that it doesn't contain any direct email address
	 * @param principalIds HashSet Principal UUID whose members needs to be retrieved.
	 * @return A map which describe relationship between a single requested UUID to its SMS address InternetAddress list.
	 */
	public final Map<String, List<String>> getMobiles(final HashSet<UUID> principalIds) {
		HashMap<String, List<String>> result = new HashMap<String, List<String>>();
		Map<UUID, GetMembersDeepTaskResult> principalMap = getMembersDeep(principalIds);
		
		if (principalMap != null) {
			for (Map.Entry<UUID, GetMembersDeepTaskResult> entry : principalMap.entrySet()) {
				GetMembersDeepTaskResult queryResult =	entry.getValue();
				List<String> principalsAddresses = queryResult.getPrincipalMobileNumber();
			    result.put(queryResult.getPrincipalUuid().toString(), principalsAddresses);
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.smartnow.engine.services.Service#startService()
	 */
	@Override
	public final void startService() {
		initPrincipalChildrenCache();
		setRunning(true);
	}
	
	
	private void initPrincipalChildrenCache() {
		if (cacheExecutor != null) {
			shutDownExecutor(cacheExecutor);
			cacheExecutor = null;
		}
		cacheExecutor = Executors.newFixedThreadPool(getExecutorThreadNumber(), new NamedThreadFactory("principal-svc-cache-"));
		if (cacheOption ==  null) {
			cacheOption = CacheOption.getDefaultRecommendedSetting();
		}
		
		GetPrincipalUnderLoader loader = new GetPrincipalUnderLoader(getDefaultApiClient());
		Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder()
				.executor(cacheExecutor);
		CacheBuilderHelper.applyCacheOption(cacheOption, cacheBuilder);
		
		AsyncLoadingCache<UUID, GetMembersDeepTaskResult> cache = cacheBuilder
			    .buildAsync(loader);
		
		principalUnderLoader = loader;
		principalUnderCache = cache;	    
			    
	}
	
	/**
	 * Clear cache.
	 */
	public final void clearCache() {
		AsyncLoadingCache<UUID, GetMembersDeepTaskResult> cache = principalUnderCache;
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
	public final SecurityApiClient getDefaultApiClient() {
		return defaultApiClient;
	}

	/**
	 * @param newDefaultApiClient the defaultApiClient to set
	 */
	public final void setDefaultApiClient(final SecurityApiClient newDefaultApiClient) {
		this.defaultApiClient = newDefaultApiClient;
		
		if (newDefaultApiClient != null) {
			logger.info("Principal Service API set to {} with {}", defaultApiClient.getBaseUri(), defaultApiClient.getAuthentication().toString());
		} else {
			logger.info("Principal Service API set to NULL");
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
		GetPrincipalUnderLoader loader = principalUnderLoader;
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
		GetPrincipalUnderLoader loader = principalUnderLoader;
		if (loader != null) {
			loader.setDebugDelayMilis(aDebugDelayMilis);
		}
	}
	
	/**
	 * Class for Cache loader in GetPrincipalUnder.
	 * Much neater/controllable than using lambda.
	 * @author aristo
	 *
	 */
	class GetPrincipalUnderLoader extends DelayableCacheLoader implements CacheLoader<UUID, GetMembersDeepTaskResult> {
		private SecurityApiClient client;
		
		/**
		 * Constructor.
		 * @param apiClient the SecurityApiClient to call to service
		 */
		GetPrincipalUnderLoader(final SecurityApiClient apiClient) {
			client =  apiClient;
		}
		
		@Override
		public GetMembersDeepTaskResult load(final UUID newPrincipalUuid) throws Exception {
			if (getDebugDelayMilis() > 0) {
				Thread.sleep(getDebugDelayMilis());
			}
			return new GetMembersDeepTask(client, newPrincipalUuid).call();
		}

	}
	
	/**
	 * The main Task logic for GetMembersDeep.
	 * @author aristo
	 *
	 */
	class GetMembersDeepTask implements Callable<GetMembersDeepTaskResult> {
		private SecurityApiClient client;
		private UUID principalUuid;
		
		/**
		 * Constructor
		 * @param apiClient
		 * @param newPrincipalUuid
		 */
		GetMembersDeepTask(final SecurityApiClient apiClient, final UUID newPrincipalUuid) {
			client =  apiClient;
			principalUuid = newPrincipalUuid;
		}
		
		@Override
		public GetMembersDeepTaskResult call() throws IOException {
			
			GetMembersDeepTaskResult result = new GetMembersDeepTaskResult(principalUuid);
			String principalId = principalUuid.toString();
			
			//Corrected API
			List<PrincipalDetails> resultList = client.getMemberDeep(principalId);
			
			//OLD Fails API
//			PrincipalDetails prin = client.getPrincipalDetail(principalId);
//			List<Principal> resultList = null;
//			
//			if (prin != null) {
//				if (prin.isGroup()) {
//					resultList = client.getMemberDeep(principalId);
//				} else {
//					Principal dummyPrincipal = new Principal();
//					dummyPrincipal.setId(prin.getId().toString());
//					dummyPrincipal.setPrincipalType(prin.getType());
//					dummyPrincipal.setName(prin.getName());
//					
//					resultList = new ArrayList<Principal>();
//					resultList.add(dummyPrincipal);
//				}
//			}
			
			//Set result
			result.setPrincipalChildren(resultList);
			return result;
		}
		
	}
	
	/**
	 * Result holder for  GetMembersDeepTask.
	 * @author aristo
	 *
	 */
	class GetMembersDeepTaskResult {
		private UUID principalUuid; 
		private List<PrincipalDetails> principalChildren;
		
		/**
		 * Constructor.
		 * @param aPrincipalUuid
		 */
		public GetMembersDeepTaskResult(final UUID aPrincipalUuid) {
			this.principalUuid = aPrincipalUuid;
		}
		/**
		 * @return the principalId
		 */
		public final UUID getPrincipalUuid() {
			return principalUuid;
		}
	
		/**
		 * @return the result
		 */
		public final List<PrincipalDetails> getPrincipalChildren() {
			return principalChildren;
		}
		/**
		 * @param result the result to set
		 */
		public final void setPrincipalChildren(final List<PrincipalDetails> result) {
			this.principalChildren = result;
		}
		
		/**
		 * Get emails contained in all children.
		 * @return emails contained in all children.
		 */
		public final List<InternetAddress> getPrincipalEmails() {
			return PrincipalDetailsUtil.getAllEmailAddress(principalChildren);
		}
		
		/**
		 * Get Sms phone emails contained in all children.
		 * @return Sms phone emails contained in all children.
		 */
		public final List<InternetAddress> getPrincipalSmsPhoneEmails() {
			return PrincipalDetailsUtil.getAllSmsPhoneEmailAddress(principalChildren);
		}
		
		/**
		 * Get Mobile Number contained in all children.
		 * @return Mobile Number contained in all children.
		 */
		public final List<String> getPrincipalMobileNumber() {
			return PrincipalDetailsUtil.getAllMobileNumber(principalChildren);
		}
		
		/**
		 * Get all principal which are users.
		 * @return all principal which are users.
		 */
		public final List<PrincipalDetails> getPrincipalWithTypeUser() {
			return PrincipalDetailsUtil.getPrincipalWithType(principalChildren, Principal.PRINCIPAL_TYPE_USER);
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
	
	
}
