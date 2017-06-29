import java.io.IOException;

import com.petrolink.mbe.engine.CommonConfigForTests;
import com.petrolink.mbe.pvclient.ResourceApiClient;
import com.petrolink.mbe.setting.HttpAuthentication;

import Petrolink.ResourceApi.Resource;

/**
 * Test Resource API.
 * @author aristo
 *
 */
public class TestResourceApi {
	
	/**
	 * Main Application method.
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			HttpAuthentication auth = CommonConfigForTests.getBasicAuth();
			String baseUri = CommonConfigForTests.getCommonPetroVaultUri("/HDAPI/");
			ResourceApiClient c = new ResourceApiClient(baseUri, auth);
			System.out.println(baseUri);
			Resource channelResource = c.getResourceByUri("./witsml141/well(MBE)/wellbore(MBE)/log(Users)/channel(d45fc9eb-e20c-43f2-a039-0e9fd3dfc580)"); 
			System.out.println("Result\n " + c.getJacksonMapper().writeValueAsString(channelResource));

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
