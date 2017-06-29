/**
 * 
 */
package Petrolink.CommonApi;

/**
 * Facade for Petrolink.PetroVaultHD.Model.Common
 * @author aristo
 *
 */
public class Link {
	private String rel;
	private String href;
	
	/**
	 * @return the rel
	 */
	public final String getRel() {
		return rel;
	}
	/**
	 * @param newRel the rel to set
	 */
	public final void setRel(final String newRel) {
		this.rel = newRel;
	}
	/**
	 * @return the href
	 */
	public final String getHref() {
		return href;
	}
	/**
	 * @param newHref the href to set
	 */
	public final void setHref(final String newHref) {
		this.href = newHref;
	}
}
