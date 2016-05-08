/**
 * 
 */
package com.fourvector.apps.dabble.common.dto;

import java.util.Date;

/**
 * @author asharma
 */
public class UserFeedbackDTO extends BaseDTO {

	private Integer	jobId;
	private String	jobTitle;
	private String	jobPosterId;
	private String	jobPosterFirstName;
	private String	jobPosterMiddleName;
	private String	jobPosterLastName;
	private String	feedback;
	private Integer	rating;
	private Date	feedbackDate;

	/**
	 * 
	 */
	public UserFeedbackDTO() {
		super();
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
	 * @return the title
	 */
	public String getJobTitle() {
		return jobTitle;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setJobTitle(String title) {
		this.jobTitle = title;
	}

	/**
	 * @return the jobPosterId
	 */
	public String getJobPosterId() {
		return jobPosterId;
	}

	/**
	 * @param jobPosterId
	 *            the jobPosterId to set
	 */
	public void setJobPosterId(String jobPosterId) {
		this.jobPosterId = jobPosterId;
	}

	/**
	 * @return the jobPosterFirstName
	 */
	public String getJobPosterFirstName() {
		return jobPosterFirstName;
	}

	/**
	 * @param jobPosterFirstName
	 *            the jobPosterFirstName to set
	 */
	public void setJobPosterFirstName(String jobPosterFirstName) {
		this.jobPosterFirstName = jobPosterFirstName;
	}

	/**
	 * @return the jobPosterMiddleName
	 */
	public String getJobPosterMiddleName() {
		return jobPosterMiddleName;
	}

	/**
	 * @param jobPosterMiddleName
	 *            the jobPosterMiddleName to set
	 */
	public void setJobPosterMiddleName(String jobPosterMiddleName) {
		this.jobPosterMiddleName = jobPosterMiddleName;
	}

	/**
	 * @return the jobPosterLastName
	 */
	public String getJobPosterLastName() {
		return jobPosterLastName;
	}

	/**
	 * @param jobPosterLastName
	 *            the jobPosterLastName to set
	 */
	public void setJobPosterLastName(String jobPosterLastName) {
		this.jobPosterLastName = jobPosterLastName;
	}

	/**
	 * @return the feedback
	 */
	public String getFeedback() {
		return feedback;
	}

	/**
	 * @param feedback
	 *            the feedback to set
	 */
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	/**
	 * @return the rating
	 */
	public Integer getRating() {
		return rating;
	}

	/**
	 * @param rating
	 *            the rating to set
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}

	/**
	 * @return the feedbackDate
	 */
	public Date getFeedbackDate() {
		return feedbackDate;
	}

	/**
	 * @param feedbackDate
	 *            the feedbackDate to set
	 */
	public void setFeedbackDate(Date feedbackDate) {
		this.feedbackDate = feedbackDate;
	}

}
