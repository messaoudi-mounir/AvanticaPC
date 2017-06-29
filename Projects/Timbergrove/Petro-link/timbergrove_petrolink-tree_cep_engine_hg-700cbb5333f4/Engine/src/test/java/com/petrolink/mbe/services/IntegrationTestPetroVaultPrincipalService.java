package com.petrolink.mbe.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.InternetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.petrolink.mbe.engine.CommonConfigForTests;
import com.petrolink.mbe.pvclient.SecurityApiClient;
import com.petrolink.mbe.setting.HttpAuthentication;
import com.petrolink.mbe.util.UUIDHelper;

import Petrolink.SecurityApi.PrincipalDetails;

/**
 * Test for PetroVaultPrincipalService 
 * @author aristo
 *
 */
@SuppressWarnings("javadoc")
public class IntegrationTestPetroVaultPrincipalService {
	public final static String GUID_USR_Administrator = "c76499fa-eb7d-4c68-8557-641b28cbca1e";
	public final static String GUID_GRP_Replicators = "3cba39c3-277d-4299-9d82-fc1d02dd3ebc";
	public final static String GUID_GRP_ServerAdministrators= "72ef7cda-11ed-4f3b-ae1d-b02356292124";
	
	PetroVaultPrincipalService service;
	
	public IntegrationTestPetroVaultPrincipalService() throws Exception {
		HttpAuthentication auth = CommonConfigForTests.getSamlTokenAuth();
		String baseUri = CommonConfigForTests.getCommonPetroVaultUri("/SecurityApi/");
		SecurityApiClient c = new SecurityApiClient(baseUri, auth);
		
		PetroVaultPrincipalService newService = new PetroVaultPrincipalService();
		newService.setDefaultApiClient(c);
		newService.startService();
		
		service = newService;
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		service.clearCache();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
// JUnit doesn't support proper multiple cleanup after all test		
//		PetroVaultPrincipalService currentService = service;
//		service = null;
//		currentService.stopService();
	}

	@Test
	public void ensureHandleNonAvailableUser()throws IOException {
		UUID id = UUIDHelper.fromStringFast("7520e7ce-9572-11e6-ae22-56b6b6499611");
		List<PrincipalDetails> result = service.getMembersDeep(id);
		assertNotNull("Result should not be null", result);
		assertTrue("Result should be empty", result.size() == 0);
	}
	
	
	
	@Test
	public void ensureAvailabilityServerAdministrators() throws IOException {
		UUID id = UUIDHelper.fromStringFast(GUID_GRP_ServerAdministrators);
		List<PrincipalDetails> result = service.getMembersDeep(id);
		assertNotNull("Result should not be null", result);
		assertFalse("Result should not be null or empty", result.isEmpty());
		System.out.print("ensureAvailabilityServerAdministrators:");
		System.out.println(result);
	}

	@Test
	public void ensureAvailabilityMultiGroup() throws IOException {
		HashSet<UUID> groupGuids = new HashSet<UUID>();
		groupGuids.add(UUIDHelper.fromStringFast(GUID_USR_Administrator)); //Administrator
		groupGuids.add(UUIDHelper.fromStringFast(GUID_GRP_ServerAdministrators)); //ServerAdministrators
		groupGuids.add(UUIDHelper.fromStringFast(GUID_GRP_Replicators)); //Replicators
		Map<String, List<InternetAddress>> result = service.getInternetAddresses(groupGuids);
		assertNotNull("Result should not be null", result);
		assertFalse("Result should not be null or empty", result.isEmpty());
		System.out.print("ensureAvailabilityMultiGroup:");
		System.out.println(result);
		System.out.print("ensureAvailabilityMultiGroupSMS:");
		Map<String, List<InternetAddress>> resultSmsPhone = service.getSmsPhoneInternetAddresses(groupGuids);
		System.out.println(resultSmsPhone);
	}
	
	/**
	 * Make sure cache delay is in production mode by default.
	 * @throws IOException
	 */
	@Test
	public void ensureCacheInProduction() throws IOException {
		//Should be long
		long timeA = System.currentTimeMillis();
		ensureAvailabilityMultiGroup();
		long timeB = System.currentTimeMillis();
		
		//should be short, even when repeated 100 times
		int repeat = 100;
		for (int i = 0; i < repeat; i++) {
			ensureAvailabilityMultiGroup();
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
	
	/**
	 * Testing cache when request is delayed. Useful for testing cache is working
	 * @throws IOException
	 */
	@Test
	public void ensureCacheIsWorking() throws IOException {
		//Debug mode
		service.setDebugDelayMilis(5000);
		ensureCacheInProduction(); 
	}
}
