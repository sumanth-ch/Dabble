/**
 * 
 */
package com.fourvector.apps.dabble.dao;

import com.fourvector.apps.dabble.model.user.UserAddressModel;
import com.fourvector.apps.dabble.model.user.UserCredentialModel;
import com.fourvector.apps.dabble.model.user.UserModel;

/**
 * @author Anantha.Sharma
 */
public interface IUserDAO extends IBaseDAO {

	/**
	 * adds a new address to an existing userModel
	 * 
	 * @param userAddressModel
	 * @param userModel
	 */
	void addAddress(final UserAddressModel userAddressModel, final UserModel userModel);

	/**
	 * checks if the provided email is valid and is the active email for the given user.
	 * 
	 * @param userEmail
	 */
	boolean isValidUserEmail(String userEmail);

	/**
	 * returns a user object by the Email
	 * 
	 * @param userEmail
	 */
	UserCredentialModel getUserCredentialByEmail(String userEmail);

}
