package com.petrolink.mbe.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.petrolink.mbe.engine.CommonConfigForTests;
import com.petrolink.mbe.parser.ResourceDataParser;
import com.petrolink.mbe.pvclient.ResourceApiClient;
import com.petrolink.mbe.setting.HttpAuthentication;

import Petrolink.ResourceApi.Resource;

/**
 * Test for PetroVaultResourceService.
 * @author aristo
 *
 */
@SuppressWarnings("javadoc")
public class IntegrationTestPetroVaultResourceService {
	private PetroVaultResourceService service;
	
	public IntegrationTestPetroVaultResourceService() {
		HttpAuthentication auth = CommonConfigForTests.getBasicAuth();
		String baseUri = CommonConfigForTests.getCommonPetroVaultUri("/HDAPI/");
		ResourceApiClient c = new ResourceApiClient(baseUri, auth);
		
		
		PetroVaultResourceService newService = new PetroVaultResourceService();
		newService.setDefaultApiClient(c);
		newService.startService();
		
		service = newService;
	}
	
	@Before
	public void setUp()  throws Exception {
		service.clearCache();
	}
	
	@After
	public void tearDown()  throws Exception {
		
	}

	@AfterClass
	public static void cleanupTests() throws Exception {
// JUnit doesn't support proper multiple cleanup after all test		
//		PetroVaultResourceService currentService = service;
//		service = null;
//		currentService.stopService();
	}

	private Resource ensureAvailabilityOfWitsml() {
		String resourceUri = "./witsml";
		Resource res = service.getResourceByUri(resourceUri);
		assertNotNull(res);
		assertEquals(resourceUri, res.getMetadata().getUri());
		return res;
	}

	@Test
	public void ensureAvailabilityOfResources(){
		Resource res = ensureAvailabilityOfWitsml();
		System.out.println("Resource available " + res.getMetadata().getUri());
		
	}
	
	@Test
	public void ensureAvailabilityOfStandardResource() {
		String resourceUri = "./witsml/well(mbe)";
		Resource res = service.getResourceByUri(resourceUri);
		assertNotNull(" Resource " + resourceUri + " should not be null",res);
		assertNotNull(res.getData());
		System.out.println(res.getData().getContentEncoding());
		System.out.println(res.getData().getContent());
		System.out.println(ResourceDataParser.getResourceDataContent(res.getData()));
		assertEquals(resourceUri, res.getMetadata().getUri());
		
	}
	
	
	/**
	 * Make sure cache delay is in production mode by default.
	 * @throws IOException
	 */
	@Test
	public void ensureCacheInProduction() throws IOException {
		//Should be long
		long timeA = System.currentTimeMillis();
		ensureAvailabilityOfWitsml();
		long timeB = System.currentTimeMillis();
		
		//should be short, even when repeated 100 times
		int repeat = 100;
		for (int i = 0; i < repeat; i++) {
			ensureAvailabilityOfWitsml();
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
		ensureAvailabilityOfWitsml(); 
	}
}
