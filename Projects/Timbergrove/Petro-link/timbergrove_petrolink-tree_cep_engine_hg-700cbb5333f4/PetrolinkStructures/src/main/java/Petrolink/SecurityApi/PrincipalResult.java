package Petrolink.SecurityApi;

import java.util.ArrayList;

public class PrincipalResult {
	private ArrayList<Principal> principals;  
	private int totalRecords = 0;
	
	public PrincipalResult() {
		principals = new ArrayList<Principal>();
	}

	/**
	 * @return the principals
	 */
	public final ArrayList<Principal> getPrincipals() {
		return principals;
	}

	/**
	 * @param newPrincipals the principals to set
	 */
	public final void setPrincipals(final ArrayList<Principal> newPrincipals) {
		this.principals = newPrincipals;
	}

	/**
	 * @return the totalRecords
	 */
	public final int getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @param totalRecords the totalRecords to set
	 */
	public final void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
}
