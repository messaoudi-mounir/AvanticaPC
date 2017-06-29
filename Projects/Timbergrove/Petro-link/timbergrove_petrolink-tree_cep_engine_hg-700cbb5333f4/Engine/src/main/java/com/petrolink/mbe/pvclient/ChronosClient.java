package com.petrolink.mbe.pvclient;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.Utf8;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;
import com.petrolink.mbe.metrics.MetricSystem;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.setting.HttpAuthentication;
import com.petrolink.mbe.util.StringBuilderCache;
import com.petrolink.mbe.util.UUIDHelper;

import Energistics.Datatypes.ChannelData.ChannelIndexTypes;
import Energistics.Datatypes.ChannelData.ChannelMetadataRecord;
import Energistics.Datatypes.ChannelData.DataItem;
import Energistics.Protocol.ChannelStreaming.ChannelData;

/**
 * A client for the Chronos API
 * @author Joel Lang
 *
 */
public final class ChronosClient extends RestHttpClient {
	/**
	 * The default scaling used for indices in Chronos.
	 */
	public static final int DOUBLE_INDEX_SCALE = 5;
	
	private static final Logger logger = LoggerFactory.getLogger(ChronosClient.class);
	
	private static final Timer getChannelTimer = MetricSystem.timer(ChronosClient.class, "get-channel-time");
	private static final Timer getDataTimer = MetricSystem.timer(ChronosClient.class, "get-data-range-time");
	private static final Timer getLatestDataTimer = MetricSystem.timer(ChronosClient.class, "get-data-latest-time");
	private static final Timer singleUpdateTimer = MetricSystem.timer(ChronosClient.class, "update-channel-single-time");
	private static final Timer multiUpdateTimer = MetricSystem.timer(ChronosClient.class, "update-channel-multi-time");
	private static final Counter updatedDataCounter = MetricSystem.counter(ChronosClient.class, "updated-datapoint-count");
	
	private final SpecificDatumReader<ChannelMetadataRecord> metadataRecordReader = new SpecificDatumReader<>(ChannelMetadataRecord.getClassSchema());
	private final SpecificDatumReader<ChannelData> dataReader = new SpecificDatumReader<>(ChannelData.getClassSchema());
	
	/**
	 * 
	 * @param urlPrefix
	 * @param auth
	 */
	public ChronosClient(String urlPrefix, HttpAuthentication auth) {
		super(urlPrefix, auth, DEFAULT_TIMEOUT);
	}

	/**
	 * Get a ChannelMetadataRecord by its ID.
	 * @param id The channel ID
	 * @return A matching ChannelMetadataRecord, or null if no match was found
	 * @throws IOException
	 */
	public SimpleChannelMetadata getChannelById(UUID id) throws IOException {
		String url = String.format("%schronos/channels/%s", urlPrefix, id);
		
		// This is a workaround for the bug detailed in PVHD-607 which is a 500 internal server error whenver
		// we try to get a ChannelMetadataRecord using Avro. The JSON API is used instead for now.
		JSONObject result;
		try (Timer.Context tc = getChannelTimer.time()) {
			result = toJSONObject(getString(url, true, -1));
		}
		
		if (result == null)
			return null;
		
		UUID uuid = UUIDHelper.fromStringFast(result.getJSONObject("Uuid").getString("string"));
		String name = result.getString("ChannelName");
		JSONObject indexObject = result.getJSONArray("Indexes").getJSONObject(0);
		ChannelIndexTypes indexType = ChannelIndexTypes.values()[indexObject.getInt("IndexType")];
		int indexScale = indexObject.getInt("Scale");
		
		return new SimpleChannelMetadata(uuid, name, indexType, indexScale);
	}

	/**
	 * Get a range of data from a channel.
	 * @param channelId A channel UUID.
	 * @param startIndex A starting index. Must be Long, Double, or OffsetDateTime.
	 * @param endIndex An ending index. Must be Long, Double, or OffsetDateTime.
	 * @return The matching ChannelData.
	 * @throws IOException
	 */
	public ChannelData getChannelData(UUID channelId, Object startIndex, Object endIndex) throws IOException {
		String url = String.format("%schronos/channels/data/%s", urlPrefix, channelId);
		
		Long startIndexLong = ApiConverter.encodeIndex(startIndex, DOUBLE_INDEX_SCALE);
		Long endIndexLong = ApiConverter.encodeIndex(endIndex,  DOUBLE_INDEX_SCALE);
		
		String queryOptions = makeChannelQueryOptions(startIndexLong, endIndexLong);
		
		try (Timer.Context tc = getDataTimer.time()) {
			return postAvro(url, queryOptions, dataReader, -1);
		}
	}
	
