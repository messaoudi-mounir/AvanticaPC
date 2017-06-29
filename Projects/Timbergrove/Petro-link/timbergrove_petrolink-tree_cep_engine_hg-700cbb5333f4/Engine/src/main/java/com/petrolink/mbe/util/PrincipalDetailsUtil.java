package com.petrolink.mbe.util;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import Petrolink.SecurityApi.PrincipalDetails;

/**
 * Utility Class for processing Principal Details.
 * May need to be put as converter or something
 * @author aristo
 *
 */
public final class PrincipalDetailsUtil {
	private PrincipalDetailsUtil() {
		
	}
	
	/**
	 * Get all Email adddress contained inside the list of Principal Details.
	 * @param principalList
	 * @return all Email adddress contained inside the list of Principal Details
	 */
	public static List<InternetAddress> getAllEmailAddress(final List<PrincipalDetails> principalList) {
		List<InternetAddress> principalsAddresses = new ArrayList<InternetAddress>();
		//For each Child Principal, get emails
		if (principalList != null) {
			for (PrincipalDetails prin:principalList) {
				try {
					//Add per principal
					List<InternetAddress> prinEmails = EmailHelper.parseAddresses(prin.getEmail());
					principalsAddresses.addAll(prinEmails);
				} catch (AddressException e) {
					//Ignore
				}
			}
		}
		return principalsAddresses;
	}
	
	/**
	 * Get all SMS Gateway Email adddress contained inside the list of Principal Details.
	 * @param principalList
	 * @return all SMS Gateway Email adddress contained inside the list of Principal Details
	 */
	public static List<InternetAddress> getAllSmsPhoneEmailAddress(final List<PrincipalDetails> principalList) {
		List<InternetAddress> principalsAddresses = new ArrayList<InternetAddress>();
		//For each Child Principal, get emails
		if (principalList != null) {
			for (PrincipalDetails prin:principalList) {
				try {
					//Add per principal
					List<InternetAddress> prinEmails = EmailHelper.parseAddresses(prin.getSMSPhone());
					principalsAddresses.addAll(prinEmails);
				} catch (AddressException e) {
					//Ignore
				}
			}
		}
		return principalsAddresses;
	}

	/**
	 * Get all Mobile number contained inside the list of Principal Details.
	 * @param principalList
	 * @return all Mobile number contained inside the list of Principal Details
	 */
	public static List<String> getAllMobileNumber(final List<PrincipalDetails> principalList) {
		List<String> principalsAddresses = new ArrayList<String>();
		//For each Child Principal, get Mobile number
		if (principalList != null) {
			for (PrincipalDetails prin:principalList) {
				principalsAddresses.add(prin.getMobile());
			}
		}
		return principalsAddresses;
	}
	
	/**
	 * Get all principal details with specified type from specified principalList.
	 * @param principalList Principals to be filtered.
	 * @param principalType Principal Type
	 * @return all principal details with specified type
	 */
	public static List<PrincipalDetails> getPrincipalWithType(final List<PrincipalDetails> principalList, final String principalType) {
		List<PrincipalDetails> principals = new ArrayList<PrincipalDetails>();
		//For each Child Principal, get specific type
		if (principalList != null) {
			for (PrincipalDetails prin:principalList) {
			    if (principalType.equalsIgnoreCase(prin.getType())) {
			    	principals.add(prin);
			    }
			}
		}
		return principals;
	}
}
