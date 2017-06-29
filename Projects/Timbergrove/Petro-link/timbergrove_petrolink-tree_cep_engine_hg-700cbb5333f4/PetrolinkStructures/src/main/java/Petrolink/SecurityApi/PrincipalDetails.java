package Petrolink.SecurityApi;

import java.util.UUID;

/**
 * Detail of Principal.
 * @author Aristo
 *
 */
public class PrincipalDetails {
    private UUID id;
    private String name;
    private String type;
    private String fullName;
    private String email;
    private String phone;
    private String mobile;
    private String smsPhone;
    
	/**
	 * @return the id
	 */
	public final UUID getId() {
		return id;
	}
	/**
	 * @param newId the id to set
	 */
	public final void setId(final UUID newId) {
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
	 * @return the type
	 */
	public final String getType() {
		return type;
	}
	/**
	 * @param newName the type to set
	 */
	public final void setType(final String newType) {
		this.type = newType;
	}
	
	/**
	 * Check whether Principal is user.
	 * @return
	 */
	public final boolean isUser() {
		return Principal.PRINCIPAL_TYPE_USER.equalsIgnoreCase(type);
	}
	
	/**
	 * Check whether Principal is group.
	 * @return
	 */
	public final boolean isGroup() {
		return Principal.PRINCIPAL_TYPE_GROUP.equalsIgnoreCase(type);
	}
	
	
	/**
	 * @return the fullName
	 */
	public final String getFullName() {
		return fullName;
	}
	/**
	 * @param principalFullName the Full Name for this principal
	 */
	public final void setFullName(final String principalFullName) {
		this.fullName = principalFullName;
	}
	/**
	 * @return the email
	 */
	public final String getEmail() {
		return email;
	}
	/**
	 * @param emailAddress the email to set
	 */
	public final void setEmail(final String emailAddress) {
		this.email = emailAddress;
	}
	/**
	 * @return the phone
	 */
	public final String getPhone() {
		return phone;
	}
	/**
	 * @param phoneNumber the phone to set
	 */
	public final void setPhone(final String phoneNumber) {
		this.phone = phoneNumber;
	}
	/**
	 * @return the mobile
	 */
	public final String getMobile() {
		return mobile;
	}
	/**
	 * @param mobilePhoneNumber the mobile to set
	 */
	public final void setMobile(final String mobilePhoneNumber) {
		this.mobile = mobilePhoneNumber;
	}
	/**
	 * @return the sms gateway email address. Eg 18887779999@text2email.net
	 */
	public final String getSMSPhone() {
		return smsPhone;
	}
	/**
	 * @param smsAlertGateway the  gateway email address. Eg 18887779999@text2email.net
	 */
	public final void setSMSPhone(final String smsAlertGateway) {
		this.smsPhone = smsAlertGateway;
	}
	
	@Override
	public String toString() {
		return PrincipalDetails.class.getName() + " = " + getId() + ", " + getName() + ", " + getType() + ", Email: " + getEmail() + ", SMSAlert:" + getSMSPhone() + ", Mobile:" + getMobile();
	}
}