	/**
	 * Get a range of data from a channel.
	 * @param channelId A channel UUID.
	 * @param startIndex A starting index. Must be Long, Double, or OffsetDateTime.
	 * @param endIndex An ending index. Must be Long, Double, or OffsetDateTime.
	 * @return A list of data points. Never null.
	 * @throws IOException
	 */
	public List<DataPoint> getChannelDataAsDataPoints(
		UUID channelId,
		Object startIndex,
		Object endIndex
	) throws IOException {
		ChannelData restResult = getChannelData(channelId, startIndex, endIndex);
		
		ArrayList<DataPoint> results = new ArrayList<>();
		
		if (restResult == null)
			return results;
		
		// We might be getting a huge amount of data, so want to allocate now
		results.ensureCapacity(restResult.getData().size());
		
		boolean isTimeIndex = startIndex instanceof OffsetDateTime || endIndex instanceof OffsetDateTime;
		int doubleIndexScale = startIndex instanceof Double || endIndex instanceof Double ? DOUBLE_INDEX_SCALE : 0;
		
		for (DataItem d : restResult.getData())
			results.add(toDataPoint(d, isTimeIndex, doubleIndexScale));

		return results;
	}
	
	/**
	 * Get the latest data points in a channel up to a specified limit.
	 * @param channelId A channel ID
	 * @param limit A limit to the number of latest data points returned. Minimum is 1
	 * @param isTimeIndex True if the channel index is a datetime index
	 * @param scale The scale of the index if its not a time index
	 * @return A list of data points
	 * @throws IOException
	 */
	public List<DataPoint> getLatestChannelDataAsDataPoints(UUID channelId, int limit, boolean isTimeIndex, int scale) throws IOException {		
		if (limit < 1)
			limit = 1;
		String url = String.format("%schronos/channels/data/latest/%s?limit=%s", urlPrefix, channelId, limit);
		
		ChannelData data;
		try (Timer.Context tc = getLatestDataTimer.time()) {
			data = getAvro(url, true, dataReader, -1);
		}

		ArrayList<DataPoint> results = new ArrayList<>();
		
		if (data == null || data.getData().isEmpty())
			return results;
		
		// HACK: Chronos API was returning 0 for scale for some versions which was fixed in PVHD-703.
		//       Since Long is never used for the index instead of Double in our case, we'll just assume that 0 indicates
		//       we're connecting to a version of PVHD with this bug.
		if (scale == 0)
			scale = DOUBLE_INDEX_SCALE;
		
		for (DataItem d : data.getData())
			results.add(toDataPoint(d, isTimeIndex, scale));
		
		return results;
	}
	
