package com.petrolink.mbe.pvclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.type.TypeReference;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.setting.HttpAuthentication;
import com.petrolink.mbe.util.JSONHelper;

import Petrolink.MbeOrchestrationApi.NotificationChannelResponse;

/**
 * Client for MBE Orchestration Web Service.
 * @author aristo
 *
 */
public class MbeOrchestrationApiClient  extends JacksonRestHttpClient {
	private static final int GET_STARTED_FLOWS_TIMEOUT = 60000;
	private static final Logger logger = LoggerFactory.getLogger(MbeOrchestrationApiClient.class);
	
	/**
	 * Constructor with auth.
	 * @param baseUri
	 * @param authentication
	 */
	public MbeOrchestrationApiClient(final String baseUri, final HttpAuthentication authentication) {
		super(baseUri, authentication, DEFAULT_TIMEOUT);
		
		//Petrolink MBE API are camel case hence the following is not needed
		//getJacksonMapper().setPropertyNamingStrategy(JacksonPropertyNamingStrategies.PASCAL_CASE_STRATEGY);
		//On Unknown, just ignore (may be new API)
		getJacksonMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	/**
	 * Get default logger for this class.
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}
	
	/**
	 * Get Notification Channel for specified principal. If principal is group, it would also check subchildren.
	 * @param principalGuids
	 * @return NotificationChannelResponse which contain the list of Notification Channel Ids
	 * @throws IOException
	 */
	public final NotificationChannelResponse getNotificationChannel(final Collection<UUID> principalGuids) throws IOException {
		if ((principalGuids == null) ||  (principalGuids.size() <= 0)) { 
			return null; 
		}
		String url = String.format("%snotificationchannel", getBaseUri());
		JSONObject request = new JSONObject();
		request.putOpt("PrincipalIds", principalGuids.toArray());
		
		//String body = getJacksonMapper().writeValueAsString(request);
		String body = request.toString();
		NotificationChannelResponse notificationResponse = postStringAndParse(url, body, new TypeReference<NotificationChannelResponse>() { });
		return notificationResponse;
	}
	
	/**
	 * Get the metadata for a well.
	 * @param id
	 * @return A JSONObject containing well metadata or null if no ID match.
	 * @throws IOException
	 */
	public final Map<String, String> getWellMetadata(UUID id) throws IOException {
		String url = String.format("%smetadata/well/%s", urlPrefix, id);
		
		JSONObject result = toJSONObject(getString(url, false, -1));
		if (result == null)
			return null;
		
		HashMap<String, String> resultMap = new HashMap<>();
		JSONHelper.update(resultMap, result);
		return resultMap;
	}
	
	/**
	 * Get the metadata for a wellbore.
	 * @param id
	 * @return A JSONObject containing wellbore metadata or null if no ID match.
	 * @throws IOException
	 */
	public final Map<String, String> getWellboreMetadata(UUID id) throws IOException {
		String url = String.format("%smetadata/wellbore/%s", urlPrefix, id);

		JSONObject result = toJSONObject(getString(url, false, -1));
		if (result == null)
			return null;
		
		HashMap<String, String> resultMap = new HashMap<>();
		JSONHelper.update(resultMap, result);
		return resultMap;
	}
	
	/**
	 * Get a rig state dictionary
	 * @param id
	 * @return A list of RigStateInfo or null if no ID match.
	 * @throws IOException
	 */
	public final List<RigStateInfo> getRigStateDictionary(UUID id) throws IOException {
		String url = String.format("%smetadata/rigstate/%s", urlPrefix, id);
		
		JSONObject response = toJSONObject(getString(url, true, -1));
		if (response == null)
			return null;
		
		ArrayList<RigStateInfo> results = new ArrayList<>();
		
		for (Object o : response.getJSONArray("rigStates")) {
			JSONObject rs = (JSONObject) o;
			
			String name = rs.getString("name");
			String alias = rs.getString("alias");
			int value = rs.getInt("value");
			
			results.add(new RigStateInfo(name, alias, value));
		}
		
		return results;
	}
	
	/**
	 * Gets the flow XML for all rules that have been started
	 * @return A list of Rule flow elements
	 * @throws IOException
	 */
	public final List<Element> getStartedRuleFlows() throws IOException {
		String url = String.format("%srule/started", urlPrefix);
		
		ArrayList<Element> results = new ArrayList<>();
		
		Document doc;
		try {
			doc = getXML(url, true, GET_STARTED_FLOWS_TIMEOUT);
		} catch (JDOMException e) {
			logger.error("fail to parse result body", e);
			doc = null;
		}
		
		if (doc == null)
			return results;
		
		for (Element f : doc.getRootElement().getChildren()) {
			if (f.getName().equals("Flows")) {
				List<Element> fc = f.getChildren();
				assert fc.size() == 1 && fc.get(0).getName().equals("Rule");
				results.add(fc.get(0));
			}
		}
		
		return results;
	}
	
	/**
	 * Describes a rig state
	 * @author langj
	 *
	 */
	public static final class RigStateInfo {
		private String name;
		private String alias;
		private int value;
		
		RigStateInfo(String name, String alias, int value) {
			this.name = name;
			this.alias = alias;
			this.value = value;
		}
		
		/**
		 * @return the display name of the rig state.
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * @return an alias suitable for use as a variable name.
		 */
		public String getAlias() {
			return alias;
		}
		
		/**
		 * @return the rig state value
		 */
		public int getValue() {
			return value;
		}
	}
}
