/**
 * 
 */
package com.petrolink.mbe.util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;

import com.petrolink.mbe.model.internet.InternetAddressContainer;

/**
 * Tool for Email/InternetAddress processing
 * @author aristo
 *
 */
public final class EmailHelper {
	/**
	 * Parse a comma separated emails into List of internet address.
	 * @param csvEmails The csv email 
	 * @return List of InternetAddress 
	 * @throws AddressException When parsing fails
	 */
	public static List<InternetAddress> parseAddresses(final String csvEmails) throws AddressException {
		if (StringUtils.isNotBlank(csvEmails)) {
			return Arrays.asList(InternetAddress.parse(csvEmails));
		} else {
			return Arrays.asList();
		}
			//manual parsing
//			String[] emailStringList = csvEmails.split(",");
//			InternetAddress address;
//			for (String addressString : emailStringList) {
//				address = new InternetAddress(addressString);
//				result.add(address);
//			}
	}
	
	
	/**
	 * Mail Action Recipient List. Resolute if necessary.
	 * @param containers 
	 * @param internetAddressDictionary Dictionary which will be used to map the InternetAddressContainer to actual InternetAddress
	 * @return List of recipients in InternetAddress.
	 */
	public static List<InternetAddress> getInternetAddress(final List<InternetAddressContainer> containers, final Map<String, List<InternetAddress>> internetAddressDictionary) {
		HashSet<InternetAddress> targetAddress =  new HashSet<InternetAddress>();
		if (containers != null) {
			for (InternetAddressContainer container:  containers) {
				container.appendTo(targetAddress, internetAddressDictionary);
			}
		}
		
		// Creating a List of HashSet elements
	     List<InternetAddress> result = new ArrayList<InternetAddress>(targetAddress);
		return result;
	}
	
	/**
	 * Get Email Message in RFC822 format
	 * @param email 
	 * @return the Email as String
	 */
	public static String getRfc822Message(final Email email) {
		if (email == null) {
			return null;
		}
		
		String emailString;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			email.buildMimeMessage();
			email.getMimeMessage().writeTo(os);
			emailString = new String(os.toByteArray());
		} catch (Exception e) {
			emailString = "(Can't construct email message: "+ e.getMessage()+")";
		} finally {
			try {
				os.close();
			} catch (Exception ex) {
				//Nothing we can do
			}
		}
		return emailString;
	}
}