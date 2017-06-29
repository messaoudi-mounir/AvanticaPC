package com.petrolink.mbe.rulesflow.variables;

import java.util.UUID;

import org.jdom2.Element;

import com.petrolink.mbe.cache.CacheFactory;
import com.petrolink.mbe.directories.WellDirectory;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.ServiceAccessor;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Channel Implementation for Historical Channel as an extension of Buffered Channel Alias
 * These channels will query the PetroVault Chronos to obtain a set of points associated from the refill point forward
 * @author paul
 *
 */
public class GlobalBufferedChannelAlias extends GlobalBufferedChannelAliasBase {
	/**
	 * Generic Constructor
	 */
	public GlobalBufferedChannelAlias() {
		
	}
	
	/**
	 * Constructor for Processor Use
	 * @param rule
	 * @param uuid
	 */
	public GlobalBufferedChannelAlias(RuleFlow rule, UUID uuid) {
		this.uuid = uuid;
		this.rule = rule;
		
		if (this.cache == null) {
			this.cache = CacheFactory.getInstance().getBufferedCache().getWell(rule.getWellId()).getOrCreateChannel(uuid);

			// Registrating Cache to Well
			WellDirectory wellDirectory = ServiceAccessor.getWellDirectory();
			if (wellDirectory != null) {
				wellDirectory.getWell(rule.getWellId()).registerChannel(uuid, this.getAlias());
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.petrolink.mbe.rulesflow.variables.ChannelAlias#load(com.petrolink.mbe.rulesflow.RuleFlow, org.jdom2.Element)
	 */
	@Override
	public void load(RuleFlow rule, Element e) throws EngineException {
		super.load(rule, e);
		
		if (this.cache == null) {
			this.cache = CacheFactory.getInstance().getBufferedCache().getWell(rule.getWellId()).getOrCreateChannel(uuid);

			// Registrating Cache to Well
			WellDirectory wellDirectory = ServiceAccessor.getWellDirectory();
			if (wellDirectory != null) {
				wellDirectory.getWell(rule.getWellId()).registerChannel(uuid, this.getAlias());
			}
		}
	}
}
