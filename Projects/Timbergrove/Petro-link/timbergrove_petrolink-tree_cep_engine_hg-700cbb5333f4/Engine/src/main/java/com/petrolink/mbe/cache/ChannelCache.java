/**
 * 
 */
package com.petrolink.mbe.cache;

import java.util.Collection;

import org.jdom2.Element;

import com.petrolink.mbe.model.channel.ChannelDataType;
import com.petrolink.mbe.model.channel.ChannelIndexType;
import com.petrolink.mbe.model.channel.DataPoint;

/**
 * A cache that stores data points from a channel.
 * @author Jose Luis Moya Sobrado
 * @author Aristo
 */
public interface ChannelCache extends Resource {
	/**
	 * Gets the well cache that owns this channel.
	 * @return the Well Cache
	 */
	WellCache getWell();
	
	/**
	 * Get the last (newest) data point of this channel. Can be null.
	 * @return the Data Point last known value
	 */
	DataPoint getLastDataPoint();

	/**
	 * The Last Known Value equal or lower than the provided Index
	 * @param endIndex The inclusive end index
	 * @return the Data Point matching the criteria
	 */
	DataPoint getLastDataPoint(Object endIndex);
	
	/**
	 * Get all data points in this cache. This will be ordered from lowest to highest index.
	 * @param collector A collector to receive the data.
	 */
	void getDataPoints(Collection<DataPoint> collector);
	
	/**
	 * Gets subset of data points from this cache.
	 * @param consumer A collector to receive the data.
	 * @param startIndex A minimum index to start at.
	 * @param inclusive Whether the minimum is inclusive.
	 */
	void getDataPoints(ChannelCacheConsumer consumer, Object startIndex, boolean inclusive);
	
	/**
	 * Add a data point to this channel cache. This implementation can determine in what order the data is added
	 * or whether it is added at all.
	 * @param dataValue
	 */
	void addDataPoint(DataPoint dataValue);
	
	/**
	 * Add the List of Data Points to the Buffered Cache
	 * @param values
	 */
	void addDataPoints(Collection<DataPoint> values);

	/**
	 * Clear the data points from the Buffered Channel Cache
	 * @param endIndex The end index
	 * @param inclusive Whether the end index is inclusive
	 */
	void clearDataPoints(Object endIndex, boolean inclusive);
	
	/**
	 * The index type of this channel. Is this Datetime Channel, Depth channel, or offset time channel
	 * @return The ChannelIndexType for this channel
	 */
    ChannelIndexType getChannelIndexType();

    /**
     * The index type of this channel. Is this Datetime Channel, Depth channel, or offset time channel
     * @param indexType The ChannelIndexType for this channel
     */
	void setChannelIndexType(ChannelIndexType indexType);
	
	
	/**
	 * The Data type for this channel. For custom data type (ChannelDataType.Custom) see also property CustomDataType.
	 * @return ChannelDataType of this channel.
	 */
	ChannelDataType getChannelDataType();
	
	/**
	 * The Data type for this channel. For custom data type (ChannelDataType.Custom) see also property CustomDataType.
	 * @param dataType ChannelDataType of this channel.
	 */
	void setChannelDataType(ChannelDataType dataType);
	
	/**
	 * If ChannelDataType is custom, this will indicate identifier (generally a namespace) of custom Data type.
	 * @return identifier of Custom Data Type if indicated by ChannelDataType
	 */
	String getCustomDataType();
	
	/**
	 * If ChannelDataType is custom, this will indicate identifier (generally a namespace) of custom Data type.
	 * @param customDataType identifier of Custom Data Type if indicated by ChannelDataType
	 */
	void setCustomDataType(String customDataType);
	
		
	/**
	 * Default unit of Measurement of this Channel.
	 * @param uom Unit of measurement of this channel
	 */
	void setUnitOfMeasurement(String uom);
	
	/**
	 * Default unit of Measurement of this Channel.
	 * @return the Channel Unit of Measurement
	 */
	String getUnitOfMeasurement();

	/**
	 * @return true if the cache is empty
	 */
	boolean isEmpty();

	/**
	 * @return a JDOM Element representing the Channel settings
	 */
	Element toElement();

	/**
	 * Clear the Channel Cache (removing all data point currently stored by the Cache)
	 */
	void clear();

	/**
	 * @return the maxSize
	 */
	long getMaxSize();

	/**
	 * @param maxSize the maxSize to set
	 */
	void setMaxSize(long maxSize);
}
