package com.petrolink.mbe.services;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.jdom2.Element;

import com.petrolink.mbe.setting.HttpAuthentication;
import com.petrolink.mbe.setting.HttpBasicAuthentication;
import com.petrolink.mbe.setting.HttpClientConnectionSettings;
import com.petrolink.mbe.setting.HttpSamlTokenAuthentication;
import com.smartnow.engine.Engine;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.services.Service;
import com.smartnow.engine.settings.EngineSettings;
import com.smartnow.engine.util.NamedValueResource;

/**
 * Base class for services that wrap HTTP clients.
 * @author langj
 *
 */
public abstract class HttpClientService extends Service {
	/**
	 * Loads connection settings from the engine.
	 * @param id The ID of the connection settings
	 * @return A new HttpClientConnectionSettings instance
	 * @throws EngineException
	 */
	protected HttpClientConnectionSettings loadConnectionSettings(Element e) throws EngineException {
		String apiPath = e.getAttributeValue("apiPath");
		String connectionId = e.getAttributeValue("id");
		
		NamedValueResource settings = (NamedValueResource) Engine.getInstance().getSetEntry(EngineSettings.CONNECTIONS_SET, connectionId);
		if (settings == null)
			throw new EngineException("invalid connection ID: " + connectionId);
		
		String urlString = (String) settings.get("URL");
		String samlToken = (String) settings.get("SamlToken");
		String userName = (String) settings.get("User");
		String password = (String) settings.get("Lock");
		
		URI workingUrl = URI.create(urlString);
		if (apiPath != null && apiPath.length() != 0)
			workingUrl = workingUrl.resolve(apiPath);
		URI url = workingUrl;
		
		HttpAuthentication authentication;
		if (samlToken != null) {
			samlToken = new String(Base64.decodeBase64(samlToken), StandardCharsets.US_ASCII);
			authentication = new HttpSamlTokenAuthentication(samlToken);
		}
		else if (userName != null && password != null) {
			authentication = new HttpBasicAuthentication(userName, password);
		}
		else {
			authentication = null;
		}
		
		return new HttpClientConnectionSettings(url, authentication);
	}
}
