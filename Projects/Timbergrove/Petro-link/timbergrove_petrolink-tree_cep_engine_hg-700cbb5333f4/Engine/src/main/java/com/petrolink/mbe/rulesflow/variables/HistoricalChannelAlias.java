package com.petrolink.mbe.rulesflow.variables;

import java.io.IOException;
import java.util.List;

import org.jdom2.Element;

import com.petrolink.mbe.cache.CacheFactory;
import com.petrolink.mbe.directories.WellDirectory;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.pvclient.ChronosClient;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.ServiceAccessor;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Channel Implementation for Historical Channel as an extension of Buffered Channel Alias
 * These channels will query the PetroVault Chronos to obtain a set of points associated from the refill point forward
 * @author paul
 *
 */
public class HistoricalChannelAlias extends GlobalBufferedChannelAliasBase {
	private final ChronosClient chronosClient = ServiceAccessor.getChronosClientService().getClient();
	private Object startIndex;
	private Object endIndex;
	Object lastUsedIndex;

	/* (non-Javadoc)
	 * @see com.petrolink.mbe.rulesflow.variables.ChannelAlias#load(com.petrolink.mbe.rulesflow.RuleFlow, org.jdom2.Element)
	 */
	@Override
	public void load(RuleFlow rule, Element e) throws EngineException {
		super.load(rule, e);
		
		if (this.cache == null) {
			this.cache = CacheFactory.getInstance().getBufferedCache().getWell(rule.getWellId()).getOrCreateChannel(uuid);

			// Registrating Cache to Well
			WellDirectory wellDirectory = ServiceAccessor.getWellDirectory();
			if (wellDirectory != null) {
				wellDirectory.getWell(rule.getWellId()).registerChannel(uuid, this.getAlias());
			}
		}
		
		startIndex = DataPoint.parseIndex(e.getAttributeValue("startIndex"));
		endIndex = DataPoint.parseIndex(e.getAttributeValue("endIndex"));
		if (startIndex.getClass() != endIndex.getClass())
			throw new EngineException("startIndex and endIndex must be of the same class");
		if (startIndex instanceof Long && (Long) startIndex == 0 && (Long) endIndex == 0) {
			logger.warn("startIndex and endIndex options are 0, do not use empty historical channels");
			return;
		}
		
		try {
			List<DataPoint> rangeResult = chronosClient.getChannelDataAsDataPoints(uuid, startIndex, endIndex);
			cache.setMaxSize(rangeResult.size());
			cache.addDataPoints(rangeResult);
		} catch (IOException ex) {
			throw new EngineException("IOException while loading historical channel data", ex);
		}
	}

	/**
	 * @return the startIndex
	 */
	public Object getStartIndex() {
		return startIndex;
	}

	/**
	 * @param startIndex the startIndex to set
	 */
	public void setStartIndex(Object startIndex) {
		this.startIndex = startIndex;
	}

	/**
	 * @return the endIndex
	 */
	public Object getEndIndex() {
		return endIndex;
	}

	/**
	 * @param endIndex the endIndex to set
	 */
	public void setEndIndex(Object endIndex) {
		this.endIndex = endIndex;
	}

	/* (non-Javadoc)
	 * @see com.petrolink.mbe.rulesflow.variables.BufferedChannelAlias#toElement()
	 */
	@Override
	public Element toElement() {
		Element e = super.toElement();
		e.setAttribute("startIndex", startIndex.toString());
		e.setAttribute("endIndex", endIndex.toString());		
		return e;
	}
}
