package com.petrolink.mbe.cache;

import com.petrolink.mbe.model.channel.DataPoint;

/**
 * A consumer function for data points in a cache.
 * @author Joel Lang
 *
 */
@FunctionalInterface
public interface ChannelCacheConsumer {
	/**
	 * Accept a data point and its numeric index.
	 * @param dp
	 * @return TODO
	 */
	boolean accept(DataPoint dp);
}