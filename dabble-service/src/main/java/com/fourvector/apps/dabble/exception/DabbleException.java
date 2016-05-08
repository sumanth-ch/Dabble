/**
 * 
 */
package com.fourvector.apps.dabble.exception;

import java.util.LinkedList;
import java.util.List;

import com.fourvector.apps.dabble.common.dto.BaseDTO;

/**
 * @author asharma
 */
public class DabbleException extends RuntimeException {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 832767654806350696L;
	private Integer				code;
	private BaseDTO				optionalResponse;

	/**
	 * 
	 */
	public DabbleException() {
		super();
	}

	/**
	 * @param message
	 */
	public DabbleException(Integer code, String message) {
		super(message);
		this.code = code;
	}

	/**
	 * @param message
	 */
	public DabbleException(Integer code, String message, BaseDTO optionalResponse) {
		super(message);
		this.code = code;
		this.optionalResponse = optionalResponse;
	}

	/**
	 * @param cause
	 */
	public DabbleException(Integer code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	@Override
	public void printStackTrace() {
		setStackTrace(this.getStackTrace());
		super.printStackTrace();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		List<StackTraceElement> steList = new LinkedList<>();
		for (StackTraceElement ste : super.getStackTrace()) {
			if (ste.getClassName().startsWith("com.fourvector")) {
				steList.add(ste);
			}
		}
		StackTraceElement[] array = new StackTraceElement[steList.size()];
		steList.toArray(array);
		return array;
	}

	/**
	 * @return the optionalResponse
	 */
	public BaseDTO getOptionalResponse() {
		return optionalResponse;
	}

}
