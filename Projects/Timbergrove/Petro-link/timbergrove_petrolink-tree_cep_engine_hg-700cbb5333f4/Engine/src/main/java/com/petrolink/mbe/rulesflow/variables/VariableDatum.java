package com.petrolink.mbe.rulesflow.variables;

/**
 * Represneting single data object
 * @author aristo
 *
 */
public class VariableDatum {

	private Object index;
	private Object value;
	/**
	 * @return the index
	 */
	public final Object getIndex() {
		return index;
	}
	/**
	 * @param index the index to set
	 */
	public final void setIndex(Object index) {
		this.index = index;
	}
	/**
	 * @return the value
	 */
	public final Object getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public final void setValue(Object value) {
		this.value = value;
	}
}
