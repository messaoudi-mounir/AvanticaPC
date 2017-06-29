package com.petrolink.mbe.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.pvclient.ChronosClient;
import com.petrolink.mbe.pvclient.ChronosClient.SimpleChannelMetadata;
import com.petrolink.mbe.setting.HttpClientConnectionSettings;
import com.petrolink.mbe.util.NamedThreadFactory;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.engine.exceptions.EngineException;

/**
 * A Service handling operation for Chronos Client
 * @author Joel Lang, Aristo
 *
 */
public final class ChronosClientService extends HttpClientService {
	private static final Logger logger = LoggerFactory.getLogger(ChronosClientService.class);
	private ChronosClient client;
	private ExecutorService executor;
	private ConcurrentHashMap<UUID, SimpleChannelMetadata> metadataCache = new ConcurrentHashMap<>();
	
	/**
	 * Get Chronos Client used by this service.
	 * @return {@link ChronosClient} used by this service
	 */
	public ChronosClient getClient() {
		return client;
	}
	
	/**
	 * Load configuration for the Chronos Client
	 */
	@Override
	public void load(Element e) throws EngineException {
		super.load(e);
		
		HttpClientConnectionSettings cs = loadConnectionSettings(e.getChild("Connection", e.getNamespace()));

		client = new ChronosClient(cs.getURL().toString(), cs.getAuthentication());
	}
	
	@Override
	public void startService() {
		// The threads used by this service will be mostly IO bound and blocking on HTTP actions.
		// Because of this we don't really need to limit the number of threads to the number of processors.
		// The ideal solution though would be an asynchronous HTTP client so threads like this aren't needed.
		// That's a solution to consider for the future.
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4,
		                                        new NamedThreadFactory("chronos-svc-async-"));
		
		setRunning(true);
	}

	@Override
	public void stopService() {
		setRunning(false);
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Get the latest data point in a channel
	 * @param channelId
	 * @return The latest data point, or null if the channel is empty or doesn't exist.
	 * @throws IOException
	 */
	public DataPoint getLatestChannelData(UUID channelId) throws IOException {
		List<DataPoint> data = getLatestChannelData(channelId, 1);
		return !data.isEmpty() ? data.get(0) : null;
	}
	
	/**
	 * Get the latest data point in a channel asynchronously
	 * @param channelId
	 * @return A future that completes with the latest data point, or null if the channel is empty or doesn't exist.
	 */
	public CompletableFuture<DataPoint> getLatestChannelDataAsync(UUID channelId) {
		Supplier<DataPoint> s = () -> {
			try {
				return getLatestChannelData(channelId);
			} catch (IOException e) {
				logger.error("exception while getting latest channel data", e);
				return null;
			}
		};
		return CompletableFuture.supplyAsync(s, executor);
	}
	
	/**
	 * Get the latest data points in a channel according to a limit
	 * @param channelId
	 * @param limit
	 * @return A list of the latest data points. Will be empty if the channel doesn't exist or there is no data.
	 * @throws IOException
	 */
	public List<DataPoint> getLatestChannelData(UUID channelId, int limit) throws IOException {
		if (limit < 1)
			limit = 1;
		
		// We're not using Caffeine here because static metadata can stay around forever
		SimpleChannelMetadata metadata = metadataCache.get(channelId);
		if (metadata == null) {
			metadata = getStaticChannelMetadata(channelId);
			if (metadata == null)
				return new ArrayList<DataPoint>();
			metadataCache.putIfAbsent(channelId, metadata);
		}
		
		return client.getLatestChannelDataAsDataPoints(channelId, limit, metadata.getIndexIsTime(), metadata.getIndexScale());
	}
	
	/**
	 * Asynchronously performs a channel update.
	 * @param channelId
	 * @param data
	 * @return A CompletableFuture that completes when the operation is finished
	 */
	public CompletableFuture<Void> updateChannelAsync(UUID channelId, List<DataPoint> data) {
		if (UUIDHelper.isNullOrEmpty(channelId))
			throw new IllegalArgumentException("channelId must not be null or empty");
		if (data == null || data.isEmpty())
			throw new IllegalArgumentException("data must not be null or empty");
		
		// Simply copy this for safety
		ArrayList<DataPoint> dataCopy = new ArrayList<>(data);
		
		Supplier<Void> s = () -> {
			try {
				client.updateChannel(channelId, dataCopy);
			} catch (IOException e) {
				throw new RuntimeException("IOException during updateChannel for channel " + channelId, e);
			}
			return null;
		};
		
		return CompletableFuture.supplyAsync(s, executor);
	}
	
	private SimpleChannelMetadata getStaticChannelMetadata(UUID channelId) throws IOException {
		return client.getChannelById(channelId);
//		ChannelMetadataRecord metadata = client.getChannelById(channelId);
//		if (metadata == null)
//			return null;
//		IndexMetadataRecord indexMetadata = metadata.getIndexes().get(0);
//		return new StaticChannelMetadata(indexMetadata.getIndexType(), indexMetadata.getScale());
	}
}
