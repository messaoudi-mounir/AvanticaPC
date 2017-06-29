package Petrolink.SharedSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class StatusIconGroup {
	private String id;
	private String description;
	private String group;
	private String foreGroundColor;
	private String backGroundColor;
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
}
