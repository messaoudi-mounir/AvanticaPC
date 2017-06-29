/**
 * 
 */
package com.petrolink.mbe.cache;

import java.util.UUID;

/**
 * @author Jose Luis Moya Sobrado
 *
 */
public interface Resource {
	/**
	 * @return The resource name
	 */
	String getName();

	/**
	 * @param name The resource name
	 */
	void setName(String name);
	
	/**
	 * @return The resource UUID
	 */
	UUID getId();
	
	/**
	 * @return The URI associated with this resource.
	 */
	String getUri();
	
	/**
	 * @param uri A new URI for this resource.
	 */
	void setUri(String uri);
	/**
	 * @return the owner
	 */
	public String getOwner();
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner);	
	
}
