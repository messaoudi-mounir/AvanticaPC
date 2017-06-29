package com.petrolink.mbe.rulesflow.variables;

import java.util.Objects;

import com.petrolink.mbe.model.channel.DataPoint;

/**
 * A compound value used for channel aliases that combines the current and previous values for use in scripting.
 * @author langj
 */
@SuppressWarnings("javadoc")
public final class ChannelAliasValue {
	private final DataPoint newData;
	private final DataPoint prevData;
	
	ChannelAliasValue(DataPoint newData, DataPoint prevData) {
		this.newData = Objects.requireNonNull(newData);
		this.prevData = prevData;
	}
	
	public DataPoint getDataPoint() {
		return newData;
	}
	
	public DataPoint getPrevDataPoint() {
		return prevData;
	}
	
	public Object getIndex() {
		return newData.getIndex();
	}
	
	public Double getIndexNumber() {
		return newData.getIndexNumber();
	}
	
	public Object getValue() {
		return newData.getValue();
	}
	
	public Object getPrevIndex() {
		return prevData != null ? prevData.getIndex() : null;
	}
	
	public Double getPrevIndexNumber() {
		return prevData != null ? prevData.getIndexNumber() : null;
	}
	
	public Object getPrevValue() {
		return prevData != null ? prevData.getValue() : null;
	}
	
	public boolean hasPrevValue() {
		return prevData != null;
	}
	
	@Override
	public String toString() {
		return newData.getValue().toString();
	}
}
