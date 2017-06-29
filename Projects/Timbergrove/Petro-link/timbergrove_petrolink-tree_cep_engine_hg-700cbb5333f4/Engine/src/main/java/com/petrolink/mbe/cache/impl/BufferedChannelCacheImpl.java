package com.petrolink.mbe.cache.impl;

import java.util.Collection;
import java.util.Properties;
import java.util.UUID;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.cache.ChannelCache;
import com.petrolink.mbe.cache.ChannelCacheConsumer;
import com.petrolink.mbe.cache.WellCache;
import com.petrolink.mbe.model.channel.ChannelDataType;
import com.petrolink.mbe.model.channel.ChannelIndexType;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.model.channel.DataPointSet;
import com.petrolink.mbe.services.ChronosClientService;
import com.petrolink.mbe.services.EngineService;
import com.petrolink.mbe.services.ServiceAccessor;

/**
 * The Buffered Channel Cache Implementation 
 * @author paul
 *
 */
public class BufferedChannelCacheImpl extends BufferedCacheResourceImpl implements ChannelCache {
	private static final String MAX_SIZE_KEY = "BufferedChannelMaxSize";
	private static final long DEFAULT_MAX_SIZE = 2000;
	
	private ChannelIndexType channelIndexType;
	private ChannelDataType channelDataType;
	private String unitOfMeasurement;
	private String customDataType;
	private BufferedWellCacheImpl well;
	private final DataPointSet dataSet = new DataPointSet();
	private long maxSize = DEFAULT_MAX_SIZE;
	private static final Logger logger = LoggerFactory.getLogger(BufferedChannelCacheImpl.class);
		
	/**
	 * Constructor
	 * @param uuid the channel UUID
	 */
	public BufferedChannelCacheImpl(UUID uuid) {
		super(uuid);
		EngineService engSvc = ServiceAccessor.getEngineService();
		if (engSvc != null) {
			Properties engineProperties = engSvc.getUserProperties();
			if (engineProperties.containsKey(MAX_SIZE_KEY)) {
				setMaxSize((long) engineProperties.get(MAX_SIZE_KEY));
			}
		}
		
		ChronosClientService ccs = ServiceAccessor.getChronosClientService();
		if (ccs != null) {
			logger.debug("Getting latest value for channel {}", uuid);
			ccs.getLatestChannelDataAsync(uuid).thenAccept(d -> onReceiveLatestChannelData(d));
		}
	}

	/**
	 * Constructor
	 * @param cache
	 * @param wellCacheImpl
	 * @param uuid
	 */
	public BufferedChannelCacheImpl(BufferedCacheImpl cache, BufferedWellCacheImpl wellCacheImpl, UUID uuid) {
		this(uuid);
		this.well = wellCacheImpl;
	}

	@Override
	public WellCache getWell() {
		return well;
	}

	@Override
	public ChannelIndexType getChannelIndexType() {
		return this.channelIndexType;
	}

	@Override
	public void setChannelIndexType(ChannelIndexType indexType) {
		this.channelIndexType = indexType;
	}

	@Override
	public ChannelDataType getChannelDataType() {
		return this.channelDataType;
	}

	@Override
	public void setChannelDataType(ChannelDataType dataType) {
		this.channelDataType = dataType;
	}

	@Override
	public String getCustomDataType() {
		return this.customDataType;
	}

	@Override
	public void setCustomDataType(String customDataType) {
		this.channelDataType = ChannelDataType.Custom;
		this.customDataType = customDataType;
	}

	@Override
	public void setUnitOfMeasurement(String uom) {
		this.unitOfMeasurement = uom;
	}

	@Override
	public String getUnitOfMeasurement() {
		return this.unitOfMeasurement;
	}

	@Override
	public DataPoint getLastDataPoint() {
		synchronized (dataSet) {
			return !dataSet.isEmpty() ? dataSet.last() : null;
		}
	}
	
	@Override
	public DataPoint getLastDataPoint(Object index) {
		synchronized (dataSet) {
			return dataSet.floor(index);
		}
	}

	@Override
	public void addDataPoint(DataPoint dp) {
		synchronized (dataSet) {
			// If it exceeds max size before we add, then we likely need a full trim.
			if (dataSet.size() > maxSize) {
				logger.debug("Buffered Channel Cache {} on rule {} exceded the defined maximum size", this.getId(), this.getOwner());
				trimDataSet();
				assert dataSet.size() <= maxSize;
			}
			dataSet.add(dp);
			// If we went one beyond the max size, just do a single poll.
			// It is possible that the data point that was just added is removed again if it is the lowest.
			if (dataSet.size() > maxSize) {
				dataSet.pollFirst();
				assert dataSet.size() == maxSize;
			}
		}
	}
	
	@Override
	public void getDataPoints(Collection<DataPoint> collection) {
		synchronized (dataSet) {
			dataSet.addAllTo(collection);
		}
	}
	
	@Override
	public void getDataPoints(ChannelCacheConsumer consumer, Object startIndex, boolean inclusive) {
		// This method can get performance intensive if there is a lot of data
		// Do not change it without understanding performance implications
		// Note that we do not use a reader writer lock or concurrent set because majority of caches
		// are local to a rule
		synchronized (dataSet) {
			DataPointSet dataSet = this.dataSet;
			int size = dataSet.size();
			int i = 0;
			
			if (startIndex != null) {
				i = dataSet.binarySearch(startIndex);
				
				if (i < 0) {
					i = -i - 1;
				} else if (!inclusive) {
					// if the search found the matching index, but we're not inclusive, skip ahead one
					i++;
				}
			} else {
				i = 0;
			}
			
			for (; i < size; i++) {
				if (!consumer.accept(dataSet.getUnsafe(i)))
					return;
			}
		}
	}
		
	@Override
	public boolean isEmpty() {
		synchronized (dataSet) {
			return dataSet.isEmpty();
		}
	}

	@Override
	public void clearDataPoints(Object endIndex, boolean inclusive) {
		synchronized (dataSet) {
			dataSet.clearHead(endIndex, inclusive);
		}
	}

	@Override
	public void addDataPoints(Collection<DataPoint> values) {
		synchronized (dataSet) {
			for (DataPoint dp : values)
				dataSet.add(dp);
			// Trim the dataSet to maxSize after all values have been added so that only the lowest indices are trimmed
			trimDataSet();
		}
	}

	@Override
	public Element toElement() {
		return null;
	}

	/**
	 * @return the maxSize
	 */
	public long getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(long maxSize) {
		boolean mustTrim = maxSize < this.maxSize;
		this.maxSize = maxSize;
		if (mustTrim) {
			synchronized (dataSet) {
				trimDataSet();
			}
		}
	}
	
	private void trimDataSet() {
		// The dataSet lock must already be held
		for (long s = dataSet.size(); s > maxSize; s--) {
			if (dataSet.pollFirst() == null)
				break;
		}
	}
	
	private void onReceiveLatestChannelData(DataPoint d) {
		logger.debug("Received latest value for channel {}: {}", getId(), d);
		// It's okay if this completes after channel data is received from RMQ because sorting ensures that older data
		// will not take precedence
		if (d != null)
			addDataPoint(d);
	}

	@Override
	public void clear() {
		synchronized (dataSet) {
			dataSet.clear();
		}
	}
}
