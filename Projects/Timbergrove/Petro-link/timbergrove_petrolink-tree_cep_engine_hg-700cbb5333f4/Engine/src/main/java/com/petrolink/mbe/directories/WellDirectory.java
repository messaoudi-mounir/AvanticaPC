package com.petrolink.mbe.directories;

import java.util.HashMap;
import java.util.UUID;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.cache.CacheFactory;
import com.petrolink.mbe.cache.GlobalCache;
import com.petrolink.mbe.cache.WellCache;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.triggers.ChannelDataAppendedTrigger;
import com.smartnow.engine.Engine;
import com.smartnow.engine.Engine.EngineState;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.executiongroups.ExecutionGroup;
import com.smartnow.engine.executiongroups.ExecutionGroupFactory;
import com.smartnow.engine.executiongroups.NativeExecutionGroup;
import com.smartnow.engine.services.Service;
import com.smartnow.rabbitmq.util.RMQConnectionSettings;

/**
 * Well Directory
 * Used to keep track of running wells and associated resources
 * @author paul
 *
 */
public class WellDirectory extends Service {
	/**
	 * Well Definition Object
	 * @author paul
	 *
	 */
	@SuppressWarnings("javadoc")
	public static class WellDefinition implements Comparable<WellDefinition> {
		/**
		 * The Well UUID
		 */
		private UUID uuid;
		/**
		 * The Well associated Trigger
		 */
		private ChannelDataAppendedTrigger trigger;
		/**
		 * The Well Associated Rules Execution Group
		 */
		private ExecutionGroup rulesExecutionGroup;
		/**
		 * The Well Associated Alert Actions Execution Group
		 */
		private ExecutionGroup alertActionsExecutionGroup;
		/**
		 * The known well associated Channels
		 */
		private HashMap<UUID, String> channels = new HashMap<>();
		
		/**
		 * Well associated rules
		 */
		private RulesDirectory rules = new RulesDirectory();
		
		public ExecutionGroup getRulesExecutionGroup() {
			return rulesExecutionGroup;
		}
		
		public ExecutionGroup getAlertActionsExecutionGroup() {
			return alertActionsExecutionGroup;
		}
		
		public RulesDirectory getRulesDirectory() {
			return rules;
		}
		
		@Override
		public int compareTo(WellDefinition o) {
			return uuid.compareTo(o.uuid);
		}
		
		public void registerChannel(UUID channel, String alias) {
			channels.put(channel, alias);
		}
		
		public boolean isChannelRegistered(UUID channel) {
			return channels.containsKey(channel);
		}
	}
	
	// well and rule registration changes are synchronized on wells map
	private HashMap<UUID, WellDefinition> wells = new HashMap<UUID, WellDefinition>();
	private RMQConnectionSettings cdaTriggerConnectionSettings;
	private int defaultRuleExecutionGroupWorkers = -1;
	private int defaultAlertExecutionGroupWorkers = -1;
	private String defaultRuleExecutionGroup;
	private String defaultAlertExecutionGroup;
	private Element clockChannelSettingsElement;
	
	private static Logger logger = LoggerFactory.getLogger(WellDirectory.class);

	/**
	 * Removes a well from the directory and destroys the corresponding
	 * execution groups and other related structures.
	 * 
	 * @param well Well UUID
	 */
	public void removeWell(UUID well) {
		synchronized (wells) {
			// Stop Execution Groups
			WellDefinition wellDef = wells.get(well);
			
			wellDef.rulesExecutionGroup.stop();
			wellDef.alertActionsExecutionGroup.stop();
			wellDef.trigger.setDaemon(false);
			wellDef.trigger.setShouldDeleteQueue(true);
			
			String triggerId = wellDef.trigger.getTriggerId();
			
			// removeTrigger will call trigger.stop()
			Engine engine = Engine.getInstance();
			engine.removeTriggerThread(triggerId);
			engine.removeTrigger(triggerId);
			
			GlobalCache cache = CacheFactory.getInstance().getLKVCache();
			cache.removeWell(wellDef.uuid);
			wells.remove(well);
		}
	}

	/**
	 * Get a WellDefinition of the specified ID.
	 * @param id A well ID
	 * @return A matching well definition, or null if no match.
	 */
	public WellDefinition getWell(UUID id) {
		synchronized (wells) {
			return wells.get(id);
		}
	}
	
