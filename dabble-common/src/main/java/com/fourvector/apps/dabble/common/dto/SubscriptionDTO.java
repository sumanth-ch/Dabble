/**
 * 
 */
package com.fourvector.apps.dabble.common.dto;

import java.util.Date;

/**
 * @author asharma
 */
public class SubscriptionDTO extends BaseDTO {

	private Integer	subscriptionId;
	private String	name, description;
	private boolean	freeTier, payAsYouGoTier, monthlyTier;
	private Integer	maxBidCount, maxJobCount;
	private Integer	durationInDays;
	private boolean	bidderExcempt;
	private Integer	userTypeCode;
	private Date	startDate, endDate;
	private Double	amountForCreateJob, amountForSubscription, amountForCreateEvent;

	/**
	 * 
	 */
	public SubscriptionDTO() {
		super();
	}

	/**
	 * @return the subscriptionId
	 */
	public Integer getSubscriptionId() {
		return subscriptionId;
	}

	/**
	 * @param subscriptionId
	 *            the subscriptionId to set
	 */
	public void setSubscriptionId(Integer subscriptionId) {
		this.subscriptionId = subscriptionId;
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
	 * @return the freeTier
	 */
	public boolean isFreeTier() {
		return freeTier;
	}

	/**
	 * @param freeTier
	 *            the freeTier to set
	 */
	public void setFreeTier(boolean freeTier) {
		this.freeTier = freeTier;
	}

	/**
	 * @return the payAsYouGoTier
	 */
	public boolean isPayAsYouGoTier() {
		return payAsYouGoTier;
	}

	/**
	 * @param payAsYouGoTier
	 *            the payAsYouGoTier to set
	 */
	public void setPayAsYouGoTier(boolean payAsYouGoTier) {
		this.payAsYouGoTier = payAsYouGoTier;
	}

	/**
	 * @return the monthlyTier
	 */
	public boolean isMonthlyTier() {
		return monthlyTier;
	}

	/**
	 * @param monthlyTier
	 *            the monthlyTier to set
	 */
	public void setMonthlyTier(boolean monthlyTier) {
		this.monthlyTier = monthlyTier;
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
