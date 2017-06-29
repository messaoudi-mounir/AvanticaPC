package com.petrolink.mbe.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

import com.petrolink.mbe.model.internet.AddressSet;
import com.petrolink.mbe.model.internet.InternetAddressContainer;
/**
 * Configuration for sending email.
 * @author aristo
 */
public class EmailSetting {
	
	/**
	 * The type of the email.
	 * @author aristo
	 *
	 */
	public enum EmailTypes {
		/**
		 * HTML email.
		 */
		HTML, 
		/**
		 * Plain text email.
		 */
		PLAINTEXT
	}
	
	/**
	 * If recipient is guid, what kind of of email needed from Principal service to obtain. Default is email. 
	 * @author aristo
	 */
	public enum GuidAddressMappings {
		/**
		 * Unknown, by defualt will resolve Guid Address to Principal's Email
		 */
		UNKNOWN, 
		/**
		 * Resolve Guid Address to Principal's Email
		 */
		EMAIL,
		/**
		 * Resolve Guid Address to Principal's SmsPhone Email
		 */
		SMS_PHONE_EMAIL
	}
	
	/**
	 * The Content Type if Email type is plain.
	 */
	public static final String CONTENT_TYPE_PLAIN =  "text/plain";
	
	/**
	 * The Content Type if Email type is HTML.
	 */
	public static final String CONTENT_TYPE_HTML =  "text/html";
	private SMTPConnectionSettings connectionSetting;
	private InternetAddress emailFrom;
	private String emailSubject;
	private String emailBody;
	private HashMap<String, Object> actionParameters;
	private AddressSet to;
	private AddressSet cc;
	private AddressSet bcc;
	private HashSet<UUID> guidRecipientsCombined;
	private String contentType = null;
	private GuidAddressMappings guidRecipientsResolutionMode = GuidAddressMappings.EMAIL;
	private String connectionSettingRefId;
	
	/**
	 * Constructor.
	 */
	public EmailSetting() {
		to = new AddressSet();
		cc = new AddressSet();
		bcc = new AddressSet();
		actionParameters = new HashMap<String, Object>();
	}
	
	/**
	 * @return the sendMailConnectionConfiguration
	 */
	public final SMTPConnectionSettings getConnectionSetting() {
		return connectionSetting;
	}

	/**
	 * @param newConfiguration the sendMailConnectionConfiguration to set
	 */
	public final void setConnectionSetting(final SMTPConnectionSettings newConfiguration) {
		this.connectionSetting = newConfiguration;
	}
	
	/**
	 * @return the emailFrom
	 */
	public final InternetAddress getEmailFrom() {
		return emailFrom;
	}

	/**
	 * @param newEmailFrom the emailFrom to set
	 */
	public final void setEmailFrom(final InternetAddress newEmailFrom) {
		this.emailFrom = newEmailFrom;
	}

	/**
	 * @return the emailSubject
	 */
	public final String getEmailSubject() {
		return emailSubject;
	}

	/**
	 * @param newEmailSubject the emailSubject to set
	 */
	public final void setEmailSubject(final String newEmailSubject) {
		this.emailSubject = newEmailSubject;
	}

	/**
	 * @return the emailBody
	 */
	public final String getEmailBody() {
		return emailBody;
	}

	/**
	 * @param newEmailBody the emailBody to set
	 */
	public final void setEmailBody(final String newEmailBody) {
		this.emailBody = newEmailBody;
	}
	
	/**
	 * Whether Email Body is available and not empty.
	 * @return Whether Email Body is available and not empty.
	 */
	public final boolean hasEmailBody() {
		return StringUtils.isNotBlank(getEmailBody());
	}
	
	/**
	 * Whether Email Subject is available and not empty.
	 * @return Whether Email Subject is available and not empty.
	 */
	public final boolean hasEmailSubject() {
		return StringUtils.isNotBlank(getEmailSubject());
	}
	
