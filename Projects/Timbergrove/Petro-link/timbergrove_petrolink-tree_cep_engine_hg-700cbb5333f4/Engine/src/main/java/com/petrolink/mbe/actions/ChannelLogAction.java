package com.petrolink.mbe.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.ChronosClientService;
import com.petrolink.mbe.services.PetroVaultMbeOrchestrationService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.engine.Engine;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;

/**
 * The common base class for actions which log information to a channel in Chronos.
 * @author langj
 *
 */
public abstract class ChannelLogAction extends MBEAction {
	protected static final int RETRY_COUNT = 3;
	protected static final String DEFAULT_CHANNEL_LOG_PATH = "logChannel";
	protected static final String UUID_DELIMITER = ";";
	protected static final Charset writerCharset = StandardCharsets.UTF_8;
	protected static final OpenOption[] writerOptions = {
		StandardOpenOption.CREATE,
		StandardOpenOption.WRITE,
		StandardOpenOption.APPEND
	};
	protected static final UUID[] EMPTY_UUIDS = new UUID[0];

	private static final Logger logger = LoggerFactory.getLogger(ChannelLogAction.class);

	protected PetroVaultMbeOrchestrationService mbeService;
	
	protected UUID[] channelUUIDs = EMPTY_UUIDS;
	protected UUID[] identityUUIDs = EMPTY_UUIDS;
	protected Path outputFilePath;
	protected ChronosClientService chronosClientService;
	
	
	
	@Override
	public void load(Element e, Node parent) throws EngineException {
		super.load(e, parent);
		
		chronosClientService = ServiceAccessor.getChronosClientService();
		mbeService = ServiceAccessor.getPVMBEService();
		
		//
		// Parse semicolon-delimited UUIDs. There must be at least one
		//
		
		Namespace ns = e.getNamespace();

		List<Element> channels = e.getChildren("Channel", ns);
		List<Element> identities = e.getChildren("Identity", ns);
		List<Element> users = e.getChildren("User", ns);
		List<Element> groups = e.getChildren("Group", ns);
		
		if (!channels.isEmpty()) {
			ArrayList<UUID> uuids = new ArrayList<>();
			for (Element c : channels) {
				String cid = (String) c.getAttributeValue("id");
				uuids.add(UUID.fromString(cid));
			}
			channelUUIDs = uuids.toArray(new UUID[uuids.size()]);
		}
		
		HashSet<UUID> principals = new HashSet<UUID>();
		if (!identities.isEmpty()) {
			ArrayList<UUID> uuids = new ArrayList<>();
			for (Element c : identities) {
				String cid = (String) c.getAttributeValue("id");
				uuids.add(UUID.fromString(cid));
			}
			principals.addAll(uuids);
			//identityUUIDs = uuids.toArray(new UUID[uuids.size()]);
		}
		
		if (!users.isEmpty()) {
			ArrayList<UUID> uuids = new ArrayList<UUID>();
			for (Element c : users) {
				String cid = (String) c.getAttributeValue("id");
				uuids.add(UUID.fromString(cid));
			}
			principals.addAll(uuids);
		}
		
		if (!groups.isEmpty()) {
			ArrayList<UUID> uuids = new ArrayList<UUID>();
			for (Element c : groups) {
				String cid = (String) c.getAttributeValue("id");
				uuids.add(UUID.fromString(cid));
			}
			principals.addAll(uuids);
		}
		
		identityUUIDs = principals.toArray(new UUID[principals.size()]);
		
		if (channelUUIDs.length == 0 && identityUUIDs.length == 0)
			throw new EngineException("No channel or identitiy UUIDs were specified");

		//
		// Create test logging directories
		//
		Properties props = Engine.getInstance().getUserDefinedProperties();
		String directoryName = props.getProperty(getDirectoryPropertyName());
		Path logDir;
		if (StringUtils.isBlank(directoryName)) {
			logDir = Paths.get(System.getProperty("user.dir"), DEFAULT_CHANNEL_LOG_PATH, getDirectoryPropertyName());
		} else {
			logDir = Paths.get(System.getProperty("user.dir"), directoryName);
		}
		
				
		try {
			Files.createDirectories(logDir);
		} catch (IOException ex) {
			throw new EngineException("Failed to create log directories: " + logDir, ex);
		}
		
		//
		// Determine the test file name and write its header
		//
		
		String testFileName = e.getAttributeValue("testFileName");
		if (testFileName != null) {
			outputFilePath = logDir.resolve(testFileName + ".csv");
		} else {
			outputFilePath = logDir.resolve("ruleOutput"+this.getRuleFlow().getRuleId()+".csv");
			//outputFilePath = logDir.resolve(channelUUIDs[0].toString() + ".csv");
		}
		
		if (!Files.exists(outputFilePath)) {
			try (BufferedWriter output = Files.newBufferedWriter(outputFilePath, writerCharset, writerOptions)) {
				writeTestLogHeader(output);
				output.flush();
			}
			catch (Exception ex) {
				EngineException en = new EngineException("Exception while writing output file header");
				en.initCause(ex);
				throw en;
			}
		}
	}

