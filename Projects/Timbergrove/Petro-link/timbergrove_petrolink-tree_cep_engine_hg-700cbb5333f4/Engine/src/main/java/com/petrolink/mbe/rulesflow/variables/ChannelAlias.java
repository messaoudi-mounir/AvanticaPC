package com.petrolink.mbe.rulesflow.variables;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jdom2.Element;
import org.mvel2.integration.VariableResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.cache.ChannelCache;
import com.petrolink.mbe.cache.ChannelCacheConsumer;
import com.petrolink.mbe.model.channel.ChannelIndexType;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.pvclient.MbeOrchestrationApiClient.RigStateInfo;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.PetroVaultMbeOrchestrationService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Channel Alias Variable.
 * 
 * It's value is always null or an instance of ChannelAliasValue.
 * 
 * @author paul
 * @author langj
 *
 */
public abstract class ChannelAlias extends Variable {
	/**
	 * The special alias for rig state.
	 */
	public static final String SA_RIG_STATE = "rigState";
	
	/**
	 * The special alias for bit depth.
	 */
	public static final String SA_BIT_DEPTH = "bitDepth";
	
	/**
	 * The special alias for hole depth.
	 */
	public static final String SA_HOLE_DEPTH = "holeDepth";
	
	/**
	 * A set of all special aliases.
	 */
	public static final Set<String> specialAliases = new HashSet<>(Arrays.asList(SA_RIG_STATE, SA_BIT_DEPTH, SA_HOLE_DEPTH));
	
	protected static Logger logger = LoggerFactory.getLogger(ChannelAlias.class);
	protected UUID uuid;
	protected ChannelCache cache;
	protected String valueUnit;
	protected String indexUnit;
	protected ChannelIndexType indexType;
	private HashMap<Integer, String> valueNames;
	private DataPoint prevDataPointValue;
	private DataPoint dataPointValue;
	private ChannelAliasValue value;
	private DataPointValueVariableResolver dataPointValueResolver;
	private double gap;
	
	/**
	 * Gets the value of this ChannelAlias, which will always be an instance of ChannelAliasValue.
	 */
	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public Class<?> getValueType() {
		return ChannelAliasValue.class;
	}
	
	/**
	 * Gets the value as a data point, ignoring the previous data point.
	 * @return The current data point value
	 */
	public DataPoint getValueAsDataPoint() {
		return dataPointValue;
	}
		
	/**
	 * @return the channel ID
	 */
	public UUID getUuid() {
		return uuid;
	}
	
	/**
	 * @return the Value Unit of Measurement
	 */
	public String getValueUnit() {
		return valueUnit;
	}
	
	/**
	 * @return the Index Unit
	 */
	public String getIndexUnit() {
		return indexUnit;
	}
	
	/**
	 * Gets the gap value that indicates the most acceptable difference in index from the newest to the oldest data.
	 * @return The gap value. For time index this represents milliseconds.
	 */
	public double getGap() {
		return gap;
	}

	@Override
	public void load(RuleFlow rule, Element e) throws EngineException {
		super.load(rule, e);
		this.uuid = UUID.fromString(e.getAttributeValue("id"));
		this.indexUnit = e.getAttributeValue("indexUnit");
		this.valueUnit = e.getAttributeValue("valueUnit");
		
		String gapString = e.getAttributeValue("gap");
		if (!StringUtils.isEmpty(gapString))
			this.gap = Double.parseDouble(gapString);

		if (e.getAttribute("indexType") != null) {
			switch (e.getAttributeValue("indexType").toLowerCase()) {
			case "long":
				this.indexType = ChannelIndexType.Long;
				break;
			case "double":
				this.indexType = ChannelIndexType.Double;
				break;
			case "datetime":
				this.indexType = ChannelIndexType.Datetime;
				break;
			}
		}
		else {
			this.indexType = ChannelIndexType.Long;
		}

		// Value names will either be loaded from explicit entries in the XML, or from the MBE API
		
		Element valueNamesElement = e.getChild("ValueNames", e.getNamespace());
		if (valueNamesElement != null) {
			// For now a source is only supported for rig states, so the attribute name is explicit about that
			
			String source = valueNamesElement.getAttributeValue("rigStateSource");
			if (source != null) {
				//TODO this may should not be done here. Probably should be done in parent level.
				UUID sourceId = UUIDHelper.fromStringFast(source);
				PetroVaultMbeOrchestrationService mbeService = ServiceAccessor.getPVMBEService();
				if (!UUIDHelper.isNullOrEmpty(sourceId) && mbeService != null) {
					try {
						logger.trace("Loading rig state value names for {} from source {}", alias, source);
						
						List<RigStateInfo> rigStates = mbeService.getRigStateDictionaryAsync(sourceId).get();
						valueNames = new HashMap<>();
						
						if (rigStates != null) {
							for (RigStateInfo i : rigStates) {
								valueNames.put(i.getValue(), i.getName());
							}
						}
						
						logger.trace("Rig state loading completed");
					}
					catch (InterruptedException | ExecutionException ex) {
						throw new EngineException("Rig state loading failed", ex);
					}
				}
				else {
					logger.warn("MBE Service not available for resolving rigStateSource from ID {}", source);
				}
			}
			else {
				valueNames = new HashMap<>();
				for (Element name : valueNamesElement.getChildren("Name", e.getNamespace())) {
					// getValueName(...) uses lowercase comparison so we make sure to do the same here
					Integer value = Integer.parseInt(name.getAttributeValue("value").toLowerCase());
					String text = name.getAttributeValue("text");
					valueNames.put(value, text);
				}
			}
		}		
	}
	