	/**
	 * Whether one of Email Body and Subject is available and not empty.
	 * @return Whether one of Email Body and Subject is available and not empty.
	 */
	public final boolean hasEmailBodyOrSubject() {
		return hasEmailBody() || hasEmailSubject();
	}
	/**
	 * Mail Action Recipient List, TO Category.
	 * @return List of TO recipients.
	 */
	public final List<InternetAddressContainer> getRecipientsTO() {
		return to.getAddresses();
	}

	/**
	 * Mail Action Recipient List, CC category.  
	 * @return List of CC recipients.
	 */
	public final List<InternetAddressContainer> getRecipientsCC() {
		return cc.getAddresses();
	}

	/**
	 * Mail Action Recipient List, BCC category.  
	 * @return List of BCC recipients.
	 */
	public final List<InternetAddressContainer> getRecipientsBCC() {
		return bcc.getAddresses();
	}
	
	/**
	 * Calculate total number of TO,CC,BCC recipients.
	 * @return
	 */
	private int getRecipientsAllCount() {

		int recipientsCount = 0;
		AddressSet currentTo = this.to;
		if (currentTo != null) {
			recipientsCount += currentTo.size();
		}
		
		AddressSet currentCc = this.cc;
		if (currentCc != null) {
			recipientsCount += currentCc.size();
		}
		
		AddressSet currentBcc = this.bcc;
		if (currentBcc != null) {
			recipientsCount += currentBcc.size();
		}
		return recipientsCount;
	}
	
	/**
	 * @return the actionParameters
	 */
	public final HashMap<String, Object> getActionParameters() {
		return actionParameters;
	}

	/**
	 * @param newActionParameters the actionParameters to set
	 */
	public final void setActionParameters(final HashMap<String, Object> newActionParameters) {
		this.actionParameters = newActionParameters;
	}
	
	/**
	 * Set Recipients TO with csv string.
	 * @param csvTo
	 */
	public final void setCsvRecipientsTO(final String csvTo) {
		setTO(AddressSet.fromCsv(csvTo));
	}
	
	/**
	 * Set Recipients CC with csv string.
	 * @param csvCc
	 */
	public final void setCsvRecipientsCC(final String csvCc) {
		setCC(AddressSet.fromCsv(csvCc));
	}
	
	/**
	 * Set Recipients BCC with csv string.
	 * @param csvBcc
	 */
	public final void setCsvRecipientsBCC(final String csvBcc) {
		setBCC(AddressSet.fromCsv(csvBcc));
	}
	
	private void updateGuidRecipientsCombined() {
		HashSet<UUID> newGuidRecipientsCombined = new HashSet<UUID>();
		
		AddressSet currentTo = getTO();
		if (currentTo != null) {
			HashSet<UUID> guidsTo = currentTo.getUUIDAddresses();
			if (guidsTo != null) {
				newGuidRecipientsCombined.addAll(guidsTo);
			}
		}
		
		AddressSet currentCC = getCC();
		if (currentCC != null) {
			HashSet<UUID> guidsCC = currentCC.getUUIDAddresses();
			if (guidsCC != null) {
				newGuidRecipientsCombined.addAll(guidsCC);
			}
		}
		
		AddressSet currentBCC = getBCC();
		if (currentBCC != null) {
			HashSet<UUID> guidsBCC = currentBCC.getUUIDAddresses();
			if (guidsBCC != null) {
				newGuidRecipientsCombined.addAll(guidsBCC);
			}
		}
		
		guidRecipientsCombined = newGuidRecipientsCombined;
	}
	
	/**
	 * Mail Action Recipient List. Resolute if necessary.
	 * @param principalToEmailsDictionary The dictionary for resolving non-email address (eg Guid) to List of Internet Address
	 * @return List of Internet address from address set which is resolved with the dictionary.
	 */
	public final List<InternetAddress> getInternetAddressListTO(final Map<String, List<InternetAddress>> principalToEmailsDictionary) {
		if (to == null) return null;
		return to.getInternetAddressList(principalToEmailsDictionary);
		
	}
	
