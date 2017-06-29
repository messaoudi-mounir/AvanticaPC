package com.petrolink.mbe.rulesflow;

import static com.petrolink.mbe.rulesflow.RuleFlow.CHANNEL_CTX_VARIABLE;
import static com.petrolink.mbe.rulesflow.RuleFlow.INDEX_CTX_VARIABLE;
import static com.petrolink.mbe.rulesflow.RuleFlow.VALUE_CTX_VARIABLE;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.mvel2.integration.VariableResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.cache.ChannelCache;
import com.petrolink.mbe.cache.impl.BufferedChannelCacheImpl;
import com.petrolink.mbe.cache.impl.LKVChannelCacheImpl;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.variables.AlertClassVariable;
import com.petrolink.mbe.rulesflow.variables.BufferedChannelAlias;
import com.petrolink.mbe.rulesflow.variables.ChannelAlias;
import com.petrolink.mbe.rulesflow.variables.RuleVariableResolverFactory;
import com.petrolink.mbe.rulesflow.variables.Variable;
import com.petrolink.mbe.rulesflow.variables.VariableDatum;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.util.EngineContext;
import com.smartnow.engine.util.MVELUtil;
import com.smartnow.engine.util.VariableResolverFactoryContext;

/**
 * Provide a way to define multiple evaluation strategies that will be used by
 * the Rule Flow. It support the externalization of the filtering and context
 * preparation processes.
 * 
 * @author Paul Solano
 *
 */
public abstract class EvaluationStrategy {
	private static final Logger logger = LoggerFactory.getLogger(EvaluationStrategy.class);
	
	protected final RuleFlow rule;
	protected boolean validateDependenciesAvailability = false;
	protected final HashMap<UUID, ChannelCache> caches = new HashMap<>();
	private final HashMap<String, Object> filteringContext = new HashMap<>();
	private UUID lastLocalChannelUpdate;
	private DataPoint lastLocalDataPointUpdate;

	/**
	 * Constructor
	 * @param ruleFlow
	 */
	public EvaluationStrategy(RuleFlow ruleFlow) {
		this.rule = ruleFlow;
	}

	/**
	 * Called before rule evaluation to check the filtering condition, expiration date, and determine if the rule
	 * is applicable at the moment. If so, returns data needed to execute the rule.
	 * @param rev Rule Flow Event being evaluated
	 * @param shouldFilter true if filtering should happen
	 * @return a list of execution data for one or more evaluations, will be empty if no evaluation is needed.
	 */
	public boolean preEvaluation(RuleFlowEvent rev,boolean shouldFilter) {
		//UUID channelId, DataPoint dp
		boolean evaluate = false;
		UUID ruleId = rule.getRuleId();
		
		//Check whether need to evaluate or not
		try {
			if (!validateDependenciesAvailability || validateDependencies()) {
				Serializable filteringCondition = rule.getFilteringCondition();
				if (!shouldFilter || filteringCondition == null) {
					evaluate = true;
				} else {
					if (prepareFilteringContext(rev.getContentIndex())) {
						try {
							logger.trace("Evaluating filtering condition");
							evaluate = MVELUtil.evaluateBoolean(filteringCondition, filteringContext);
							logger.debug("Filtering condition result: {}", evaluate);
						} catch (Exception e) {
							logger.error("PreEvaluation rule {} after receiving update {}. Failing in evaluating filtering condition = {}", ruleId, rev, filteringCondition, e);
						} finally {
							filteringContext.clear();
						}
					} else {
						logger.trace("Filtering context was invalid");
					}
				}
			}
		} catch (Exception e) {
			logger.error("PreEvaluation rule {} after receiving update {}. Unexpected exception validating if the Rule is applicable. Evaluate = {} ", ruleId, rev, evaluate, e);
			evaluate = false; //Forcing not to evaluate
		}
		
		//If need to evaluate actual execution data need to be prepared
		if (evaluate) {
			// Update local buffered caches with the new data this only happens if the filter condition passes because
			// data received beforehand is not relevant
			try {
				updateLocalCache(rev);
			} catch (Exception e) {
				logger.error("PreEvaluation rule {} after receiving update {}. Failure updating local cache. ", ruleId, rev, e);
				return false;
			}
			
			try {
				EventDataPointContent dpc = rev.getDataPoint();
				if (dpc != null) {
					return prepareExecutionData(dpc.getChannelId(), dpc.receivedDataPoint);
				} else {
					return true;
				}
			} catch  (Exception e) {
				logger.error("PreEvaluation rule {} after receiving update {}. Failing in prepareExecutionData.", ruleId, rev,  e);
			}
		}
		return false;
	}
	
	/**
	 * Update local cache based on submitted RuleFlowEvent
	 * @param rev
	 * @throws Exception
	 */
	public void updateLocalCache(RuleFlowEvent rev) throws Exception {
		EventDataPointContent dpEvent = rev.getDataPoint();
		if (dpEvent != null) {
			updateLocalCache(dpEvent.getChannelId(), dpEvent.receivedDataPoint);
		} else if (rev.getAlertStatus() != null) {
			rule.updateLocalCache(rev.getAlertStatus().getCepEvent());
		}
	}
	
	/**
	 *  Called after rule evaluation to cleanup.
	 */
	public void postEvaluation() {
	}
	
