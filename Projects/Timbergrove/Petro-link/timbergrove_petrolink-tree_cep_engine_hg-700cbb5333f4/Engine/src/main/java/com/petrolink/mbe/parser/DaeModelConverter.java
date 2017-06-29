package com.petrolink.mbe.parser;

import java.util.ArrayList;
import java.util.Collection;

import Petrolink.Datatypes.DataValue;
import Petrolink.Datatypes.IndexValue;
import Petrolink.Datatypes.UUID;

import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.util.UUIDHelper;

import Petrolink.Microservice.DAE.ChannelDataValues;
import Petrolink.WITSML.Datatypes.DataItem;

public class DaeModelConverter {

	/**
	 * Convert DataPoint to ChannelDataValues with specified channelId
	 * @param channelId
	 * @param data
	 * @return
	 */
	public static ChannelDataValues toChannelDataValues(java.util.UUID channelId, Collection<DataPoint> data) {
		ChannelDataValues datum = new ChannelDataValues();
		Petrolink.Datatypes.UUID plinkChannelId = new Petrolink.Datatypes.UUID(UUIDHelper.toBytes(channelId));
		
		datum.setChannelId(plinkChannelId);
		ArrayList<DataItem> dataItems =  new ArrayList<DataItem>();
		for(DataPoint dp: data) {
			DataItem di = toDataItem(dp.getIndex(),dp.getValue());
			dataItems.add(di);
		}
		datum.setData(dataItems);
		return datum;
	}
	
	/**
	 * Convert Index and Value Object to DataItem
	 * @param index
	 * @param value
	 * @return
	 */
	public static DataItem toDataItem(Object index, Object value) {
		DataItem di = new DataItem();

		IndexValue iv = new IndexValue();
		iv.setItem(index);
		
		ArrayList<IndexValue> indices = new ArrayList<IndexValue>();
		indices.add(iv);

		DataValue dv = new DataValue();
		dv.setItem(value);
		
		di.setIndexes(indices);
		di.setValue(dv);
		return di;
	}
}
