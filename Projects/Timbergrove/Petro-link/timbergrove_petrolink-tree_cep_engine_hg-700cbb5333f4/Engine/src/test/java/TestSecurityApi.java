import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

import com.petrolink.mbe.engine.CommonConfigForTests;
import com.petrolink.mbe.pvclient.SecurityApiClient;
import com.petrolink.mbe.setting.HttpBasicAuthentication;

import Petrolink.SecurityApi.Principal;
import Petrolink.SecurityApi.PrincipalDetails;
import Petrolink.SecurityApi.PrincipalResult;

/**
 * Test Security API.
 */
public class TestSecurityApi {
	private static String defaultSamlToken ="IDxzYW1sOkFzc2VydGlvbiBWZXJzaW9uPSIyLjAiIElEPSJTYW1sU2VjdXJpdHlUb2tlbi1hMWUxM2JmZC00MzYzLTQzODktYjQ2ZC1mNDA5ZmY0MTdjMDgiIElzc3VlSW5zdGFudD0iMjAxNi0wOS0yOVQxOToyNTo1NloiIHhtbG5zOnNhbWw9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDphc3NlcnRpb24iPjxzYW1sOklzc3Vlcj5odHRwOi8vcHZjbG91ZDIvUGV0cm9WYXVsdERldi88L3NhbWw6SXNzdWVyPjxzYW1sOlN1YmplY3Q+PHNhbWw6TmFtZUlEPmFkbWluaXN0cmF0b3I8L3NhbWw6TmFtZUlEPjxzYW1sOlN1YmplY3RDb25maXJtYXRpb24gTWV0aG9kPSJOb01ldGhvZFlldC1UT0RPIiAvPjwvc2FtbDpTdWJqZWN0PjxzYW1sOkNvbmRpdGlvbnMgTm90QmVmb3JlPSIyMDE2LTA5LTI5VDE5OjI1OjU2WiIgTm90T25PckFmdGVyPSIyMDE2LTEwLTA2VDIwOjI1OjU2WiIgLz48c2FtbDpBdHRyaWJ1dGVTdGF0ZW1lbnQ+PHNhbWw6QXR0cmlidXRlIE5hbWU9IlByaW5jaXBhbCIgRnJpZW5kbHlOYW1lPSJVc2VyIFByaW5jaXBhbCBOYW1lIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZT5hZG1pbmlzdHJhdG9yPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9IlByaW5jaXBhbElEIiBGcmllbmRseU5hbWU9IlVzZXIgUHJpbmNpcGFsIElEIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZT40OTk1ZjFjMi0yZjg4LTQwYWYtYTFlZi01NWU4NmVhNTMxOTc8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0iUHJpbmNpcGFsRW1haWwiIEZyaWVuZGx5TmFtZT0iVXNlciBQcmluY2lwYWwgRW1haWwiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlPmFkbWluaXN0cmF0b3JAcGV0cm9saW5rLmNvbTwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48L3NhbWw6QXR0cmlidXRlPjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJQcmluY2lwYWxGaXJzdE5hbWUiIEZyaWVuZGx5TmFtZT0iVXNlciBQcmluY2lwYWwgRmlyc3QgTmFtZSI+PHNhbWw6QXR0cmlidXRlVmFsdWU+QWRtaW5pc3RyYXRvcjwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48L3NhbWw6QXR0cmlidXRlPjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJQcmluY2lwYWxMYXN0TmFtZSIgRnJpZW5kbHlOYW1lPSJVc2VyIFByaW5jaXBhbCBMYXN0IE5hbWUiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIC8+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0iUHJpbmNpcGFsVENBY2NlcHRlZE9uIiBGcmllbmRseU5hbWU9IlVzZXIgVGVybSBDb25kaXRpb24gQWNjZXB0YW5jZSBUaW1lc3RhbXAiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlPjIwMTYtMDgtMjBUMTE6MTU6NTUuMzkxNzU4OVo8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0iUHJpbmNpcGFsVENEZWNsaW5lZE9uIiBGcmllbmRseU5hbWU9IlVzZXIgVGVybSBDb25kaXRpb24gRGVjbGluYXRpb24gVGltZXN0YW1wIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZSAvPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9IlByaW5jaXBhbFRDU2tpcCIgRnJpZW5kbHlOYW1lPSJVc2VyIHJlcXVpcmVzIHRvIGFjY2VwdCBUZXJtIENvbmRpdGlvbiI+PHNhbWw6QXR0cmlidXRlVmFsdWU+RmFsc2U8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0iUHJpbmNpcGFsTXlEcm9wQm94U3RhdHVzIiBGcmllbmRseU5hbWU9IlVzZXIgTXlEcm9wQm94IFN0YXR1cyI+PHNhbWw6QXR0cmlidXRlVmFsdWU+RGlzYWJsZWQ8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0iUGV0cm9WYXVsdEV4cGlyYXRpb25EYXRlIiBGcmllbmRseU5hbWU9IlBldHJvVmF1bHQgRXhwaXJhdGlvbiBEYXRlIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZT4yMDE2LTA5LTI5VDE5OjI1OjU2LjgxNDAwOTRaPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9IlBhc3N3b3JkRXhwaXJlSW5EYXlzIiBGcmllbmRseU5hbWU9IlBhc3N3b3JkIEV4cGlyZSBpbiAoRGF5cykiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlPjYwPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9Ikdyb3VwcyIgRnJpZW5kbHlOYW1lPSJHcm91cCBNZW1iZXJzaGlwIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZSBVVUlEPSIxMDg0ZGZjOS01ZTQzLTQ0M2EtYTJlNS00MTBmMzc2Nzk2YjQiPkV2ZXJ5b25lPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIFVVSUQ9IjJjYTcyMjZlLTkxYWQtNDRmNC04ZmEzLWZkZGE3YjY3YTczZSI+U3lzdGVtQ29uZmlndXJhdGlvbjwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48c2FtbDpBdHRyaWJ1dGVWYWx1ZSBVVUlEPSI1Njg3NDUxZC0xZDhjLTQxNGQtYTlmZC1jYzdhNTlmNGQ2ZDAiPlJlcG9ydGluZ0VkaXRvcjwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48c2FtbDpBdHRyaWJ1dGVWYWx1ZSBVVUlEPSI3MmVmN2NkYS0xMWVkLTRmM2ItYWUxZC1iMDIzNTYyOTIxMjQiPlNlcnZlckFkbWluaXN0cmF0b3JzPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIFVVSUQ9Ijc3Mjc0M2QwLTVhNGEtNDE5Ni04OGYxLTVhNzQ1ZjE5MzdhMiI+UHVibGljUHJvZmlsZUFjY2Vzczwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48c2FtbDpBdHRyaWJ1dGVWYWx1ZSBVVUlEPSJiZmYwNDFhOC01ZDI1LTQ5YWItYjFhMS00ZTAwNTRlNTgyNDUiPlJlYWxUaW1lU3lzdGVtRWRpdG9yPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIFVVSUQ9ImM0MDAwZjhiLWY5NjQtNDg0MC04YjNjLTBlODE5ZTRlNWQzYSI+V2VsbEFkbWluPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIFVVSUQ9ImM3NjQ5OWZhLWViN2QtNGM2OC04NTU3LTY0MWIyOGNiY2ExZSI+QWRtaW5pc3RyYXRvcnM8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0iQ2xpZW50SVBBZGRyZXNzIiBGcmllbmRseU5hbWU9IklQIEFkZHJlc3Mgb2YgdGhlIENsaWVudCI+PHNhbWw6QXR0cmlidXRlVmFsdWUgLz48L3NhbWw6QXR0cmlidXRlPjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJHcm91cFJvbGVzIiBGcmllbmRseU5hbWU9Ikdyb3VwIFJvbGVzIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZSBVVUlEPSI5NWNkY2E0Mi1jYzQ0LTQ3OTktYjc3My04NWY3ZTA1YzgwODciIFByaW5jaXBhbElkPSIxMDg0ZGZjOS01ZTQzLTQ0M2EtYTJlNS00MTBmMzc2Nzk2YjQiIFJvbGU9IkFkbWluaXN0cmF0b3IiPkFkbWluaXN0cmF0b3I8L3NhbWw6QXR0cmlidXRlVmFsdWU+PHNhbWw6QXR0cmlidXRlVmFsdWUgVVVJRD0iYjUwNmRkZWUtMTcxMy00Y2ZjLTk4Y2YtNWM4ZDg3ZTc2MGM3IiBQcmluY2lwYWxJZD0iMTA4NGRmYzktNWU0My00NDNhLWEyZTUtNDEwZjM3Njc5NmI0IiBSb2xlPSJTdXBlclVzZXIiPlN1cGVyVXNlcjwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48L3NhbWw6QXR0cmlidXRlPjwvc2FtbDpBdHRyaWJ1dGVTdGF0ZW1lbnQ+PHNhbWw6QXV0aG5TdGF0ZW1lbnQgQXV0aG5JbnN0YW50PSIyMDE2LTA5LTI5VDE5OjI1OjU2WiIgLz48U2lnbmF0dXJlIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwLzA5L3htbGRzaWcjIj48U2lnbmVkSW5mbz48Q2Fub25pY2FsaXphdGlvbk1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIgLz48U2lnbmF0dXJlTWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3JzYS1zaGExIiAvPjxSZWZlcmVuY2UgVVJJPSIjU2FtbFNlY3VyaXR5VG9rZW4tYTFlMTNiZmQtNDM2My00Mzg5LWI0NmQtZjQwOWZmNDE3YzA4Ij48VHJhbnNmb3Jtcz48VHJhbnNmb3JtIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI2VudmVsb3BlZC1zaWduYXR1cmUiIC8+PFRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyI+PEluY2x1c2l2ZU5hbWVzcGFjZXMgUHJlZml4TGlzdD0iI2RlZmF1bHQgY29kZSBkcyBraW5kIHJ3IHNhbWwgc2FtbHAgdHlwZW5zIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMS8xMC94bWwtZXhjLWMxNG4jIiAvPjwvVHJhbnNmb3JtPjwvVHJhbnNmb3Jtcz48RGlnZXN0TWV0aG9kIEFsZ29yaXRobT0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnI3NoYTEiIC8+PERpZ2VzdFZhbHVlPmx5bzVKanF5bmlrR3hweG9IQjBuUmNFalIwTT08L0RpZ2VzdFZhbHVlPjwvUmVmZXJlbmNlPjwvU2lnbmVkSW5mbz48U2lnbmF0dXJlVmFsdWU+SWJ5YzlYdW8vam1jM0wzUGZRenBWOHVwTno2MENuWk5qTnlVYU44TURteWxvN1g4Y0hnM0ZmSmpaS3pYOC9mNzBPR0lFTHdDTFcyeHA2R1Y3b1FkK2g4dlpQalM0T1hCajB1bFNXRDZVeGI1MmVBOHc2Wk13dS9NcjdYQkJoL29vNWdRbHVhR2lwYnU0Z2Z0dGdqKzRnWERWaTg2SXhhenRjWGlQaUtIOFc4PTwvU2lnbmF0dXJlVmFsdWU+PEtleUluZm8+PFg1MDlEYXRhPjxYNTA5Q2VydGlmaWNhdGU+TUlJQm5qQ0NBUWNDQkViVG1kQXdEUVlKS29aSWh2Y05BUUVFQlFBd0ZqRVVNQklHQTFVRUF4TUxkM2QzTG1sa2NDNWpiMjB3SGhjTk1EY3dPREk0TURNME16RXlXaGNOTVRjd09ESTFNRE0wTXpFeVdqQVdNUlF3RWdZRFZRUURFd3QzZDNjdWFXUndMbU52YlRDQm56QU5CZ2txaGtpRzl3MEJBUUVGQUFPQmpRQXdnWWtDZ1lFQW8zMXEzbUpaYXlYZlprTER1TGNuYW5jL0tHK1JERlcrT2xZRFArUnVidldudDhYNWp0aVVUY3A4SVE0NlRORVVGc2ttc29uVWI1QW5HK3pPQ2Nhd2IyZEpyOGtCdENOaGZpL1R1ZlpHQlFOanVBeE5NaTM0eUlnUmRHaW5hem5IZ2NsckFJSVpUeUtlclFxWWpQTDF4UkRzRkdwenFHR2kvMm9wek44blY1a0NBd0VBQVRBTkJna3Foa2lHOXcwQkFRUUZBQU9CZ1FCbU53Rk4rOThheWJ1UUtGSkZyNjlzOUJ2QlZZdGsrSHN4M2d4MGc0ZTVzTFRsa2NTVTAzWFo4QU9ldDBteTRSdlVzcGFEUnpEcnYrZ0VnZzdnRFAvcnNWQ1NzM2RrdVl1VXZ1V2JpaVRxL0hqNEVLdUtaYThuSWVyWjNPejRYYTEvYks4OGVUN1JWc3Y1Yk1PeGdKYlNFdlRpZFR2T3BWMEcxM2R1SXF5ckN3PT08L1g1MDlDZXJ0aWZpY2F0ZT48L1g1MDlEYXRhPjwvS2V5SW5mbz48L1NpZ25hdHVyZT48L3NhbWw6QXNzZXJ0aW9uPg=="; 
	
