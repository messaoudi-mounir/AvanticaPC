package Petrolink.ResourceApi;

/**
 * Facade for Petrolink.PetroVaultHD.Model.Resource.ResourceData.
 * @author aristo
 *
 */
public class ResourceData {
	
	private String contentEncoding;
	
	private byte[] content;
    
	private long modifyTimestamp;

	/**
	 * @return the contentEncoding
	 */
	public final String getContentEncoding() {
		return contentEncoding;
	}

	/**
	 * @param newContentEncoding the contentEncoding to set
	 */
	public final void setContentEncoding(final String newContentEncoding) {
		this.contentEncoding = newContentEncoding;
	}

	/**
	 * @return the content
	 */
	public final byte[] getContent() {
		return content;
	}

	/**
	 * @param contentArray the content to set
	 */
	public final void setContent(final byte[] contentArray) {
		this.content = contentArray;
	}

	/**
	 * @return the modifyTimestamp
	 */
	public final long getModifyTimestamp() {
		return modifyTimestamp;
	}

	/**
	 * @param modifiedTimestamp the modifyTimestamp to set
	 */
	public final void setModifyTimestamp(final long modifiedTimestamp) {
		this.modifyTimestamp = modifiedTimestamp;
	}
}
