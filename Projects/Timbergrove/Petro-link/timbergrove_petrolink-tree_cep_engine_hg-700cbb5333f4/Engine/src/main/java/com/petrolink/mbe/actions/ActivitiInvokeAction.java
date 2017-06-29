package com.petrolink.mbe.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.jdom2.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;
import com.smartnow.engine.util.EngineParam;
import com.smartnow.engine.util.MVELUtil;
import com.smartnow.mediation.util.MappingField;


/**
 * Action to Invoke an Activiti BPM Flow
 * @author paul
 *
 */
public class ActivitiInvokeAction extends MBEAction {
	protected String user;
	protected String password;
	protected String host;
	protected Integer port;
	protected String path;
	protected String input;
	protected String content;
	Serializable responseScript;

	protected String name;
	protected Boolean isAuthenticated = false;
	protected Boolean scriptedPath = false;
	protected TreeSet<EngineParam> params = new TreeSet<EngineParam>();
	protected List<MappingField> fields = new ArrayList<MappingField>();
	
	private String requestContentType = "application/json";
	private String contentType = "application/json";

	
	private Logger logger = LoggerFactory.getLogger(ActivitiInvokeAction.class);

	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		// - Create content
		context.put("content", new StringBuffer());
		System.out.println("executeAction" +this.content);

		StringBuffer buffer = new StringBuffer();
		try {
			int status = doPost(prepareURI(context), content.toString(), buffer);
			logger.trace("Status:" + status);

		} catch (URISyntaxException e) {
			logger.error("Error preparing connection", e);
		}	

