package com.petrolink.mbe.pvclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.EntityUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.httpclient.HttpClientMetricNameStrategies;
import com.codahale.metrics.httpclient.InstrumentedHttpClientConnectionManager;
import com.codahale.metrics.httpclient.InstrumentedHttpRequestExecutor;
import com.petrolink.mbe.metrics.MetricSystem;
import com.petrolink.mbe.services.EngineService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.setting.HttpAuthentication;

/**
 * The base class for classes exposing REST API's over HTTP.
 * 
 * Subclasses are meant to persist for the lifetime of a program and be reused.
 * 
 * @author langj
 *
 */
public abstract class RestHttpClient implements AutoCloseable {
	protected static final int DEFAULT_TIMEOUT = 5000; // default timeout before a request fails
	protected final String urlPrefix;
	protected final HttpAuthentication authentication;
	
	private static final int CLEANUP_RATE = 5000; // the rate at which expired and idle connection cleanups are performed
	private static final int CLEANUP_IDLE_TIMEOUT = 30000; // the time a connection has to be idle before being closed
	private static final Logger logger = LoggerFactory.getLogger(RestHttpClient.class);
	private final InstrumentedHttpClientConnectionManager connectionManager;
	private final CloseableHttpClient client;
	private long nextCleanup;
	private Charset defaultCharset = StandardCharsets.UTF_8;
	
	
	/**
	 * Construct the REST client with the specified URL prefix and authentication.
	 * @param urlPrefix
	 * @param authentication
	 * @param timeout request timeout in milliseconds, 0 is infinite
	 */
	public RestHttpClient(String urlPrefix, HttpAuthentication authentication, int timeout) {
		if (!urlPrefix.endsWith("/"))
			urlPrefix += "/";
		this.urlPrefix = urlPrefix;
		this.authentication = authentication;
		nextCleanup = System.currentTimeMillis() + CLEANUP_RATE;
		
		int maxTotal = 20;
		int maxPerRoute = 8;
		EngineService engSvc = ServiceAccessor.getEngineService();
		if (engSvc != null) {
			Properties props = engSvc.getUserProperties();
			if (props != null) {
				maxTotal = Integer.parseInt(props.getProperty("HttpClientMaxTotalConnections", "20"));
				maxPerRoute = Integer.parseInt(props.getProperty("HttpClientMaxConnectionsPerRoute", "8"));
			}
		}
		
		
		MetricRegistry metricRegistry = MetricSystem.getRegistry();
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
		// The instrumented connection manager extends PoolingHttpClientConnectionManager
		// We use this more detailed constructor so we can specify the name of the metrics created by this CM
		connectionManager = new InstrumentedHttpClientConnectionManager(metricRegistry,
		                                                                socketFactoryRegistry,
		                                                                null,
		                                                                null,
		                                                                SystemDefaultDnsResolver.INSTANCE,
		                                                                -1,
		                                                                TimeUnit.MILLISECONDS,
		                                                                getClass().getSimpleName());
		connectionManager.setMaxTotal(maxTotal);
		connectionManager.setDefaultMaxPerRoute(maxPerRoute);
		
		HttpRequestExecutor requestExecutor = new InstrumentedHttpRequestExecutor(metricRegistry,
		                                                                          HttpClientMetricNameStrategies.METHOD_ONLY,
		                                                                          getClass().getSimpleName());
		
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).build();
		