	/**
	 * Mail Action Recipient List. Resolute if necessary.
	 * @param principalToEmailsDictionary The dictionary for resolving non-email address (eg Guid) to List of Internet Address
	 * @return List of Internet address from address set which is resolved with the dictionary.
	 */
	public final List<InternetAddress> getInternetAddressListCC(final Map<String, List<InternetAddress>> principalToEmailsDictionary) {
		if (cc == null) return null;
		return cc.getInternetAddressList(principalToEmailsDictionary);
	}
	
	/**
	 * Mail Action Recipient List. Resolute if necessary.
	 * @param principalToEmailsDictionary The dictionary for resolving non-email address (eg Guid) to List of Internet Address
	 * @return List of Internet address from address set which is resolved with the dictionary.
	 */
	public final List<InternetAddress> getInternetAddressListBCC(final Map<String, List<InternetAddress>> principalToEmailsDictionary) {
		if (bcc == null) return null;
		return bcc.getInternetAddressList(principalToEmailsDictionary);
	}
	
	/**
	 * Get Unresolved UUID address for All Recipients.
	 * @return Get all UUID address for all recipients
	 */
	public final HashSet<UUID> getGuidRecipientsCombined() {
		return guidRecipientsCombined;
	}

		
	/**
	 * Check this object for missing parameter for actual action.
	 * Useful for debugging/logging
	 * @return List of missing parameter for this action
	 */
	public final List<String> getMissingSettings() {
		ArrayList<String> missingParameters = new ArrayList<String>();
		if (getConnectionSetting() == null) {
			missingParameters.add("Connection");
		}
		
		if (StringUtils.isBlank(getEmailBody())) {
			missingParameters.add("Body");
		}
		
		if (this.getEmailFrom() == null) {
			missingParameters.add("From");
		}
		
		if (this.getRecipientsAllCount() <= 0) {
			missingParameters.add("Recipients (to/cc/bcc)");
		}
		
		return missingParameters;
	}
	
	/**
	 * Get a new merged EmailSetting by combining this instance with the template.
	 * @param template
	 * @return A new merged EmailSetting
	 */
	public final EmailSetting mergeWithTemplate(final EmailSetting template) {
		EmailSetting result = new EmailSetting();
		
		EmailSetting main = this;
		result.setConnectionSetting(main.getConnectionSetting());
		if (result.getConnectionSetting() == null) {
			result.setConnectionSetting(template.getConnectionSetting());
		}
		
		result.setContentType(main.getContentType());
		if (StringUtils.isBlank(result.getContentType())) {
			result.setContentType(template.getContentType());
		}
		
		result.setGuidRecipientsMappingMode(main.getGuidRecipientsMappingMode());
		if (result.getGuidRecipientsMappingMode() == GuidAddressMappings.UNKNOWN) {
			result.setGuidRecipientsMappingMode(template.getGuidRecipientsMappingMode());
		}
		
		result.setEmailBody(main.getEmailBody());
		if (StringUtils.isBlank(result.getEmailBody())) {
			result.setEmailBody(template.getEmailBody());
		}
		
		result.setEmailSubject(main.getEmailSubject());
		if (StringUtils.isBlank(result.getEmailSubject())) {
			result.setEmailSubject(template.getEmailSubject());
		}
		if (result.getEmailSubject() == null) {
			result.setEmailSubject(StringUtils.EMPTY);
		}
		
		result.setEmailFrom(main.getEmailFrom());
		if (result.getEmailFrom() == null) {
			result.setEmailFrom(template.getEmailFrom());
		}
		
		result.setTO(main.getTO());
		if (result.getTO() == null) {
			result.setTO(template.getTO());
		}
		
		result.setCC(main.getCC());
		if (result.getCC() == null) {
			result.setCC(template.getCC());
		}
		
		result.setBCC(main.getBCC());
		if (result.getBCC() == null) {
			result.setBCC(template.getBCC());
		}
		
		//Param should be merged instead
		HashMap<String, Object> newActionParameters = new HashMap<String, Object>();
		//Template goes first as the current setting may be overriden
		HashMap<String, Object> templateParameters = template.getActionParameters();
		HashMap<String, Object> thisParameters = main.getActionParameters();
		if (templateParameters != null) {
			newActionParameters.putAll(templateParameters);
		}
		if (thisParameters != null) {
			newActionParameters.putAll(thisParameters);
		}
		result.setActionParameters(newActionParameters);
		
		
		
		return result;
	}

