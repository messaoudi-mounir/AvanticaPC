package com.petrolink.mbe.model.internet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

import com.petrolink.mbe.util.EmailHelper;
import com.petrolink.mbe.util.UUIDHelper;

/**
 * An Address Collection which is able to convert from csv string to addresses.
 * @author aristo
 *
 */
public class AddressSet {
	
	private HashSet<UUID> uuidAddresses;
	private List<InternetAddressContainer> addresses;
	
	/**
	 * Constructor. See {@link #fromCsv(String)} for alternative way to construct.
	 */
	public AddressSet() {
		uuidAddresses = new HashSet<UUID>();
		addresses = new ArrayList<InternetAddressContainer>();
	}
	
	
	
	/**
	 * Get Addresses which are simply UUID Address as collection of GUIDS.
	 * @return A hashset containing UUID which is part of this address set
	 */
	public final HashSet<UUID> getUUIDAddresses() {
		return uuidAddresses;
	}
	
	/**
	 * Mail Action Recipient List. Resolute if necessary.
	 * @param principalToEmailsDictionary The dictionary for resolving non-email address (eg Guid) to List of Internet Address
	 * @return Internet address list 
	 */
	public final List<InternetAddress> getInternetAddressList(final Map<String, List<InternetAddress>> principalToEmailsDictionary) {
		return EmailHelper.getInternetAddress(getAddresses(), principalToEmailsDictionary);
	}
	
	/**
	 * Mail Action Recipient List, TO Category.
	 * @return List of TO recipients.
	 */
	public final List<InternetAddressContainer> getAddresses() {
		return addresses;
	}

	/**
	 * @param internetAddressContainers the internetAddressContainers to set
	 */
	public final void setAddresses(final List<InternetAddressContainer> internetAddressContainers) {
		if (internetAddressContainers ==  null) {
			this.addresses = null;
			this.uuidAddresses = null;
			return;
		}
		
		//Collect Things need UUID
		HashSet<UUID> principalUUIDs = new HashSet<UUID>();
		for (InternetAddressContainer address: internetAddressContainers) {
			if (address instanceof InternetAddressPrincipalGuidContainer) {
				InternetAddressPrincipalGuidContainer prinAddress = (InternetAddressPrincipalGuidContainer) address;
				principalUUIDs.add(prinAddress.getPrincipalUUID());
			}
		}
		
		this.addresses = internetAddressContainers;
		this.uuidAddresses = principalUUIDs;
	}
	
	/**
	 * Return number of addresses.
	 * @return number of addresses
	 */
	public final int size() {
		List<InternetAddressContainer> currentAddresses = getAddresses();
		if (currentAddresses != null) {
			return currentAddresses.size();
		}
		return 0;
	}
	
	/**
	 * Whether this address set is empty.
	 * @return True if size is 0 or less, false otherwise
	 */
	public final boolean isEmpty() {
		return (size() <= 0);
	}
	/**
	 * Generate Address set from Comma Separated Value.
	 * @param csvAddress
	 * @return AddressSet object containing information from the csv
	 */
	public static AddressSet fromCsv(final String csvAddress) {
		//Null is different to empty string
		if (csvAddress == null) {
			return null;
		}
		
		AddressSet parsingResult = new AddressSet();
		if (StringUtils.isNotBlank(csvAddress)) {
			//manual parsing
			String[] emailStringList = csvAddress.split(",");
			ArrayList<InternetAddressContainer> result = new ArrayList<InternetAddressContainer>(emailStringList.length);

			InternetAddressContainer recipient;
			for (String addressString : emailStringList) {
				recipient = null;
				
				//Try Guid
				if (recipient == null) {
					UUID principalId = UUIDHelper.fromStringFast(addressString, true);
					
					//If not Empty. This use UUID Fast as there would be many UUID
					if (!UUIDHelper.isNullOrEmpty(principalId)) {
						recipient = new InternetAddressPrincipalGuidContainer(principalId);
					}
				}
				
				//Try Internet Address
				if (recipient == null) {
					try {
						InternetAddress emailAddress = new InternetAddress(addressString);
						recipient = new InternetAddressBasicContainer(emailAddress);
					} catch (AddressException e) {
						// Not as often, should be fine
					}
				}
				
				
				if (recipient != null) {
					result.add(recipient);
				}
			}
			
			parsingResult.setAddresses(result);
			return parsingResult;
		} else {
			return parsingResult;
		}
	}
}

