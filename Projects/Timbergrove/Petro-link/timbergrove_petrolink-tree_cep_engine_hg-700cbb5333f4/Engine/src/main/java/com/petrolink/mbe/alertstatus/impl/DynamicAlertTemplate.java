package com.petrolink.mbe.alertstatus.impl;

import static com.petrolink.mbe.rulesflow.RuleFlow.CHANNEL_CTX_VARIABLE;
import static com.petrolink.mbe.rulesflow.RuleFlow.INDEX_CTX_VARIABLE;
import static com.petrolink.mbe.rulesflow.RuleFlow.VALUE_CTX_VARIABLE;
import static com.petrolink.mbe.rulesflow.RuleFlow.WELL_CTX_VARIABLE;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.alertstatus.Alert;
import com.petrolink.mbe.alertstatus.processors.PetrolinkProcessor;
import com.petrolink.mbe.model.channel.ComplexValue;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.model.message.AlertCepEvent;
import com.petrolink.mbe.rulesflow.EventCepEventContent;
import com.petrolink.mbe.rulesflow.EventDataPointContent;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.rulesflow.variables.ChannelAlias;
import com.petrolink.mbe.rulesflow.variables.ChannelAliasValue;
import com.petrolink.mbe.rulesflow.variables.Variable;
import com.petrolink.mbe.rulesflow.variables.VariableDatum;
import com.petrolink.mbe.util.JSONHelper;
import com.smartnow.alertstatus.AlertTemplate;
import com.smartnow.alertstatus.AlertsFactory;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.util.MVELUtil;

import freemarker.core.InvalidReferenceException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Petrolink Dynamic Alert Template for the Alerts generation
 * @author paul
 *
 */
// TODO Add more logging
public class DynamicAlertTemplate extends AlertTemplateImpl {
	private static final long serialVersionUID = -8352960688121301747L;
	/**
	 * Evaluation Context constant for the Details 
	 */
	public static final int EVALUATION_CONTEXT = 1;
	/**
	 * Scripted constant
	 */
	public static final int SCRIPTED = 2;
	/**
	 * Scripted constant
	 */
	public static final int TEMPLATED = 3;
	/**
	 * Static constant
	 */
	public static final int STATIC = 4;
	
	/**
	 * Custom hashmap coming from smartnow library
	 */
	public static final String SMARTNOW_EVALUATION_VARIABLE = "vars";
	
	private int detailsType;
	private int descriptionType;
	private Object details;
	private Object description;
	private Object metadata;
	
