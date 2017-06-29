package com.petrolink.mbe.model.channel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

/**
 * Maps ChannelData objects to channel UUID's.
 * @author langj
 * @author aristo
 *
 */
public class ChannelDataMap {
	private HashMap<UUID, ChannelData> channels;
	
	/**
	 * Constructor.
	 */
	public ChannelDataMap() {
		channels = new HashMap<UUID, ChannelData>();
	}
	
	/**
	 * Add a data point for the specified channel.
	 * @param channelId A channel UUID.
	 * @param dataPoint A data point.
	 */
	public final void addDataPoint(final UUID channelId, final DataPoint dataPoint) {
		Objects.requireNonNull(channelId);
		Objects.requireNonNull(dataPoint);
		
		ChannelData cdp = channels.get(channelId);
		if (cdp == null) {
			cdp = new ChannelData(channelId);
			channels.put(channelId, cdp);
		}
		
		cdp.getDataPoints().add(dataPoint);
	}
	
	/**
	 * Get all Channel data contained in this map.
	 * @return Collection of ChannelData
	 */
	public final Collection<ChannelData> getAllChannelData() {
		return channels.values();
	}
}
