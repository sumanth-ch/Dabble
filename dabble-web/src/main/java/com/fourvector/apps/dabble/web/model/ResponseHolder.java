/**
 * 
 */
package com.fourvector.apps.dabble.web.model;

import java.io.Serializable;

/**
 * @author asharma
 */
public class ResponseHolder implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8583649972750985012L;
	private String				message;
	private int					code;
	private Object				payload;

	/**
	 * @param code
	 * @param message
	 * @param payload
	 */
	public ResponseHolder(int code, String message, Object payload) {
		super();
		this.code = code;
		this.message = message;
		this.payload = payload;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the payload
	 */
	public Object getPayload() {
		return payload;
	}

	/**
	 * @param payload
	 *            the payload to set
	 */
	public void setPayload(Object payload) {
		this.payload = payload;
	}

}