	private static final Logger logger = LoggerFactory.getLogger(DynamicAlertTemplate.class);
	
	
	/**
	 * @param context
	 * @param ev
	 * @param rule
	 * @param processor 
	 * @return the Actual Alert Template instance based on the Dynamic Template
	 */
	public AlertTemplate getAlertTemplate(Map<String, Object> context, Event ev, RuleFlow rule, PetrolinkProcessor processor) {		
		AlertTemplateImpl template = (AlertTemplateImpl) AlertsFactory.getAlertTemplate();
		Object dpIndex = rule.getEventPoint(ev).getIndex();
		
		
		// TODO Use processor to obtain the RigState, BitDepth and HoleDepth values
		// Eg.
		
		if (dpIndex != null) {
			DataPoint supplementDp = processor.getChannelCacheValue("bitDepth", dpIndex);
			if (supplementDp != null)
				template.setBitDepth(supplementDp.getValueAsNumber().doubleValue());
			
			supplementDp = processor.getChannelCacheValue("holeDepth", dpIndex);
			if (supplementDp != null)
				template.setHoleDepth(supplementDp.getValueAsNumber().doubleValue());				
			
			supplementDp = processor.getChannelCacheValue("rigState", dpIndex);
			if (supplementDp != null)
				template.setRigState(supplementDp.getValueAsNumber().intValue());
	
			template.setCreatedIndex(dpIndex.toString());
		}
		
		template.setClassId(this.getClassId());
		template.setName(this.getName());
		template.setClassification(this.getClassification());
		template.setDomain(this.getDomain());
		template.setPriority(this.getPriority());
		template.setSeverity(this.getSeverity());
		template.setDetailsContentType(this.detailsContentType);
		template.setWellId(rule.getWellId().toString());
		template.setCreatedByRuleId(rule.getUniqueId());
		
		Map<String, Object> alertCtx = prepareAlertContext(context, ev, rule, processor);
		
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.putAll(alertCtx);
		ctx.put("alertContext", alertCtx);
		ctx.put("rule", rule);			
		ctx.put("processor", processor);
		ctx.put("template", template);
		ctx.put(SMARTNOW_EVALUATION_VARIABLE, context.get(SMARTNOW_EVALUATION_VARIABLE));
		
		
		if (getDetailsType() == EVALUATION_CONTEXT) {
			assert ev.getContentType() == Event.TYPE_OBJECT;

			// Copying non-generic context elements
			JSONObject details = new JSONObject(alertCtx);
			
			template.setDetails(details);
		} else if (getDetailsType() == SCRIPTED) {	
			
			Object origDetails = template.getDetails();
			Object mvelReturn =	MVELUtil.evaluate((Serializable) details, ctx, logger);
			Object currentDetails = template.getDetails();
			
			if (currentDetails == origDetails) {
				Object evaluatedResult = evaluateScriptedResult(ctx.get("details"), mvelReturn,Map.class);
				@SuppressWarnings("unchecked")
				Map<String, Object> detailsMap = (Map<String, Object>)evaluatedResult;
				template.setDetails(new JSONObject(detailsMap));
			}
						
		} else if (getDetailsType() == TEMPLATED) {
			try {
				StringWriter writer = new StringWriter();
				((Template) details).process(ctx, writer);
				JSONObject object = new JSONObject(writer.toString());
				template.setDetails(object);
			} catch (TemplateException | IOException e) {
				logger.error("Exception while executing description template",e);
			}
		} else {
			template.setDetails(this.getDetails());
		}
		
		
		if (getDescriptionType() == SCRIPTED) {
			String originalDesc = template.getDescription();
			Object mvelReturn = MVELUtil.evaluate((Serializable) description, ctx, logger);
			String currentDesc = template.getDescription();

			//This check whether in the script, the actual script already call setProperty directly
			//if yes, there is no need to check other way to setup
			if (StringUtils.equals(originalDesc, currentDesc)) {
				String templateDesc = evaluateScriptedResult(ctx.get("description"), mvelReturn,String.class);
				template.setDescription(templateDesc);
			}
		} else if (getDescriptionType() == TEMPLATED) {
			try {
				StringWriter writer = new StringWriter();
				((Template) description).process(ctx, writer);
				template.setDescription(writer.toString());
			} catch (InvalidReferenceException e) {
				// Handle this specially so we're not vomiting huge error messages into the log file
				logger.error("Missing reference while executing description template: {}", e.getBlamedExpressionString());
			} catch (TemplateException | IOException e) {
				logger.error("Exception while executing description template",e);
			}
		} else {
			template.setDescription(this.getDescription());
		}

		JSONObject alertMetadata = new JSONObject();
		// Rule properties contains metadata such as wellName and rigName
		JSONHelper.update(alertMetadata, rule.getProperties());

		Serializable script = (Serializable) metadata;
		if(script != null) {			
			MVELUtil.evaluate(script, ctx, logger);
		}

		template.setMetadata(alertMetadata);
		
		return template;
	}

	/**
	 * Evaluate scripted result, which prioritize context variable if any and type is correct
	 * @param contextVariable Object coming from context's variable
	 * @param returnValue MVEL expression result
	 * @param resultClass Object which it should cast to
	 * @return
	 */
	private static <T> T evaluateScriptedResult(Object contextVariable, Object returnValue, Class<T> resultClass) {
		T templateUpdateValue;
		if ((contextVariable != null) && (resultClass.isInstance(contextVariable))) {
			//Updating through new variable
			templateUpdateValue = resultClass.cast(contextVariable);
		} else if ((returnValue != null) && (resultClass.isInstance(returnValue))) {
			//Updating through return value
			templateUpdateValue = resultClass.cast(returnValue);
		} else {
			//default value
			templateUpdateValue = null;
		}
		return templateUpdateValue;
	}

