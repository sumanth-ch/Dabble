/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author Anantha.Sharma
 */
public class UserPhoneModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 147795147507277745L;

	private UserModel	user;
	private String		phone;
	private Integer		status;

	/**
	 * 
	 */
	public UserPhoneModel() {
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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
		return String.format("UserEmailModel [user=%s, phone=%s, status=%s]", DAOUtils.showEntity(user), phone, status);
	}

}
