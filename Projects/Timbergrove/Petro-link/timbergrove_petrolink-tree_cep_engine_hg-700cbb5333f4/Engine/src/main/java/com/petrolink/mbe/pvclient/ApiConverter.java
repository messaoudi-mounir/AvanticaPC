package com.petrolink.mbe.pvclient;

import java.time.OffsetDateTime;
import java.util.HashMap;

import org.json.JSONObject;

import com.petrolink.mbe.util.DateTimeHelper;

import Energistics.Datatypes.DataValue;
import Energistics.Datatypes.ChannelData.ChannelMetadataRecord;
import Energistics.Datatypes.ChannelData.ChannelStatuses;

/**
 * Provides conversion methods for working with the PetroVaultHD API's.
 * @author langj
 *
 */
public final class ApiConverter {
	private static final HashMap<Integer, ChannelStatuses> channelStatusesMap = new HashMap<>();
	
	static {
		channelStatusesMap.put(ChannelStatuses.Active.ordinal(), ChannelStatuses.Active);
		channelStatusesMap.put(ChannelStatuses.Inactive.ordinal(), ChannelStatuses.Inactive);
		channelStatusesMap.put(ChannelStatuses.Closed.ordinal(), ChannelStatuses.Closed);
	}
	
	private ApiConverter() {}
	
	/**
	 * Decodes an index stored as a long. If isTimr is true, returns OffsetDateTime. If scale is nonzero, returns a Double,
	 * otherwise returns the Long value as-is.
	 * @param value The encoded value
	 * @param isTime True if the value is an encoded time index
	 * @param scale The scale of the depth index double. Set to 0 if the index is a long.
	 * @return An OffsetDateTime, Double, or Long, or null if the value was null.
	 */
	public static Object decodeIndex(Long value, boolean isTime, int scale) {
		if (value != null) {
			if (isTime)
				return DateTimeHelper.fromEpochMicros(value);
			if (scale != 0)
				return new Double(value.doubleValue() / getScaleMultiplier(scale));
		}
		return value;
	}
	
	/**
	 * Encode an index as a Long. The value must be either an OffsetDateTime, Double, or Long.
	 * @param value The value to encode.
	 * @param scale The scale to use for doubles.
	 * @return The encoded value, or null if the value was null or not of a valid type.
	 */
	public static Long encodeIndex(final Object value, final int scale) {
		if (value instanceof OffsetDateTime) {
			return DateTimeHelper.toEpochMicros((OffsetDateTime) value);
		}
		
		if (value instanceof Double) {
			return (long) ((double) value * getScaleMultiplier(scale));
		}
		
		if (value instanceof Long) {
			return (Long) value;
		}
		
		return null;
	}
	
	/**
	 * Convert a JSONObject to ChannelMetadataRecord.
	 * @param j
	 * @return ChannelMetadataRecord which has information copied from the specified JSONObject
	 */
	public static ChannelMetadataRecord toChannelMetadataRecord(final JSONObject j) {
		//{
		//  "Status": 1,
		//  "Description": "< SPARE 3 >",
		//  "StartIndex": {"long": 0},
		//  "ContentType": null,
		//  "ChannelId": 0,
		//  "Indexes": [{
		//    "DepthDatum": null,
		//    "Uom": "ft",
		//    "Description": {"string": "Measured depth"},
		//    "Mnemonic": {"string": "Depth_01"},
		//    "CustomData": {},
		//    "Scale": 0,
		//    "IndexType": 1,
		//    "Direction": 0,
		//    "Uri": {"string": "eml://witsml141/well(timbergrove)/wellbore(timbergrove)/log(depth_test)/channel(spr3)"},
		//    "TimeDatum": null
		//  }],
		//  "EndIndex": {"long": 0},
		//  "Source": "",
		//  "ChannelUri": "eml://witsml141/well(timbergrove)/wellbore(timbergrove)/log(depth_test)/channel(spr3)",
		//  "Uom": "unitless",
		//  "ChannelName": "SPR3",
		//  "Uuid": {"string": "050f3d51-78b9-4d6e-989f-6485e30b4984"},
		//  "CustomData": {
		//    "traceState": {"Item": {"string": "unknown"}},
		//    "axisDefinitions": {"Item": {"string": "[]"}},
		//    "sensorOffsetValue": {"Item": {"string": ""}},
		//    "mnemAlias": {"Item": {"string": ""}},
		//    "densDataValue": {"Item": {"string": ""}},
		//    "traceOrigin": {"Item": {"string": "unknown"}},
		//    "typeLogData": {"Item": {"string": "double"}},
		//    "sensorOffsetUom": {"Item": {"string": ""}},
		//    "densDataUom": {"Item": {"string": ""}}
		//  },
		//  "DomainObject": null,
		//  "DataType": "Double",
		//  "MeasureClass": ""
		//}
		
		ChannelMetadataRecord r = new ChannelMetadataRecord();
		
		r.setStatus(channelStatusesMap.get(j.get("Status")));
		r.setDescription(j.getString("Description"));
		r.setStartIndex((Long) parseUnion(j, "StartIndex"));
		r.setEndIndex((Long) parseUnion(j, "EndIndex"));
		r.setContentType(j.optString("ContentType", null));
		r.setChannelId(j.getLong("ChannelId"));
		r.setSource(j.getString("Source"));
		r.setChannelUri(j.getString("ChannelUri"));
		r.setUom(j.getString("Uom"));
		r.setChannelName(j.getString("ChannelName"));
		r.setUuid((String) parseUnion(j, "Uuid"));
		r.setDataType((String) j.getString("DataType"));
		r.setMeasureClass((String) j.getString("MeasureClass"));
		
		return r;
	}
	
