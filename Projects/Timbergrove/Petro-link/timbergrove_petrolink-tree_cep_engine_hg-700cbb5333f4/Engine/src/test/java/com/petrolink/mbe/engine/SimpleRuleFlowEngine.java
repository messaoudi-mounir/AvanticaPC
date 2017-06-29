package com.petrolink.mbe.engine;

import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import com.petrolink.mbe.cache.ChannelCache;
import com.petrolink.mbe.cache.WellCache;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.rulesflow.variables.ChannelAlias;
import com.petrolink.mbe.rulesflow.variables.Variable;

/**
 * Simple RuleFlow Engine for Testing
 * @author aristo
 *
 */
public class SimpleRuleFlowEngine {
	/**
	 * A rule flow rule
	 */
	public RuleFlow rule;
	/**
	 * A well cache
	 */
	public WellCache wellCache;
	
	/**
	 * Update Channel
	 * @param channelName = the name of the channel
	 * @param dp New Data point
	 * @throws IllegalArgumentException If channel is not a ChannelAlias
	 */
	public void updateChannel(String channelName, DataPoint dp) throws IllegalArgumentException {
		RuleFlow currentRule = rule;
		
		Variable singleVariable = currentRule.getConditionVariables().get(channelName);
		UUID channelId = null;
		if (singleVariable instanceof ChannelAlias) {
			ChannelAlias channel = (ChannelAlias)singleVariable;
			channelId = channel.getUuid();
			
		} else {
			throw new IllegalArgumentException("Can't update non ChannelAlias");
		}
		
		
		updateChannel(channelId,dp);
			
	}
	
	/**
	 * Update channel based on its UUID
	 * @param channelId
	 * @param dp
	 */
	public void updateChannel(UUID channelId, DataPoint dp){
		assertNotNull(channelId);
		WellCache currentWcache = wellCache;
		RuleFlow currentRule = rule;
		
		//Try Update Channel cache
		ChannelCache channelCache = currentWcache.getChannel(channelId);
		if (channelCache != null){
			channelCache.addDataPoint(dp);
		}
		
		//Update the RuleFlow
		currentRule.updateLocalCache(channelId, dp);
	}
}

