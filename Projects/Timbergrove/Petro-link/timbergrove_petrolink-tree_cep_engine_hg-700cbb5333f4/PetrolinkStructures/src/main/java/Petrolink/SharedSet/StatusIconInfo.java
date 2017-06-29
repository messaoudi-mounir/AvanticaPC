package Petrolink.SharedSet;

import javax.xml.bind.annotation.XmlElement;

/**
 * Basic details of StatusIconInfo (used by SharedSet).
 * @author aristo
 */
public class StatusIconInfo implements IGenericXmlInfo {
	private String id;
	private String name;
	private String description;
	/* (non-Javadoc)
	 * @see Petrolink.SharedSet.IGenericXmlInfo#getId()
	 */
	@Override
	@XmlElement(name = "Id")  
	public final String getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see Petrolink.SharedSet.IGenericXmlInfo#setId(java.lang.String)
	 */
	@Override
	public final void setId(final String newId) {
		this.id = newId;
	}
	/* (non-Javadoc)
	 * @see Petrolink.SharedSet.IGenericXmlInfo#getName()
	 */
	@Override
	@XmlElement(name = "Name")
	public final String getName() {
		return name;
	}
	/* (non-Javadoc)
	 * @see Petrolink.SharedSet.IGenericXmlInfo#setName(java.lang.String)
	 */
	@Override
	public final void setName(String newName) {
		this.name = newName;
	}
	/* (non-Javadoc)
	 * @see Petrolink.SharedSet.IGenericXmlInfo#getDescription()
	 */
	@Override
	@XmlElement(name = "Description")
	public final String getDescription() {
		return description;
	}
	/* (non-Javadoc)
	 * @see Petrolink.SharedSet.IGenericXmlInfo#setDescription(java.lang.String)
	 */
	@Override
	public final void setDescription(String newDescription) {
		this.description = newDescription;
	}
}
