/**
 * 
 */
package com.fourvector.apps.dabble.model.job;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.Auditable;
import com.fourvector.apps.dabble.model.BaseModel;
import com.fourvector.apps.dabble.model.user.UserModel;

/**
 * @author asharma
 */
public class BidModel extends BaseModel implements Auditable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9102751454892373966L;

	private JobModel	job;
	private UserModel	bidder;
	private Double		amount;
	private String		currency;
	private String		comment;

	/**
	 * 
	 */
	public BidModel() {
		super();
	}

	/**
	 * @return the job
	 */
	public JobModel getJob() {
		return job;
	}

	/**
	 * @param job
	 *            the job to set
	 */
	public void setJob(JobModel job) {
		this.job = job;
	}

	/**
	 * @return the bidder
	 */
	public UserModel getBidder() {
		return bidder;
	}

	/**
	 * @param bidder
	 *            the bidder to set
	 */
	public void setBidder(UserModel bidder) {
		this.bidder = bidder;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("BidModel [job=%s, bidder=%s, amount=%s, currency=%s, comment=%s]", DAOUtils.showEntity(job), DAOUtils.showEntity(bidder), amount, currency, comment);
	}

}
