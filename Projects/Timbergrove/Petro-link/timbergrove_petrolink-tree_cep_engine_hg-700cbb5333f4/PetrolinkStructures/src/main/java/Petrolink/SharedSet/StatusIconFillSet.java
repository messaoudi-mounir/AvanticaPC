package Petrolink.SharedSet;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement (name = "StatusIconFillSet")
public class StatusIconFillSet {
	private StatusIconInfo info;
	private List<StatusIconFill> statusIconFills;
	private List<StatusIconGroup> statusIconGroups;
	private boolean isHidden = false;
	/**
	 * @return the info
	 */
	@XmlElement(name = "Info")  
	public final StatusIconInfo getInfo() {
		return info;
	}
	/**
	 * @param info the info to set
	 */
	public final void setInfo(StatusIconInfo info) {
		this.info = info;
	}
	/**
	 * @return the statusIconFills
	 */
	@XmlElementWrapper(name="StatusIconFills")
	@XmlElement(name = "StatusIconFill")
	public final List<StatusIconFill> getStatusIconFills() {
		return statusIconFills;
	}
	/**
	 * @param statusIconFills the statusIconFills to set
	 */
	public final void setStatusIconFills(final List<StatusIconFill> statusIconFills) {
		this.statusIconFills = statusIconFills;
	}
	/**
	 * @return the statusIconGroups
	 */
	@XmlElementWrapper(name="StatusIconGroups")
	@XmlElement(name = "StatusIconGroup")
	public final List<StatusIconGroup> getStatusIconGroups() {
		return statusIconGroups;
	}
	/**
	 * @param statusIconGroups the statusIconGroups to set
	 */
	public final void setStatusIconGroups(final List<StatusIconGroup> statusIconGroups) {
		this.statusIconGroups = statusIconGroups;
	}
	/**
	 * @return the isHidden
	 */
	@XmlElement (name = "IsHidden")
	public final boolean isHidden() {
		return isHidden;
	}
	/**
	 * @param isHidden the isHidden to set
	 */
	public final void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}
	
}
