package com.petrolink.mbe.setting;

/**
 * Setting for a Path.
 * @author aristo
 *
 */
public class PathSetting {
	private boolean relative= false;
	private String value;
	
	/**
	 * Whether Path is Relative.
	 * @return true if it is set as relative path, false otherwise.
	 */
	public final boolean isRelative() {
		return relative;
	}
	
	/**
	 * Whether Path is Relative.
	 * @param valueOf true if this instance should be set set as relative path, false otherwise.
	 */
	public final void setRelative(final boolean valueOf) {
		relative = valueOf;
	}

	/**
	 * @return the value
	 */
	public final String getValue() {
		return value;
	}

	/**
	 * @param newValue the value to set
	 */
	public final void setValue(final String newValue) {
		this.value = newValue;
	}

}
