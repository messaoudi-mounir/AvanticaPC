package com.petrolink.mbe.setting;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import org.apache.http.client.methods.HttpRequestBase;

/**
 * Support for Basic Authentication. 
 * @author aristo
 */
public final class HttpBasicAuthentication extends HttpAuthentication {
	private final String basicAuthString;
	
	/**
	 * Constructor.
	 * @param anUsername
	 * @param aPassword
	 */
	public HttpBasicAuthentication(final String anUsername, final String aPassword) {
		String authStr = String.format("%s:%s", Objects.requireNonNull(anUsername), Objects.requireNonNull(aPassword));
		basicAuthString = "Basic " + Base64.getEncoder().encodeToString(authStr.getBytes(StandardCharsets.US_ASCII));
	}
	
	@Override
	public void prepare(final HttpRequestBase request) {
		Objects.requireNonNull(request);
		request.setHeader("Authorization", basicAuthString);
	}
}