	/**
	 * @param context
	 * @param ev
	 * @param rule
	 * @param processor 
	 * @return the Rule Context
	 */
	public Map<String, Object> prepareAlertContext(Map<String, Object> context, Event ev, RuleFlow rule, PetrolinkProcessor processor) {
		String channelIdString = ev.getProperties().getProperty(CHANNEL_CTX_VARIABLE);
		
		VariableDatum vd = rule.getEventPoint(ev);
		
		// Copying non-generic context elements
		Map<String, Object> ctx = new HashMap<String, Object>();
		
		ctx.put(CHANNEL_CTX_VARIABLE, channelIdString);
		ctx.put(WELL_CTX_VARIABLE, ev.getProperties().getProperty(WELL_CTX_VARIABLE));
		ctx.put(INDEX_CTX_VARIABLE, vd.getIndex());
		ctx.put(VALUE_CTX_VARIABLE, vd.getValue());
		ctx.put(SMARTNOW_EVALUATION_VARIABLE, context.get(SMARTNOW_EVALUATION_VARIABLE)); 
		ctx.put(RuleFlow.EVALUATED_TIMESTAMP,  context.get(RuleFlow.EVALUATED_TIMESTAMP));
		ctx.put(RuleFlow.ALERT_STATUS_CTX_VARIABLE,  context.get(RuleFlow.ALERT_STATUS_CTX_VARIABLE));
		
		for (Variable v : rule.getConditionVariables().values()) {
			if (v instanceof ChannelAlias) {
				ChannelAlias ca = (ChannelAlias) v;
				ChannelAliasValue cav = (ChannelAliasValue) ca.getValue();
				
				if (cav != null) {
					Map<String, Object> dataPoint = getDataPointAsMap(cav, ca);
					ctx.put(Variable.RESOLVABLE_VARIABLE_PREFIX + v.getAlias(), dataPoint);
					ctx.put(v.getAlias(), cav.getValue());						
				} 
			} else {
				ctx.put(v.getAlias(), v.getValue());
			}
		}

		for (Variable v : rule.getFilteringVariables().values()) {
			if (v instanceof ChannelAlias) {
				ChannelAlias ca = (ChannelAlias) v;
				ChannelAliasValue cav = (ChannelAliasValue) ca.getValue();
				
				if (cav != null) {
					Map<String, Object> dataPoint = getDataPointAsMap(cav, ca);
					ctx.put(Variable.RESOLVABLE_VARIABLE_PREFIX + v.getAlias(), dataPoint);
					ctx.put(v.getAlias(), cav.getValue());						
				} 
			} else {
				ctx.put(v.getAlias(), v.getValue());
			}
		}
		
		// Adding Processor channels except BitDepth, HoleDepth and RigState
		for (Variable v : processor.getAlertChannels().values()) {
			if (ChannelAlias.specialAliases.contains(v.getAlias()))
				continue;
			
			// Filtering and condition variables take priority over processing-only variables, as they will have
			// more data available like previous value
			if (ctx.containsKey(v.getAlias()))
				continue;
			
			if (v instanceof ChannelAlias) {
				ChannelAlias ca = (ChannelAlias) v;
				ChannelAliasValue cav = (ChannelAliasValue) ca.getValue();
				
				if (cav != null) {
					Map<String, Object> dataPoint = getDataPointAsMap(cav, ca);
					ctx.put(Variable.RESOLVABLE_VARIABLE_PREFIX + v.getAlias(), dataPoint);
					ctx.put(v.getAlias(), cav.getValue());						
				} 
			} else {
				ctx.put(v.getAlias(), v.getValue());
			}
		}
		
		return ctx;
	}
	