	/**
	 * @return The value names map or null if none are defined or they are being asynchronously loaded.
	 */
	public Map<Integer, String> getValueNames() {
		return valueNames;
	}
	
	/**
	 * Gets the name matching the specified value.
	 * @param value
	 * @return A matching value name, or null if no match was found.
	 */
	public String getValueName(Object value) {
		// Convert the value to a string first as thats how things are defined in XML
		if (value == null || valueNames == null)
			return null;
		String sv = value.toString().toLowerCase();
		if (!NumberUtils.isCreatable(sv))
			return null;
		try {
			// Need to parse as double then cast to int because channel value is 99% likely to be double
			return valueNames.get((int) Double.parseDouble(sv));
		} catch (NumberFormatException e) {
			return null; // this shouldn't happen, but we don't want getValueName() to fail anyways
		}
	}	

	/**
	 * @return the cache
	 */
	public ChannelCache getCache() {
		return cache;
	}

	@Override
	public Element toElement() {
		Element element = new Element("Channel");
		element.setAttribute("name", cache.getName());
		element.setAttribute("id", cache.getId().toString());
		element.setAttribute("indexType", cache.getChannelIndexType().toString());
		element.setAttribute("index", cache.getLastDataPoint().getIndex().toString());
		element.setAttribute("valueType",cache.getChannelDataType().toString());
		element.setAttribute("indexUnit", indexUnit);
		element.setAttribute("valueUnit", valueUnit);
		element.setAttribute("gap", Double.toString(gap));
		//element.addContent(cache.getLastKnownValue().getValue().toString());
		
		if (valueNames != null) {
			Element eValueNames = new Element("ValueNames");
			
			for (Entry<Integer, String> e : valueNames.entrySet()) {
				Element eName = new Element("Name");
				eName.setAttribute("text", e.getValue());
				eName.setAttribute("value", e.getKey().toString());
				eValueNames.addContent(eName);
			}
			element.addContent(eValueNames);
		}
		
		return element;
	}

	/**
	 * @return true if the cache is empty
	 */
	public boolean isEmpty() {
		return cache.isEmpty();
	}
	
	@Override
	public String toString() {
		return getAlias() + "-" + getUuid() + ":" + getClass().getSimpleName();
	}
	
	/**
	 * @return a variable resolver that provides the value portion of the data point value
	 */
	public DataPointValueVariableResolver getDataPointValueResolver() {
		if (dataPointValueResolver == null)
			dataPointValueResolver = new DataPointValueVariableResolver();
		return dataPointValueResolver;
	}
	
	/**
	 * Update the value by combining the new data point with the previous data point in a ChannelAliasValue object.
	 * 
	 * If newValue is null variable value is also set to null.
	 * 
	 * This will also clear data in the cache up to the new value's index.
	 * @param newValue The new data point.
	 */
	public void updateValue(DataPoint newValue) {
		prevDataPointValue = dataPointValue;
		dataPointValue = newValue;
		
		if (newValue != null) {
			value = new ChannelAliasValue(newValue, prevDataPointValue);
			clearUsedCacheData(newValue.getIndex());
		} else {
			value = null;
		}
	}
	
	/**
	 * @return if the Channel is a Global or Rule (local) Channel
	 */
	public abstract boolean isCacheGlobal();
	
	/**
	 * Return the current object as a BufferedChannelAlias if its an instance of it, otherwise returns null.
	 * @return A BufferedChannelAlias or null
	 */
	public abstract BufferedChannelAlias asBuffered();

	/**
	 * Gets data from the cache in order from oldest index to newest index.
	 * @param consumer A collection to receive the data points
	 * @param startIndex The index to start at
	 * @param inclusive Whether the start index is inclusive
	 */
	public abstract void getCacheData(ChannelCacheConsumer consumer, Object startIndex, boolean inclusive);

	/**
	 * Returns the Last known value with index equal or lower than the index provided
	 * @param endIndex
	 * @return Data Point with index lower or equal to the provide index
	 */
	public Object getCacheData(Object endIndex) {
		return getCache().getLastDataPoint(endIndex);
	}

	/**
	 * Increment the the position in the cache to the specified exclusive end index. This should clear data if the
	 * cache is a local buffered cache.
	 * @param endIndex The inclusive end index
	 */
	public abstract void clearUsedCacheData(Object endIndex);
	
	/**
	 * Describes the result of addToEvaluationContext().
	 */
	public enum Validity {
		/**
		 * Valid 
		 */
		VALID,
		/**
		 * Invalid
		 */
		INVALID,
		/**
		 * Ignore
		 */
		IGNORE
	}
	
	/**
	 * A variable resolver that provides the value of a channel's current data point value.
	 * @author langj
	 *
	 */
	@SuppressWarnings("rawtypes")
	public final class DataPointValueVariableResolver implements VariableResolver {
		private static final long serialVersionUID = -6004207202413154036L;
		
		DataPointValueVariableResolver() {}

		@Override
		public String getName() {
			return getAlias();
		}

		@Override
		public Class getType() {
			return Object.class;
		}

		@Override
		public void setStaticType(Class type) {
			// Non-applicable
		}

		@Override
		public int getFlags() {
			return 0;
		}

		@Override
		public Object getValue() {
			DataPoint dp = dataPointValue;
			return dp != null ? dp.getValue() : null;
		}

		@Override
		public void setValue(Object value) {
			 throw new UnsupportedOperationException("This resolver is read-only");		
		}
	}
}
