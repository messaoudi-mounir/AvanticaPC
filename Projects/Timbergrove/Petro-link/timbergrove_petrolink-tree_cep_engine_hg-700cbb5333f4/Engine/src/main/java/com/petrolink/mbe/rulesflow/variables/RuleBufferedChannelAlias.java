package com.petrolink.mbe.rulesflow.variables;

import org.jdom2.Element;

import com.petrolink.mbe.rulesflow.RuleFlow;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Buffered Channel Alias private per Rule
 * @author paul
 *
 */
public class RuleBufferedChannelAlias extends BufferedChannelAlias {
	public void load(RuleFlow rule, Element e) throws EngineException {
		super.load(rule, e);
		
		cache = rule.getEvaluationStrategy().createLocalBufferedCache(uuid);		
		cache.setOwner(rule.getUniqueId());
		
		String maxSizeString = e.getAttributeValue("maxSize");
		if (maxSizeString != null) {
			getCache().setMaxSize(Long.parseLong(maxSizeString));
		}
	}

	/**
	 * Clear the Channel Alias associated channel buffer
	 */
	public void clear() {
		cache.clear();
	}
}
