package com.petrolink.mbe.model.channel;

import java.util.Objects;
import java.util.UUID;

import com.petrolink.mbe.util.HashHelper;

@SuppressWarnings("javadoc")
public final class ChannelDataPoint implements Comparable<ChannelDataPoint> {
	private final UUID channelId;
	private final DataPoint dataPoint;
	
	public ChannelDataPoint(UUID channelId, DataPoint dataPoint) {
		this.channelId = Objects.requireNonNull(channelId);
		this.dataPoint = Objects.requireNonNull(dataPoint);
	}
	
	public UUID getChannelId() {
		return channelId;
	}
	
	public DataPoint getDataPoint() {
		return dataPoint;
	}
	
	public boolean isEmptyChannelId() {
		return channelId.getLeastSignificantBits() == 0 && channelId.getMostSignificantBits() == 0;
	}
	
	@Override
	public int hashCode() {
		return HashHelper.combineHashCodes(channelId.hashCode(), dataPoint.hashCode());
	}
	
	@Override
	public int compareTo(ChannelDataPoint o) {
		Objects.requireNonNull(o);
		int ci = channelId.compareTo(o.channelId);
		return ci != 0 ? ci : dataPoint.compareTo(o.dataPoint);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ChannelDataPoint) {
			ChannelDataPoint other = (ChannelDataPoint) obj;
			return channelId.equals(other.channelId) && dataPoint.equals(other.dataPoint);
		}
		return false;
	}
}
