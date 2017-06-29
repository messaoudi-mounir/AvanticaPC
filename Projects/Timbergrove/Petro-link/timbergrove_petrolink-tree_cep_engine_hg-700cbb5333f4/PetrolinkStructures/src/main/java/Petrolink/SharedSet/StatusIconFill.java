package Petrolink.SharedSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class StatusIconFill {
	private String id;
	private String activity;
	private String description;
	private String codeStatus;
	private String foreGroundColor;
	private String backGroundColor;
	private double flashDuration;
	private String group;
	private boolean _default;
	
	/**
	 * @return the id
	 */
	@XmlElement(name = "Id")  
	public final String getId() {
		return id;
	}
	/**
	 * @param newId the id to set
	 */
	public final void setId(final String newId) {
		this.id = newId;
	}
	/**
	 * @return the description
	 */
	@XmlElement(name = "Description")  
	public final String getDescription() {
		return description;
	}
	/**
	 * @param newDescription the description to set
	 */
	public final void setDescription(final String newDescription) {
		this.description = newDescription;
	}
	/**
	 * @return the Activity
	 */
	@XmlElement(name = "Activity")
	public final String getActivity() {
		return activity;
	}
	/**
	 * @param newActivity the Activity to set
	 */
	public final void setActivity(final String newActivity) {
		this.activity = newActivity;
	}
	/**
	 * @return the group
	 */
	@XmlElement(name = "Group")
	public final String getGroup() {
		return group;
	}
	/**
	 * @param newGroup the group to set
	 */
	public final void setGroup(final String newGroup) {
		this.group = newGroup;
	}
	/**
	 * @return the foreGroundColor
	 */
	@XmlElement(name = "ForeGroundColor")
	public final String getForeGroundColor() {
		return foreGroundColor;
	}
	/**
	 * @param newForeGroundColor the foreGroundColor to set
	 */
	public final void setForeGroundColor(final String newForeGroundColor) {
		this.foreGroundColor = newForeGroundColor;
	}
	/**
	 * @return the backGroundColor
	 */
	@XmlElement(name = "BackGroundColor")
	public final String getBackGroundColor() {
		return backGroundColor;
	}
	/**
	 * @param newBackGroundColor the backGroundColor to set
	 */
	public final void setBackGroundColor(final String newBackGroundColor) {
		this.backGroundColor = newBackGroundColor;
	}
	/**
	 * @return the codeStatus
	 */
	@XmlElement(name = "CodeStatus")
	public final String getCodeStatus() {
		return codeStatus;
	}
	/**
	 * @param newCodeStatus the codeStatus to set
	 */
	public final void setCodeStatus(final String newCodeStatus) {
		this.codeStatus = newCodeStatus;
	}
	/**
	 * @return the flashDuration
	 */
	public final double getFlashDuration() {
		return flashDuration;
	}
	/**
	 * @param newFlashDuration the flashDuration to set
	 */
	@XmlElement(name = "FlashDuration")
	public final void setFlashDuration(final double newFlashDuration) {
		this.flashDuration = newFlashDuration;
	}
	/**
	 * @return the _default
	 */
	@XmlElement(name = "Default")
	public final boolean isDefault() {
		return _default;
	}
	/**
	 * @param _default the _default to set
	 */
	public final void setDefault(boolean newDefault) {
		this._default = newDefault;
	}
	
	
}
