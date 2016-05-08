/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import java.util.Date;

import com.fourvector.apps.dabble.model.BaseModel;
import com.fourvector.apps.dabble.model.payment.SubscriptionModel;

/**
 * @author asharma
 */
public class UserSubscriptionModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1186153133326726876L;

	private UserModel			user;
	private SubscriptionModel	subscription;
	private Date				subscriptionDate;
	private Date				expiryDate;
	private transient Integer	bidCount, jobCount;
	private String				subscriptionReference;

	/**
	 * 
	 */
	public UserSubscriptionModel() {
		super();
	}

	/**
	 * @return the user
	 */
	public UserModel getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(UserModel user) {
		this.user = user;
	}

	/**
	 * @return the subscription
	 */
	public SubscriptionModel getSubscription() {
		return subscription;
	}

	/**
	 * @param subscription
	 *            the subscription to set
	 */
	public void setSubscription(SubscriptionModel subscription) {
		this.subscription = subscription;
	}

	/**
	 * @return the subscriptionDate
	 */
	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	/**
	 * @param subscriptionDate
	 *            the subscriptionDate to set
	 */
	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
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
	 * @return the bidCount
	 */
	public Integer getBidCount() {
		return bidCount;
	}

	/**
	 * @param bidCount
	 *            the bidCount to set
	 */
	public void setBidCount(Integer bidCount) {
		this.bidCount = bidCount;
	}

	/**
	 * @return the jobCount
	 */
	public Integer getJobCount() {
		return jobCount;
	}

	/**
	 * @param jobCount
	 *            the jobCount to set
	 */
	public void setJobCount(Integer jobCount) {
		this.jobCount = jobCount;
	}

	/**
	 * @return the subscriptionReference
	 */
	public String getSubscriptionReference() {
		return subscriptionReference;
	}

	/**
	 * @param subscriptionReference
	 *            the subscriptionReference to set
	 */
	public void setSubscriptionReference(String subscriptionReference) {
		this.subscriptionReference = subscriptionReference;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserSubscriptionModel [user=" + user + ", subscription=" + subscription + ", subscriptionDate=" + subscriptionDate + ", expiryDate=" + expiryDate + ", subscriptionReference=" + subscriptionReference + "]";
	}

}
