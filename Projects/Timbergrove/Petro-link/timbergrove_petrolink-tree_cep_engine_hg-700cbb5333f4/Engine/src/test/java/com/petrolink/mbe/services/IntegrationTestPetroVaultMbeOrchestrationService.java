package com.petrolink.mbe.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.petrolink.mbe.engine.CommonConfigForTests;
import com.petrolink.mbe.pvclient.MbeOrchestrationApiClient;
import com.petrolink.mbe.setting.HttpAuthentication;
import com.petrolink.mbe.util.UUIDHelper;

@SuppressWarnings("javadoc")
public class IntegrationTestPetroVaultMbeOrchestrationService {
	PetroVaultMbeOrchestrationService service;
	private UUID instanceId;
	
	public IntegrationTestPetroVaultMbeOrchestrationService() {
		
		HttpAuthentication auth = CommonConfigForTests.getBasicAuth();
		String baseUri = CommonConfigForTests.getCommonPetroVaultUri("/mbe/");
		MbeOrchestrationApiClient c = new MbeOrchestrationApiClient(baseUri, auth);
		
		instanceId = UUID.randomUUID();
		
		PetroVaultMbeOrchestrationService newService = new PetroVaultMbeOrchestrationService();
		newService.setDefaultApiClient(c);
		newService.startService();
		
		service = newService;
	}
	
	@Before
	public void setUp() throws Exception {
		service.clearCache();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void ensureNotificationChannels() {
		List<UUID> principalIds = new ArrayList<UUID>();
//		principalIds.add(UUIDHelper.fromStringFast("c76499fa-eb7d-4c68-8557-641b28cbca1e"));
//		principalIds.add(UUIDHelper.fromStringFast("d76499fa-eb7d-4c68-8557-641b28cbca1e"));
//		principalIds.add(UUIDHelper.fromStringFast("e76499fa-eb7d-4c68-8557-641b28cbca1e"));
		principalIds.add(UUIDHelper.fromStringFast("4995f1c2-2f88-40af-a1ef-55e86ea53197")); //administrator user
		principalIds.add(UUIDHelper.fromStringFast("72ef7cda-11ed-4f3b-ae1d-b02356292124")); //ServerAdministrators group
		HashSet<UUID> result = service.getNotificationChannelIds(instanceId, principalIds);
		
		assertNotNull("Result should not be null", result);
		assertFalse("Result should not be null or empty", result.isEmpty());
		System.out.print("ensureNotificationChannelsRequest:");
		System.out.println(principalIds);
		System.out.print("ensureNotificationChannelsResponse:");
		System.out.println(result);
	}

	/**
	 * Make sure cache delay is in production mode by default.
	 * @throws IOException
	 */
	@Test
	public void ensureCacheInProduction() throws IOException {
		//Should be long
		long timeA = System.currentTimeMillis();
		ensureNotificationChannels();
		long timeB = System.currentTimeMillis();
		
		//should be short, even when repeated 100 times
		int repeat = 100;
		for (int i = 0; i < repeat; i++) {
			ensureNotificationChannels();
		}
		
		long timeC = System.currentTimeMillis();
		
		long noncachedTime = timeB - timeA;
		long cachedTime = timeC - timeB;
		System.out.print("Non Cached Load(ms)=");
		System.out.println(noncachedTime);
		System.out.print("Cached Load(ms) " + repeat + "times =");
		System.out.println(cachedTime);
		assertTrue("cachedTime is low", cachedTime < 100);
	}
}
