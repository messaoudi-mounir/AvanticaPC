package com.petrolink.mbe.model.internet;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.mail.internet.InternetAddress;

/**
 * Container for Basic Internet Address (a single Internet Address).
 * @author aristo
 *
 */
public class InternetAddressBasicContainer implements InternetAddressContainer {

	private InternetAddress content; 
	/**
	 * Constructor.
	 * @param input
	 */
	public InternetAddressBasicContainer(final InternetAddress input) {
		content = input;
	}
	
	@Override
	public void appendTo(final Collection<InternetAddress> targetList, final Map<String, List<InternetAddress>> internetAddressDictionary) {
		if (targetList == null) {
			return;
		}
		
		targetList.add(content);
	}

}
