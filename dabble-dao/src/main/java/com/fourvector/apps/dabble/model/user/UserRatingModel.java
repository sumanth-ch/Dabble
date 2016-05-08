/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.BaseModel;
import com.fourvector.apps.dabble.model.job.JobModel;

/**
 * @author Anantha.Sharma
 */
public class UserRatingModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -404220733156949622L;

	private UserModel	ratingByUser;
	private UserModel	ratingForUser;
	private String		comment;
	private Integer		rating;
	private JobModel	job;

	/**
	 * 
	 */
	public UserRatingModel() {
		super();
	}

	/**
	 * @return the ratingByUser
	 */
	public UserModel getRatingByUser() {
		return this.ratingByUser;
	}

	/**
	 * @param ratingByUser
	 *            the ratingByUser to set
	 */
	public void setRatingByUser(final UserModel ratingByUser) {
		this.ratingByUser = ratingByUser;
	}

	/**
	 * @return the ratingForUser
	 */
	public UserModel getRatingForUser() {
		return this.ratingForUser;
	}

	/**
	 * @param ratingForUser
	 *            the ratingForUser to set
	 */
	public void setRatingForUser(final UserModel ratingForUser) {
		this.ratingForUser = ratingForUser;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return this.comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}

	/**
	 * @return the rating
	 */
	public Integer getRating() {
		return this.rating;
	}

	/**
	 * @param rating
	 *            the rating to set
	 */
	public void setRating(final Integer rating) {
		this.rating = rating;
	}

	/**
	 * @return the job
	 */
	public JobModel getJob() {
		return this.job;
	}

	/**
	 * @param job
	 *            the job to set
	 */
	public void setJob(final JobModel job) {
		this.job = job;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("UserRatingModel [ratingByUser=%s, ratingForUser=%s, comment=%s, rating=%s, id=%s, status=%s]", DAOUtils.showEntity(this.ratingByUser), DAOUtils.showEntity(this.ratingForUser), this.comment, this.rating, this.id, this.status);
	}

}
