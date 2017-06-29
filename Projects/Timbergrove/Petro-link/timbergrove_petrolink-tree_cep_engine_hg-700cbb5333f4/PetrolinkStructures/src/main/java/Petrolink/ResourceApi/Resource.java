package Petrolink.ResourceApi;

import java.util.List;
import java.util.Map;

import Petrolink.CommonApi.Property;

/**
 * Facade for Petrolink.PetroVaultHD.Model.Resource.Resource
 * @author aristo
 *
 */
public class Resource {
	/**
	 * Metadata of the resource [optional].
	 */
	private ResourceMetadata metadata;

	/**
	 * Holds resource properties [optional].
	 */
    private Map<String, List<Property>> properties;

    /**
     * Data of the resource [optional].
     */
    private ResourceData data;

    /**
     * Resource link with parent(s) (read only).
     */
    private List<ResourceLink> parents;

    /**
     * Hypermedia link.
     */
    private Petrolink.CommonApi.Link link;

	/**
	 * @return the metadata
	 */
	public final ResourceMetadata getMetadata() {
		return metadata;
	}

	/**
	 * @param newMetadata the metadata to set
	 */
	public final void setMetadata(final ResourceMetadata newMetadata) {
		metadata = newMetadata;
	}

	/**
	 * @return the properties
	 */
	public final Map<String, List<Property>> getProperties() {
		return properties;
	}

	/**
	 * @param newProperties the properties to set
	 */
	public final void setProperties(final Map<String, List<Property>> newProperties) {
		properties = newProperties;
	}

	/**
	 * @return the data
	 */
	public final ResourceData getData() {
		return data;
	}

	/**
	 * @param newData the data to set
	 */
	public final void setData(final ResourceData newData) {
		data = newData;
	}

	/**
	 * @return the parents
	 */
	public final List<ResourceLink> getParents() {
		return parents;
	}

	/**
	 * @param newParents the parents to set
	 */
	public final void setParents(final List<ResourceLink> newParents) {
		parents = newParents;
	}

	/**
	 * @return the link
	 */
	public final Petrolink.CommonApi.Link getLink() {
		return link;
	}

	/**
	 * @param newLink the link to set
	 */
	public final void setLink(final Petrolink.CommonApi.Link newLink) {
		link = newLink;
	}
}