		return 0;
	}
		
	/**
	 * Invokes the Activiti Engine
	 * @param url
	 * @param content
	 * @param responseContent
	 * @return the Invocation response status code
	 * @throws EngineException
	 */
	public int doPost(URI url, String content, StringBuffer responseContent) throws EngineException {
		try {
			CloseableHttpClient httpclient = (CloseableHttpClient) getSharedObject(this.getName()+"_HTTPClient");
			
			StringEntity postEntity = new StringEntity(content);
			postEntity.setContentType(requestContentType);
			HttpEntityEnclosingRequestBase req = null;
			
			long t0 = System.currentTimeMillis();
			
			req = new HttpPost(url);				
			req.addHeader("Accept", contentType);
			req.setEntity(postEntity);
			logger.trace("Content:" + content);
			logger.trace("httppost: " + req);

			CloseableHttpResponse response = httpclient.execute(req);

			int statusCode = response.getStatusLine().getStatusCode();

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line = "";
				while ((line = rd.readLine()) != null) {
					responseContent.append(line);
				}
			}
			
			logger.info("HTTP Request elapsed time: " + Long.toString(System.currentTimeMillis()-t0) + "ms");

			if (statusCode == 201) {
				if(responseScript != null) {
					ObjectReader reader = new ObjectMapper().reader(Map.class);
					Map<String, Object> map = reader.readValue(responseContent.toString());
					MVEL.executeExpression(responseScript, map);
				}			
			}
			
			logger.info(responseContent.toString());
			response.close();

			return statusCode;
		} catch (ClientProtocolException e) {
			logger.error("Invocation failed due to connection issues");
			throw new EngineException("Invocation failed due to connection issues",e);
		} catch (IOException e) {
			logger.error("Invocation failed due to IO issues",e);
			throw new EngineException("Invocation failed due to IO issues",e);
		} catch (Exception e) {
			logger.error("Invocation failed due to Unknown issues", e);
			throw new EngineException("Invocation failed due to Unknown issues",e);
		}
	}	
		

	@Override
	protected int executeTestAction(Map<String, Object> arg0) throws EngineException {
		// TODO Write to log the Activiti Invocation parameters
		return 0;
	}
	@Override
	public void finalize(Map<String, Object> arg0) throws EngineException {
		// Not required since the HTTP Client will be automatically closed
	}
	
	@Override
	public void init(Map<String, Object> arg0) throws EngineException {
		/**
		 * The credentials provider
		 */
		CredentialsProvider credsProvider = null;
		/**
		 * Represent an http client
		 */
		CloseableHttpClient httpclient = null;
		
		if (isAuthenticated) {
			credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(new AuthScope(host, port), new UsernamePasswordCredentials(user, password));
			httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		} else {
			httpclient = HttpClients.createDefault();
		}	
		storeSharedObject(this.getName()+"_HTTPClient", httpclient);
	}
	
	
	/**
	 * Load attributes.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void load(final Element e, final Node parent) throws EngineException {
		super.load(e, parent);
		JSONObject bpmParameters = new JSONObject();
		
		this.name = e.getAttributeValue("name");
		if (e.getChild("Credentials", e.getNamespace()) != null) {
			this.user = e.getChild("Credentials",e.getNamespace()).getAttributeValue("user");
			this.password = e.getChild("Credentials",e.getNamespace()).getAttributeValue("password");
			this.isAuthenticated = true;

		}
	 
		if(e.getChild("ProcessDefinitionKey",e.getNamespace()) != null) {
			bpmParameters.put("processDefinitionKey",e.getChildText("ProcessDefinitionKey",e.getNamespace())); 
		}
		if(e.getChild("Variables",e.getNamespace()) != null) {
			JSONArray array = new JSONArray();
			Element variables = e.getChild("Variables",e.getNamespace());
			for (Element field : variables.getChildren()) {
				JSONObject ofield = new JSONObject();
				String name = field.getAttributeValue("name");
				String type = field.getAttributeValue("type");
				Object value = field.getText();
				
				ofield.put("name", name);
				switch(type.toUpperCase()){
				case "STRING":
					ofield.put("value", value.toString());
					break;
				case "INTEGER":
					ofield.put("value", Integer.parseInt(value.toString()));
					break;
				default:
					ofield.put("value", value);
				}
				array.add(ofield);
			}

			bpmParameters.put("variables", array);
		}
		
		this.content = bpmParameters.toJSONString();
		
		this.host = e.getChildText("Host",e.getNamespace());
		if(e.getChild("Port",e.getNamespace()) != null) {
			this.port = Integer.parseInt(e.getChildText("Port",e.getNamespace()));
		}
		
		// - Get path info
		if (e.getChild("Path", e.getNamespace()) != null) {
			this.path = e.getChildText("Path", e.getNamespace());			
		} else {
			this.path = e.getChildText("PathScript", e.getNamespace());
			this.scriptedPath  = true;
		}
		if(e.getChild("input", e.getNamespace()) != null) {
			this.input = e.getChildText("input", e.getNamespace());
		}
		// - Compile response script
		if(e.getChild("ResponseScript", e.getNamespace()) != null) {
			String responseScript = e.getChildText("ResponseScript", e.getNamespace());
			this.responseScript = MVELUtil.compileScript(responseScript);
		}
		
		if(e.getChild("Params", e.getNamespace()) != null) {
			for (Element p : e.getChild("Params", e.getNamespace()).getChildren()) {
				EngineParam param = new EngineParam();
				param.loadParam(p);
				params.add(param);
			}
		}
		
		if (e.getAttribute("requestContentType", e.getNamespace()) != null) {
			this.requestContentType = e.getAttributeValue("requestContentType", e.getNamespace());
		}
		if (e.getAttribute("contentType", e.getNamespace()) != null) {
			this.contentType = e.getAttributeValue("contentType", e.getNamespace());
		}
	}
	
	/**
	 * @param context
	 * @return the Invocation URI based on the provided parameters
	 * @throws URISyntaxException
	 */
	public URI prepareURI(Map<String, Object> context) throws URISyntaxException {
		URIBuilder uriBuilder = null;
		
		if (scriptedPath) {
			String scriptedPath = MVEL.evalToString(path, context);
			uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPort(port).setPath(scriptedPath);
		} else {
			uriBuilder = new URIBuilder().setScheme("http").setHost(host).setPort(port).setPath(path);			
		}

		for (EngineParam param : params) {
			uriBuilder.addParameter(param.id, param.getValue(context));
		}

		URI uri = uriBuilder.build();
		return uri;
	}


}
