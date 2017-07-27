import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.petrolink.mbe.engine.CommonConfigForTests;
import com.petrolink.mbe.pvclient.MbeOrchestrationApiClient;
import com.petrolink.mbe.setting.HttpBasicAuthentication;
import com.petrolink.mbe.util.UUIDHelper;

/**
 * Test Orchestration API
 * @author aristo
 */
public class TestOrchestrationApi {
	private static String defaultSamlToken ="PHNhbWw6QXNzZXJ0aW9uIFZlcnNpb249IjIuMCIgSUQ9IlNhbWxTZWN1cml0eVRva2VuLTQxZjZiZjZiLTZlNjQtNDJkMy04MDIxLWEyMzI1NTE2NWYxOSIgSXNzdWVJbnN0YW50PSIyMDE2LTEwLTMxVDIwOjE5OjU1WiIgeG1sbnM6c2FtbD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFzc2VydGlvbiI+PHNhbWw6SXNzdWVyPmh0dHA6Ly9wdmNsb3VkMi9QZXRyb1ZhdWx0SEQvPC9zYW1sOklzc3Vlcj48c2FtbDpTdWJqZWN0PjxzYW1sOk5hbWVJRD5hZG1pbmlzdHJhdG9yPC9zYW1sOk5hbWVJRD48c2FtbDpTdWJqZWN0Q29uZmlybWF0aW9uIE1ldGhvZD0iTm9NZXRob2RZZXQtVE9ETyIgLz48L3NhbWw6U3ViamVjdD48c2FtbDpDb25kaXRpb25zIE5vdEJlZm9yZT0iMjAxNi0xMC0zMVQyMDoxOTo1NVoiIE5vdE9uT3JBZnRlcj0iMjAxNi0xMS0wN1QyMToxOTo1NVoiIC8+PHNhbWw6QXR0cmlidXRlU3RhdGVtZW50PjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJQcmluY2lwYWwiIEZyaWVuZGx5TmFtZT0iVXNlciBQcmluY2lwYWwgTmFtZSI+PHNhbWw6QXR0cmlidXRlVmFsdWU+YWRtaW5pc3RyYXRvcjwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48L3NhbWw6QXR0cmlidXRlPjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJQcmluY2lwYWxJRCIgRnJpZW5kbHlOYW1lPSJVc2VyIFByaW5jaXBhbCBJRCI+PHNhbWw6QXR0cmlidXRlVmFsdWU+NDk5NWYxYzItMmY4OC00MGFmLWExZWYtNTVlODZlYTUzMTk3PC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9IlByaW5jaXBhbEVtYWlsIiBGcmllbmRseU5hbWU9IlVzZXIgUHJpbmNpcGFsIEVtYWlsIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZT5hZG1pbmlzdHJhdG9yQHBldHJvbGluay5jb208L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0iUHJpbmNpcGFsRmlyc3ROYW1lIiBGcmllbmRseU5hbWU9IlVzZXIgUHJpbmNpcGFsIEZpcnN0IE5hbWUiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlPkFkbWluaXN0cmF0b3I8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0iUHJpbmNpcGFsTGFzdE5hbWUiIEZyaWVuZGx5TmFtZT0iVXNlciBQcmluY2lwYWwgTGFzdCBOYW1lIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZSAvPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9IlByaW5jaXBhbFRDQWNjZXB0ZWRPbiIgRnJpZW5kbHlOYW1lPSJVc2VyIFRlcm0gQ29uZGl0aW9uIEFjY2VwdGFuY2UgVGltZXN0YW1wIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZT4yMDE2LTEwLTI3VDE5OjM5OjEwLjc5NTc5OTZaPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9IlByaW5jaXBhbFRDRGVjbGluZWRPbiIgRnJpZW5kbHlOYW1lPSJVc2VyIFRlcm0gQ29uZGl0aW9uIERlY2xpbmF0aW9uIFRpbWVzdGFtcCI+PHNhbWw6QXR0cmlidXRlVmFsdWUgLz48L3NhbWw6QXR0cmlidXRlPjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJQcmluY2lwYWxUQ1NraXAiIEZyaWVuZGx5TmFtZT0iVXNlciByZXF1aXJlcyB0byBhY2NlcHQgVGVybSBDb25kaXRpb24iPjxzYW1sOkF0dHJpYnV0ZVZhbHVlPkZhbHNlPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9IlByaW5jaXBhbE15RHJvcEJveFN0YXR1cyIgRnJpZW5kbHlOYW1lPSJVc2VyIE15RHJvcEJveCBTdGF0dXMiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlPkRpc2FibGVkPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9IlBldHJvVmF1bHRFeHBpcmF0aW9uRGF0ZSIgRnJpZW5kbHlOYW1lPSJQZXRyb1ZhdWx0IEV4cGlyYXRpb24gRGF0ZSI+PHNhbWw6QXR0cmlidXRlVmFsdWU+MjAxNi0xMC0zMVQyMDoxOTo1NS4zNTIyMDE2Wjwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48L3NhbWw6QXR0cmlidXRlPjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJQYXNzd29yZEV4cGlyZUluRGF5cyIgRnJpZW5kbHlOYW1lPSJQYXNzd29yZCBFeHBpcmUgaW4gKERheXMpIj48c2FtbDpBdHRyaWJ1dGVWYWx1ZT42MDwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48L3NhbWw6QXR0cmlidXRlPjxzYW1sOkF0dHJpYnV0ZSBOYW1lPSJHcm91cHMiIEZyaWVuZGx5TmFtZT0iR3JvdXAgTWVtYmVyc2hpcCI+PHNhbWw6QXR0cmlidXRlVmFsdWUgVVVJRD0iMTA4NGRmYzktNWU0My00NDNhLWEyZTUtNDEwZjM3Njc5NmI0Ij5FdmVyeW9uZTwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48c2FtbDpBdHRyaWJ1dGVWYWx1ZSBVVUlEPSIyY2E3MjI2ZS05MWFkLTQ0ZjQtOGZhMy1mZGRhN2I2N2E3M2UiPlN5c3RlbUNvbmZpZ3VyYXRpb248L3NhbWw6QXR0cmlidXRlVmFsdWU+PHNhbWw6QXR0cmlidXRlVmFsdWUgVVVJRD0iNTY4NzQ1MWQtMWQ4Yy00MTRkLWE5ZmQtY2M3YTU5ZjRkNmQwIj5SZXBvcnRpbmdFZGl0b3I8L3NhbWw6QXR0cmlidXRlVmFsdWU+PHNhbWw6QXR0cmlidXRlVmFsdWUgVVVJRD0iNzJlZjdjZGEtMTFlZC00ZjNiLWFlMWQtYjAyMzU2MjkyMTI0Ij5TZXJ2ZXJBZG1pbmlzdHJhdG9yczwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48c2FtbDpBdHRyaWJ1dGVWYWx1ZSBVVUlEPSI3NzI3NDNkMC01YTRhLTQxOTYtODhmMS01YTc0NWYxOTM3YTIiPlB1YmxpY1Byb2ZpbGVBY2Nlc3M8L3NhbWw6QXR0cmlidXRlVmFsdWU+PHNhbWw6QXR0cmlidXRlVmFsdWUgVVVJRD0iYmZmMDQxYTgtNWQyNS00OWFiLWIxYTEtNGUwMDU0ZTU4MjQ1Ij5SZWFsVGltZVN5c3RlbUVkaXRvcjwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48c2FtbDpBdHRyaWJ1dGVWYWx1ZSBVVUlEPSJjNDAwMGY4Yi1mOTY0LTQ4NDAtOGIzYy0wZTgxOWU0ZTVkM2EiPldlbGxBZG1pbjwvc2FtbDpBdHRyaWJ1dGVWYWx1ZT48c2FtbDpBdHRyaWJ1dGVWYWx1ZSBVVUlEPSJjNzY0OTlmYS1lYjdkLTRjNjgtODU1Ny02NDFiMjhjYmNhMWUiPkFkbWluaXN0cmF0b3JzPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjwvc2FtbDpBdHRyaWJ1dGU+PHNhbWw6QXR0cmlidXRlIE5hbWU9IkNsaWVudElQQWRkcmVzcyIgRnJpZW5kbHlOYW1lPSJJUCBBZGRyZXNzIG9mIHRoZSBDbGllbnQiPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIC8+PC9zYW1sOkF0dHJpYnV0ZT48c2FtbDpBdHRyaWJ1dGUgTmFtZT0iR3JvdXBSb2xlcyIgRnJpZW5kbHlOYW1lPSJHcm91cCBSb2xlcyI+PHNhbWw6QXR0cmlidXRlVmFsdWUgVVVJRD0iOTVjZGNhNDItY2M0NC00Nzk5LWI3NzMtODVmN2UwNWM4MDg3IiBQcmluY2lwYWxJZD0iMTA4NGRmYzktNWU0My00NDNhLWEyZTUtNDEwZjM3Njc5NmI0IiBSb2xlPSJBZG1pbmlzdHJhdG9yIj5BZG1pbmlzdHJhdG9yPC9zYW1sOkF0dHJpYnV0ZVZhbHVlPjxzYW1sOkF0dHJpYnV0ZVZhbHVlIFVVSUQ9ImI1MDZkZGVlLTE3MTMtNGNmYy05OGNmLTVjOGQ4N2U3NjBjNyIgUHJpbmNpcGFsSWQ9IjEwODRkZmM5LTVlNDMtNDQzYS1hMmU1LTQxMGYzNzY3OTZiNCIgUm9sZT0iU3VwZXJVc2VyIj5TdXBlclVzZXI8L3NhbWw6QXR0cmlidXRlVmFsdWU+PC9zYW1sOkF0dHJpYnV0ZT48L3NhbWw6QXR0cmlidXRlU3RhdGVtZW50PjxzYW1sOkF1dGhuU3RhdGVtZW50IEF1dGhuSW5zdGFudD0iMjAxNi0xMC0zMVQyMDoxOTo1NVoiIC8+PFNpZ25hdHVyZSB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC8wOS94bWxkc2lnIyI+PFNpZ25lZEluZm8+PENhbm9uaWNhbGl6YXRpb25NZXRob2QgQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiIC8+PFNpZ25hdHVyZU1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNyc2Etc2hhMSIgLz48UmVmZXJlbmNlIFVSST0iI1NhbWxTZWN1cml0eVRva2VuLTQxZjZiZjZiLTZlNjQtNDJkMy04MDIxLWEyMzI1NTE2NWYxOSI+PFRyYW5zZm9ybXM+PFRyYW5zZm9ybSBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNlbnZlbG9wZWQtc2lnbmF0dXJlIiAvPjxUcmFuc2Zvcm0gQWxnb3JpdGhtPSJodHRwOi8vd3d3LnczLm9yZy8yMDAxLzEwL3htbC1leGMtYzE0biMiPjxJbmNsdXNpdmVOYW1lc3BhY2VzIFByZWZpeExpc3Q9IiNkZWZhdWx0IGNvZGUgZHMga2luZCBydyBzYW1sIHNhbWxwIHR5cGVucyIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDEvMTAveG1sLWV4Yy1jMTRuIyIgLz48L1RyYW5zZm9ybT48L1RyYW5zZm9ybXM+PERpZ2VzdE1ldGhvZCBBbGdvcml0aG09Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvMDkveG1sZHNpZyNzaGExIiAvPjxEaWdlc3RWYWx1ZT55WlVXK2xwOWQ3TUR0R2hIUDNubTRkdS9LbUU9PC9EaWdlc3RWYWx1ZT48L1JlZmVyZW5jZT48L1NpZ25lZEluZm8+PFNpZ25hdHVyZVZhbHVlPmVwVFJyTkc5RkZzNVV5dXNxS1ZDanZBSHZUNjRFM1RrTFR2Um1PQ3hvVXJDWGQ4Wk9pNFFXUzQzZEMwVkpERTNvbEpUZGc5OWZvVFhlYjRuU0tDTlR0cU5tbm55NHZOTlNsSVA3UkNkQ3YrMmlsS3FSUUFzVXJRTHZOSFB4YytNeXpFU3VtNGo5ZG95eFAvZWVoSEt2MmdWcTJDVnBmSDNmZVZQMmFuQ0xnVT08L1NpZ25hdHVyZVZhbHVlPjxLZXlJbmZvPjxYNTA5RGF0YT48WDUwOUNlcnRpZmljYXRlPk1JSUJuakNDQVFjQ0JFYlRtZEF3RFFZSktvWklodmNOQVFFRUJRQXdGakVVTUJJR0ExVUVBeE1MZDNkM0xtbGtjQzVqYjIwd0hoY05NRGN3T0RJNE1ETTBNekV5V2hjTk1UY3dPREkxTURNME16RXlXakFXTVJRd0VnWURWUVFERXd0M2QzY3VhV1J3TG1OdmJUQ0JuekFOQmdrcWhraUc5dzBCQVFFRkFBT0JqUUF3Z1lrQ2dZRUFvMzFxM21KWmF5WGZaa0xEdUxjbmFuYy9LRytSREZXK09sWURQK1J1YnZXbnQ4WDVqdGlVVGNwOElRNDZUTkVVRnNrbXNvblViNUFuRyt6T0NjYXdiMmRKcjhrQnRDTmhmaS9UdWZaR0JRTmp1QXhOTWkzNHlJZ1JkR2luYXpuSGdjbHJBSUlaVHlLZXJRcVlqUEwxeFJEc0ZHcHpxR0dpLzJvcHpOOG5WNWtDQXdFQUFUQU5CZ2txaGtpRzl3MEJBUVFGQUFPQmdRQm1Od0ZOKzk4YXlidVFLRkpGcjY5czlCdkJWWXRrK0hzeDNneDBnNGU1c0xUbGtjU1UwM1haOEFPZXQwbXk0UnZVc3BhRFJ6RHJ2K2dFZ2c3Z0RQL3JzVkNTczNka3VZdVV2dVdiaWlUcS9IajRFS3VLWmE4bkllclozT3o0WGExL2JLODhlVDdSVnN2NWJNT3hnSmJTRXZUaWRUdk9wVjBHMTNkdUlxeXJDdz09PC9YNTA5Q2VydGlmaWNhdGU+PC9YNTA5RGF0YT48L0tleUluZm8+PC9TaWduYXR1cmU+PC9zYW1sOkFzc2VydGlvbj4="; 
	
	/**
	 * Main Application method.
	 * @param args
	 */
	public static void main(String[] args) {
	
		try {
			HttpBasicAuthentication auth = CommonConfigForTests.getBasicAuth();
			String baseUri = CommonConfigForTests.getCommonPetroVaultUri("/mbe/");
			
			//HttpAuthentication auth = new HttpBasicAuthentication("Administrator","p^5CAq97he");
			MbeOrchestrationApiClient c = new MbeOrchestrationApiClient(baseUri, auth);
			System.out.println(baseUri);
			
			List<UUID> principalIds = new ArrayList<UUID>();
			principalIds.add(UUIDHelper.fromStringFast("c76499fa-eb7d-4c68-8557-641b28cbca1e"));
			principalIds.add(UUIDHelper.fromStringFast("d76499fa-eb7d-4c68-8557-641b28cbca1e"));
			principalIds.add(UUIDHelper.fromStringFast("e76499fa-eb7d-4c68-8557-641b28cbca1e"));
			
			System.out.println(c.getNotificationChannel(principalIds).getChannels());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}