/**
 * 
 */
package com.fourvector.apps.dabble.common.dto;

import java.util.Date;

/**
 * @author asharma
 */
public class BidDTO extends BaseDTO {
	private Integer			bidId;
	private Integer			jobId;
	private Integer			bidderId;
	private String			bidderName;
	private String			bidderProfilePic;
	private Double			amount;
	private String			currency;
	private String			comment;
	private UserDetailsDTO	userDetails;
	private Date			createdDate;

	/**
	 * 
	 */
	public BidDTO() {
		super();
	}

	/**
	 * @return the bidId
	 */
	public Integer getBidId() {
		return bidId;
	}

	/**
	 * @param bidId
	 *            the bidId to set
	 */
	public void setBidId(Integer bidId) {
		this.bidId = bidId;
	}

	/**
	 * @return the jobId
	 */
	public Integer getJobId() {
		return jobId;
	}

	/**
	 * @param jobId
	 *            the jobId to set
	 */
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	/**
	 * @return the bidderId
	 */
	public Integer getBidderId() {
		return bidderId;
	}

	/**
	 * @param bidderId
	 *            the bidderId to set
	 */
	public void setBidderId(Integer bidderId) {
		this.bidderId = bidderId;
	}

	/**
	 * @return the bidderName
	 */
	public String getBidderName() {
		return bidderName;
	}

	/**
	 * @param bidderName
	 *            the bidderName to set
	 */
	public void setBidderName(String bidderName) {
		this.bidderName = bidderName;
	}

	/**
	 * @return the bidderProfilePic
	 */
	public String getBidderProfilePic() {
		return bidderProfilePic;
	}

	/**
	 * @param bidderProfilePic
	 *            the bidderProfilePic to set
	 */
	public void setBidderProfilePic(String bidderProfilePic) {
		this.bidderProfilePic = bidderProfilePic;
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

	/**
	 * @return the userDetails
	 */
	public UserDetailsDTO getUserDetails() {
		return userDetails;
	}

	/**
	 * @param userDetails
	 *            the userDetails to set
	 */
	public void setUserDetails(UserDetailsDTO userDetails) {
		this.userDetails = userDetails;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

}