	private static Map<String, Object> getDataPointAsMap(ChannelAliasValue cav, ChannelAlias ca) {
		HashMap<String, Object> dataPoint = new HashMap<>();
		String valueName = ca.getValueName(cav.getValue());
		dataPoint.put("index", cav.getIndex());
		dataPoint.put("value", toMapIfComplexValue(cav.getValue()));
		dataPoint.put("prevIndex", cav.getPrevIndex());
		dataPoint.put("prevValue", toMapIfComplexValue(cav.getPrevValue()));
		dataPoint.put("hasPrevValue", cav.hasPrevValue());
		//The Units should be empty strings
		//1. Some unit is actually called "unitless"  as they have no dimension
		//2. If using string "unknown", we may have translation issue for future
		dataPoint.put("valueUnit", ca.getValueUnit() == null ? "" : ca.getValueUnit());
		dataPoint.put("indexUnit", ca.getIndexUnit() == null ? "" : ca.getIndexUnit());
		dataPoint.put("valueName", valueName != null ? valueName : "");
		return dataPoint;
	}
	
	private static Object toMapIfComplexValue(Object value) {
		return value instanceof ComplexValue ? ((ComplexValue) value).toMap() : value;
	}

	/**
	 * @return the detailsType
	 */
	public int getDetailsType() {
		return detailsType;
	}


	/**
	 * @param detailsType the detailsType to set
	 */
	public void setDetailsType(int detailsType) {
		this.detailsType = detailsType;
	}


	/**
	 * @return the descriptionType
	 */
	public int getDescriptionType() {
		return descriptionType;
	}

	/**
	 * @param descriptionType the descriptionType to set
	 */
	public void setDescriptionType(int descriptionType) {
		this.descriptionType = descriptionType;
	}
	
	/**
	 * Dynamic Alert template loader from XML
	 * @param classId
	 * @param className
	 * @param template
	 * @param cfg 
	 * @throws EngineException
	 */
	public void load(String classId, String className, Element template, Configuration cfg) throws EngineException {
		this.setClassId(classId);
		this.setName(className);
		this.setClassification(template.getChildText("Classification",template.getNamespace()));
		this.setDomain(template.getChildText("Domain",template.getNamespace()));
		this.setPriority(Integer.parseInt(template.getChildText("Priority",template.getNamespace())));
		this.setSeverity(Integer.parseInt(template.getChildText("Severity",template.getNamespace())));

		if (template.getChild("Details",template.getNamespace()) != null) {
			Element details = template.getChild("Details",template.getNamespace());
			switch (details.getAttributeValue("contentType")) {
			case Alert.DETAILS_JSON:
			case Alert.DETAILS_LIST:
			case Alert.DETAILS_STRING:
				break;
			default:
				throw new EngineException("Unexpected Details Content Type");
			}
			this.setDetailsContentType(details.getAttributeValue("contentType"));
			
			String source = details.getAttributeValue("source");
			switch (source.toLowerCase()) {
			case "context":
				this.setDetailsType(EVALUATION_CONTEXT);
				break;
			case "scripted":
				this.setDetailsType(SCRIPTED);
				this.details = MVELUtil.compileScript(details.getText(), JSONObject.class, JSONArray.class);
				break;
			case "templated":
				this.setDetailsType(TEMPLATED);
				try {
					Template t = new Template("name", new StringReader(details.getText()),
							cfg);
					this.setDetails(t);
				} catch (IOException e) {
					logger.error("Unable to load description template");
				}
				break;
			case "static":
				this.setDetailsType(STATIC);
				this.setDetails(details.getText());
				break;
			default:
				throw new EngineException("unknown details source type: " + source);
			}
		}
	
		Element description = template.getChild("Description", template.getNamespace());
		if (description != null) {
			String source = description.getAttributeValue("source", "static");
			if (source.equalsIgnoreCase("scripted")) {
				this.setDescriptionType(SCRIPTED);
				this.description = MVELUtil.compileScript(description.getText());
			} else if (source.equalsIgnoreCase("templated")) {
				this.setDescriptionType(TEMPLATED);
				try {
					this.description = new Template("name", new StringReader(description.getText()), cfg);
				} catch (IOException e) {
					throw new EngineException("Unable to load description template", e);
				}
			} else {
				this.setDescriptionType(STATIC);
				this.setDescription(description.getText());
			}
		}	
		
		Element metadata = template.getChild("Metadata", template.getNamespace());
		if (metadata != null) {
			this.metadata = MVELUtil.compileScript(metadata.getText(), JSONObject.class, JSONArray.class);
		}		
	}
}