	/**
	 * Convert a JSONObject to DataValue. This will {@link #parseUnion(Object)} of the specified Object and use {@link DataValue#setItem(Object)} in DataValue.
	 * @param j JsonObject which will be converted
	 * @return a Data Value representing the JSONObject
	 * @see DataValue
	 */
	public static DataValue toDataValue(final JSONObject j) {
		DataValue r = new DataValue();
		r.setItem(parseUnion(j, "Item"));
		return r;
	}
	
	/**
	 * Convert an Object to AVRO Union type.
	 * @param value value which will be converted
	 * @return JSONObject holding the value and specifying the type.
	 */
	public static JSONObject toUnion(final Object value) {
		JSONObject u = new JSONObject();
		
		if (value instanceof Double) {
			u.put("double", value);
		} else if (value instanceof Long) {
			u.put("long", value);
		} else if (value instanceof Boolean) {
			u.put("boolean", value);
		} else if (value instanceof Integer) {
			u.put("int", value);
		} else if (value instanceof Float) {
			u.put("float", value);
		} else {
			u.put("string", value.toString());
		}
		return u;
	}
	
	/**
	 * Parse a specified child Union Object (JSONObject) to its specific type in Java.
	 * @param parent The union Object to parse
	 * @param key The child key for that Union Type
	 * @return Object which hold the Union type. eg double and long to type number. a boolean to type Boolean. Int to type Integer
	 */
	public static Object parseUnion(final JSONObject parent, final String key) {
		return parseUnion(parent.getJSONObject(key));
	}
	
	/**
	 * Parse a specified Object (JSONObject) to its specific type in Java.
	 * @param maybeUnion
	 * @return Object which hold the Union type. eg double and long to type number. a boolean to type Boolean. Int to type Integer
	 */
	public static Object parseUnion(final Object maybeUnion) {
		if (!(maybeUnion instanceof JSONObject)) {
			return maybeUnion;
		}
		
		JSONObject union = (JSONObject) maybeUnion;
		
		// PERF: Not using the key iterator as that causes both the key set and its iterator to be allocated
		//       Instead, try guessing from the most common to least common values.
		
		Object value = union.opt("double");
		if (value != null) {
			return widen((Number) value); // widen floats to doubles
		}
		
		value = union.opt("long");
		if (value != null) {
			return widen((Number) value); // widen integers to longs
		}
		
		value = union.opt("string");
		if (value != null) {
			return (String) value;
		}
		
		value = union.opt("boolean");
		if (value != null) {
			return (Boolean) value;
		}
		
		value = union.opt("int");
		if (value != null) {
			return (Integer) value;
		}
		
		value = union.opt("float");
		if (value != null) {
			return (Float) value;
		}
		
//		Set<String> keys = union.keySet();
//		if (keys.size() != 1)
//			return null; // not a union
//		
//		String key = keys.iterator().next(); // The first and only key
//		
//		switch (key) {
//		case "boolean":
//			return (Boolean) union.get(key);
//		case "int":
//			return (Integer) union.get(key);
//		case "long":
//			// Stored as Integer object (sometimes?)
//			Object value = union.get(key);
//			if (value instanceof Integer)
//				return (long) (int) value;
//			return (Long) value;
//		case "float":
//			return (Float) union.get(key);
//		case "double":
//			return (Double) union.get(key);
//		case "string":
//			return (String) union.get(key);
//		//default:
//		//	return Class.forName(union.getString(key));
//		}
		
		return maybeUnion;
	}
	
	/**
	 * Widens boxed numbers to Double or Long
	 * @param number A number to widen.
	 * @return A boxed Double or Long depending on whether or not the number was floating-point.
	 */
	public static Number widen(Number number) {
		if (number == null)
			return null;
		if (number instanceof Double || number instanceof Long)
			return number;
		if (number instanceof Float)
			return (Double) number.doubleValue();
		return (Long) number.longValue();
	}
	
	/**
	 * Get the multiplier for a scale
	 * @param scale
	 * @return The multiplier
	 */
	public static double getScaleMultiplier(int scale) {
		switch (scale) {
		case 0:
			return 1;
		case 1:
			return 10;
		case 2:
			return 100;
		case 3:
			return 1000;
		case 4:
			return 10000;
		case 5:
			return 100000;
		default:
			return Math.pow(10, scale);
		}
	}
	
	/**
	 * Returns true if the index type is a type valid for representing a channel index:
	 * OffsetDateTime, Double, or Long.
	 * @param index
	 * @return True if the value is a valid channel index type.
	 */
	public static boolean isValidIndexType(Object index) {
		return index instanceof OffsetDateTime
				|| index instanceof Double
				|| index instanceof Long;
	}

	/**
	 * Returns true if the index type is a type valid for representing a channel value:
	 * Double, Long, String, byte[], double[], String[], or OffsetDateTime.
	 * @param value
	 * @return True if the value is a valid channel value type.
	 */
	public static boolean isValidValueType(Object value) {
		return value instanceof Double
				|| value instanceof Long
				|| value instanceof String
				|| value instanceof byte[]
				|| value instanceof double[]
				|| value instanceof String[]
				|| value instanceof OffsetDateTime;
	}
}
