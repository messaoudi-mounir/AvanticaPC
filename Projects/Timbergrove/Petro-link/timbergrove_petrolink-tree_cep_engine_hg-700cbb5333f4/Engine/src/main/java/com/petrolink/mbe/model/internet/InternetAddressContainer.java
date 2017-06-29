package com.petrolink.mbe.model.internet;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

/**
 * Interface for Container which can resolve to Internet address.
 * @author aristo
 *
 */
public interface InternetAddressContainer {
	/**
	 * Append InternetAddresses available in this object to specified targetList, this should use dictionary when needed.
	 * @param targetList
	 * @param internetAddressDictionary
	 */
	void appendTo(Collection<InternetAddress> targetList, Map<String, List<InternetAddress>> internetAddressDictionary);
}