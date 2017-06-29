package com.petrolink.mbe.cache.impl;

import java.util.Objects;
import java.util.UUID;

import com.petrolink.mbe.cache.Resource;

/**
 * Generic Cache Resource Implementation
 * @author paul
 *
 */
public class CacheResourceImpl implements Resource {
	private UUID uuid;
	private String name;
	private String uri;
	private String owner;

	/**
	 * @param uuid
	 */
	public CacheResourceImpl(UUID uuid) {
		this.uuid = Objects.requireNonNull(uuid);
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	@Override
	public UUID getId() {
		return uuid;
	}
	
	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
}
