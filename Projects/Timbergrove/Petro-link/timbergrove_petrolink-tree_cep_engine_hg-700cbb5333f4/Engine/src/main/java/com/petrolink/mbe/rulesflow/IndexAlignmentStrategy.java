package com.petrolink.mbe.rulesflow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.cache.ChannelCacheConsumer;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.variables.ChannelAlias;
import com.petrolink.mbe.rulesflow.variables.Variable;
import com.petrolink.mbe.util.HashHelper;

/**
 * Cache Window Indexed aligment evaluation strategy. The Rule keeps a local buffer of the dependent channels and evaluates to 
 * a set of unique evaluation contexts containing all the data points from the channels within the defined window. The window 
 * is defined as the higher of the gaps between to channels.
 * @author paul
 *
 */
public final class IndexAlignmentStrategy extends EvaluationStrategy {
	private static final Logger logger = LoggerFactory.getLogger(IndexAlignmentStrategy.class);
	
	private final HashMap<ChannelAlias, DataPoint> newValues = new HashMap<>();
	private final HashMap<ChannelAlias, Object> lastUsedIndices = new HashMap<>();
	//private final ArrayDeque<DataPoint> loggerQueue = new ArrayDeque<>();
	private int lastValidIndexHash;
	private final ArrayDeque<DataPoint> eventQueue = new ArrayDeque<>();
	//private final ArrayDeque<DataPoint> nextDataPointQueue = new ArrayDeque<>();
	private final NextDataPointConsumer ndpConsumer = new NextDataPointConsumer();
	private final QueueConsumer queueConsumer = new QueueConsumer();
	private ChannelAlias[] channelAliases;
	
	/**
	 * Constructor
	 * @param ruleFlow
	 */
	public IndexAlignmentStrategy(RuleFlow ruleFlow) {
		super(ruleFlow);
		queueConsumer.queue = eventQueue;
	}

	@Override
	protected boolean prepareExecutionData(UUID eventChannelId, DataPoint eventData) {
		// PERF This is a very frequently used code path. Code has been optimized to minimize object allocations
		//      unless absolutely necessary.
		assert newValues.isEmpty() : "newValues should have been cleared";
		
		//if (logger.isTraceEnabled()) {
		//	for (ChannelAlias ca : rule.getDependencies().values()) {
		//		ca.getCacheData(loggerQueue);
		//		logger.trace("{} data points: {}", ca.getAlias(), StringHelper.join(", ", loggerQueue));
		//		loggerQueue.clear();
		//	}
		//}
		
		// All data in the event channel is copied into a local queue in order from lowest to highest index
		// That means we're always trying to realign with old data that was already queued.
		ChannelAlias eventChannel = rule.getDependencies().get(eventChannelId);
		ArrayDeque<DataPoint> eventQueue = this.eventQueue;
		eventChannel.getCacheData(queueConsumer, lastUsedIndices.get(eventChannel), true);
		if (eventQueue.isEmpty())
			return false; // this shouldn't really happen
		
		// Lazily initialize the array of channel aliases available from this rule
		ChannelAlias[] channelAliases = this.channelAliases;
		if (channelAliases == null) {
			ArrayList<ChannelAlias> ca = new ArrayList<>();
			for (Variable v : rule.getConditionVariables().values()) {
				if (v instanceof ChannelAlias)
					ca.add((ChannelAlias) v);
			}
			this.channelAliases = channelAliases = ca.toArray(new ChannelAlias[ca.size()]);
		}
		
		boolean result = false;
		DataPoint edp = eventQueue.poll();
		
		while (edp != null) {
			while (true) {
				// this is a hash of the indices for all channels, it relies on the hashing being
				// done in the same order every loop no matter which channel is the event channel
				// this will be compared to the hash produced for the last valid data set to
				// determine if they are the same. Note that hashing isn't 100% reliable as there is a
				// small chance of collision, but this is very remote and there is no harm in doing so.
				int indexHash = HashHelper.PRIME_INTEGER; 
				boolean valid = true;
				for (ChannelAlias oc : channelAliases) {
					DataPoint ndp;
					if (oc != eventChannel) {
						ndp = nextDataPoint(oc, eventChannel, edp);
						if (ndp == null) {
							valid = false;
							break;
						}
					} else {
						ndp = edp;
					}
					
					newValues.put(oc, ndp);
					indexHash = HashHelper.combineHashCodes(indexHash, ndp.getIndex().hashCode());
				}
				
				// If the index hashes match then there was no change
				if (!valid || indexHash == lastValidIndexHash) {
					newValues.clear();
					break;
				}
				lastValidIndexHash = indexHash;
				
				// At this point the data is ready for evaluation 
				for (Entry<ChannelAlias, DataPoint> e : newValues.entrySet()) {
					ChannelAlias channel = e.getKey();
					UUID channelId = channel.getUuid();
					DataPoint channelData = e.getValue();
					
					channel.updateValue(channelData);
					lastUsedIndices.put(channel, channelData.getIndex());
					
					logger.trace("Channel {} with data point {} used", channelId, channelData);
				}
				
				newValues.clear();
				result = true;
				break;
			}
			
			if (result)
				break;
			
			edp = eventQueue.poll();
		}
		eventQueue.clear();

		return result;
	}