	/**
	 * @return the to
	 */
	public final AddressSet getTO() {
		return to;
	}

	/**
	 * @param toAddr the to to set
	 */
	public final void setTO(final AddressSet toAddr) {
		this.to = toAddr;
		updateGuidRecipientsCombined();
	}

	/**
	 * @return the cc
	 */
	public final AddressSet getCC() {
		return cc;
	}

	/**
	 * @param ccAddr the cc to set
	 */
	public final void setCC(final AddressSet ccAddr) {
		this.cc = ccAddr;
		updateGuidRecipientsCombined();
	}

	/**
	 * @return the bcc
	 */
	public final AddressSet getBCC() {
		return bcc;
	}

	/**
	 * @param bccAddr the bcc to set
	 */
	public final void setBCC(final AddressSet bccAddr) {
		this.bcc = bccAddr;
		updateGuidRecipientsCombined();
	}

	/**
	 * @return the contentType
	 */
	public final String getContentType() {
		return contentType;
	}

	/**
	 * @param newContentType the contentType to set
	 */
	public final void setContentType(final String newContentType) {
		this.contentType = newContentType;
		updateEmailType();
	}
	
	
	private EmailTypes emailType = EmailTypes.HTML;
	/**
	 * Update EmailType.
	 * emailType is Plaintext if it contains  <a href="#CONTENT_TYPE_PLAIN">CONTENT_TYPE_PLAIN</a>, otherwise it is HTML
	 */
	private void updateEmailType() {
		String currentContentType = getContentType();
		if (StringUtils.isBlank(currentContentType)) {
			emailType = EmailTypes.HTML;
		} else if (currentContentType.contains(CONTENT_TYPE_PLAIN)) {
			emailType = EmailTypes.PLAINTEXT;
		} else if (currentContentType.contains(CONTENT_TYPE_HTML)) {
			emailType = EmailTypes.HTML;
		} else {
			emailType = EmailTypes.HTML;
		}
	}
	
	/**
	 * Whether the Content type of this email is plain text.
	 * @return True when current ContentType is contains <a href="#CONTENT_TYPE_PLAIN">CONTENT_TYPE_PLAIN</a> , false otherwise 
	 */
	public final boolean isPlainTextEmail() {
		return emailType == EmailTypes.PLAINTEXT;
	}
	
	/**
	 * Whether the Content type of this email is Html. if content type is not set, this also true since html is default.
	 * @return True if current ContentType is blank or contains <a href="#CONTENT_TYPE_HTML">CONTENT_TYPE_HTML</a>, false otherwise
	 */
	public final boolean isHtmlEmail() {
		return emailType == EmailTypes.HTML;
	}

	/**
	 * @return the guidRecipientsResolutionMode
	 */
	public final GuidAddressMappings getGuidRecipientsMappingMode() {
		return guidRecipientsResolutionMode;
	}

	/**
	 * @param resolutionMode the guidRecipientsResolutionMode to set
	 */
	public final void setGuidRecipientsMappingMode(GuidAddressMappings resolutionMode) {
		guidRecipientsResolutionMode = resolutionMode;
	}

	/**
	 * Use reference to other Connection setting
	 * @return a connectionSettingRefId
	 */
	public String getConnectionSettingRef() {
		return connectionSettingRefId;
	}
	
	/**
	 * Set Reference to other connection setting
	 * @param connectionId
	 */
	public void setConnectionSettingRef(String connectionId) {
		connectionSettingRefId = connectionId;
	}
}