	/**
	 * Main Application method.
	 * @param args
	 */
	public static void main(String[] args) {
	
		
			
		String baseUri = CommonConfigForTests.getCommonPetroVaultUri("/SecurityApi/");
		HttpBasicAuthentication auth = CommonConfigForTests.getBasicAuth();
		
		SecurityApiClient c = new SecurityApiClient(baseUri, auth);
		System.out.println(baseUri);
		//= c.getPrincipals();
		
//		PrincipalResult result = testGetPrincipals(c);
//		if (result.getTotalRecords() > 0) {
//			checkDetails(c,result.getPrincipals());
//		} else {
//			System.out.println("ZERO Principal");
//		}
			
		testAdministrators(c);
		testPrincipalDetails(c);
		
	}
	
	/**
	 * Test get principals.
	 * @param client SecurityApiClient to use
	 * @return PrincipalResult
	 */
	public static PrincipalResult testGetPrincipals(SecurityApiClient client) {
		try {
			PrincipalResult result = client.getPrincipals();
			Assert.assertNotNull(result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Test availability for administrators.
	 * @param client SecurityApiClient to use
	 * @return PrincipalDetails list
	 */
	public static List<PrincipalDetails> testAdministrators(SecurityApiClient client) {
		System.out.println("Test Administrator:c76499fa-eb7d-4c68-8557-641b28cbca1e");
		try {
			List<PrincipalDetails> result = client.getMemberDeep("c76499fa-eb7d-4c68-8557-641b28cbca1e");
			Assert.assertNotNull(result);
			if (result != null  && !result.isEmpty()) {
				for (Iterator<PrincipalDetails> iterator = result.iterator(); iterator.hasNext();) {
					PrincipalDetails principalDetails = (PrincipalDetails) iterator.next();
					System.out.println(principalDetails.toString());
				}
			} else {
				System.out.println("ZERO Admins");
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Check Details of Principal (get Detail for specified Principal List).
	 * @param client SecurityApiClient to use
	 * @param principals Principal List to use
	 * @throws IOException
	 */
	public static void checkDetails(SecurityApiClient client, List<Principal> principals) throws IOException {
		for (Principal prin: principals) {
			//BUGGY SERVICE
			//String raw = c.getPrincipalNamesById(prin.getId());
			//System.out.println(prin.getId() +", "+ prin.getPrincipalType() + "=" + raw);
			
			System.out.println(prin.getFullName() + ", " + prin.getPrincipalType() + "=" + prin.getId());
			//Group checking
			if (prin.isGroup()) {
				//String raw = c.getGroupDetailWithMembers(prin.getName());
				//System.out.println("Detail: " + raw);
				Principal detail = client.getGroupDetailWithMembers(prin.getName());
				System.out.println("Detail: " + detail.getFullName() + "/" + detail.getName() + "<" + detail.getEmail() + "> " + detail.getMembers().size() + " members");
				
				List<PrincipalDetails> members = client.getMemberDeep(prin.getId());
				for (PrincipalDetails member: members) {
					System.out.println("- Member: " + member.getFullName() + "<" + member.getEmail() + ">");
				}
				
			} else if (prin.isUser()) {
				//String raw = c.getGroupsFromUser(prin.getId());
				//System.out.println("Members Of: " + raw);
				List<Principal> members = client.getGroupsFromUser(prin.getId());
				for (Principal member: members) {
					System.out.println("- MemberOf: " + member.getFullName() + "<" + member.getEmail() + ">");
				}
			}
			System.out.println();
		}
	}

	/**
	 * Test get Principal Detail.
	 * @param client SecurityApiClient to use
	 */
	public static void testPrincipalDetails(SecurityApiClient client) {
		try {
			
			//test group retrieval
     		PrincipalDetails serverAdminGroup = client.getPrincipalDetail("72ef7cda-11ed-4f3b-ae1d-b02356292124");
			Assert.assertNotNull(serverAdminGroup);
			Assert.assertEquals("ServerAdministrators", serverAdminGroup.getName());
			System.out.println(serverAdminGroup);
			
			//test user retrieval
			PrincipalDetails administratorUser= client.getPrincipalDetail("4995f1c2-2f88-40af-a1ef-55e86ea53197");
			Assert.assertNotNull(administratorUser);
			Assert.assertEquals("administrator", administratorUser.getName());
			System.out.println(administratorUser);
			
			//test user and group mix
			System.out.println("Test Mixed User and groups:");
			List<String> principalQuery3 = new ArrayList<String>();
			principalQuery3.add("4995f1c2-2f88-40af-a1ef-55e86ea53197");
			principalQuery3.add("72ef7cda-11ed-4f3b-ae1d-b02356292124");
			List<PrincipalDetails> mixed = client.getPrincipalDetails(principalQuery3);
			Assert.assertEquals(principalQuery3.size(), mixed.size());
			for (PrincipalDetails principalDetails : mixed) {
				String prinId = principalDetails.getId().toString();
				if ("4995f1c2-2f88-40af-a1ef-55e86ea53197".equals(prinId)) {
					Assert.assertEquals("administrator", principalDetails.getName());
				} else if ("72ef7cda-11ed-4f3b-ae1d-b02356292124".equals(prinId)) {
					Assert.assertEquals("ServerAdministrators", principalDetails.getName());
				} else {
					Assert.fail("The Principal should not be included since it is not queried: " + principalDetails.toString() + " \nQueried Id(s):" + principalQuery3);
				}
				System.out.println(principalDetails);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

