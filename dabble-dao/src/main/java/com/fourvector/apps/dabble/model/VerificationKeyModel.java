/**
 * 
 */
package com.fourvector.apps.dabble.model;

import java.util.Date;

/**
 * @author asharma
 */
public class VerificationKeyModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3094658976991650218L;

	private String	key;
	private String	email;

	private Date expiryDate;

	/**
	 * 
	 */
	public VerificationKeyModel() {
		super();
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate
	 *            the expiryDate to set
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

}
