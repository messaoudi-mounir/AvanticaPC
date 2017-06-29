package com.petrolink.mbe.directories;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import com.rabbitmq.client.Channel;
import com.smartnow.rabbitmq.service.RMQRestfulService;

/**
 * Provide support to load and maintain Global Context variables that can be use across the system.
 * @author paul
 *
 */
public class GlobalContext extends RMQRestfulService {
	protected class GlobalContextConsumer extends RMQRestfulServiceConsumer {
		GlobalContext ctx = null;
		
		public GlobalContextConsumer(GlobalContext ctx, Channel channel) {
			super(channel);
			this.ctx = ctx;
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.RMQRestfulService.RMQRestfulServiceConsumer#doDelete(java.util.Map, org.json.simple.JSONObject)
		 */
		@Override
		protected JSONObject doDelete(Map<String, Object> map, JSONObject json) {
			json.getJSONArray("ids").forEach(id -> ctx.variables.remove(id));
			return json;
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.RMQRestfulService.RMQRestfulServiceConsumer#doPost(java.util.Map, org.json.simple.JSONObject)
		 */
		@Override
		protected JSONObject doPost(Map<String, Object> map, JSONObject json) {
			json.getJSONArray("variables").forEach(var -> addVariable((JSONObject) var));
			return json;
		}

		private Object addVariable(JSONObject var) {
			switch (var.getString("type")) {
			case "string":
				ctx.variables.put(var.getString("id"), var.getString("value"));
				break;
			case "integer":
				ctx.variables.put(var.getString("id"), var.getInt("value"));
				break;
			case "long":
				ctx.variables.put(var.getString("id"), var.getLong("value"));
				break;
			case "float":
				ctx.variables.put(var.getString("id"), var.getDouble("value"));
				break;
			case "double":
				ctx.variables.put(var.getString("id"), var.getDouble("value"));
				break;
			case "date":
				OffsetDateTime offset = OffsetDateTime.parse(var.getString("value"));
				ctx.variables.put(var.getString("id"), offset);				
				break;
			case "boolean":
				ctx.variables.put(var.getString("id"), var.getBoolean("value"));
				break;
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.RMQRestfulService.RMQRestfulServiceConsumer#doGet(java.util.Map, org.json.simple.JSONObject)
		 */
		@Override
		protected JSONObject doGet(Map<String, Object> map, JSONObject json) {
			return new JSONObject(ctx.variables);
		}

		/* (non-Javadoc)
		 * @see com.smartnow.rabbitmq.service.RMQRestfulService.RMQRestfulServiceConsumer#doPut(java.util.Map, org.json.simple.JSONObject)
		 */
		@Override
		protected JSONObject doPut(Map<String, Object> map, JSONObject json) {
			// Same as doPost
			return doPost(map, json);
		}
		
	}
	
	private Map<String, Object> variables = new ConcurrentHashMap<String, Object>();
	private VariableResolverFactory variableResolverFactory = new MapVariableResolverFactory(variables);
	
	@Override
	protected GenericRPCServiceConsumer getConsumer(Channel channel) {
		return new GlobalContextConsumer(this,channel);
	}
	
	/**
	 * @return the Global Context MVEL Variable Resolver
	 */
	public VariableResolverFactory getVariableResolverFactory() {
		return variableResolverFactory;
	}

}
