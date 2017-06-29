package com.petrolink.mbe.setting;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpRequestBase;
/**
 * A HttpAuthentication which use SAML Token
 * @author aristo
 *
 */
public final class HttpSamlTokenAuthentication extends HttpAuthentication {
	private final String samlToken;
	
	/**
	 * Constructor
	 * @param samlToken
	 */
	public HttpSamlTokenAuthentication(final String samlToken) {
		this.samlToken = Objects.requireNonNull(samlToken);
	}
	
	/**
	 * Prepare a request to send to server. For example by setting necessary header.
	 */
	@Override
	public void prepare(final HttpRequestBase request) {
		Objects.requireNonNull(request);
		request.setHeader("samlToken", samlToken);
	}
	
	/**
	 * Create HttpSamlTokenAuthentication  instance from Base64 Token.
	 * @param base64Token
	 * @return HttpSamlTokenAuthentication containing the specified token
	 * @throws UnsupportedEncodingException 
	 */
	public static HttpSamlTokenAuthentication fromBase64(final String base64Token) throws UnsupportedEncodingException {
		String token = new String(Base64.decodeBase64(base64Token), "US-ASCII");
		return new HttpSamlTokenAuthentication(token);
	}
}