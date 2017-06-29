package com.petrolink.mbe.rulesflow;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.cache.ChannelCache;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.variables.ChannelAlias;

/**
 * Last Known Value Evaluation Strategy
 * @author paul
 *
 */
public final class LastKnownValueStrategy extends EvaluationStrategy {
	private static final Logger logger = LoggerFactory.getLogger(LastKnownValueStrategy.class);
	private HashMap<ChannelAlias, DataPoint> newValues = new HashMap<>();
	private EventCepEventContent lastAlertStatusEvent;
	private EventDataPointContent lastChannelEvent;
	
	/**
	 * Constructor
	 * @param ruleFlow
	 */
	public LastKnownValueStrategy(RuleFlow ruleFlow) {
		super(ruleFlow);
	}

	@Override
	public ChannelCache createLocalBufferedCache(UUID uuid) {
		return createLocalLKVCache(uuid);
	}
	
	@Override
	public boolean preEvaluation(RuleFlowEvent rev, boolean shouldFilter) {
		EventDataPointContent dpc= rev.getDataPoint();
		EventCepEventContent asc =rev.getAlertStatus();
		
		if (dpc != null && dpc.equals(lastChannelEvent)) {
			return false;
		}
		if (asc != null && asc.equals(lastAlertStatusEvent)) {
			return false;
		}
		
		boolean result = super.preEvaluation(rev, shouldFilter);
		
		if (dpc != null) {
			lastChannelEvent = dpc;
		}
		if (asc != null) {
			lastAlertStatusEvent = asc;
		}
		
		return result;
	}
	
	@Override
	protected boolean prepareExecutionData(UUID eventChannelId, DataPoint eventData) {
		ChannelAlias eventChannel = rule.getDependencies().get(eventChannelId);
		// Event data might not be the last known value if the channel is buffered, so we get that now
		eventData = eventChannel.getCache().getLastDataPoint();
		
		boolean valid = true;
		for (ChannelAlias c : rule.getDependencies().values()) {
			DataPoint dp = c.getCache().getLastDataPoint();
			if (dp == null) {
				valid = false;
				break;
			}
			newValues.put(c, dp);
		}
		
		if (!valid) {
			newValues.clear();
			return false;
		}
		
		newValues.put(eventChannel, eventData);
		
		for (Entry<ChannelAlias, DataPoint> e : newValues.entrySet()) {
			ChannelAlias channel = e.getKey();
			DataPoint dp = e.getValue();
			channel.updateValue(dp);
			logger.trace("Channel {} with data point {} used", channel, dp);
		}
		
		newValues.clear();
		return true;
	}
}
