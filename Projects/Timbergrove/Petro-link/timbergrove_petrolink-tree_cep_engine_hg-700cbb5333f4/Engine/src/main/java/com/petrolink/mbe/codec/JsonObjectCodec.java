package com.petrolink.mbe.codec;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;


/**
 * Helper to encode/decode object
 * @author aristo
 *
 */
public class JsonObjectCodec {

	private ObjectMapper jacksonMapper;
	
	/**
	 * Constructor
	 */
	public JsonObjectCodec() {
		//For JSON parsing
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jacksonMapper = mapper;
	}
	
	/**
	 * Decode to object. Eg decode(raw, UnnamedClass.class);
	 * @param raw
	 * @param valueType
	 * @return Decoded Object
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public <T> T decode(final String raw, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {
		if (StringUtils.isNotBlank(raw)) {
			return jacksonMapper.readValue(raw, valueType);
		} else {
			return null;
		}
	}
	
	/**
	 * Decode to object. Eg decode(raw, new TypeReference<List<UnnamedClass>>() { });
	 * @param raw
	 * @param valueTypeRef
	 * @return Decoded Object
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public <T> T decode(final String raw, TypeReference<T> valueTypeRef) throws JsonParseException, JsonMappingException, IOException {
		if (StringUtils.isNotBlank(raw)) {
			return jacksonMapper.readValue(raw, valueTypeRef);
		} else {
			return null;
		}
	}
	
	/**
	 * Decode to object. Eg decode(raw, UnnamedClass.class);
	 * @param raw
	 * @param valueType
	 * @return Decoded Object
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public <T> T decode(final byte[] raw, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {
		if (raw != null && raw.length > 0 ) {
			return jacksonMapper.readValue(raw, valueType);
		} else {
			return null;
		}
	}
	
	/**
	 * Decode to object. Eg decode(raw, new TypeReference<List<UnnamedClass>>() { });
	 * @param raw
	 * @param valueTypeRef
	 * @return Decoded Object
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public <T> T decode(final  byte[] raw, TypeReference<T> valueTypeRef) throws JsonParseException, JsonMappingException, IOException {
		if (raw != null && raw.length > 0 ) {
			return jacksonMapper.readValue(raw, valueTypeRef);
		} else {
			return null;
		}
	}
	
	
	/**
	 * Encode to JSON String
	 * @param obj
	 * @return JSON String
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public String encodeToJsonString(Object obj) throws JsonGenerationException, JsonMappingException, IOException {
		if (obj== null) return null;
		return jacksonMapper.writeValueAsString(obj);
	}
	
	/**
	 * Encode to UTF-8 Bytes
	 * @param obj
	 * @return JSON Bytes
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	public byte[] encodeToJsonUtf8Bytes(Object obj) throws JsonGenerationException, JsonMappingException, IOException {
		if (obj== null) return null;
		return jacksonMapper.writeValueAsBytes(obj);
	}

	/**
	 * @return the jacksonMapper
	 */
	public final ObjectMapper getJacksonMapper() {
		return jacksonMapper;
	}

	/**
	 * @param jacksonMapper the jacksonMapper to set
	 */
	public final void setJacksonMapper(ObjectMapper jacksonMapper) {
		this.jacksonMapper = jacksonMapper;
	}
	
}
