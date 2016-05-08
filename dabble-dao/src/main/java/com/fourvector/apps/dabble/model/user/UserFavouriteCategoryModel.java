/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.BaseModel;
import com.fourvector.apps.dabble.model.job.JobCategoryModel;

/**
 * @author Anantha.Sharma
 */
public class UserFavouriteCategoryModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5897352289347336078L;

	private UserModel			user;
	private JobCategoryModel	category;

	/**
	 * 
	 */
	public UserFavouriteCategoryModel() {
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
	 * @return the category
	 */
	public JobCategoryModel getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(JobCategoryModel category) {
		this.category = category;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("UserFavouriteCategory [user=%s, category=%s]", DAOUtils.showEntity(user), DAOUtils.showEntity(category));
	}

}
