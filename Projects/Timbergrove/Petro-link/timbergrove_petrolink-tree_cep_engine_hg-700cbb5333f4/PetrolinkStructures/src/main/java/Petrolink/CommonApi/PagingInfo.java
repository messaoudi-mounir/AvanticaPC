package Petrolink.CommonApi;

import java.util.List;

/**
 * Facade for Petrolink.PetroVaultHD.Model.Common.PagingInfo
 * @author aristo
 *
 */
public class PagingInfo {
	private int page;
	private int pageSize;
	private int pageCount;
	private int totalCount;
	private List<Link> links;
	/**
	 * @return the page
	 */
	public final int getPage() {
		return page;
	}
	/**
	 * @param newPage the page to set
	 */
	public final void setPage(final int newPage) {
		this.page = newPage;
	}
	/**
	 * @return the pageSize
	 */
	public final int getPageSize() {
		return pageSize;
	}
	/**
	 * @param newPageSize the pageSize to set
	 */
	public final void setPageSize(final int newPageSize) {
		this.pageSize = newPageSize;
	}
	/**
	 * @return the pageCount
	 */
	public final int getPageCount() {
		return pageCount;
	}
	/**
	 * @param newPageCount the pageCount to set
	 */
	public final void setPageCount(final int newPageCount) {
		this.pageCount = newPageCount;
	}
	/**
	 * @return the totalCount
	 */
	public final int getTotalCount() {
		return totalCount;
	}
	/**
	 * @param totalCount the totalCount to set
	 */
	public final void setTotalCount(final int newTotalCount) {
		this.totalCount = newTotalCount;
	}
	/**
	 * @return the links
	 */
	public final List<Link> getLinks() {
		return links;
	}
	/**
	 * @param links the links to set
	 */
	public final void setLinks(final List<Link> newLinks) {
		this.links = newLinks;
	}
}
