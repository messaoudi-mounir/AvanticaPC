package com.petrolink.mbe.rulesflow;

import com.petrolink.mbe.model.message.AlertCepEvent;

/**
 * Event Content for CepEvent
 * @author aristo
 *
 */
public class EventCepEventContent extends BaseEventContent {
	private AlertCepEvent cepEvent;

	/**
	 * Constructor
	 * @param alertCepEvent
	 */
	public EventCepEventContent(AlertCepEvent alertCepEvent) {
		setCepEvent(alertCepEvent);
	}
	
	/**
	 * Convenience Method for AlertClassId, instead of traversing content
	 * @return AlertClassId if alert, alert's definition, and class id are exists. Null otherwise
	 */
	public String getAlertClassId() {
		if (cepEvent == null) {
			return null;
		}
		return cepEvent.getAlertClassId();
	}

	/**
	 * @return the cepEvent
	 */
	public final AlertCepEvent getCepEvent() {
		return cepEvent;
	}

	/**
	 * @param cepEvent the cepEvent to set
	 */
	public final void setCepEvent(AlertCepEvent cepEvent) {
		this.cepEvent = cepEvent;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("EventCepEventContent:");
		AlertCepEvent  evt = getCepEvent();
		evt.toString(sb);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof EventCepEventContent) {
			EventCepEventContent other = (EventCepEventContent)obj;
			return cepEvent.equals(other.cepEvent)
					;
		}
		return false;
	}
}