	@Override
	protected int executeTestAction(Map<String, Object> context) throws EngineException {
		try (BufferedWriter output = Files.newBufferedWriter(outputFilePath, writerCharset, writerOptions)) {
			writeTestLog(context, output);
			output.flush();
		}
		catch (Exception e) {
			throw new EngineException("Exception while writing output file", e);
			//return FAIL;
		}
		return SUCCESS;
	}

	@Override
	public void init(Map<String, Object> context) throws EngineException {
		// No Initialization required
	}

	@Override
	public void finalize(Map<String, Object> context) throws EngineException {
		// No Finalization required		
	}
	
	/**
	 * Define what is actual content being sent, will be used for logging
	 * @return
	 */
	protected abstract String getChannelLogType();
	
	protected abstract String getDirectoryPropertyName();
	
	protected abstract void writeTestLogHeader(BufferedWriter writer) throws Exception;
	
	protected abstract void writeTestLog(Map<String, Object> context, BufferedWriter writer) throws Exception;
	
	protected void updateChannels(Object index, Object data) {
		// This is an uncommon path
		ArrayList<DataPoint> list = new ArrayList<>(1);
		list.add(new DataPoint(index, data));
		updateChannelsCore(list, false);
	}
	
	protected void updateChannels(List<DataPoint> data) {
		updateChannelsCore(data, false);
	}
	
	protected void updateChannelsAsync(Object index, Object data) {
		// This is an uncommon path
		ArrayList<DataPoint> list = new ArrayList<>(1);
		list.add(new DataPoint(index, data));
		updateChannelsCore(list, true);
	}
	
	protected void updateChannelsAsync(List<DataPoint> data) {
		updateChannelsCore(data, true);
	}
	
	private void updateChannelsCore(List<DataPoint> data, boolean async) {
		if (data.isEmpty()) {
			logger.warn("no data for update channels call");
			return;
		}
		
		RuleFlow ruleFlow = this.getRuleFlow();
		String ruleId = ruleFlow != null ? ruleFlow.getRuleId().toString() : "(unknown)";
		
		ArrayList<CompletableFuture<Void>> futures = null;
		
		if (channelUUIDs.length != 0) {
			logger.debug("Updating {} {} channels by rule {}",  getChannelLogType(),  channelUUIDs.length, ruleId);
			
			for (UUID uuid : channelUUIDs) {
				logger.debug("Sending {} to channel {} with {} data points starting at index {}", getChannelLogType(), uuid, data.size(), data.get(0).getIndex());
				CompletableFuture<Void> f = updateChannelWithRetry(uuid, data, async);
				if (f != null) {
					if (futures == null)
						futures = new ArrayList<>();
					futures.add(f);
				}
			}
		}
		
		if (identityUUIDs.length != 0) {
			logger.debug("Resolving {} identities to user channels by rule {}", identityUUIDs.length, ruleId);
			
			// User and group identity UUIDs are resolved to user-specific channels that need notifications
			HashSet<UUID> userChannels = getNotificationChannelIds(Arrays.asList(identityUUIDs));
			
			logger.debug("Updating {} {} user channels by rule {}", getChannelLogType(), userChannels.size(), ruleId);
			
			for (UUID uuid : userChannels) {
				logger.debug("Sending {} to user channel {} with {} data points starting at index {}", getChannelLogType(), uuid, data.size(), data.get(0).getIndex());
				CompletableFuture<Void> f = updateChannelWithRetry(uuid, data, async);
				if (f != null) {
					if (futures == null)
						futures = new ArrayList<>();
					futures.add(f);
				}
			}
		}
		
		if (futures != null) {
			CompletableFuture<Void> allf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
			try {
				allf.join();
			} catch (Exception e) {
				logger.error("Error occurred during channel updates", e);
			}
		}
	}
	
