package Petrolink.SecurityApi;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for storing Principal from securityAPI.
 * @author aristo
 */
public class Principal {
	public static final String PRINCIPAL_TYPE_USER = "u";
	public static final String PRINCIPAL_TYPE_GROUP = "g";
    private String id;
    private String name;
    private String fullName;
    private String email;
    private boolean isLocal;
    private String principalType;
    private String company;
    private List<Principal> members;
    
    public Principal() {
    	members = new ArrayList<Principal>();
	}
    
    /**
	 * @return the id
	 */
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
	 * @return the name
	 */
	public final String getName() {
		return name;
	}
	/**
	 * @param newName the name to set
	 */
	public final void setName(final String newName) {
		this.name = newName;
	}
	/**
	 * @return the fullName
	 */
	public final String getFullName() {
		return fullName;
	}
	/**
	 * @param newFullName the fullName to set
	 */
	public final void setFullName(final String newFullName) {
		this.fullName = newFullName;
	}
	/**
	 * @return the email
	 */
	public final String getEmail() {
		return email;
	}
	/**
	 * @param newEmail the email to set
	 */
	public final void setEmail(final String newEmail) {
		this.email = newEmail;
	}
	/**
	 * @return the principalType
	 */
	public final String getPrincipalType() {
		return principalType;
	}
	
	/**
	 * Check whether Principal is user.
	 * @return
	 */
	public final boolean isUser() {
		return PRINCIPAL_TYPE_USER.equalsIgnoreCase(principalType);
	}
	
	/**
	 * Check whether Principal is group.
	 * @return
	 */
	public final boolean isGroup() {
		return PRINCIPAL_TYPE_GROUP.equalsIgnoreCase(principalType);
	}
	
	/**
	 * @param newPrincipalType the principalType to set
	 */
	public final void setPrincipalType(final String newPrincipalType) {
		this.principalType = newPrincipalType;
	}
	/**
	 * @return the company
	 */
	public final String getCompany() {
		return company;
	}
	/**
	 * @param newCompany the company to set
	 */
	public final void setCompany(final String newCompany) {
		this.company = newCompany;
	}
	/**
	 * @return the members
	 */
	public final List<Principal> getMembers() {
		return members;
	}
	/**
	 * @param newMembers the members to set
	 */
	public final void setMembers(final List<Principal> newMembers) {
		this.members = newMembers;
	}

	/**
	 * @return the isLocal
	 */
	public final boolean getIsLocal() {
		return isLocal;
	}

	/**
	 * @param newIsLocal the isLocal to set
	 */
	public final void setIsLocal(final boolean newIsLocal) {
		this.isLocal = newIsLocal;
	}
	
	@Override
	public String toString() {
		return Principal.class.getName() + " (" + getId() + ", " + getName() + ", " + getPrincipalType() + ")";
	}
}
