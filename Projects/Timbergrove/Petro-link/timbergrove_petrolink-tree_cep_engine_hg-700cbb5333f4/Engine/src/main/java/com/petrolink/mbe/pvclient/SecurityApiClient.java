package com.petrolink.mbe.pvclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.setting.HttpAuthentication;
import com.petrolink.mbe.util.JacksonPropertyNamingStrategies;

import Petrolink.SecurityApi.Principal;
import Petrolink.SecurityApi.PrincipalDetails;
import Petrolink.SecurityApi.PrincipalResult;

/**
 * Rest Client for Security API.
 * @author aristo
 */
public class SecurityApiClient extends JacksonRestHttpClient {

	private String principalsBaseUri;
	
	private Logger logger = LoggerFactory.getLogger(SecurityApiClient.class);
		
	/**
	 * Constructor with auth.
	 * @param baseUri
	 * @param authentication
	 */
	public SecurityApiClient(final String baseUri, final HttpAuthentication authentication) {
		super(baseUri, authentication, DEFAULT_TIMEOUT);
		
		//Petrolink API are Pascal case
		getJacksonMapper().setPropertyNamingStrategy(JacksonPropertyNamingStrategies.PASCAL_CASE_STRATEGY);
		//On Unknown, just ignore (may be new API)
		getJacksonMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		principalsBaseUri = urlPrefix + "principals/";
	}
	
	
	@Override
	protected Logger getLogger() {
		return logger;
	}
	
	/**
	 * Get All Principals available in the system. Use with care as this is large and slow. 
	 * @return PrincipalResult containing all principals.
	 * @throws IOException
	 */
	public final PrincipalResult getPrincipals() throws IOException {
		String url = String.format("%sgetAll", principalsBaseUri);
		return getStringAndParse(url, PrincipalResult.class);
	}
	
	//BUGGY SERVICE, the property name is incorrect, it is just array of 2 item, id and name
//	public final String getPrincipalNamesById(String principalId) throws IOException {
//		String url = String.format("%sprincipalName/%s", principalsBaseUri, principalId);
//		return this.getString(url, true);
//	}
	
	/**
	 * Get member of the specified principal Id. will also get sub groups/sub user. if the principal is user, only return the user principal.
	 * @param principalId Identifier of the principal.
	 * @return List of principal details under specified principal Id
	 * @throws IOException
	 */
	public final List<PrincipalDetails> getMemberDeep(final String principalId) throws IOException {
		String url = String.format("%smemberdeep/%s", principalsBaseUri, principalId);
		return getStringAndParse(url, new TypeReference<List<PrincipalDetails>>() { });
	}
	
	/**
	 * Get Groups where specified user is member of.
	 * @param userId  Identifier of the user.
	 * @return Principal where specified user is member of.
	 * @throws IOException
	 */
	public final List<Principal> getGroupsFromUser(final String userId) throws IOException {
		String url = String.format("%sgroups/%s", principalsBaseUri, userId);
		return getStringAndParse(url, new TypeReference<List<Principal>>() { });
	}
	
	/**
	 * Get group detail based on group name.
	 * @param name Name of the group
	 * @return Principal detail of specified group
	 * @throws IOException
	 */
	public final Principal getGroupDetailWithMembers(final String name) throws IOException {
		String url = String.format("%sgroupDetail/%s", principalsBaseUri, name);
		return getStringAndParse(url, Principal.class);
	}
	
	/**
	 * Get principal details for specified GUIDs
	 * @param principalGuids  Identifier of the principals.
	 * @return  principal details for specified GUIDs
	 * @throws IOException
	 */
	public final List<PrincipalDetails> getPrincipalDetails(final List<String> principalGuids) throws IOException {
		if ((principalGuids == null) ||  (principalGuids.size() <= 0)) { 
			return null; 
		}
		
		String url = String.format("%sprincipalDetails", principalsBaseUri);
		String body = getJacksonMapper().writeValueAsString(principalGuids);
		List<PrincipalDetails> principals = postStringAndParse(url, body, new TypeReference<List<PrincipalDetails>>() { });
		return principals;
	}
	
	/**
	 * Get principal detail for specified principalGuid
	 * @param principalGuid  Identifier of the principal.
	 * @return principal detail for specified principalGuid
	 * @throws IOException
	 */
	public final PrincipalDetails getPrincipalDetail(final String principalGuid) throws IOException {
		//TODO: FIX with proper PVHD's new API
		List<String> principalQuery = new ArrayList<String>();
		principalQuery.add(principalGuid);
		
		List<PrincipalDetails> result = getPrincipalDetails(principalQuery);
		if ((result != null) && (result.size() ==  1)) {
			return result.get(0);
		}
		return null;
	}
	
	
	
}
