package com.petrolink.mbe.rulesflow;

import java.util.List;
import java.util.UUID;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnow.alertstatus.flow.AlertActionsFlow;
import com.smartnow.engine.Engine;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.xflow.Flow;

/**
 * @author paul
 *
 */
public class PetrolinkStandAloneAlertFlow extends AlertActionsFlow {
	private Logger logger = LoggerFactory.getLogger(PetrolinkStandAloneAlertFlow.class);
	
	private RuleFlow parent;

	/* (non-Javadoc)
	 * @see com.smartnow.engine.xflow.ActionSequenceFlow#cleanup()
	 */
	@Override
	public void cleanup() {
		this.parent = null;
		super.cleanup();
	}

	/**
	 * @return the Parent Rule
	 */
	public RuleFlow getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see com.smartnow.engine.xflow.ActionSequenceFlow#load(org.jdom2.Element)
	 */
	@Override
	public List<Flow> load(Element cfg) throws EngineException {
		parent = (RuleFlow) Engine.getInstance().getFlow(cfg.getAttributeValue("ruleFlow"));

		List<Flow> flows = super.load(cfg);
		
		for (Flow flow : flows) {
			parent.addDependentFlow(flow.getUniqueId());			
		}
		return flows;
	}
	
	@Override
	public int execute(Event ev, UUID sharedObjects) throws EngineException {
		try {
			return super.execute(ev, sharedObjects);
		} catch (EngineException eng) {
			logger.error("Error executing StandAloneFlow {}", getUniqueId(), eng);
			throw eng;
		}
		
	}


}
