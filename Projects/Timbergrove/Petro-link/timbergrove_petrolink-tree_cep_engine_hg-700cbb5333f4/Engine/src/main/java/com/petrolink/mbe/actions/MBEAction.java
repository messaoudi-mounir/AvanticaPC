package com.petrolink.mbe.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.NotImplementedException;
import org.json.JSONObject;

import com.petrolink.mbe.alertstatus.Alert;
import com.petrolink.mbe.alertstatus.impl.serializers.AlertJSONSerializer;
import com.petrolink.mbe.alertstatus.processors.PetrolinkProcessor;
import com.petrolink.mbe.rulesflow.PetrolinkAlertActionsFlow;
import com.petrolink.mbe.rulesflow.PetrolinkStandAloneAlertFlow;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.rulesflow.variables.ChannelAlias;
import com.petrolink.mbe.rulesflow.variables.Variable;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.alertstatus.AlertJournal;
import com.smartnow.engine.Engine;
import com.smartnow.engine.Engine.OperationStatus;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.actions.Action;

/**
 * The common base class for actions defined in MBE. Provides helper methods for authoring actions.
 * @author langj
 *
 */
public abstract class MBEAction extends Action {
	// Action context keys
	static final String CK_ALERT = "alert";
	static final String CK_DETAILS = "details";
	static final String CK_PROPERTIES = "properties";
	static final String CK_INDEX = "index";
	static final String CK_VALUE = "value";
	static final String CK_RESULT = "result";
	static final String CK_JOURNAL = "journal";
	static final String CK_EVENT = "event";

	// Template context keys
	static final String TCK_ALERT = "alert";
	static final String TCK_DETAILS = "details";
	static final String TCK_EVENT = "event";
	static final String TCK_ROOT = "rootCtx";
	static final String TCK_VARIABLES = "variables";
	
	private UUID instanceId;
	
	@Override
	public int execute(Map<String, Object> context) throws EngineException {
		// This overrides the base class in order to avoid locking multiple times when getting operation status
		OperationStatus actionOperationMode = getOperationMode();
		OperationStatus engineOperationStatus  = Engine.getInstance().getOperationMode(this);
		
		getLogger().debug("MBEAction {} Status is {} , Engine Status is {}", this.getName(),actionOperationMode, engineOperationStatus);
		
		switch (actionOperationMode) {
		case ACTIVE:
			switch (engineOperationStatus) {
			case ACTIVE:
				return executeAction(context);
			case PASSIVE:
				return executePasiveAction(context);
			case TEST:
				return executeTestAction(context);
			default:
				throw new NotImplementedException("unknown OperationStatus");
			}
		case TEST:
			if (engineOperationStatus == OperationStatus.PASSIVE)
				return executePasiveAction(context);
			return executeTestAction(context);
		case PASSIVE:
			return executePasiveAction(context);
		default:
			throw new NotImplementedException("unknown OperationStatus");
		}
	}
	
	protected static Object getIndex(final Map<String, Object> context) {
		return context.get(CK_INDEX);
	}
	
	protected static Object getValue(final Map<String, Object> context) {
		return context.get(CK_VALUE);
	}
	
	protected static Alert getAlert(final Map<String, Object> context) {
		return (Alert) context.get(CK_ALERT);
	}
	
	protected static Boolean getEvaluationResult(final Map<String, Object> context) {
		return (Boolean) context.get(CK_RESULT);
	}
	
	protected static Properties getProperties(final Map<String, Object> context) {
		return (Properties) context.get(CK_PROPERTIES);
	}
	
	protected static JSONObject getDetails(Map<String, Object> context) {
		return (JSONObject) context.get(CK_DETAILS);
	}
	
	protected static AlertJournal getJournal(Map<String, Object> context) {
		return (AlertJournal) context.get(CK_JOURNAL);
	}
	