		client = HttpClientBuilder.create().
		                           setDefaultRequestConfig(requestConfig).
		                           setRequestExecutor(requestExecutor).
		                           setConnectionManager(connectionManager).
		                           build();
	}
	
	/**
	 * Gets the base URI
	 * @return The base URI string
	 */
	public String getBaseUri() {
		return urlPrefix;
	}
	
	/**
	 * Gets the authentication method.
	 * @return The authentication method object
	 */
	public HttpAuthentication getAuthentication() {
		return authentication;
	}
	
	public void close() {
		try {
			client.close();
		} catch (IOException e) {
			logger.error("HTTP client close exception", e);
		}
	}
	
	protected String getString(String url, boolean nullOnError, int timeoutMillis) throws IOException {
		logger.trace("getString {}", url);
		
		tryCleanup();
		
		HttpGet request = new HttpGet(url);
		prepareRequest(request, false, timeoutMillis);
		
		try (CloseableHttpResponse response = client.execute(request)) {
			if (handleError(request, response, nullOnError))
				return null;
			HttpEntity e = response.getEntity();
			if (e != null)
				return EntityUtils.toString(e);
		}
		
		return null;
	}
	
	protected Document getXML(String url, boolean nullOnError, int timeoutMillis) throws IOException, JDOMException {
		logger.trace("getXML {}", url);
		
		tryCleanup();
		
		HttpGet request = new HttpGet(url);
		
		prepareRequest(request, false, timeoutMillis);
		
		try (CloseableHttpResponse response = client.execute(request)) {
			if (handleError(request, response, nullOnError))
				return null;
			HttpEntity e = response.getEntity();
			if (e != null)
				return new SAXBuilder().build(e.getContent());
		}
		
		return null;
	}
	
	protected <T> T getAvro(String url, boolean nullOnError, DatumReader<T> reader, int timeoutMillis) throws IOException {
		logger.trace("getAvro {}", url);
		
		tryCleanup();
		
		HttpGet request = new HttpGet(url);
		prepareRequest(request, true, timeoutMillis);
		
		try (CloseableHttpResponse response = client.execute(request)) {
			if (handleError(request, response, nullOnError))
				return null;
			HttpEntity e = response.getEntity();
			if (e != null)
				return readAvroObject(e, reader, null);
		}
		
		return null;
	}
	
	protected String postString(String url, String body, int timeoutMillis) throws IOException {
		logger.trace("postString {}\n  body:{}", url, body);
		
		tryCleanup();
		
		HttpPost request = new HttpPost(url);
		prepareRequest(request, false, timeoutMillis);
		
		if (body != null) {
			request.addHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(body, defaultCharset));
		}
		
		try (CloseableHttpResponse response = client.execute(request)) {
			handleError(request, response, false);
			HttpEntity e = response.getEntity();
			if (e != null)
				return EntityUtils.toString(e);
		}
		
		return null;
	}
	
	protected <T> T postAvro(String url, String body, DatumReader<T> responseReader, int timeoutMillis) throws IOException {
		logger.trace("postAvro {}\n  body:{}", url, body);
		
		tryCleanup();

		HttpPost request = new HttpPost(url);
		prepareRequest(request, true, timeoutMillis);
		
		if (body != null) {
			request.addHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(body, defaultCharset));
		}
		
		try (CloseableHttpResponse response = client.execute(request)) {
			handleError(request, response, false);
			HttpEntity e = response.getEntity();
			if (e != null)
				return readAvroObject(e, responseReader, null);
		}
		
		return null;
	}
	
	protected <T> T postAvro(String url, T body, DatumWriter<T> bodyWriter, DatumReader<T> responseReader, int timeoutMillis) throws IOException {
		logger.trace("postAvro {} binary", url);
		
		tryCleanup();

		HttpPost request = new HttpPost(url);
		prepareRequest(request, true, timeoutMillis);
		
		if (body != null) {
			request.addHeader("Content-Type", "application/binary");
			request.setEntity(new ByteArrayEntity(writeAvroObject(bodyWriter, body)));
		}
		
		try (CloseableHttpResponse response = client.execute(request)) {
			handleError(request, response, false);
			HttpEntity e = response.getEntity();
			if (e != null)
				return readAvroObject(e, responseReader, null);
		}

		return null;
	}
	
	protected void putString(String url, String body, int timeoutMillis) throws IOException {
		logger.trace("putString {}\n  body: {}", url, body);
		
		tryCleanup();

		HttpPut request = new HttpPut(url);
		prepareRequest(request, false, timeoutMillis);
		
		if (body != null) {
			request.addHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(body, defaultCharset));
		}
		
		try (CloseableHttpResponse response = client.execute(request)) {
			handleError(request, response, false);
		}
	}
	
	protected <T> void putAvro(String url, T body, DatumWriter<T> bodyWriter, int timeoutMillis) throws IOException {
		logger.trace("putAvro {} binary", url);
		
		tryCleanup();

		HttpPut request = new HttpPut(url);
		prepareRequest(request, true, timeoutMillis);
		
		if (body != null) {
			request.addHeader("Content-Type", "application/binary");
			request.setEntity(new ByteArrayEntity(writeAvroObject(bodyWriter, body)));
		}
		try (CloseableHttpResponse response = client.execute(request)) {
			handleError(request, response, false);
		}
	}
	
	protected void delete(String url, int timeoutMillis) throws IOException {
		logger.trace("delete {}", url);
		
		tryCleanup();

		HttpDelete request = new HttpDelete(url);
		prepareRequest(request, false, timeoutMillis);
		
		try (CloseableHttpResponse response = client.execute(request)) {
			handleError(request, response, false);
		}
	}
	
	/**
	 * Invoked to prepare a request before before it is executed. Handles adding the proper Accept header, adds
	 * authentication information, and sets up the request timeout configuration.
	 * @param request
	 * @param avro
	 * @param timeout
	 */
	protected void prepareRequest(HttpRequestBase request, boolean avro, int timeout) {
		request.addHeader("Accept", avro ? "application/binary" : "application/json");
		if (authentication != null) {
			authentication.prepare(request);
		}
		if (timeout < 0 || timeout != DEFAULT_TIMEOUT) {
			RequestConfig c = RequestConfig.custom()
					.setSocketTimeout(timeout)
					.build();
			request.setConfig(c);
		}
	}
	
	protected static JSONObject toJSONObject(String value) {
		return value != null ? new JSONObject(value) : null;
	}
	
	protected static Document toXMLDocument(String value) throws JDOMException {
		if (value == null)
			return null;
		try {
			return new SAXBuilder().build(new StringReader(value));
		} catch (IOException e) {
			return null; // StringReader wont give us an IOException
		}
	}
	
	protected static boolean handleError(HttpRequestBase request, HttpResponse response, boolean logOnly) throws HttpResponseException {
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode < 200 || statusCode > 299) {
			if (logOnly) {
				logger.error(buildErrorMessage(request, response));
			}
			else {
				throw new HttpResponseException(statusCode, buildErrorMessage(request, response));
			}
			return true;
		}
		return false;
	}
	
	protected static String buildErrorMessage(HttpRequestBase request, HttpResponse response) {
		StatusLine statusLine = response.getStatusLine();
		String msg = statusLine.getStatusCode() + " " + statusLine.getReasonPhrase() + " for request " + request.toString();
		
		HttpEntity e = response.getEntity();
		if (e != null) {
			try {
				msg += ":\n" + EntityUtils.toString(e);
			} catch (IOException e1) {
			}
		}
		
		return msg;
	}
	
	protected static <T> T readAvroObject(HttpEntity e, DatumReader<T> reader, T reuseObject) throws IOException {
		BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(e.getContent(), null);
		return reader.read(reuseObject, decoder);
	}
	
	protected static <T> byte[] writeAvroObject(DatumWriter<T> writer, T object) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
		writer.write(object, encoder);
		return out.toByteArray();
	}
	
	private void tryCleanup() {
		if (System.currentTimeMillis() < nextCleanup)
			return;
		connectionManager.closeExpiredConnections();
		connectionManager.closeIdleConnections(CLEANUP_IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
		nextCleanup = System.currentTimeMillis() + CLEANUP_RATE;
	}

	/**
	 * Get/Set default body's charset, primarily during POST/PUT. During get it will respect charset specified by response.
	 * @return the defaultCharset
	 */
	protected final Charset getDefaultCharset() {
		return defaultCharset;
	}

	/**
	 * Get/Set default body's charset, primarily during POST/PUT. During get it will respect charset specified by response.
	 * @param defaultCharset the defaultCharset to set
	 */
	protected final void setDefaultCharset(Charset defaultCharset) {
		this.defaultCharset = defaultCharset;
	}
}
