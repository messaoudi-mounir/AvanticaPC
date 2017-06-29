package com.petrolink.mbe.setting;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpRequestBase;
import org.jdom2.Element;

/**
 * Authentication helper of HTTP Request Base.
 * @author aristo
 */
public abstract class HttpAuthentication {
	/**
	 * Prepare a request to send to server. For example by setting necessary header.
	 * @param request
	 */
	public abstract void prepare(HttpRequestBase request);
	
	/**
	 * Parse an element for a basic or SAML authentication object.
	 * @param element
	 * @return HttpAuthentication based on Xml Element
	 */
	public static HttpAuthentication parse(final Element element) {
		Objects.requireNonNull(element);
		
		Element samlTokenElement = element.getChild("SamlToken", element.getNamespace());
		if (samlTokenElement != null) {
			boolean isBase64 = Boolean.valueOf(samlTokenElement.getAttributeValue("base64"));
			String samlToken = samlTokenElement.getValue();
			if (isBase64) {
				try {
					samlToken = new String(Base64.decodeBase64(samlToken), "US-ASCII");
				} catch (UnsupportedEncodingException e) {
					// US-ASCII is always supported
				}
			}
			return new HttpSamlTokenAuthentication(samlToken);
		}
		
		Element basicAuthElement = element.getChild("Basic", element.getNamespace());
		if (basicAuthElement != null) {
			String username = basicAuthElement.getAttributeValue("username");
			String password = basicAuthElement.getAttributeValue("password");
			return new HttpBasicAuthentication(username, password);
		}
		
		return null;
	}
}