	/**
	 * Gets the RuleFlow that owns this action.
	 * @return
	 */
	protected RuleFlow getRuleFlow() {
		RuleFlow ruleFlow = null;
		
		if (flow instanceof RuleFlow)
			ruleFlow = (RuleFlow) flow;
		else if (flow instanceof PetrolinkAlertActionsFlow)
			ruleFlow = ((PetrolinkAlertActionsFlow) flow).getParent();
		else if (flow instanceof PetrolinkStandAloneAlertFlow)
			ruleFlow = ((PetrolinkStandAloneAlertFlow) flow).getParent();
		
		assert ruleFlow != null : "ruleFlow != null";
		return ruleFlow;
	}

	/**
	 * Get Event Object from context. Alternative to Node.getEvent()
	 * @param context
	 * @return
	 */
	protected static Event getEventObject(final Map<String, Object> context) {
		return (Event) context.get(CK_EVENT);
	}
	
//	protected static String getFlowId(final Map<String, Object> context) {
//		Event e = getEventObject(context);
//		return e != null ? e.getClassId() : null;
//	}
	
	protected Map<String, Object> createTemplateContext(final Map<String, Object> context) {
		HashMap<String, Object> ctx = new HashMap<String, Object>();
		ctx.put(TCK_ROOT, context);
		
		//DETAILS
		JSONObject details = getDetails(context);
		if (details != null)
			ctx.put(TCK_DETAILS, details.toMap()); // Alert Actions expect to have the Alert Details in the context
		
		//ALERT
		Alert alertObject = getAlert(context);
		if (alertObject != null)
			ctx.put(TCK_ALERT, AlertJSONSerializer.serializeAlertAsJSONObject(alertObject)); // Alert Actions expect to have the Alert in the context
		
		//EVENT
		Event eventObj = getEventObject(context);
		if (eventObj != null) {
			ctx.put(TCK_EVENT, eventObj);
		}
		
		// Rule properties
		ctx.putAll(getRuleFlow().getProperties());
		
		//Variables
		Map<String, Object> variablesObj = createVariablesObject(context);
		if (variablesObj != null) {
			ctx.put(TCK_VARIABLES, variablesObj);
			//Also put as root (to make it compatible with UINotification format)
			ctx.putAll(variablesObj);
		}
		
		return ctx;
	}
	
	/**
	 * Creates a JSON object with the current values of all filtering and condition variables for use in template processing.
	 * Additional data such as value name mapping and units are provided for variables that are channel aliases.
	 * 
	 * Example:
	 * {
	 *   "DRILLING": 3,
	 *   "rop": 200,
	 *   "$rop": {
	 *     "value": 200,
	 *     "valueUnit": "m",
	 *     "index": 100,
	 *     "indexUnit": "m"
	 *   },
	 *   "rigState": 3,
	 *   "$rigState": {
	 *     "value": 3,
	 *     "valueName": "Drilling",
	 *     "index": 100
	 *   }
	 * }
	 * 
	 * @param context The action context
	 * @return JSONObject containing multiple JSONObject containing single Variable
	 */
	protected Map<String, Object> createVariablesObject(final Map<String, Object> context) {
		RuleFlow rule = getRuleFlow();
		Alert alert = getAlert(context);
		Event evt = getEvent(context);
		
		// NOTE: Do not call getValue() on variables because those might have changed since evaluation.
		JSONObject details = getDetails(context);
		HashMap<String, Object> variables = new HashMap<>();
				
		if (details != null) {
			Object index = details.get("index");
			
			for (Variable v : rule.getFilteringVariables().values()) {
				Map<String, Object> vobj = createVariableObject(v, index, details, alert, evt);
				variables.put(v.getAlias(), vobj.get("value"));
				variables.put(Variable.RESOLVABLE_VARIABLE_PREFIX+v.getAlias(), vobj);
			}
			
			for (Variable v : rule.getConditionVariables().values()) {
				Map<String, Object> vobj = createVariableObject(v, index, details, alert, evt);
				variables.put(v.getAlias(), vobj.get("value"));
				variables.put(Variable.RESOLVABLE_VARIABLE_PREFIX+v.getAlias(), vobj);				
			}

			for (ChannelAlias v : ((PetrolinkProcessor) rule.getProcessor()).getAlertChannels().values()) {
				if (variables.containsKey(v.getAlias()))
					continue; // do not override if its defined in filter or condition
				Map<String, Object> vobj = createVariableObject(v, index, details, alert, evt);
				variables.put(v.getAlias(), vobj.get("value"));
				variables.put(Variable.RESOLVABLE_VARIABLE_PREFIX+v.getAlias(), vobj);				
			}
		}
		
		return variables;
	}
	
