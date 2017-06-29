package Petrolink.ResourceApi;

/**
 * Facade for Petrolink.PetroVaultHD.Model.Resource.ResourceLink
 * @author aristo
 *
 */
public class ResourceLink {
	private String uri;
	private String id;
	/**
	 * @return the uri
	 */
	public final String getUri() {
		return uri;
	}
	/**
	 * @param newUri the uri to set
	 */
	public final void setUri(final String newUri) {
		this.uri = newUri;
	}
	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}
	/**
	 * @param newId the id to set
	 */
	public final void setId(final String newId) {
		this.id = newId;
	}
	
	
}
