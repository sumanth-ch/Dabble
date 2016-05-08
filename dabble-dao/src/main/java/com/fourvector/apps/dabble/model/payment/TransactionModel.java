/**
 * 
 */
package com.fourvector.apps.dabble.model.payment;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.Auditable;
import com.fourvector.apps.dabble.model.BaseModel;
import com.fourvector.apps.dabble.model.job.JobModel;
import com.fourvector.apps.dabble.model.user.UserModel;

/**
 * @author Anantha.Sharma
 */
public class TransactionModel extends BaseModel implements Auditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5228716990086892329L;

	private SubscriptionModel			subscription;
	private Double						amount;
	private String						currency;
	private Integer						jobId;
	private String						transactionReference;
	private UserModel					transactingUser;
	private Integer						userTypeCode;
	private Set<TransactionDetailModel>	transactionDetails	= new LinkedHashSet<>();

	/**
	 * 
	 */
	public TransactionModel() {
		super();
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return this.amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(final Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return this.currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(final String currency) {
		this.currency = currency;
	}

	/**
	 * @return the transactingUser
	 */
	public UserModel getTransactingUser() {
		return this.transactingUser;
	}

	/**
	 * @param transactingUser
	 *            the transactingUser to set
	 */
	public void setTransactingUser(final UserModel transactingUser) {
		this.transactingUser = transactingUser;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("TransactionModel [amount=%s, currency=%s, transactingUser=%s, userTypeCode=%s]", amount, currency, DAOUtils.showEntity(transactingUser), userTypeCode);
	}

	/**
	 * @return the transactionDetails
	 */
	public Set<TransactionDetailModel> getTransactionDetails() {
		return transactionDetails;
	}

	/**
	 * @param transactionDetails
	 *            the transactionDetails to set
	 */
	public void setTransactionDetails(Set<TransactionDetailModel> transactionDetails) {
		this.transactionDetails = transactionDetails;
	}

	/**
	 * @return the transactionReference
	 */
	public String getTransactionReference() {
		return transactionReference;
	}

	/**
	 * @param transactionReference
	 *            the transactionReference to set
	 */
	public void setTransactionReference(String transactionReference) {
		this.transactionReference = transactionReference;
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
	 * @return the userTypeCode
	 */
	public Integer getUserTypeCode() {
		return userTypeCode;
	}

	/**
	 * @param userTypeCode
	 *            the userTypeCode to set
	 */
	public void setUserTypeCode(Integer userTypeCode) {
		this.userTypeCode = userTypeCode;
	}

	/**
	 * @return the jobId
	 */
	public Integer getJobId() {
		return jobId;
	}

	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

}
