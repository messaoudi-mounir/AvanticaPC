package com.petrolink.mbe.rulesflow;

import java.util.Objects;
import java.util.UUID;

import com.petrolink.mbe.model.channel.DataPoint;

/**
 * Data Point EventContent container
 * @author langj
 * @author aristo
 *
 */
public class EventDataPointContent extends BaseEventContent {
	private final UUID channelId;
	
	/**
	 * DataPoint which is received for this content
	 */
	public final DataPoint receivedDataPoint;
	
	EventDataPointContent(UUID channelId, DataPoint receivedDataPoint) {
		this.channelId = Objects.requireNonNull(channelId);
		this.receivedDataPoint = Objects.requireNonNull(receivedDataPoint);
	}

	/**
	 * @return the channelId
	 */
	public final UUID getChannelId() {
		return channelId;
	}
	
	@Override
	public String toString() {
		return "EventDataPointContent for channel "+ channelId + " data "+ receivedDataPoint;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EventDataPointContent) {
			EventDataPointContent other = (EventDataPointContent)obj;
			return channelId.equals(other.channelId)
					&& receivedDataPoint.equals(other.receivedDataPoint)
					;
		}
		return false;
	}
}
