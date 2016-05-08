/**
 * 
 */
package com.fourvector.apps.dabble.model.payment;

import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author asharma
 */
public class SubscriptionModel extends BaseModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3570474479156537236L;
	private String				name, description;
	private Integer				maxBidCount, maxJobCount;
	private Integer				subscriptionType;
	private Integer				durationInDays;
	private boolean				bidderExcempt;
	private String				planReference;
	private Integer				userTypeCode;
	private Double				amountForCreateJob, amountForCreateEvent, amountForSubscription, amountForAcceptBid, amountForAcceptEvent;

	/**
	 * 
	 */
	public SubscriptionModel() {
		super();
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the maxBidCount
	 */
	public Integer getMaxBidCount() {
		return maxBidCount;
	}

	/**
	 * @param maxBidCount
	 *            the maxBidCount to set
	 */
	public void setMaxBidCount(Integer maxBidCount) {
		this.maxBidCount = maxBidCount;
	}

	/**
	 * @return the maxJobCount
	 */
	public Integer getMaxJobCount() {
		return maxJobCount;
	}

	/**
	 * @param maxJobCount
	 *            the maxJobCount to set
	 */
	public void setMaxJobCount(Integer maxJobCount) {
		this.maxJobCount = maxJobCount;
	}

	/**
	 * @return the durationInDays
	 */
	public Integer getDurationInDays() {
		return durationInDays;
	}

	/**
	 * @param durationInDays
	 *            the durationInDays to set
	 */
	public void setDurationInDays(Integer durationInDays) {
		this.durationInDays = durationInDays;
	}

	/**
	 * @return the bidderExcempt
	 */
	public boolean isBidderExcempt() {
		return bidderExcempt;
	}

	/**
	 * @param bidderExcempt
	 *            the bidderExcempt to set
	 */
	public void setBidderExcempt(boolean bidderExcempt) {
		this.bidderExcempt = bidderExcempt;
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
	 * @return the subscriptionType
	 */
	public Integer getSubscriptionType() {
		return subscriptionType;
	}

	/**
	 * @param subscriptionType
	 *            the subscriptionType to set
	 */
	public void setSubscriptionType(Integer subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	/**
	 * @return the amountForCreateJob
	 */
	public Double getAmountForCreateJob() {
		return amountForCreateJob;
	}

	/**
	 * @param amountForCreateJob
	 *            the amountForCreateJob to set
	 */
	public void setAmountForCreateJob(Double amountForCreateJob) {
		this.amountForCreateJob = amountForCreateJob;
	}

	/**
	 * @return the amountForCreateEvent
	 */
	public Double getAmountForCreateEvent() {
		return amountForCreateEvent;
	}

	/**
	 * @param amountForCreateEvent
	 *            the amountForCreateEvent to set
	 */
	public void setAmountForCreateEvent(Double amountForCreateEvent) {
		this.amountForCreateEvent = amountForCreateEvent;
	}

	/**
	 * @return the amountForSubscription
	 */
	public Double getAmountForSubscription() {
		return amountForSubscription;
	}

	/**
	 * @param amountForSubscription
	 *            the amountForSubscription to set
	 */
	public void setAmountForSubscription(Double amountForSubscription) {
		this.amountForSubscription = amountForSubscription;
	}

	/**
	 * @return the amountForAcceptBid
	 */
	public Double getAmountForAcceptBid() {
		return amountForAcceptBid;
	}

	/**
	 * @param amountForAcceptBid
	 *            the amountForAcceptBid to set
	 */
	public void setAmountForAcceptBid(Double amountForAcceptBid) {
		this.amountForAcceptBid = amountForAcceptBid;
	}

	/**
	 * @return the amountForAcceptEvent
	 */
	public Double getAmountForAcceptEvent() {
		return amountForAcceptEvent;
	}

	/**
	 * @param amountForAcceptEvent
	 *            the amountForAcceptEvent to set
	 */
	public void setAmountForAcceptEvent(Double amountForAcceptEvent) {
		this.amountForAcceptEvent = amountForAcceptEvent;
	}

	/**
	 * @return the planReference
	 */
	public String getPlanReference() {
		return planReference;
	}

	/**
	 * @param planReference the planReference to set
	 */
	public void setPlanReference(String planReference) {
		this.planReference = planReference;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubscriptionModel [name=" + name + ", description=" + description + ", maxBidCount=" + maxBidCount + ", maxJobCount=" + maxJobCount + ", subscriptionType=" + subscriptionType + ", durationInDays=" + durationInDays + ", bidderExcempt=" + bidderExcempt + ", planReference=" + planReference + ", userTypeCode=" + userTypeCode + ", amountForCreateJob=" + amountForCreateJob
				+ ", amountForCreateEvent=" + amountForCreateEvent + ", amountForSubscription=" + amountForSubscription + ", amountForAcceptBid=" + amountForAcceptBid + ", amountForAcceptEvent=" + amountForAcceptEvent + "]";
	}

}
