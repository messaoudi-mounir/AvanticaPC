package com.petrolink.mbe.setting;

import java.net.URI;

/**
 * Encapsulates connections settings for HTTP clients
 * @author langj
 *
 */
public class HttpClientConnectionSettings {
	private final URI url;
	private final HttpAuthentication authentication;
	
	/**
	 * Initialize the settings
	 * @param url
	 * @param auth
	 */
	public HttpClientConnectionSettings(URI url, HttpAuthentication auth) {
		this.url = url;
		this.authentication = auth;
	}
	
	/**
	 * @return the URL to be used by the HTTP client
	 */
	public URI getURL() {
		return url;
	}
	
	/**
	 * @return the authentication method
	 */
	public HttpAuthentication getAuthentication() {
		return authentication;
	}
}
