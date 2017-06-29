package com.petrolink.mbe.alertstatus.processors;

import static com.smartnow.engine.impl.BaseFlowImpl.CONTEXT_EVENT;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.alertstatus.Alert;
import com.petrolink.mbe.alertstatus.impl.AlertsDAO;
import com.petrolink.mbe.alertstatus.impl.AutoAlertDismissal;
import com.petrolink.mbe.alertstatus.impl.DynamicAlertTemplate;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.smartnow.alertstatus.AlertTemplate;
import com.smartnow.alertstatus.AlertsFactory;
import com.smartnow.engine.Resource;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.util.MVELUtil;

/**
 * Petrolink Auto Clear Alert Processor
 * @author paul
 *
 */
public class PetrolinkAutoClear extends PetrolinkProcessor {
	private static Logger logger = LoggerFactory.getLogger(PetrolinkAutoClear.class);
	private Serializable deduplicationScript = null;
	private boolean defaultCorrelation = true;
	

	@Override
	public void processAlert(String classId, boolean triggerCondition, AlertTemplate template, Map<String, Object> context) {
		logger.trace("processAlert alert {} , triggerCondition = {} ", classId, triggerCondition);
		com.petrolink.mbe.alertstatus.impl.AlertsDAO dao = (com.petrolink.mbe.alertstatus.impl.AlertsDAO) AlertsFactory.getAlertsDAO();
		RuleFlow rule = (RuleFlow) context.get("rule");		
		Event ev = (Event) context.get(CONTEXT_EVENT);

		if (defaultCorrelation) {
			Alert alert = (Alert) dao.getActiveAlertByWellClassId(rule.getWellId().toString(), classId);
			processSingleAlert(rule, dao, alert, ev, triggerCondition, template, context);
		} else {
			processDecuplicationScript(dao, triggerCondition, template, context);			
		}
	}
	
	private void processDecuplicationScript(AlertsDAO dao, boolean triggerCondition, AlertTemplate template,
			Map<String, Object> context) {
		// Creating a copy of the 
		Map<String, Object> ctx = new HashMap<String, Object>();
		ctx.putAll(context);
		
		ctx.put("template", template);
		ctx.put("alertsDAO", dao);
		
		MVELUtil.evaluate(deduplicationScript, ctx);
	}

	private void processSingleAlert(RuleFlow rule,
			com.petrolink.mbe.alertstatus.impl.AlertsDAO dao, 
			Alert alert, 
			Event ev, 
			boolean triggerCondition, 
			AlertTemplate template, 
			Map<String, Object> context) {
		
		if (alert != null) {
			if (!triggerCondition) {
				logger.trace("Inactivating alert instance with UUID {}", alert.getUuid());				
				dao.changeAlertStatus(alert, Alert.INACTIVE);
			} else {
				// If Alert is Active in the Alert Status Table and the condition is true
				logger.trace("Deduplicating alert with classId {} and name {}", alert.getClassId(), alert.getName());
				DynamicAlertTemplate _template = (DynamicAlertTemplate) template;
				Alert _alert = (Alert) AlertsFactory.getAlert(_template.getAlertTemplate(context, ev, rule, this));
				
				if (deduplicationScript != null) {
					// Creating a copy of the 
					Map<String, Object> ctx = new HashMap<String, Object>();
					ctx.putAll(context);
					
					if (defaultCorrelation) {
						alert.deduplicate(_alert);
						alert.setUpdatedByRule(rule.getUniqueId());						
					}
					
					ctx.put("alert", alert);
					ctx.put("new", _alert);
					
					MVELUtil.evaluate(deduplicationScript, ctx);
				} else {
					alert.deduplicate(_alert);
					alert.setUpdatedByRule(rule.getUniqueId());					
				}
				
				dao.updateAlert(alert);
			}
		} else {
			if (triggerCondition) {
				DynamicAlertTemplate _template = (DynamicAlertTemplate) template;
				alert = (Alert) AlertsFactory.getAlert(_template.getAlertTemplate(context, ev, rule, this));
				logger.trace("Creating new alert instance with classId {}, name {} and UUID {}", alert.getClassId(), alert.getName(), alert.getUuid());
				dao.createAlert(alert);
			} 
		}
	}

	@Override
	public void load(Element e, Resource parent) {
		super.load(e, parent);
		
		if (e.getChild("DeduplicationScript") != null) {
			deduplicationScript = MVELUtil.compileScript(e.getChildText("DeduplicationScript"), 
					AlertsFactory.class, 
					DynamicAlertTemplate.class);
			
			if (e.getChild("DeduplicationScript").getAttribute("executeDefault") != null) {
				defaultCorrelation = e.getChild("DeduplicationScript").getAttributeValue("executeDefault").toLowerCase().equals("true");
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.smartnow.engine.Resource#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return logger;
	};
}