	/**
	 * Create the template variable context for a single variable.
	 * @param variable A variable
	 * @param index The index that the RuleFlow was triggered at
	 * @param details The container of current state of all variables
	 * @param alert The current alert
	 * @param evt The current event
	 * @return
	 */
	private static Map<String, Object> createVariableObject(Variable variable, Object index, JSONObject details, Alert alert, Event evt) {
		HashMap<String, Object> result = new HashMap<>();
		
		Object value = getVariableValue(variable.getAlias(), details, alert);
		
		result.put("value", nullToEmpty(jsonObjectToMap(value)));
		
		if (variable instanceof ChannelAlias) {
			JSONObject cav = details.optJSONObject(Variable.RESOLVABLE_VARIABLE_PREFIX + ((ChannelAlias) variable).getAlias());
			if (cav != null) {
				result.put("index", nullToEmpty(cav.opt("index")));
				result.put("valueUnit", nullToEmpty(cav.opt("valueUnit")));
				result.put("indexUnit", nullToEmpty(cav.opt("indexUnit")));
				result.put("valueName", nullToEmpty(cav.opt("valueName")));
				result.put("hasPrevValue", nullToEmpty(cav.opt("hasPrevValue")));
				result.put("prevIndex", nullToEmpty(cav.opt("prevIndex")));
				result.put("prevValue", nullToEmpty(jsonObjectToMap(cav.opt("prevValue"))));
			}
		}
		
		return result;
	}
	
	/**
	 * Gets the value of a variable from either the details object or an alert property depending on whether or not
	 * that variable has a special alias name.
	 * @param alias The alias
	 * @param details The details object from the alert context
	 * @param alert The alert object from the alert context
	 * @return The current variable value
	 */
	protected static Object getVariableValue(String alias, JSONObject details, Alert alert) {
		if (ChannelAlias.specialAliases.contains(alias)) {
			switch (alias) {
			case ChannelAlias.SA_RIG_STATE:
				return alert.getRigState();
			case ChannelAlias.SA_BIT_DEPTH:
				return alert.getBitDepth();
			case ChannelAlias.SA_HOLE_DEPTH:
				return alert.getHoleDepth();
			default:
				throw new NotImplementedException(alias);
			}
		}
		return details.opt(alias);
	}
	
	/**
	 * Gets a UUID that uniquely identifies this action instance. It will never be null or empty.
	 * @return the instanceId
	 */
	public final UUID getInstanceId() {
		UUID id = instanceId;
		if (UUIDHelper.isNullOrEmpty(id)) {
			id = getUniqueId(); // Try using the node's unique ID if it is set
			if (UUIDHelper.isNullOrEmpty(id)) {
				id = UUID.randomUUID();
			}
			instanceId = id;
		}
		return id;
	}
	
	/**
	 * @param value The new instanceId value
	 */
	protected final void setInstanceId(final UUID value) {
		instanceId = value;
	}
	
	/**
	 * Cleaning up which can be called by Ruleflow when ruleflow doing deprovision
	 */
	public void deprovision() {
		
	}
	
	private static Object nullToEmpty(Object value) {
		return value != null ? value : "";
	}
	
	protected static Object jsonObjectToMap(Object value) {
		return value instanceof JSONObject ? ((JSONObject) value).toMap() : value;
	}
}
