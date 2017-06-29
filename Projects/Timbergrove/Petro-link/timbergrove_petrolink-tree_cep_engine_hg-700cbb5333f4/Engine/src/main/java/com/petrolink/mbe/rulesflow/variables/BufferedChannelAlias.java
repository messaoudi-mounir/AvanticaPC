package com.petrolink.mbe.rulesflow.variables;

import com.petrolink.mbe.cache.ChannelCacheConsumer;

/**
 * Buffered Data Channel uses Buffered Cache to hold the associated buffered channel data points.
 * @author paul
 *
 */
public class BufferedChannelAlias extends ChannelAlias {
	@Override
	public final BufferedChannelAlias asBuffered() {
		return this;
	}

	@Override
	public void getCacheData(ChannelCacheConsumer consumer, Object startIndex, boolean inclusive) {
		getCache().getDataPoints(consumer, startIndex, inclusive);
	}

	@Override
	public void clearUsedCacheData(Object endIndex) {
		getCache().clearDataPoints(endIndex, false);
	}

	@Override
	public boolean isCacheGlobal() {
		return false;
	}
}
