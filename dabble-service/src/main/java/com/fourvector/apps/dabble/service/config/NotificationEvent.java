/**
 * 
 */
package com.fourvector.apps.dabble.service.config;

/**
 * @author asharma
 */
public enum NotificationEvent {

	/**
	 * a bid retraction notification.
	 */
	RETRACT_BID, /**
					 * a bid retraction requested by the bidder notification.
					 */
	RETRACT_BID_REQUESTED, /**
							 * on successful retraction of the Job, the Job creator and bidder (if any) will recieve notification.
							 */
	RETRACT_JOB_BIDDER, /**
						 * the job posted has retracted the bid, this status represents the notification which is sent to the job creator that the bidder needs to accept his
						 * request.
						 */
	RETRACT_JOB_SELLER, /**
						 * verify user email
						 */
	USER_EMAIL_VERIFICATION, /**
								 * event for forgot password action
								 */
	USER_FORGOT_PASSWORD, /**
							 * send a chat message
							 */
	CHAT, /**
			 * when a user password is changed successfully.
			 */
	PASSWORD_CHANGE_SUCCESS, /**
								 * for when a job is successfully created.
								 */
	CREATE_JOB_SUCCESS, /**
						 * for when a bidder is to be notified of a bid being successfully placed.
						 */
	BID_PLACED_BIDDER, /**
						 * for when a seller is to be notified of a bid being successfully placed.
						 */
	BID_PLACED_SELLER, /**
						 * for when a bidder is to be notified of a bid being successfully retracted.
						 */
	BID_RETRACTED_BIDDER, /**
							 * for when a seller is to be notified of a bid being successfully retracted.
							 */
	BID_RETRACTED_SELLER, /**
							 * for when a seller is to be notified of a bid being accepted.
							 */
	ACCEPT_BID_SELLER, /**
						 * for when a bidder is to be notified of a bid being successfully retracted.
						 */
	ACCEPT_BID_BIDDER,

	/**
	 * Job completed.
	 */
	JOB_COMPLETED, /**
					 * job Completion requested.
					 */
	JOB_COMPLETION_REQUESTED, /**
								 * when a bid is placed on an event.
								 */
	EVENT_BID_PLACED_SELLER, /**
								 * when an bid is placde on an event.
								 */
	EVENT_BID_PLACED_BIDDER, /**
								 * for when a bid is accepted in an Event.
								 */
	EVENT_ACCEPT_BID_BIDDER, SUBSCRIBE_TO_PAY_AS_YOU_GO, SUBSCRIBE_TO_MONTHLY_PAYMENT, RETRACT_EVENT_SELLER;
}
