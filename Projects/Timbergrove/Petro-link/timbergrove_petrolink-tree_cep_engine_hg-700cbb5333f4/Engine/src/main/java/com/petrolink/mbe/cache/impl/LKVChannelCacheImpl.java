/**
 * 
 */
package com.petrolink.mbe.cache.impl;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.cache.ChannelCache;
import com.petrolink.mbe.cache.ChannelCacheConsumer;
import com.petrolink.mbe.cache.WellCache;
import com.petrolink.mbe.model.channel.ChannelDataType;
import com.petrolink.mbe.model.channel.ChannelIndexType;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.services.ChronosClientService;
import com.petrolink.mbe.services.ServiceAccessor;

/**
 * @author Jose Luis Moya Sobrado
 * @author Aristo
 */
public class LKVChannelCacheImpl extends LKVCacheResourceImpl implements ChannelCache {
	private static final Logger logger = LoggerFactory.getLogger(LKVChannelCacheImpl.class);
	private WellCache parent;
	private ChannelIndexType channelIndexType;
	private ChannelDataType channelDataType;
	private String customDataType;
	private String unitOfMeasurement;
	private final AtomicReference<DataPoint> lastValue = new AtomicReference<>();
	
	/**
	 * Initialize a local cache
	 * @param uuid
	 */
	public LKVChannelCacheImpl(UUID uuid) {
		super(uuid);
	}
	
	/**
	 * Constructor
	 * @param cache
	 * @param parent 
	 * @param uuid 
	 */
	public LKVChannelCacheImpl(LKVCacheImpl cache, WellCache parent, UUID uuid) {
		super(cache, uuid);
		this.parent = parent;
		ChronosClientService ccs = ServiceAccessor.getChronosClientService();
		if (ccs != null) {
			logger.debug("Getting latest value for channel {}", uuid);
			ccs.getLatestChannelDataAsync(uuid).thenAccept(d -> onReceiveLatestChannelData(d));
		}
	}
	
	@Override
	public DataPoint getLastDataPoint() {
		return lastValue.get();
	}

	@Override
	public void addDataPoint(DataPoint value) {
		Objects.requireNonNull(value);
		// Keep trying to update the value so long as its null or the new value is at a greater index
		while (true) {
			DataPoint last = lastValue.get();
			if (last != null && DataPoint.numericSubtract(value.getIndex(), last.getIndex()) <= 0) {
				logger.warn("Ignoring data point {} with index less than or equal to current value {}", value, last);
				break;
			}
			if (lastValue.compareAndSet(last, value))
				break;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.petrolink.mbe.cache.Channel#getWellbore()
	 */
	@Override
	public WellCache getWell() {
		return parent;
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
	public boolean isEmpty() {
		return lastValue.get() == null;
	}

	@Override
	public Element toElement() {
		Element channel = new Element("Channel");
		channel.setAttribute("id", this.getId().toString());
		channel.setAttribute("name", this.getName());
		channel.setAttribute("indexType", "");
		channel.setAttribute("valueType", "");
		return channel;
	}
	
	private void onReceiveLatestChannelData(DataPoint d) {
		logger.debug("Received latest value for channel {}: {}", getId(), d);
		// setLastKnownValue does an index check
		if (d != null)
			addDataPoint(d);
	}

	@Override
	public void clear() {
		lastValue.set(null);
	}

	@Override
	public void getDataPoints(Collection<DataPoint> collector) {
		DataPoint value = lastValue.get();
		if (value != null)
			collector.add(value);
	}

	@Override
	public void getDataPoints(ChannelCacheConsumer consumer, Object startIndex, boolean inclusive) {
		DataPoint value = lastValue.get();
		if (value != null)
			consumer.accept(value);
	}

	@Override
	public void clearDataPoints(Object endIndex, boolean inclusive) {
		// Can't clear LKV
	}

	@Override
	public void addDataPoints(Collection<DataPoint> values) {
		for (DataPoint dp : values)
			addDataPoint(dp);
	}

	@Override
	public DataPoint getLastDataPoint(Object endIndex) {
		return getLastDataPoint();
	}

	@Override
	public long getMaxSize() {
		return 1;
	}

	@Override
	public void setMaxSize(long maxSize) {
	}
}
