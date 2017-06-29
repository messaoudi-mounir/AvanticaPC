package com.petrolink.mbe.directories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.rulesflow.variables.ChannelAlias;

/**
 * The Rules Directory is associated to each Well and contains the references to the well active rules
 * by rule firing channels and updateing only channels
 * @author paul
 *
 */
public class RulesDirectory {
	private static final Object PRESENT = new Object();
	private final ConcurrentHashMap<RuleFlow, Object> allRules = new ConcurrentHashMap<RuleFlow, Object>();
	private final ConcurrentHashMap<UUID, HashMap<String, RuleFlow>> rulesByChannel = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<UUID, HashMap<String, RuleFlow>> rulesUpdatedByChannel = new ConcurrentHashMap<>();
	
	/**
	 * @return true if the directory has no rules in it
	 */
	public boolean isEmpty() {
		return allRules.isEmpty();
	}
	
	/**
	 * Get the rules that need to be executed due to new data on the specified channel
	 * @param channelId the Channel UUID
	 * @return the Rules firing for a given channel UUID
	 */
	public Collection<RuleFlow> getRulesExecuted(UUID channelId) {
		HashMap<String, RuleFlow> rulesMap = rulesByChannel.get(channelId);
		return rulesMap != null ? rulesMap.values() : null;
	}

	/**
	 * Get the rules that need to have a local cache updated, but not be executed, due to new data on the specified channel
	 * @param channelId
	 * @return the Rules related to a given channel (firing or not)
	 */
	public Collection<RuleFlow> getRulesUpdated(UUID channelId) {
		HashMap<String, RuleFlow> rulesMap = rulesUpdatedByChannel.get(channelId);
		return rulesMap != null ? rulesMap.values() : null;
	}

	/**
	 * Register the Rule to the Rule directory associated to the Well. 
	 * @param rule
	 */
	public void registerRule(RuleFlow rule) {
		allRules.put(rule, PRESENT);
		
		for (ChannelAlias ca : rule.getDependencies().values()) {
			// Changes to non-reference channels in the condition scope must cause an execution
			boolean isExecuted = false;
			if (ca.getScope() == ChannelAlias.GLOBAL_SCOPE || ca.getScope() == ChannelAlias.CONDITION_SCOPE) {
				HashMap<String, RuleFlow> rules = getRulesMap(rulesByChannel, ca.getUuid());
				rules.put(rule.getUniqueId(), rule);
				isExecuted = true;
			} 
			
			// Register rule to be updated if this channel is locally cached and not being executed due to that channel
			// This is because when a rule is executed the local cache is only updated is filtering passes
			if (!ca.isCacheGlobal() && !isExecuted) {
				HashMap<String, RuleFlow> rules = getRulesMap(rulesUpdatedByChannel, ca.getUuid());
				rules.put(rule.getUniqueId(), rule);				
			}
		}
		
		// Cleaning up previously registered dependencies no longer applicable
		for (Entry<UUID, HashMap<String, RuleFlow>> e : rulesByChannel.entrySet()) {
			if (rule.getDependencies().containsKey(e.getKey())) {
				ChannelAlias ca = rule.getDependencies().get(e.getKey());
				if ((ca.getScope() != ChannelAlias.GLOBAL_SCOPE) && (ca.getScope() != ChannelAlias.CONDITION_SCOPE)) {
					HashMap<String, RuleFlow> rules = getRulesMap(rulesByChannel, ca.getUuid());
					rules.remove(rule.getUniqueId());
				} 								
			} else {
				// Removing no longer dependent rules
				HashMap<String, RuleFlow> rules = getRulesMap(rulesByChannel, e.getKey());
				rules.remove(rule.getUniqueId());
				
				// Removing Updatable Rules
				rules = getRulesMap(rulesUpdatedByChannel, e.getKey());
				rules.remove(rule.getUniqueId());				
			}
		}
	}

	/**
	 * Removes the rule from the directory
	 * @param rule
	 */
	public void removeRule(RuleFlow rule) {
		for (ChannelAlias ca : rule.getDependencies().values()) {
			HashMap<String, RuleFlow> rules = getRulesMap(rulesByChannel, ca.getUuid());
			rules.remove(rule.getUniqueId());
			
			if (rules.isEmpty())
				rulesByChannel.remove(ca.getUuid());
			
			rules = getRulesMap(rulesUpdatedByChannel, ca.getUuid());
			rules.remove(rule.getUniqueId());
			
			if (rules.isEmpty())
				rulesUpdatedByChannel.remove(ca.getUuid());
		}
		
		allRules.remove(rule);
	}

	private HashMap<String, RuleFlow> getRulesMap(ConcurrentHashMap<UUID, HashMap<String,RuleFlow>> map, UUID uuid) {
		if (map.containsKey(uuid)) {
			return map.get(uuid);
		} else {
			HashMap<String, RuleFlow> rulesMap = new HashMap<>();
			map.put(uuid, rulesMap);
			return rulesMap;
		}
	}	
}
