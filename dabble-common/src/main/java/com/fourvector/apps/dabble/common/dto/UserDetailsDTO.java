/**
 * 
 */
package com.fourvector.apps.dabble.common.dto;

/**
 * @author asharma
 */
public class UserDetailsDTO extends BaseDTO {

	private Integer	jobsBidFor;
	private Integer	jobsBidAndCompleted;
	private Integer	jobsBidAndInProgress;
	private Integer	jobsWon;

	private Integer	jobsCreated;
	private Integer	jobsCreatedAndCompleted;
	private Integer	jobsCreatedInProgress;

	private Double userRating;

	/**
	 * 
	 */
	public UserDetailsDTO() {
		super();
	}

	/**
	 * @return the jobsBidFor
	 */
	public Integer getJobsBidFor() {
		return jobsBidFor;
	}

	/**
	 * @param jobsBidFor
	 *            the jobsBidFor to set
	 */
	public void setJobsBidFor(Integer jobsBidFor) {
		this.jobsBidFor = jobsBidFor;
	}

	/**
	 * @return the jobsBidAndCompleted
	 */
	public Integer getJobsBidAndCompleted() {
		return jobsBidAndCompleted;
	}

	/**
	 * @param jobsBidAndCompleted
	 *            the jobsBidAndCompleted to set
	 */
	public void setJobsBidAndCompleted(Integer jobsBidAndCompleted) {
		this.jobsBidAndCompleted = jobsBidAndCompleted;
	}

	/**
	 * @return the jobsBidAndInProgress
	 */
	public Integer getJobsBidAndInProgress() {
		return jobsBidAndInProgress;
	}

	/**
	 * @param jobsBidAndInProgress
	 *            the jobsBidAndInProgress to set
	 */
	public void setJobsBidAndInProgress(Integer jobsBidAndInProgress) {
		this.jobsBidAndInProgress = jobsBidAndInProgress;
	}

	/**
	 * @return the jobsWon
	 */
	public Integer getJobsWon() {
		return jobsWon;
	}

	/**
	 * @param jobsWon
	 *            the jobsWon to set
	 */
	public void setJobsWon(Integer jobsWon) {
		this.jobsWon = jobsWon;
	}

	/**
	 * @return the jobsCreated
	 */
	public Integer getJobsCreated() {
		return jobsCreated;
	}

	/**
	 * @param jobsCreated
	 *            the jobsCreated to set
	 */
	public void setJobsCreated(Integer jobsCreated) {
		this.jobsCreated = jobsCreated;
	}

	/**
	 * @return the jobsCreatedAndCompleted
	 */
	public Integer getJobsCreatedAndCompleted() {
		return jobsCreatedAndCompleted;
	}

	/**
	 * @param jobsCreatedAndCompleted
	 *            the jobsCreatedAndCompleted to set
	 */
	public void setJobsCreatedAndCompleted(Integer jobsCreatedAndCompleted) {
		this.jobsCreatedAndCompleted = jobsCreatedAndCompleted;
	}

	/**
	 * @return the jobsCreatedInProgress
	 */
	public Integer getJobsCreatedInProgress() {
		return jobsCreatedInProgress;
	}

	/**
	 * @param jobsCreatedInProgress
	 *            the jobsCreatedInProgress to set
	 */
	public void setJobsCreatedInProgress(Integer jobsCreatedInProgress) {
		this.jobsCreatedInProgress = jobsCreatedInProgress;
	}

	/**
	 * @return the userRating
	 */
	public Double getUserRating() {
		return userRating;
	}

	/**
	 * @param userRating
	 *            the userRating to set
	 */
	public void setUserRating(Double userRating) {
		this.userRating = userRating;
	}

}
