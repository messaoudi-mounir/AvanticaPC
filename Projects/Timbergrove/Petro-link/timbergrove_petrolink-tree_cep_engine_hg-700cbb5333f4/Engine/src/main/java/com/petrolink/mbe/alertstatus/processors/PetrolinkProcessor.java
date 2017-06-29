package com.petrolink.mbe.alertstatus.processors;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;

import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.rulesflow.variables.ChannelAlias;
import com.petrolink.mbe.rulesflow.variables.GlobalBufferedChannelAlias;
import com.petrolink.mbe.rulesflow.variables.VariableFactory;
import com.smartnow.alertstatus.processors.AlertProcessor;
import com.smartnow.engine.Resource;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Support other specific implementations of Alert Processors
 * @author paul
 *
 */
public abstract class PetrolinkProcessor extends AlertProcessor {
	private Map<String, ChannelAlias> alertChannels = new HashMap<>();

	@Override
	public void load(Element e, Resource parent) {
		super.load(e, parent);
		RuleFlow rule = (RuleFlow) parent;
		
		for (Element c : e.getChildren("Channel", e.getNamespace())) {
			ChannelAlias ca;
			try {
				ca = (ChannelAlias) VariableFactory.getChannel(rule, c, "globalbuffered");
				alertChannels.put(ca.getAlias(),ca);
				
				rule.getWellDefinition().registerChannel(ca.getUuid(), ca.getAlias());
			} catch (EngineException e1) {
				logger.error("Unable to create channel alias", e1);
			}

		}
	}	
	
	/**
	 * @param alias
	 * @return the Channel Cache wit the given alias
	 */
	public ChannelAlias getChannelCache(String alias) {
		return alertChannels.get(alias);
	}
	
	/**
	 * Helper to obtain the Last Known Value of a Alert Channel based on the channel type and Index (if applicable)
	 * @param alias
	 * @param index
	 * @return the applicable LKV
	 */
	public DataPoint getChannelCacheValue(String alias, Object index) {
		ChannelAlias alertCache = this.getChannelCache(alias);
		if (alertCache == null)
			return null;
		
		if (alertCache instanceof GlobalBufferedChannelAlias) {
			DataPoint supplementDp = alertCache.getCache().getLastDataPoint(index);
			if (supplementDp != null)
				return supplementDp;
		} else {
			DataPoint supplementDp = alertCache.getCache().getLastDataPoint();
			if (supplementDp != null)
				return supplementDp;
		} 
		
		return null;
	}

	/**
	 * @return the alertChannels
	 */
	public Map<String, ChannelAlias> getAlertChannels() {
		return alertChannels;
	}
}
