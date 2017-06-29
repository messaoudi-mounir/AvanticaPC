package com.petrolink.mbe.pvclient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.petrolink.mbe.engine.CommonConfigForTests;
import com.petrolink.mbe.setting.HttpBasicAuthentication;

public class IntegrationTestChronosClient {

	ChronosClient client;
	
	@Before
	public void setUp() throws Exception {
		HttpBasicAuthentication auth = CommonConfigForTests.getBasicAuth();
		String baseUri = CommonConfigForTests.getCommonPetroVaultUri("/HDAPI/");
		client = new ChronosClient(baseUri,auth );
	}

	/**
	 * Test Update channel
	 * @throws IOException
	 */
	@Test
	public void testUpdateChannel() throws IOException {
		UUID channelId = UUID.fromString("21042c27-04c5-4cc6-b4ff-49608fb1306f");
		OffsetDateTime index = OffsetDateTime.parse("2017-02-13T16:26:25.136Z");
		
		//Non Ascii Character test
		String jsonString1 = "{\"principal\":\"pratama\",\"alertUUID\":\"4c4b3d75-0775-44ce-b8bd-8e0b9ecb1416\",\"alertClass\":\"bd91c838-d031-47cc-b711-6fd6c5d5c4e1\",\"details\":{\"comment\":[\"Ã±\"]},\"type\":\"comment\",\"uuid\":\"630dbdcf-5097-4be0-a25c-51afe1dec5c1\",\"timestamp\":\"2017-02-13T16:26:22.365845Z\"}";
		String jsonString2 = "{\"principal\":\"pratama\",\"alertUUID\":\"4c4b3d75-0775-44ce-b8bd-8e0b9ecb1416\",\"alertClass\":\"bd91c838-d031-47cc-b711-6fd6c5d5c4e1\",\"details\":{\"comment\":[\"ñ\"]},\"type\":\"comment\",\"uuid\":\"630dbdcf-5097-4be0-a25c-51afe1dec5c1\",\"timestamp\":\"2017-02-13T16:26:22.365845Z\"}";
		JSONObject value = new JSONObject(jsonString2);
		client.updateChannel(channelId, index, value);
	}

}
