package com.petrolink.mbe.parser;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.petrolink.mbe.model.channel.ComplexValue;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.model.operation.DataPointsAppended;
import com.petrolink.mbe.model.operation.DataPointsChanged;
import com.petrolink.mbe.util.DateTimeHelper;
import com.petrolink.mbe.util.UUIDHelper;

import Petrolink.WITSML.Datatypes.ChannelDataItem;
import Petrolink.WITSML.Events.ChannelDataAppended;
import Petrolink.WITSML.Events.ChannelDataChanged;

/**
 * Contains methods for converting objects from the message model to the internal model.
 * @author langj
 *
 */
public class MessageConverter {
	private static final int SECONDS_PER_HOUR = 3600;

	/**
	 * Convert PetroVault.Realtime.Raw Events to internal model
	 * @param cda The PetroVault.Realtime.Raw Events to be converted
	 * @return Corresponding internal Model
	 */
	public static DataPointsAppended toInternalModel(ChannelDataAppended cda) {
		if (cda == null)
			return null;
		
		DataPointsAppended model = new DataPointsAppended();
		
		List<ChannelDataItem> items = cda.getData();
		if (items != null) {
			for (ChannelDataItem item: items) {
				DataPoint dp = toInternalModel(item);
				if(dp != null) {
					//model.addDataPoint(UUIDHelper.fromBytesDotNet(item.getId().bytes()), dp);
					model.addDataPoint(UUIDHelper.fromBytes(item.getId().bytes()), dp);
				}
			}
		}
		
		return model;
	}
	
	/**
	 * Convert PetroVault.Realtime.Raw Events to internal model
	 * @param cda The PetroVault.Realtime.Raw Events to be converted
	 * @return Corresponding internal Model
	 */
	public static DataPointsChanged toInternalModel(ChannelDataChanged cda) {
		if (cda == null)
			return null;
		
		DataPointsChanged model = new DataPointsChanged();
		List<ChannelDataItem> items = cda.getData();
		
		if (items != null) {
			for (ChannelDataItem item: items) {
				DataPoint dp = toInternalModel(item);
				if(dp != null) {
					model.addDataPoint(UUIDHelper.fromBytes(item.getId().bytes()), dp);
					//model.addDataPoint(UUIDHelper.fromBytesDotNet(item.getId().bytes()), dp);
				}
			}
		}
		
		return model;
	}
	
	/**
	 * Convert PetroVault.Realtime.Raw Events to internal model.
	 * @param item The PetroVault.Realtime.Raw Events to be converted
	 * @return Corresponding internal Model
	 */
	public static DataPoint toInternalModel(final ChannelDataItem item) {
		if (item == null) {
			return null;
		}

		// Avro does not allow these to be null
		Object index = item.getItem().getIndexes().get(0).getItem();
		Object value = item.getItem().getValue().getItem();
		
		// Must convert Petrolink DateTime objects to Java OffsetDateTime objects
		if (index instanceof Petrolink.Datatypes.DateTime) {
			// Transforming DateTime to OffsetDateTime
			index = toInternalModel((Petrolink.Datatypes.DateTime) index);
		}
		if (value instanceof Petrolink.Datatypes.DateTime){
			value = toInternalModel((Petrolink.Datatypes.DateTime) value);
		} else if (value instanceof CharSequence) {
			// must check for CharSequence because Avro likes to return Utf8 instead of String
			String svalue = ((CharSequence) value).toString();
			ComplexValue cvalue = ComplexValue.tryParse(svalue);
			value = cvalue != null ? cvalue : svalue;
		}

		return new DataPoint(index, value);
		// Do not silently fail here. invalid data types need to be investigated.
//		try	{
//			return new DataPoint(index, value);
//		} catch (Exception ex) {
//			if (BuildConfig.DEBUG) {
//				System.out.println("Exception to Parse Channel:" 
//					+ UUIDHelper.fromBytesDotNet(item.getId().bytes())
//					+ "; Index=" + index
//					+ "; Value=" + value);
//			}
//			return null;
//		}
	}
	
	/**
	 * Convert Petrolink's DateTime to OffsetDateTime.
	 * @param value The value to be converted
	 * @return An equivalent OffsetDateTime instance
	 */
	public static OffsetDateTime toInternalModel(final Petrolink.Datatypes.DateTime value) {
		// The Petrolink Avro DateTime has two parts:
		//   Time:   The number of *microseconds* since the Unix epoch
		//   Offset: The time zone offset in hours
		
		OffsetDateTime resultUTC = DateTimeHelper.fromEpochMicros(value.getTime());
		
		double offsetHours = value.getOffset();
		if (offsetHours == 0) {
			return resultUTC;
		} else {
			int offsetSeconds = (int) (SECONDS_PER_HOUR * offsetHours);
			return resultUTC.withOffsetSameInstant(ZoneOffset.ofTotalSeconds(offsetSeconds));
		}
	}
		
	/**
	 * Convert Java OffsetDateTime to Petrolink.Datatypes.DateTime.
	 * @param value The value to be converted
	 * @return An equivalent DateTime instance
	 */
	public static Petrolink.Datatypes.DateTime toMessageModel(final OffsetDateTime value) {
		OffsetDateTime valueUtc = value.withOffsetSameInstant(ZoneOffset.UTC);
		
		Petrolink.Datatypes.DateTime dt = new Petrolink.Datatypes.DateTime();
		dt.setTime(DateTimeHelper.toEpochMicros(valueUtc));
		return dt;
	}
	
	/**
	 * Convert data Points to  ChannelDataAppended with specified channel UUID
	 * @param uuid
	 * @param dps
	 * @return ChannelDataAppended representing data in DataPoints
	 */
	public static Petrolink.WITSML.Events.ChannelDataAppended toMessageModel(UUID uuid, List<DataPoint> dps) {
		Petrolink.Datatypes.UUID channelId = new Petrolink.Datatypes.UUID(
				UUIDHelper.toBytes(uuid));
		
		ArrayList<Petrolink.WITSML.Datatypes.ChannelDataItem> cdal 
			= new ArrayList<Petrolink.WITSML.Datatypes.ChannelDataItem>();
		
		for (DataPoint dp : dps) {
			ArrayList<Petrolink.Datatypes.IndexValue> indices = new ArrayList<Petrolink.Datatypes.IndexValue>();
			Petrolink.Datatypes.IndexValue iv = new Petrolink.Datatypes.IndexValue();
			
			Object idx = dp.getIndex();
			if (idx instanceof OffsetDateTime) {
				iv.setItem(MessageConverter.toMessageModel((OffsetDateTime)dp.getIndex()));
			} else {
				iv.setItem(dp.getIndex());
			}
			
	
			Petrolink.Datatypes.DataValue dv = new Petrolink.Datatypes.DataValue();
			dv.setItem(dp.getValue());
			
			indices.add(iv);
	
			Petrolink.WITSML.Datatypes.DataItem di = new Petrolink.WITSML.Datatypes.DataItem();
			di.setIndexes(indices);
			di.setValue(dv);
			di.setValueAttributes(new ArrayList<Petrolink.Datatypes.DataAttribute>());
			
			Petrolink.WITSML.Datatypes.ChannelDataItem cdi = new Petrolink.WITSML.Datatypes.ChannelDataItem();
			cdi.setId(channelId);
			cdi.setItem(di);
			
			cdal.add(cdi);
		}
	
		
		Petrolink.WITSML.Events.ChannelDataAppended cda = new Petrolink.WITSML.Events.ChannelDataAppended();
		cda.setData(cdal);
	
		return cda;
	}
}
