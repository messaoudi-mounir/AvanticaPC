package com.petrolink.mbe.model.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Combines a list of data points with a channel UUID.
 * @author aristo
 *
 */
public class ChannelData {
	private ArrayList<DataPoint> dataPoints;
	private UUID channelId;
	
	/**
	 * Constructor
	 * @param channelId Channel Id for the DataPoints for this instance.
	 */
	public ChannelData(UUID channelId) {
		this.channelId = Objects.requireNonNull(channelId);
		dataPoints = new ArrayList<DataPoint>();
	}
	
	/**
	 * Get the list of DataPoints.
	 * @return  list of DataPoint.
	 */
	public final List<DataPoint> getDataPoints() {
		return dataPoints;
	}
	
	/**
	 * Set Data points for this ChannelData
	 * @param replacementDataPoints DataPoints to be set for this ChannelData
	 */
	public final void setDataPoints(final List<DataPoint> replacementDataPoints) {
		Objects.requireNonNull(replacementDataPoints);
		dataPoints.clear();
		dataPoints.addAll(replacementDataPoints);
	}
	
	/**
	 * Get last Data point.
	 * @return Null if empty, otherwise return last added data Points
	 */
	public final DataPoint getLastDataPoint() {
		return dataPoints.isEmpty()	? null : dataPoints.get(dataPoints.size() - 1);
	}
	
	/**
	 * Channel ID for this channel Data
	 * @return  UUID for this channel Data
	 */
	public final UUID getChannelId() {
		return channelId;
	}
}