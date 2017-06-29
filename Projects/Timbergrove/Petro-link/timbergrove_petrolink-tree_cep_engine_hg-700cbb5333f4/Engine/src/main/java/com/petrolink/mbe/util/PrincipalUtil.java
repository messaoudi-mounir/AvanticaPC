package com.petrolink.mbe.util;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import Petrolink.SecurityApi.Principal;

/**
 * Utility Class for processing Principal.
 * May need to be pput as converter or something
 * @author aristo
 *
 */
public final class PrincipalUtil {
	private PrincipalUtil() {
		
	}
	
	/**
	 * Get all Email adddress contained inside the list of Principal Details.
	 * @param principalList
	 * @return all Email adddress contained inside the list of Principal Details
	 */
	public static List<InternetAddress> getAllEmailAddress(final List<Principal> principalList) {
		List<InternetAddress> principalsAddresses = new ArrayList<InternetAddress>();
		//For each Child Principal, get emails
		if (principalList != null) {
			for (Principal prin:principalList) {
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
	 * Get all principal details with specified type from specified principalList.
	 * @param principalList Principals to be filtered.
	 * @param principalType Principal Type
	 * @return all principal details with specified type
	 */
	public static List<Principal> getPrincipalWithType(final List<Principal> principalList, final String principalType) {
		List<Principal> principals = new ArrayList<Principal>();
		//For each Child Principal, get specific type
		if (principalList != null) {
			for (Principal prin:principalList) {
			    if (principalType.equalsIgnoreCase(prin.getPrincipalType())) {
			    	principals.add(prin);
			    }
			}
		}
		return principals;
	}
}
