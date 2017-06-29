package com.petrolink.mbe.rulesflow.variables;

import java.time.OffsetDateTime;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.mvel2.integration.VariableResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.alertstatus.Alert;
import com.petrolink.mbe.model.message.AlertCepEvent;
import com.petrolink.mbe.model.message.AlertSnapshotSummary;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.smartnow.engine.exceptions.EngineException;

/**
 * Alert Class variable. Intended only being used in global rule scope.
 * Remember it is always LKV (at least for now)! Will check getIndex() during update
 * @author aristo
 *
 */
public class AlertClassVariable extends Variable{
	protected Logger logger = LoggerFactory.getLogger(AlertClassVariable.class);
	
	protected String alertClassId;
	protected AlertCepEvent latestEvent;
	protected AlertSnapshotSummary activeAlert; //Beware that this MUST be null when active alert status is not active
	protected AlertSnapshotResolver alertSnapshotResolver;
	
	/**
	 * Load element's Configuration
	 */
	@Override
	public void load(RuleFlow rule, Element e) throws EngineException {
		super.load(rule, e);
		String alertClassId = e.getAttributeValue("alertClassId");
		setAlertClassId(alertClassId);
		this.scope = Variable.GLOBAL_SCOPE;
	}
	
	/**
	 * Update specified event to cache
	 * @param cepEvent
	 */
	public void update(AlertCepEvent cepEvent) {
		if (cepEvent == null) return;
		AlertCepEvent latest = latestEvent;
		if (latest == null) {
			latestEvent = cepEvent;
			setActiveAlert(cepEvent.getAlert());
		} else if(latest.compareTo(cepEvent) < 0){
			latestEvent = cepEvent;
			setActiveAlert(cepEvent.getAlert());
		}
	}

	/**
	 * @return the alertClassId
	 */
	public final String getAlertClassId() {
		return alertClassId;
	}

	/**
	 * @param alertClassId the alertClassId to set
	 */
	public final void setAlertClassId(String alertClassId) {
		if (StringUtils.isBlank(alertClassId)) {
			throw new IllegalArgumentException("Can't load AlertClassVariable from null element without alertClassId attribute");
		}
		this.alertClassId = alertClassId.toLowerCase();
	}
	
	/**
	 * Get Value of current Alert CEP
	 * @return latest Alert CEP event update
	 */
	public AlertCepEvent getValueAsAlertCepEvent() {
		return latestEvent;
	}
	
	/**
	 * Get index of last update Event
	 * @return Index of last event
	 */
	public Object getValueIndex() {
		AlertCepEvent lastEvent = getValueAsAlertCepEvent();
		if(lastEvent == null) {
			return null;
		}
		return lastEvent.getIndex();
	}

	/**
	 * Indicates whether this alertclass has value or not
	 * @return whether this variable has event or not
	 */
	public boolean isEmpty() {
		return latestEvent == null;
	}
	
	

	@Override
	public Element toElement() {
		Element element = new Element("AlertClass");
		// TODO NOT clear what is this used for
		return element;
	}
	
	/**
	 * @return a variable resolver that provides the value portion of the data point value
	 */
	public AlertSnapshotResolver getAlertSnapshotResolver() {
		if (alertSnapshotResolver == null)
			alertSnapshotResolver = new AlertSnapshotResolver();
		return alertSnapshotResolver;
	}
	
	/**
	 * A variable resolver that provides the value of active alert, or null otherwise
	 * @author Aristo
	 *
	 */
	@SuppressWarnings("rawtypes")
	public final class AlertSnapshotResolver implements VariableResolver {
		private static final long serialVersionUID = -7854366997163761345L;

		AlertSnapshotResolver() {}

		@Override
		public String getName() {
			return getAlias();
		}

		@Override
		public Class getType() {
			return AlertSnapshotSummary.class;
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
			return getActiveAlert();
		}

		@Override
		public void setValue(Object value) {
			 throw new UnsupportedOperationException("AlertClassVariableResolver is read-only");		
		}
	}

	@Override
	public Object getValue() {
		return latestEvent;
	}

	@Override
	public Class<?> getValueType() {
		return AlertCepEvent.class;
	}

	/**
	 * @return the activeAlert
	 */
	public final AlertSnapshotSummary getActiveAlert() {
		return activeAlert;
	}

	/**
	 * Set active alert. If supplied snapshot summary is not in active, it will set to null 
	 * @param updatedAlert the activeAlert to set
	 */
	public final void setActiveAlert(AlertSnapshotSummary updatedAlert) {
		if ((updatedAlert != null) && (updatedAlert.getStatus() == Alert.ACTIVE)) {
			activeAlert = updatedAlert;
		} else {
			activeAlert = null;
		}
	}
	
}
