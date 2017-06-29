package com.petrolink.mbe.model.message;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.petrolink.mbe.model.channel.DataPoint;


/**
 * A Class for defining changes within alert and its detail
 * @author aristo
 *
 */
public class AlertCepEvent implements Comparable<AlertCepEvent>{

	private String type;
	private Instant timestamp;
	//private OffsetDateTime index; //depends on timestamp
	private String principal;
	private AlertSnapshotSummary alert;
//	private String alertInstanceId;
//	private String alertClassId;
//	private String alertStatusName;
//	private ContentContainer contextDetail;
	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public final void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the timestamp
	 */
	public final Instant getTimestamp() {
		return timestamp;
	}
	/**
	 * @param newTimestamp the timestamp to set
	 */
	public final void setTimestamp(Instant newTimestamp) {
		this.timestamp = newTimestamp;
		//this.index = OffsetDateTime.ofInstant(newTimestamp, ZoneOffset.UTC);
	}
	/**
	 * @return the principal
	 */
	public final String getPrincipal() {
		return principal;
	}
	/**
	 * @param principal the principal to set
	 */
	public final void setPrincipal(String principal) {
		this.principal = principal;
	}
//	/**
//	 * @return the alertInstanceId
//	 */
//	public final String getAlertInstanceId() {
//		return alertInstanceId;
//	}
//	/**
//	 * @param alertInstanceId the alertInstanceId to set
//	 */
//	public final void setAlertInstanceId(String alertInstanceId) {
//		this.alertInstanceId = alertInstanceId;
//	}
//	/**
//	 * @return the alertClassId
//	 */
//	public final String getAlertClassId() {
//		return alertClassId;
//	}
//	/**
//	 * @param alertClassId the alertClassId to set
//	 */
//	public final void setAlertClassId(String alertClassId) {
//		this.alertClassId = alertClassId;
//	}
//	/**
//	 * @return the alertStatusName
//	 */
//	public final String getAlertStatusName() {
//		return alertStatusName;
//	}
//	/**
//	 * @param alertStatusName the alertStatusName to set
//	 */
//	public final void setAlertStatusName(String alertStatusName) {
//		this.alertStatusName = alertStatusName;
//	}
//	/**
//	 * @return the contextDetail
//	 */
//	public final ContentContainer getContextDetail() {
//		return contextDetail;
//	}
//	/**
//	 * @param contextDetail the contextDetail to set
//	 */
//	public final void setContextDetail(ContentContainer contextDetail) {
//		this.contextDetail = contextDetail;
//	}
	/**
	 * @return the alert
	 */
	public final AlertSnapshotSummary getAlert() {
		return alert;
	}
	
	
	/**
	 * @param alert the alert to set
	 */
	public final void setAlert(AlertSnapshotSummary alert) {
		this.alert = alert;
	}
	
	/**
	 * Convenience Method for AlertClassId, instead of traversing content
	 * @return AlertClassId if alert, alert's definition, and class id are exists. Null otherwise
	 */
	public String getAlertClassId() {
		String result = null;
		if (alert != null) {
			AlertDefinition alertDefinition = alert.getDefinition();
			if (alertDefinition != null) {
				result = alertDefinition.getClassId();
			}
		}
		return result;
	}
	
	String indexText;
	Object indexObject;
	/**
	 * @return the index
	 */
	public final Object getIndex() {
		AlertSnapshotSummary alert = getAlert();
		String curIndexText = alert.getLatestIndex();
		if (!StringUtils.equals(indexText, curIndexText )) {
			indexObject = DataPoint.tryParseIndex(curIndexText);
			indexText = curIndexText;
		}
		return indexObject;
	}
	
	@Override
	public String toString() {
		StringBuilder detailBuilder = new StringBuilder();
		return toString(detailBuilder).toString();
	}
	
	/**
	 * For debug string
	 * @param sb
	 * @return String detailing this object
	 */
	public StringBuilder toString(StringBuilder sb){
		if (sb == null) return null;
		
		sb.append("AlertCepEvent");
		sb.append("( ");
		sb.append(" type ").append(getType());
		sb.append(" on ").append(getIndex());
		sb.append(" processed at ").append(getTimestamp());
		AlertSnapshotSummary alert = getAlert();
		if (alert != null) {
			AlertDefinition def = alert.getDefinition();
			if (def != null) {
				sb.append(" classid ").append(def.getClassId());
			}
			sb.append(" status ").append(alert.getStatus());
		}
		sb.append(") ");
		return sb;
	}
	
	/**
	 * Check whether two object are same
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AlertCepEvent) {
			AlertCepEvent other = (AlertCepEvent) obj;
			return timestamp.equals(other.timestamp)
					&& StringUtils.equals(principal, other.principal)
					&& StringUtils.equals(type, other.type)
					&& StringUtils.equals(type, other.type)
					&& Objects.deepEquals(alert, other.alert)
					;
		}
		return false;
	}
	
	/**
	 * Compare both event's index. If index can't be compared correctly use timestamp
	 */
	@Override
	public int compareTo(AlertCepEvent o) {
		AlertSnapshotSummary tAlert = getAlert();
		AlertSnapshotSummary oAlert = o.getAlert();
		if ((tAlert != null) && (oAlert != null)) {
			//TODO may be better probably to include inactivated INDEX in future
			return tAlert.getLatestStatusChange().compareTo(oAlert.getLatestStatusChange());
		}
		
		
		try {
			Object tIdx = getIndex();
			Object oIdx = o.getIndex();
			if (tIdx instanceof OffsetDateTime){
				OffsetDateTime cIdx =(OffsetDateTime)tIdx;
				OffsetDateTime cOIdx =(OffsetDateTime)oIdx;
				return cIdx.compareTo(cOIdx);
			} else if (tIdx instanceof Double){
				Double nIdx =(Double)tIdx;
				Double nOIdx =(Double)oIdx;
				return nIdx.compareTo(nOIdx);
			} else if (tIdx instanceof Long){
				Long nIdx =(Long)tIdx;
				Long nOIdx =(Long)oIdx;
				return nIdx.compareTo(nOIdx);
			}  
		} catch (Exception e) {
			//Do Nothing
		}
		return timestamp.compareTo(o.timestamp);
	}
}