	/**
	 * Update a channel with a single value.
	 * @param channelId The channel ID
	 * @param index The index to update
	 * @param value The value to update with
	 * @throws IOException
	 */
	public void updateChannel(UUID channelId, Object index, Object value) throws IOException {
		logger.debug("Updating channel {} at index {} with value {}", channelId, index, value);
		
		Object encodedIndex = ApiConverter.encodeIndex(Objects.requireNonNull(index), DOUBLE_INDEX_SCALE);
		if (encodedIndex == null)
			throw new IllegalArgumentException("invalid index type: " + index.getClass());
		
		// PERF: Due to the frequency of evaluation, this could be a pretty hot path for evaluation log updates
		//       with a large number of rules. We're using a StringBuilder here to try to avoid some extra overhead
		//       from the String.format() method.
		StringBuilder sb = StringBuilderCache.allocate(120);
		
		sb.append(urlPrefix)
		  .append("chronos/channels/data/")
		  .append(channelId.toString());
		
		String url = sb.toString();
		sb.setLength(0);
		
		sb.append("{\"Data\":[{\"Indexes\":[")
		  .append(encodedIndex)
		  .append("],\"ChannelId\":0,\"Value\":{\"Item\":")
		  .append(JSONObject.valueToString(value))
		  .append("},\"ValueAttributes\":[]}]}");
		
		String body = StringBuilderCache.toStringAndFree(sb);
		
		//logger.trace("update body: {}", body);
		
//		String url = String.format("%schronos/channels/data/%s", urlPrefix, channelId);
//		
//		// For a single index and value just do it quick and dirty
//		String bodyFormat = "{\"Data\":[{\"Indexes\":[%s],\"ChannelId\":0,\"Value\":{\"Item\":%s},\"ValueAttributes\":[]}]}";
//		
//		String body = String.format(bodyFormat,
//				                    JSONObject.valueToString(toJsonVariant(index)),
//				                    JSONObject.valueToString(toJsonVariant(value)));
		try (Timer.Context tc = singleUpdateTimer.time()) {
			putString(url, body, -1);
		}
		
		updatedDataCounter.inc();
	}

	
	/**
	 * Update a channel with a series of data points.
	 * @param channelId The channel ID
	 * @param data A collection of data points.
	 * @throws IOException
	 */
	public void updateChannel(UUID channelId, Collection<DataPoint> data) throws IOException {
		logger.debug("Updating channel {} with {} data points", channelId, data.size());
		
		if (data.isEmpty()) {
			logger.warn("no data points for update");
			return;
		}
		
		StringBuilder sb = new StringBuilder(20 + data.size() * 70);
		
		sb.append(urlPrefix)
		  .append("chronos/channels/data/")
		  .append(channelId.toString());
		
		String url = sb.toString();
		sb.setLength(0);
		
		sb.append("{\"Data\":[");
		for (DataPoint dp : data) {
			Object encodedIndex = ApiConverter.encodeIndex(dp.getIndex(), DOUBLE_INDEX_SCALE);
			sb.append("{\"Indexes\":[")
			  .append(encodedIndex)
			  .append("],\"ChannelId\":0,\"Value\":{\"Item\":")
			  .append(JSONObject.valueToString(dp.getValue()))
			  .append("},\"ValueAttributes\":[]},");
		}
		sb.setLength(sb.length() - 1); // chop off the last comma
		sb.append("]}");
		
		try (Timer.Context tc = multiUpdateTimer.time()) {
			putString(url, sb.toString(), -1);
		}
		updatedDataCounter.inc(data.size());
	}
	
	private static String makeChannelQueryOptions(Long startIndex, Long endIndex) {
		StringBuilder sb = StringBuilderCache.allocate(64);
		
		sb.append("{\"StartIndex\":").
		   append(startIndex != null ? startIndex : "null").
		   append(",\"EndIndex\":").
		   append(endIndex != null ? endIndex : "null").
		   append("}");
		
		return StringBuilderCache.toStringAndFree(sb);
		
//		JSONObject op = new JSONObject();
//		op.put("StartIndex", startIndex != null ? startIndex : JSONObject.NULL);
//		op.put("EndIndex", endIndex != null ? endIndex : JSONObject.NULL);
//		return op.toString();
	}
	
	private static DataPoint toDataPoint(DataItem d, boolean isTimeIndex, int scale) {
		Object value = d.getValue().getItem();
		if (value instanceof Number)
			value = ApiConverter.widen((Number) value);
		else if (value instanceof Utf8)
			value = ((Utf8) value).toString();
		Object index = ApiConverter.decodeIndex(d.getIndexes().get(0), isTimeIndex, scale);
		return new DataPoint(index, value);
	}
	
	// Alternative to ChannelMetadataRecord for PVHD-607 workaround
	@SuppressWarnings("javadoc")
	public static class SimpleChannelMetadata {
		private final UUID id;
		private final String name;
		private final ChannelIndexTypes indexType;
		private final int indexScale;
		
		public SimpleChannelMetadata(UUID id, String name, ChannelIndexTypes indexType, int indexScale) {
			this.id = id;
			this.name = name;
			this.indexType = indexType;
			this.indexScale = indexScale;
		}
		
		public UUID getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public ChannelIndexTypes getIndexType() {
			return indexType;
		}
		
		public boolean getIndexIsTime() {
			return indexType == ChannelIndexTypes.Time;
		}
		
		public int getIndexScale() {
			return indexScale;
		}
	}
}
