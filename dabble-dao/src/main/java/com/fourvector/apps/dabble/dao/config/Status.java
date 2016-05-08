/**
 * 
 */
package com.fourvector.apps.dabble.dao.config;

/**
 * @author asharma
 */
public final class Status {

	public static final Integer	ACTIVE										= 0;
	public static final Integer	INACTIVE									= 1;
	public static final Integer	PENDING_VERIFICATION						= 2;
	public static final Integer	JOB_OR_BID_RETRACTED						= 3;
	public static final Integer	JOB_OR_BID_ACCEPTED							= 4;
	public static final Integer	JOB_COMPLETED								= 5;
	public static final Integer	JOB_OR_BID_RETRACTION_VERIFICATION_PENDING	= 6;
	public static final Integer	JOB_OR_BID_RETRACTION_REQUESTED				= 7;
	public static final Integer	JOB_OR_BID_RETRACTION_APPROVED				= 8;
	public static final Integer	JOB_COMPLETION_REQUESTED					= 9;

}
