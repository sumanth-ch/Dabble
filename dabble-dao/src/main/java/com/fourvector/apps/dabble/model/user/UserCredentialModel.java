/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author Anantha.Sharma
 */
public class UserCredentialModel extends BaseModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 147795147507277745L;

	private UserModel			user;
	private String				login;
	private String				password;
	private String				type;

	/**
	 * 
	 */
	public UserCredentialModel() {
		super();
	}

	/**
	 * @return the user
	 */
	public UserModel getUser() {
		return this.user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(final UserModel user) {
		this.user = user;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return this.login;
	}

	/**
	 * @param login
	 *            the login to set
	 */
	public void setLogin(final String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("UserCredentialModel [user=%s, login=%s, password=%s, type=%s, id=%s, status=%s]", DAOUtils.showEntity(this.user), this.login, this.password, this.type, this.id, this.status);
	}

}
