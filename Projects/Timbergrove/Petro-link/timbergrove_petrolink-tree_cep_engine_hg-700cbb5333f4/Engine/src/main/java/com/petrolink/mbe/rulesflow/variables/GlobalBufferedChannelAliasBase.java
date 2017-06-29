package com.petrolink.mbe.rulesflow.variables;

import com.petrolink.mbe.cache.ChannelCacheConsumer;
import com.petrolink.mbe.model.channel.DataPoint;

/**
 * A base for aliases for global buffered channels that implements getCacheData() and clearUsedCacheData() to
 * record the last used index instead of actually clearing data from the cache.
 * @author langj
 *
 */
public abstract class GlobalBufferedChannelAliasBase extends BufferedChannelAlias {
	protected Object lastUsedIndex;
	
	@Override
	public void getCacheData(ChannelCacheConsumer consumer, Object startIndex, boolean inclusive) {
		getCache().getDataPoints(consumer, startIndex != null ? startIndex : lastUsedIndex, true);
	}
	
	@Override
	public void clearUsedCacheData(Object endIndex) {
		if (lastUsedIndex == null || DataPoint.compareIndices(endIndex, lastUsedIndex) > 0)
			lastUsedIndex = endIndex;
	}

	@Override
	public boolean isCacheGlobal() {
		return true;
	}
}
