package Petrolink.ResourceApi;

import java.util.Map;

/**
 * Facade for Petrolink.PetroVaultHD.Model.Resource.ResourceMetadata .
 * @author aristo
 *
 */
public class ResourceMetadata {
	/**
	 * Id of the resource [required] [unique].
	 */
	private String id;

    /**
     * Uri of the resource [required] [unique].
     */
    private String uri;
    
    /**
     *  Type of the resource [required].
     */
    private String type;

    /**
     * Name of the resource [required].
     */
    private String name;

    /**
     * Originator ID.
     */
    private String creationUserId;

    /**
     * Originator name.
     */
    private String creationUserName;

    
    /**
     * Created time.
     */
    private long creationTimestamp;

    /**
     *  Id of user performing last update.
     */
    private String lastUpdateUserId;

    /**
     *  Name of user performing last update.
     */
    private String lastUpdateUserName;

    /**
     * Last Update Time.
     */
    private long lastUpdateTimestamp;

    private String version;

    private String description;
    
    private String keywords;
    
	/**
	 * Flag to determine whether its marked as hidden or not [optional].
	 */
    private boolean isMarkedDeleted;

    /**
     * Flag to determine whether the resource is in readonly mode or not [optional].
     */
    private boolean isHidden;
    
    /**
     * Flag to determine whether the resource is internal or not [optional].
     */
    private boolean isReadOnly;

    /**
     * Flag to determine whether the resource is internal or not [optional].
     */
    private boolean isInternal;

    /**
     * true if has children.
     */
    private boolean hasChildren;


    /**
     * Workflow steps.
     */
    private Map<String, String> workflowStep;

    /**
     * Mime type.
     */
    private String contentType;

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(final String newId) {
		id = newId;
	}

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
		uri = newUri;
	}

	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @param newType the type to set
	 */
	public final void setType(final String newType) {
		type = newType;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param newName the name to set
	 */
	public final void setName(final String newName) {
		name = newName;
	}

	/**
	 * @return the creationUserId
	 */
	public final String getCreationUserId() {
		return creationUserId;
	}

	/**
	 * @param newCreationUserId the creationUserId to set
	 */
	public final void setCreationUserId(final String newCreationUserId) {
		creationUserId = newCreationUserId;
	}

	/**
	 * @return the creationUserName
	 */
	public final String getCreationUserName() {
		return creationUserName;
	}

	/**
	 * @param newCreationUserName the creationUserName to set
	 */
	public final void setCreationUserName(final String newCreationUserName) {
		creationUserName = newCreationUserName;
	}

	/**
	 * @return the creationTimestamp
	 */
	public final long getCreationTimestamp() {
		return creationTimestamp;
	}

	/**
	 * @param newCreationTimestamp the creationTimestamp to set
	 */
	public final void setCreationTimestamp(final long newCreationTimestamp) {
		creationTimestamp = newCreationTimestamp;
	}

	/**
	 * @return the lastUpdateUserId
	 */
	public final String getLastUpdateUserId() {
		return lastUpdateUserId;
	}

	/**
	 * @param newLastUpdateUserId the lastUpdateUserId to set
	 */
	public final void setLastUpdateUserId(final String newLastUpdateUserId) {
		lastUpdateUserId = newLastUpdateUserId;
	}

	/**
	 * @return the lastUpdateUserName
	 */
	public final String getLastUpdateUserName() {
		return lastUpdateUserName;
	}

	/**
	 * @param newLastUpdateUserName the lastUpdateUserName to set
	 */
	public final void setLastUpdateUserName(final String newLastUpdateUserName) {
		lastUpdateUserName = newLastUpdateUserName;
	}

	/**
	 * @return the lastUpdateTimestamp
	 */
	public final long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	/**
	 * @param newLastUpdateTimestamp the lastUpdateTimestamp to set
	 */
	public final void setLastUpdateTimestamp(final long newLastUpdateTimestamp) {
		lastUpdateTimestamp = newLastUpdateTimestamp;
	}

	/**
	 * @return the version
	 */
	public final String getVersion() {
		return version;
	}

	/**
	 * @param newVersion the version to set
	 */
	public final void setVersion(final String newVersion) {
		version = newVersion;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @param newDescription the description to set
	 */
	public final void setDescription(final String newDescription) {
		description = newDescription;
	}

	/**
	 * @return the keywords
	 */
	public final String getKeywords() {
		return keywords;
	}

	/**
	 * @param newKeywords the keywords to set
	 */
	public final void setKeywords(final String newKeywords) {
		keywords = newKeywords;
	}

	/**
	 * @return the isMarkedDeleted
	 */
	public final boolean isIsMarkedDeleted() {
		return isMarkedDeleted;
	}

	/**
	 * @param markedDeleted the isMarkedDeleted to set
	 */
	public final void setIsMarkedDeleted(final boolean markedDeleted) {
		isMarkedDeleted = markedDeleted;
	}

	/**
	 * @return the isHidden
	 */
	public final boolean isIsHidden() {
		return isHidden;
	}

	/**
	 * @param hidden the isHidden to set
	 */
	public final void setIsHidden(final boolean hidden) {
		isHidden = hidden;
	}

	/**
	 * @return the isReadOnly
	 */
	public final boolean isIsReadOnly() {
		return isReadOnly;
	}

	/**
	 * @param readOnly the isReadOnly to set
	 */
	public final void setIsReadOnly(final boolean readOnly) {
		isReadOnly = readOnly;
	}

	/**
	 * @return the isInternal
	 */
	public final boolean isIsInternal() {
		return isInternal;
	}

	/**
	 * @param internal the isInternal to set
	 */
	public final void setIsInternal(final boolean internal) {
		isInternal = internal;
	}

	/**
	 * @return the hasChildren
	 */
	public final boolean isHasChildren() {
		return hasChildren;
	}

	/**
	 * @param hasChildren the hasChildren to set
	 */
	public final void setHasChildren(final boolean isHasChildren) {
		hasChildren = isHasChildren;
	}

	/**
	 * @return the workflowStep
	 */
	public final Map<String, String> getWorkflowStep() {
		return workflowStep;
	}

	/**
	 * @param newWorkflowStep the workflowStep to set
	 */
	public final void setWorkflowStep(final Map<String, String> newWorkflowStep) {
		workflowStep = newWorkflowStep;
	}

	/**
	 * @return the contentType
	 */
	public final String getContentType() {
		return contentType;
	}

	/**
	 * @param newContentType the contentType to set
	 */
	public final void setContentType(final String newContentType) {
		contentType = newContentType;
	}
}
