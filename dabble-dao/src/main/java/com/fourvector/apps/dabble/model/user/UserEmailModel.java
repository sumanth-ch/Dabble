/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author Anantha.Sharma
 */
public class UserEmailModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 147795147507277745L;

	private UserModel	user;
	private String		email;
	private Integer		status;

	/**
	 * 
	 */
	public UserEmailModel() {
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("UserEmailModel [user=%s, email=%s, status=%s]", DAOUtils.showEntity(user), email, status);
	}

}
