/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import com.fourvector.apps.dabble.model.BaseModel;
import com.fourvector.apps.dabble.model.VerificationKeyModel;

/**
 * @author asharma
 */
public class TempKey extends BaseModel {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= -7064294177542125836L;
	private String					name;
	private VerificationKeyModel	key;

	/**
	 * 
	 */
	public TempKey() {
		super();
	}

	/**
	 * @param name
	 * @param key
	 */
	public TempKey(String name, VerificationKeyModel key) {
		super();
		this.name = name;
		this.key = key;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the key
	 */
	public VerificationKeyModel getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(VerificationKeyModel key) {
		this.key = key;
	}

}
