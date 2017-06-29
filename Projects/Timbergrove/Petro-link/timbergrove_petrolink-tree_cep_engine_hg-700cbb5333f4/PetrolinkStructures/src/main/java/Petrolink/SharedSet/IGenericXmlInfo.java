package Petrolink.SharedSet;

/**
 * Basic details of GenericXml (used by SharedSet).
 * @author aristo
 */
public interface IGenericXmlInfo {

	/**
	 * @return the id
	 */
	String getId();

	/**
	 * @param newId the id to set
	 */
	void setId(String newId);

	/**
	 * @return the name
	 */
	String getName();

	/**
	 * @param newName the name to set
	 */
	void setName(String newName);

	/**
	 * @return the description
	 */
	String getDescription();

	/**
	 * @param newDescription the description to set
	 */
	void setDescription(String newDescription);

}