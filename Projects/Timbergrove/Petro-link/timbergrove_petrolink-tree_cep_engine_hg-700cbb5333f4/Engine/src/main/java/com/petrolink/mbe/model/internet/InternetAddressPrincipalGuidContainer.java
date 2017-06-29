package com.petrolink.mbe.model.internet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.InternetAddress;
/**
 * Container for Internet address based on principal's GUID.
 * @author aristo
 *
 */
public class InternetAddressPrincipalGuidContainer implements InternetAddressContainer {

	private UUID principalUUID;
	/**
	 * Constructor.
	 * @param newPrincipalId
	 */
	public InternetAddressPrincipalGuidContainer(UUID newPrincipalId) {
		principalUUID = newPrincipalId;
	}
	
	/**
	 * Append InternetAddresses available in this object to specified targetList, this will use dictionary to convert UUID to internet address
	 * @param targetList
	 * @param internetAddressDictionary
	 */
	@Override
	public final void appendTo(Collection<InternetAddress> targetList,  Map<String,List<InternetAddress>> internetAddressDictionary) {
		if (targetList == null) {
			return;
		}
		if (internetAddressDictionary == null) {
			return;
		}
		String principalId = principalUUID.toString();
		List<InternetAddress> addresses =  internetAddressDictionary.get(principalId);
		targetList.addAll(addresses);
	}

	/**
	 * @return the principalUUID
	 */
	public final UUID getPrincipalUUID() {
		return principalUUID;
	}

}