	private CompletableFuture<Void> updateChannelWithRetry(UUID uuid, List<DataPoint> data, boolean async) {
		if (UUIDHelper.isNullOrEmpty(uuid)) {
			logger.warn("skipping empty UUID");
			return null;
		}
		
		if (async) {
			return new RetryingChannelUpdateExecutor(uuid, data, RETRY_COUNT).execute();
		}
		
		for (int i = 1; i <= RETRY_COUNT; i++) {
			try {
				chronosClientService.getClient().updateChannel(uuid, data);
				break;
			}
			catch (Exception e) {
				logger.error("exception when updating channel", e);
				if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 403) {
					logger.warn("will not retry due to HTTP code 403");
					break;
				}
				if (i < RETRY_COUNT) {
					logger.warn("retrying update");
					continue;
				}
				logger.error("Channel {} update failed: {}. Index {} , data: \n  {}", getChannelLogType(), uuid, data.get(0).getIndex(), data);
				break;
			}
		}
		return null;
	}
	
	/**
	 * Resolve list of principal Ids to Notification Channel UUID.
	 * @param principals
	 * @return
	 */
	protected HashSet<UUID> getNotificationChannelIds(final Iterable<UUID> principals) {
// OLD HACK		
//		HashSet<String> channelUris = principalService.getNotificationChannelUris(principals);
//		
//		HashSet<UUID> results = new HashSet<>();
//		for (String channelUri : channelUris) {
//			UUID channelUUID = resourceService.getResourceIdByUri(channelUri);
//			if (!UUIDHelper.isNullOrEmpty(channelUUID)) {
//				results.add(channelUUID);
//			}
//		}
//		return results;
		HashSet<UUID> results = mbeService.getNotificationChannelIds(getInstanceId(), principals);
		return results;
	}
	
	protected static String escapeCsv(String value) {
		if (value.contains("\""))
			value = value.replace("\"", "\"\"");
		if (value.contains(","))
			value = "\"" + value + "\"";
		return value;
	}
	
	protected static Object toJsonVariant(Object value) {
		if (value instanceof DataPoint) {
			DataPoint dp = (DataPoint) value;
			JSONObject jo = new JSONObject();
			jo.put("index", dp.getIndex());
			jo.put("value", dp.getValue());
			return jo;
		}
		return value;
	}
	
	private class RetryingChannelUpdateExecutor {
		private final UUID uuid;
		private final List<DataPoint> data;
		private final int maxRetries;
		private int retryNumber;
		private final CompletableFuture<Void> future = new CompletableFuture<>();
		
		RetryingChannelUpdateExecutor(UUID uuid, List<DataPoint> data, int maxRetries) {
			this.uuid = uuid;
			this.data = data;
			this.maxRetries = maxRetries;
		}
		
		CompletableFuture<Void> execute() {
			chronosClientService.updateChannelAsync(uuid, data).whenComplete((r, e) -> onComplete(e));
			return future;
		}
		
		private void onComplete(Throwable e) {
			if (e == null) {
				future.complete(null);
				return;
			}

			logger.error("An exception occurred during an asynchronous update for channel " + uuid, e);
			// If we're not authorized then no point in retries
			if (e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() == 403) {
				logger.warn("will not retry due to HTTP code 403");
				future.completeExceptionally(e);
				return;
			}
			if (retryNumber >= maxRetries) {
				logger.error("Channel {} update failed: {}. Index {} , data: \n  {}", getChannelLogType(), uuid, data.get(0).getIndex(), data);
				future.completeExceptionally(e);
				return;
			}
			retryNumber++;
			logger.error("Beginning retry {} of {}", retryNumber, maxRetries);
			chronosClientService.updateChannelAsync(uuid, data).whenComplete((r, uex) -> onComplete(uex));
		}
	}
}