	/**
	 * Gets the next data point to be used for a channel for evaluation. This is the most significant part of the
	 * index alignment algorithm. If no valid data point exists for evaluation, null will be returned.
	 * 
	 * The algorithm will look at a range of indices for a target channel and select the data point within this
	 * range with the highest index. This range is determined by two things: the index of the data point that
	 * triggered evaluation (the event index), and a gap value. The gap value is the maximum allowed difference between
	 * the event index and the index of the target channel data point being tested. This means the next data point
	 * can have an index lower or higher than the event index.
	 * 
	 * The gap value is the essential component of the algorithm and will determine the nature of rule evaluations.
	 * An example case would have channels with different data frequencies and gaps:
	 * 
	 * Channel | Frequency |    Gap
	 * ----------------------------
	 *       A |       1 s |  0.5 s
	 *       B |       1 s |  0.5 s
	 *       C |      10 s | 10.0 s
	 * 
	 * If a data point is received on channel A. It will use a half second gap when aligning with channel B, but a
	 * 10 second gap when aligning with channel C. This will allow 10 evaluations in 10 seconds because the value from
	 * channel C can be reused within a 10 second window. If channel C's gap were set to a half second, then
	 * evaluation would only happen once every 10 seconds because that is the only time channel C would have a value
	 * that would be able to align with channels A and B.
	 * 
	 * It's also important to note that the half second gap in a channel with a one second frequency ensures that when
	 * a new data point is received on channel A it is not aligned with the value from the previous second from channel B.
	 * If the gap were set to one second then each new data point received for A and B would trigger a new evaluation
	 * even before a whole new "row" of data had been received.
	 * 
	 * Using 0.5s gap for A and B and 10s gap for C:
	 * 
	 * | Receive | Evaluated
	 * |---------|---------------
	 * | A-1     | 
	 * | B-1     | 
	 * | C-1     | A-1 B-1 C-1
	 * | A-2     | 
	 * | B-2     | A-2 B-2 C-1
	 * | A-3     |
	 * | B-3     | A-3 B-3 C-1
	 * | ...     | ...
	 * | A-10    |
	 * | B-10    | A-10 B-10 C-1
	 * | C-10    | A-10 B-10 C-10
	 * | A-11    | 
	 * | B-11    | A-11 B-11 C-10
	 * 
	 * Using 1s gap for A and B and 10s gap for C:
	 * 
	 * | Receive | Evaluated
	 * |---------|---------------
	 * | A-1     |
	 * | B-1     |
	 * | C-1     | A-1 B-1 C-1
	 * | A-2     | A-2 B-1 C-1
	 * | B-2     | A-2 B-2 C-1
	 * | A-3     | A-3 B-2 C-1
	 * | B-3     | A-3 B-3 C-1
	 * | ...     | ...
	 * | A-10    | A-10 B-9 C-1
	 * | B-10    | A-10 B-10 C-1
	 * | C-10    | A-10 B-10 C-10
	 * | A-11    | A-11 B-10 C-10
	 * | B-11    | A-11 B-11 C-10
	 * 
	 * The algorithm never looks at data points with indices that are lower than the one previously used for evaluation
	 * on a particular channel. However, the lowest data point in a channel can be used for multiple evaluations.
	 * 
	 * @param targetChannel The channel that data is being retrieved from
	 * @param eventChannel The channel that caused the rule evaluation event
	 * @param eventData The data point in the event channel that is used to determine the index alignment range.
	 * @return A valid data point from the target channel, or null if none was found.
	 */
	private DataPoint nextDataPoint(ChannelAlias targetChannel, ChannelAlias eventChannel, DataPoint eventData) {
		NextDataPointConsumer consumer = this.ndpConsumer;
		
		// Prepare the consumer for this operation.
		consumer.gap = Math.max(targetChannel.getGap(), eventChannel.getGap());
		consumer.numericEventIndex = DataPoint.numericValue(eventData.getIndex());
		consumer.result = null;

		// lastUsedIndices will be empty if this is the first evaluation, but getCacheData() will handle null appropriately.
		targetChannel.getCacheData(consumer, lastUsedIndices.get(targetChannel), true);

		return consumer.result;
	}
	
	private static class NextDataPointConsumer implements ChannelCacheConsumer {
		double gap;
		double numericEventIndex;
		DataPoint result;

		@Override
		public boolean accept(DataPoint dp) {
			//Object index = dp.getIndex();
			//// Break if the index types do not match. If this is true there is a problem somewhere else but
			//// we break here to avoid exception
			//if (!eventIndexClass.isInstance(index)) {
			//	logger.warn("comparing indices of different types between channel {} and {}", eventChannel, this);
			//	continue;
			//}
			double numericIndex = DataPoint.numericValue(dp.getIndex());
			if (Math.abs(numericIndex - numericEventIndex) <= gap) {
				// If the received index is within the gap in either direction then the data point is valid
				result = dp;
				// Continue iterating through the cache until we reach the highest value that is also within the gap
			}
			else if (numericIndex > numericEventIndex) {
				// This index is outside the gap range but greater than the event index, so no action can be taken.
				return false;
			}
			return true;
		}
	}
	
	private static class QueueConsumer implements ChannelCacheConsumer {
		ArrayDeque<DataPoint> queue;
		
		@Override
		public boolean accept(DataPoint dp) {
			queue.add(dp);
			return true;
		}
	}
}
