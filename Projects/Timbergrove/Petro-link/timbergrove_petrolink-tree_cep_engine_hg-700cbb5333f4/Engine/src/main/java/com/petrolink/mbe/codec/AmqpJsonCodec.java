package com.petrolink.mbe.codec;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.type.TypeReference;

import org.json.JSONObject;

import com.petrolink.mbe.amqp.AmqpMessage;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * Codec for AmqpJsonCodec
 * @author aristo
 */
public class AmqpJsonCodec {

	private JsonObjectCodec jsonCodec;
	private Charset encodeCharset;
	protected ContentType encodeContentType;
	
	/**
	 * Constructor
	 */
	public AmqpJsonCodec() {
		jsonCodec = new JsonObjectCodec();
		setEncodeCharset(StandardCharsets.UTF_8);
	}

	/**
	 * @return the encodeCharacterSet
	 */
	public final Charset getEncodeCharset() {
		return encodeCharset;
	}

	/**
	 * @param encodeCharacterSet the encodeCharacterSet to set
	 */
	public final void setEncodeCharset(Charset encodeCharacterSet) {
		this.encodeCharset = encodeCharacterSet;
		updateContentType();
	}
	
	/**
	 * Update Content Type
	 */
	private void updateContentType() {
		encodeContentType = ContentType.create("application/json", encodeCharset);
	}

	/**
	 * @return the encodeContentType
	 */
	public final ContentType getEncodeContentType() {
		return encodeContentType;
	}
	
	/**
	 * Encode to bytes based on charset
	 * @param object
	 * @return Encoded Bytes of the object.
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public final byte[] encodeToBytes(Object object) throws JsonGenerationException, JsonMappingException, IOException {
		String jsonString;
		if(object instanceof JSONObject) {
			jsonString = ((JSONObject)object).toString();
		} else {
			jsonString = jsonCodec.encodeToJsonString(object);
		}
		
		return jsonString.getBytes(getEncodeCharset());
	}
	
	/**
	 * Decode Message as specified Type
	 * @param message
	 * @param valueTypeRef
	 * @return Decoded Class Object
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public <T> T decode(AmqpMessage message, TypeReference<T> valueTypeRef) throws JsonParseException, JsonMappingException, IOException {
		ContentType ctype = extractContentType(message);
		if (ctype == null) {
			ctype =	getEncodeContentType();
		}
		String jsonString = new String(message.getContent(),ctype.getCharset());
		return jsonCodec.decode(jsonString,valueTypeRef);
	}
	
	/**
	 * Decode Message as specified Type
	 * @param message
	 * @param valueType
	 * @return Decoded Class Object
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public <T> T decode(AmqpMessage message, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {
		ContentType ctype = extractContentType(message);
		if (ctype == null) {
			ctype =	getEncodeContentType();
		}
		
		String jsonString = new String(message.getContent(),ctype.getCharset());
		return jsonCodec.decode(jsonString,valueType);
	}
	
	/**
	 * Extract Content Type from AmqpMessage
	 * @param message
	 * @return parsed ContentType. Null of message or its content type properties are null/blank
	 */
	public static ContentType extractContentType(AmqpMessage message) {
		if(message == null) {
			return null;
		}
		
		BasicProperties prop = message.getProperties();
		if(prop == null) {
			return null;
		}
		
		String contentTypeString = prop.getContentType();
		if (StringUtils.isBlank(contentTypeString)) {
			return null;
		}
		
		ContentType ctype = ContentType.parse(contentTypeString);
		return ctype;
	}
}