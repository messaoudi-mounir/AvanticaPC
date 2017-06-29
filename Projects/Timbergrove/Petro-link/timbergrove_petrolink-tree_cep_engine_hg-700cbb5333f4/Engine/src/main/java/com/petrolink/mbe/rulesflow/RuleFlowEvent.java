package com.petrolink.mbe.rulesflow;

import org.apache.commons.lang3.NotImplementedException;

import com.smartnow.engine.event.Event;

/**
 * Convenient container conversion from Smartnow Event.
 * This extract general content into appropriate type
 * @author aristo
 *
 */
public class RuleFlowEvent {

	private Object content;
	private EventDataPointContent dataPoint;
	private EventCepEventContent alertStatus;
	
	/**
	 * Create new  RuleFlowEvent from Smartnow Engine Event
	 * @param ev Smartnow Engine Event
	 * @return RuleFlowEvent which has Smartnow 
	 */
	public static RuleFlowEvent from(Event ev) {
		RuleFlowEvent result = new RuleFlowEvent();
		result.setContent(ev.getContent());
		return result;
	}

	/**
	 * Set the event content
	 * @param evcObject
	 */
	protected void setContent(Object evcObject) {
		content = evcObject;
		dataPoint = null;
		alertStatus = null;
		
		if (evcObject instanceof EventDataPointContent) {
			dataPoint = (EventDataPointContent)evcObject;
		} else if (evcObject instanceof EventCepEventContent) {
			alertStatus = (EventCepEventContent) evcObject;
		} else {
			throw new NotImplementedException("Content is not acceptable");
		}
	}
	
	/**
	 * @return the dataPoint
	 */
	public final EventDataPointContent getDataPoint() {
		return dataPoint;
	}

	/**
	 * @return the alertStatus
	 */
	public final EventCepEventContent getAlertStatus() {
		return alertStatus;
	}

	/**
	 * @return the content
	 */
	public final Object getContent() {
		return content;
	}

	/**
	 * find Index of the content
	 * @return Index of the content
	 */
	public Object getContentIndex() {
		if (dataPoint != null) {
			return dataPoint.receivedDataPoint.getIndex();
		} else if (alertStatus != null) {
			return alertStatus.getCepEvent().getIndex();
		}
		return null;
	}
}
