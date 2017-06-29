package com.petrolink.mbe.rulesflow.variables;

import org.jdom2.Element;

import com.petrolink.mbe.cache.CacheFactory;
import com.petrolink.mbe.cache.ChannelCacheConsumer;
import com.petrolink.mbe.cache.WellCache;
import com.petrolink.mbe.directories.WellDirectory;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.ServiceAccessor;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Last Known Value concreate channel variable implementation
 * @author paul
 *
 */
public class LKVChannelAlias extends ChannelAlias {
	/* (non-Javadoc)
	 * @see com.petrolink.mbe.rulesflow.variables.ChannelAlias#load(com.petrolink.mbe.rulesflow.RuleFlow, org.jdom2.Element)
	 */
	@Override
	public void load(RuleFlow rule, Element e) throws EngineException {
		super.load(rule, e);
		
		if (cache == null) {
			logger.info("Registrating Channel with UUID {} to Well and Cache on demand", uuid);
			WellCache wellCache = CacheFactory.getInstance().getLKVCache().getWell(rule.getWellId());
			
			if (wellCache != null) {
				cache = wellCache.getOrCreateChannel(uuid);
			}

			// Registrating Cache to Well
			WellDirectory wellDirectory = ServiceAccessor.getWellDirectory();
			if (wellDirectory != null) {
				wellDirectory.getWell(rule.getWellId()).registerChannel(uuid, this.getAlias());
			}
		}		
	}

	@Override
	public boolean isCacheGlobal() {
		return true;
	}
	
	@Override
	public final BufferedChannelAlias asBuffered() {
		return null;
	}
	
	@Override
	public void getCacheData(ChannelCacheConsumer consumer, Object startIndex, boolean inclusive) {
		// startIndex is ignored for LKV
		DataPoint dp = cache.getLastDataPoint();
		if (dp != null)
			consumer.accept(dp);
	}
	
	@Override
	public void clearUsedCacheData(Object endIndex) {
		// Can't clear LKV
	}
}