	/**
	 * Implementers should use allocateDataSet(), fill these in, then add them to the results collection.
	 * @param eventChannelId The ID of the channel that caused the evaluation event
	 * @param eventData The data point on the channel that caused the evaluation event
	 */
	protected abstract boolean prepareExecutionData(UUID eventChannelId, DataPoint eventData);
	
	/**
	 * Make sure that all dependencies (ChannelAlias and AlertClassVariable) has value
	 * @return
	 */
	private boolean validateDependencies() {
		if (validateDependenciesAvailability) {
			for (ChannelAlias v : rule.getDependencies().values()) {
				if (v.isEmpty())
					return false;
			}
			
			for (AlertClassVariable v: rule.getAlertClassDependencies().values()) {
				if (v.isEmpty())
					return false;
			}
		}
		return true;
	}

	/**
	 * @param ev
	 * @param sharedObjects
	 * @return the Engine Context
	 */
	public EngineContext prepareContext(Event ev, UUID sharedObjects) {
		String channel = ev.getProperties().getProperty(CHANNEL_CTX_VARIABLE);
		
		VariableResolverFactory vrf = new RuleVariableResolverFactory(rule, null);
		EngineContext dctx = rule.prepareContext(ev, sharedObjects, new VariableResolverFactoryContext(vrf));

		if(StringUtils.isNotBlank(channel)) {
			UUID channelUUID = UUIDHelper.fromStringFast(channel);
			DataPoint dp = (DataPoint) rule.getDependencies().get(channelUUID).getValueAsDataPoint();
			dctx.put(CHANNEL_CTX_VARIABLE, channel);
			dctx.put(INDEX_CTX_VARIABLE, dp.getIndex());
			dctx.put(VALUE_CTX_VARIABLE, dp.getValue());
		} else {
			VariableDatum vd = rule.getEventPoint(ev);
			if (vd != null) {
				dctx.put(INDEX_CTX_VARIABLE, vd.getIndex());
				dctx.put(VALUE_CTX_VARIABLE, vd.getValue());
			}
		}
		dctx.put(RuleFlow.ALERT_STATUS_CTX_VARIABLE, rule.generateAlertStatusContext());
		
		return dctx;
	}
	
	

	/**
	 * Updates the current filtering evaluation context for this strategy.
	 * @return Whether the filtering context is valid.
	 */
	private boolean prepareFilteringContext(Object indexToBeUsed) {
		assert filteringContext.size() == 0;
		
		if (validateDependenciesAvailability) {
			for (Variable v : rule.getConditionVariables().values()) {
				if (v.getValue() == null)
					return false;
			}
		}
		
		for (Variable v : rule.getFilteringVariables().values()) {
			String alias = v.getAlias();
			
			if (v instanceof ChannelAlias) {
				// TODO Need to make this behave like the condition evaluation where getValue() on channel alias
				//      returns what is determined to be the next value. Either that or need a getFilterValue()
				Object value = ((ChannelAlias) v).getCacheData(indexToBeUsed);
				
				filteringContext.put(Variable.RESOLVABLE_VARIABLE_PREFIX + alias, value);
				if (value != null) {
					filteringContext.put(alias, ((DataPoint) value).getValue());
				} else {
					filteringContext.put(alias, null);					
				} 
			} else {
				Object value = v.getValue();
				if (value != null) {
					filteringContext.put(alias, value);
				} else {
					filteringContext.put(alias, null);					
				}
			}
		}
		
		return true;
	}

	/**
	 * @return the validateDependenciesAvailability
	 */
	public boolean isValidateDependenciesAvailability() {
		return validateDependenciesAvailability;
	}

	/**
	 * @param validateDependenciesAvailability the validateDependenciesAvailability to set
	 */
	public void setValidateDependenciesAvailability(boolean validateDependenciesAvailability) {
		this.validateDependenciesAvailability = validateDependenciesAvailability;
	}
	
	/**
	 * Create a local LKV cache
	 * @param channelId
	 * @return A new LKV channel cache
	 */
	public ChannelCache createLocalLKVCache(UUID channelId) {
		synchronized (caches) {
			if (caches.containsKey(channelId))
				throw new IllegalArgumentException("local cache already exists");
			LKVChannelCacheImpl cache = new LKVChannelCacheImpl(channelId);
			caches.put(channelId, cache);
			return cache;
		}
	}

	/**
	 * Create a local buffered cache.
	 * @param channelId
	 * @return A new buffered channel cache
	 */
	public ChannelCache createLocalBufferedCache(UUID channelId) {
		synchronized (caches) {
			if (caches.containsKey(channelId))
				throw new IllegalArgumentException("local cache already exists");
			BufferedChannelCacheImpl cache = new BufferedChannelCacheImpl(channelId);
			caches.put(channelId, cache);
			return cache;
		}
	}

	/**
	 * Updates the local channel cache with the incoming data point. If the strategy does not have local caches
	 * this is a no-op.
	 * @param channelId
	 * @param dp
	 */
	void updateLocalCache(UUID channelId, DataPoint dp) {
		// Avoid updating local cache multiple times in pre-evaluation loop
		if (dp == lastLocalDataPointUpdate && channelId == lastLocalChannelUpdate)
			return;
		
		ChannelCache c;
		synchronized (caches) {
			c = caches.get(channelId);
		}
		if (c == null)
			return;
		
		try {
			c.addDataPoint(dp);
		} catch (Exception e) {
			logger.error("Unexcepted exception while updating channel {}", channelId.toString(), e);
		}
		
		lastLocalChannelUpdate = channelId;
		lastLocalDataPointUpdate = dp;
	}
}