	/**
	 * Get a WellDefinition of the specified ID, or create one if it does not exist.
	 * @param wellId A well ID
	 * @return A matching new or existing well definition.
	 */
	public WellDefinition getOrCreateWell(UUID wellId) {
		synchronized (wells) {
			if (wells.containsKey(wellId)) {
				return wells.get(wellId);
	 		} else {
	 			logger.info("Creating new well {} on demand", wellId);
	 			return createWellDefinition(wellId,
					                        cdaTriggerConnectionSettings.getConcurrentListeners(),
					                        defaultRuleExecutionGroupWorkers,
					                        defaultAlertExecutionGroupWorkers);
	 		}
		}
	}

	/**
	 * @param well
	 * @return The Well Rules Execution Group
	 */
	public ExecutionGroup getWellRulesExecutionGroup(UUID well) {
		synchronized (wells) {
			return wells.get(well).rulesExecutionGroup;
		}
	}

	/**
	 * @param well
	 * @return The Well Alert Actions Execution Group
	 */
	public ExecutionGroup getWellAlertActionsExecutionGroup(UUID well) {
		synchronized (wells) {
			return wells.get(well).alertActionsExecutionGroup;
		}
	}

	@Override
	public void startService() throws EngineException {
	}

	@Override
	public void stopService() {
		synchronized (wells) {
			for (WellDefinition def : wells.values()) {
				def.rulesExecutionGroup.stop();
				def.alertActionsExecutionGroup.stop();
				def.trigger.stop();
			}
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	/* (non-Javadoc)
	 * @see com.smartnow.engine.services.Service#load(org.jdom2.Element)
	 */
	@Override
	public void load(Element e) throws EngineException {
		super.load(e);
		
		Element wellDef = e.getChild("DefaultWellDefinition", e.getNamespace());
		if (wellDef != null) {
			loadDefaultWellDefinition(wellDef);
		}
		
		Element clockChannelSettings = e.getChild("ClockChannelSettings", e.getNamespace());
		if (clockChannelSettings != null) {
			clockChannelSettingsElement = clockChannelSettings.clone();
		}
	}
	
	/**
	 * Register a rule with its directory
	 * @param rule
	 */
	public void registerRule(RuleFlow rule) {
		synchronized (wells) {
			getWell(rule.getWellId()).rules.registerRule(rule);
		}
	}
	
	/**
	 * Clear a rule by removing it from its directory. Also deletes a well if it no longer has any rules
	 * @param rule
	 */
	public void unregisterRule(RuleFlow rule) {
		synchronized (wells) {
			logger.debug("Clearing rule {}", rule.getUniqueId());
			WellDefinition def = getWell(rule.getWellId());
			def.rules.removeRule(rule);
			if (def.rules.isEmpty()) {
				logger.info("Deleting well {} which no longer has any rules", def.uuid);
				removeWell(def.uuid);
			}
		}
	}

	/**
	 * /** Adds a well to the directory and creates the corresponding execution
	 * groups and other related structures.
	 * @param wellId 
	 * @param channelDataAppendedListeners 
	 * @param ruleExecutionGroupWorkers 
	 * @param alertExecutionGroupWorkers 
	 * @return the Well Definition Entry
	 */
	private WellDefinition createWellDefinition(
		UUID wellId,
		int channelDataAppendedListeners,
		int ruleExecutionGroupWorkers,
		int alertExecutionGroupWorkers
	) {
		assert Thread.holdsLock(wells);
		
		WellDefinition entry = new WellDefinition();
		entry.uuid = wellId;
		String wellIdString = wellId.toString();
		
		if (ruleExecutionGroupWorkers == -1) {
			entry.rulesExecutionGroup = Engine.getInstance().getExecutionGroup(defaultRuleExecutionGroup);
		} else {
			// Create the rulesExecutionGroup for the Well
			NativeExecutionGroup execGrp = createExecutionGroup("Affinity", "REG-", wellIdString, ruleExecutionGroupWorkers);
			entry.rulesExecutionGroup= execGrp;	
		}
		
		if (ruleExecutionGroupWorkers == -1) {
			entry.alertActionsExecutionGroup = Engine.getInstance().getExecutionGroup(defaultAlertExecutionGroup);
		} else {
			// Create the AlertActionsExectution Group for the Well
			NativeExecutionGroup execGrp = createExecutionGroup("Native", "AEG-", wellIdString, alertExecutionGroupWorkers);
			entry.alertActionsExecutionGroup = execGrp;
		}
		
		// Creating Trigger
		String routingKey = String.format(cdaTriggerConnectionSettings.getRoutingKey(), entry.uuid.toString());
		String queue = String.format(cdaTriggerConnectionSettings.getQueue(), entry.uuid.toString());
		
		RMQConnectionSettings settings = new RMQConnectionSettings();
		settings.setConnectionURI(cdaTriggerConnectionSettings.getConnectionURI());
		settings.setExchange(cdaTriggerConnectionSettings.getExchange());
		settings.setExchangeType(cdaTriggerConnectionSettings.getExchangeType());
		settings.setQueue(queue);
		settings.setDurable(cdaTriggerConnectionSettings.isDurable());
		settings.setRoutingKey(routingKey);
		settings.setConcurrentListeners(channelDataAppendedListeners);
		
		entry.trigger = new ChannelDataAppendedTrigger("T_" + entry.uuid.toString(), settings, entry.uuid);
		
		if (clockChannelSettingsElement != null)
			entry.trigger.loadClockChannelSettings(clockChannelSettingsElement);
				
		// Adding Trigger Thread
		Engine engine = Engine.getInstance();
		engine.publishTrigger(entry.trigger);
		// Temporary workaround for engine starting a trigger again if this is called during service startup
		if (engine.getEngineState() == EngineState.RUNNING)
			engine.addTriggerThread(entry.trigger);
		
		// Create Well in the BufferedCache
		GlobalCache cache = CacheFactory.getInstance().getBufferedCache();
		WellCache wcache = cache.getOrCreateWell(wellId);
		wcache.setName(wellIdString);
		
		// Create Well in the LKVCache
		cache = CacheFactory.getInstance().getLKVCache();
		wcache = cache.getOrCreateWell(wellId);
		wcache.setName(wellIdString);
		
		wells.put(wellId, entry);
		
		return entry;
	}
	
	private NativeExecutionGroup createExecutionGroup(String type, String prefix, String well, int workers) {
		assert Thread.holdsLock(wells);
		Engine engine = Engine.getInstance();
		try {
			NativeExecutionGroup execGrp = (NativeExecutionGroup) ExecutionGroupFactory.getExecutionGroup(type);
			execGrp.setEventStore(null);
			execGrp.setGroupName(prefix + well);
			execGrp.setWorkersCount(workers);
			engine.publishExecutionGroup(execGrp);
			return execGrp;
		} catch (EngineException e) {
			logger.error("FATAL Error while creating Execution Group");
		}
		
		return null;
	}

	private void loadDefaultWellDefinition(Element e) throws EngineException {
		if (e.getChild("ChannelDataAppendedSettings", e.getNamespace()) != null) {
			cdaTriggerConnectionSettings = new RMQConnectionSettings();
	 		cdaTriggerConnectionSettings.load(e.getChild("ChannelDataAppendedSettings", e.getNamespace()));			
		}
		
		if (e.getChild("ExecutionGroupTemplates", e.getNamespace()) != null) {
			Element t = e.getChild("ExecutionGroupTemplates", e.getNamespace());
			if (t.getChild("RuleActionTemplate", t.getNamespace()) != null) {
				this.defaultRuleExecutionGroupWorkers = Integer
						.parseInt(t.getChild("RuleActionTemplate", t.getNamespace()).getAttributeValue("workers").trim());
			} else {
				this.defaultRuleExecutionGroupWorkers = 1;
			}

			if (t.getChild("AlertActionTemplate", t.getNamespace()) != null) {
				this.defaultAlertExecutionGroupWorkers = Integer
						.parseInt(t.getChild("AlertActionTemplate", t.getNamespace()).getAttributeValue("workers").trim());
			} else {
				this.defaultAlertExecutionGroupWorkers = 1;
			}			
		} else if (e.getChild("ExecutionGroups", e.getNamespace()) != null) {
			Element t = e.getChild("ExecutionGroups", e.getNamespace());
			this.defaultRuleExecutionGroup = t.getChildText("RuleActionsExecutionGroup", t.getNamespace());
			this.defaultAlertExecutionGroup = t.getChildText("AlertActionsExecutionGroup", t.getNamespace());
		}
		
	}
}
