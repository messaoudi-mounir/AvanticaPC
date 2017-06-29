package com.petrolink.mbe.pvclient;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;

import com.petrolink.mbe.setting.HttpAuthentication;
/**
 * Class which use jackson mapper
 * @author aristo
 *
 */
public abstract class JacksonRestHttpClient extends RestHttpClient {
	private ObjectMapper jacksonMapper;
	
	/**
	 * Constructor with auth.
	 * @param urlPrefix
	 * @param authentication
	 * @param timeout
	 */
	public JacksonRestHttpClient(final String urlPrefix, final HttpAuthentication authentication, final int timeout) {
		super(urlPrefix, authentication, timeout);
		
		//For JSON parsing
		ObjectMapper mapper = new ObjectMapper();
		jacksonMapper = mapper;
	}
	
	/**
	 * Logger for logging whether the call is successfull or not.
	 * @return
	 */
	protected abstract Logger getLogger();
	
	/**
	 * @return the jacksonMapper
	 */
	public final ObjectMapper getJacksonMapper() {
		return jacksonMapper;
	}

	/**
	 * @param newJacksonMapper the jacksonMapper to set
	 */
	protected final void setJacksonMapper(final ObjectMapper newJacksonMapper) {
		this.jacksonMapper = newJacksonMapper;
	}

	protected <T> T getStringAndParse(final String url, Class<T> valueType) throws IOException {
		String raw = null;
		try {
			raw = this.getString(url, true, -1);
			if (StringUtils.isNotBlank(raw)) {
				return jacksonMapper.readValue(raw, valueType);
			} else {
				return null;
			}
		} catch (Exception e) {
			getLogger().error("Error response from {} \n RawResponse={} \n {} ", url, raw, e.toString());
			throw e;
		}
	}
	
	protected <T> T getStringAndParse(final String url, TypeReference<T> valueTypeRef) throws IOException {
		String raw = null;
		try {
			raw = this.getString(url, true, -1);
			if (StringUtils.isNotBlank(raw)) {
				return jacksonMapper.readValue(raw, valueTypeRef);
			} else {
				return null;
			}
		} catch (Exception e) {
			getLogger().error("Error response from {} \n RawResponse={} \n {} ", url, raw, e.toString());
			throw e;
		}
	}
	
	protected <T> T postStringAndParse(final String url, final String body, TypeReference<T> valueTypeRef) throws IOException {
		String raw = null;
		try {
			raw = this.postString(url, body, -1);
			if (StringUtils.isNotBlank(raw)) {
				return jacksonMapper.readValue(raw, valueTypeRef);
			} else {
				return null;
			}
		} catch (Exception e) {
			getLogger().error("Error response from {} \n RawResponse={} \n {} ", url, raw, e.toString());
			throw e;
		}
	}

}
