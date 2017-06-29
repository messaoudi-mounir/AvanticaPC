package com.petrolink.mbe.pvclient;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.setting.HttpAuthentication;
import com.petrolink.mbe.util.JacksonPropertyNamingStrategies;

import Petrolink.ResourceApi.Resource;


/**
 * Rest Client for Resource API.
 * @author aristo
 */
public class ResourceApiClient extends JacksonRestHttpClient  {
	private String resourcesBaseUri;
	private Logger logger = LoggerFactory.getLogger(ResourceApiClient.class);
	private URLCodec urlCodec = new URLCodec();
	
	/**
	 * Constructor with auth.
	 * @param baseUri
	 * @param authentication
	 */
	public ResourceApiClient(final String baseUri, final HttpAuthentication authentication) {
		super(baseUri, authentication, DEFAULT_TIMEOUT);
		
		
		//Petrolink API are Pascal case
		getJacksonMapper().setPropertyNamingStrategy(JacksonPropertyNamingStrategies.PASCAL_CASE_STRATEGY);
		//On Unknown, just ignore (may be new API)
		getJacksonMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);		
		
		resourcesBaseUri = urlPrefix + "resources";
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}
	
	/**
	 * Get resource by api
	 * @param uri Petrolink's Uri-like structure for resource, eg ./witsml141/well(mbe)/wellbore(mbe)/log(users)/channel(abc)
	 * @return The matching resource, or null if none was found
	 * @throws IOException
	 */
	public final Resource getResourceByUri(final String uri) throws IOException {
		if (StringUtils.isBlank(uri)) {
			throw new IllegalArgumentException("Resource URI is not set");
		}
		
		String url = null;
		try {
			url = String.format("%s?uri=%s", resourcesBaseUri, urlCodec.encode(uri));
		} catch (EncoderException e) {
			throw new IllegalArgumentException("Resource URI is illegal.", e);
		}
		
		return getStringAndParse(url, Resource.class);
	}
	
	/**
	 * Get metadata about a well.
	 * @param id The well resource id
	 * @return A WellMetadata instance if the well was found, or null if the id did not match a well
	 * @throws IOException
	 */
	public final WellMetadata getWellById(UUID id) throws IOException {
		String url = String.format("%s/%s", resourcesBaseUri, id);
		
		JSONObject obj = toJSONObject(getString(url, true, -1));
		if (obj == null)
			return null;
		
		JSONObject metadata = obj.getJSONObject("Metadata");
		if (!metadata.getString("Type").equalsIgnoreCase("well"))
			return null;
		
		String wellName = metadata.getString("Name");
		
		Element wellElement = decodeWitsmlObject(obj.getJSONObject("Data").getString("Content"), "well");
		
		String rigName = null;
		if (wellElement != null)
			rigName = getRigNameFromWell(wellElement);
		
		return new WellMetadata(wellName, rigName);
	}
	
	private static Element decodeWitsmlObject(String encodedContent, String objectName) {
		byte[] raw = Base64.getDecoder().decode(encodedContent);
		
		// This crap is totally broken and has garbage bytes at the beginning. The workaround is
		// to search for the first and last XML brackets
		String rawString = new String(raw, StandardCharsets.UTF_8);
		String xmlText = rawString.substring(rawString.indexOf('<'), rawString.lastIndexOf('>') + 1);
		
		Document doc;
		SAXBuilder b = new SAXBuilder();
		try {
			doc = b.build(new StringReader(xmlText));
		} catch (JDOMException | IOException e) {
			return null;
		}
		
		assert doc.getRootElement().getName().endsWith("s");
		
		return doc.getRootElement().getChild(objectName, doc.getRootElement().getNamespace());
	}
	
	private static String getRigNameFromWell(Element well) {
		Namespace ns = well.getNamespace();
		Element datum = well.getChild("wellDatum", ns);
		if (datum != null) {
			Element rig = datum.getChild("rig", ns);
			if (rig != null) {
				Element rigReference = rig.getChild("rigReference", ns);
				if (rigReference != null) {
					return rigReference.getTextTrim();
				}
			}
		}
		return null;
	}
	
	/**
	 * Contains metadata about a well
	 * @author langj
	 *
	 */
	public static class WellMetadata {
		private final String name;
		private final String rigName;
		
		private WellMetadata(String name, String rigName) {
			this.name = name;
			this.rigName = rigName;
		}
		
		/**
		 * Gets the well name.
		 * @return The well name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * Gets the rig name. Can be null if no rig specified.
		 * @return The rig name
		 */
		public String getRigName() {
			return rigName;
		}
	}
}
