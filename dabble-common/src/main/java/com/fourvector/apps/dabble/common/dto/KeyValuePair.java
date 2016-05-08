/**
 * 
 */
package com.fourvector.apps.dabble.common.dto;

/**
 * @author asharma
 */
public class KeyValuePair {

	private Object	value;
	private Integer	key;

	/**
	 * 
	 */
	public KeyValuePair() {
		super();
	}

	/**
	 * @param value
	 * @param key
	 */
	public KeyValuePair(Integer key, Object value) {
		super();
		this.value = value;
		this.key = key;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the key
	 */
	public Integer getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(Integer key) {
		this.key = key;
	}

}
