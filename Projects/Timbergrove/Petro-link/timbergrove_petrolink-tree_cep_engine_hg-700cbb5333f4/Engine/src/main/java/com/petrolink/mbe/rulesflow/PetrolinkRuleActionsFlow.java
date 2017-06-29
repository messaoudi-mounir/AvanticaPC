package com.petrolink.mbe.rulesflow;

import java.util.UUID;

import com.smartnow.engine.event.Event;
import com.smartnow.engine.util.EngineContext;
import com.smartnow.engine.xflow.EmbeddedActionSequenceFlow;

/**
 * Actions Flow for the Rule Actions
 * @author paul
 *
 */
public class PetrolinkRuleActionsFlow extends EmbeddedActionSequenceFlow {
	final RuleFlow rule;
	
	/**
	 * @param rule
	 */
	public PetrolinkRuleActionsFlow(RuleFlow rule) {
		this.rule = rule;
	}

	@Override
	public EngineContext prepareContext(Event ev, UUID sharedObjects) {
		EngineContext context = super.prepareContext(ev, sharedObjects);
		context.put("result", ev.getProperty("result"));
		return context;
	}

	/**
	 * @return the Rule
	 */
	public RuleFlow getRule() {
		return rule;
	}

}
