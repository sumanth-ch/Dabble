/**
 * 
 */
package com.fourvector.apps.dabble.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fourvector.apps.dabble.common.dto.AddressDTO;
import com.fourvector.apps.dabble.common.dto.SubscriptionDTO;
import com.fourvector.apps.dabble.common.dto.UserDTO;
import com.fourvector.apps.dabble.common.dto.UserDetailsDTO;
import com.fourvector.apps.dabble.common.dto.UserFeedbackDTO;
import com.fourvector.apps.dabble.model.user.UserModel;

/**
 * @author Anantha.Sharma
 */
public interface IUserService {
	/**
	 * creates a new user in the database.
	 * 
	 * @param userModel
	 * @param imageStream
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	Integer createUser(final UserDTO userModel, InputStream imageStream);

	/**
	 * adds a new address to an existing user.
	 * 
	 * @param userAddressModel
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void addAddress(AddressDTO address, final Integer userId);

	/**
	 * marks the given email as primary for a user.
	 * 
	 * @param userModel
	 * @param email
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void setEmailAsPrimary(UserModel userModel, String email);

	/**
	 * updates an existing user in the database.
	 * 
	 * @param userId
	 * @param userDto
	 * @param is
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void updateUser(Integer userId, UserDTO userDto, InputStream is);

	/**
	 * activates an email id (sets as active)
	 * 
	 * @param userId
	 * @param emailId
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void activateEmail(Integer userId, String emailId);

	/**
	 * authenticate a user
	 * 
	 * @param login
	 * @param password
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	UserDTO authenticate(String login, String password);

	/**
	 * get details for the user dashboard
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	UserDetailsDTO getUserDetails(Integer userId);

	/**
	 * issues a forgot password pin
	 * 
	 * @param userEmail
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void forgotPassword(String userEmail);

	/**
	 * verifies the given pin and if correct sets the new password, else throws a <code>DabbleException</code>
	 * 
	 * @param userEmail
	 * @param pin
	 * @param newPassword
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void verifyAndSetPassword(String userEmail, String pin, String newPassword);

	/**
	 * @param fbId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	UserDTO fbAuthenticate(String fbId);

	/**
	 * returns a profile image for the user
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	InputStream getProfileImage(Integer userId);

	/**
	 * Verifies a user profile creation link
	 * 
	 * @param key
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	int verifyLink(String key);

	/**
	 * adds a new device token and enables notifications.
	 * 
	 * @param userId
	 * @param deviceToken
	 * @param deviceType
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void addToken(Integer userId, String deviceToken, Integer deviceType);

	/**
	 * gets all user feedbacks for a given user.
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	List<UserFeedbackDTO> getUserFeedback(Integer userId);

	/**
	 * retrieves all subscription options for a given user type
	 * 
	 * @param userTypeCode
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	List<SubscriptionDTO> getSubscriptionsByUserType(Integer userTypeCode);

	/**
	 * creates a user subscription
	 * 
	 * @param userId
	 * @param subscriptionId
	 * @param nonce
	 * @param piId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	UserDTO createSubscription(Integer userId, Integer subscriptionId, String nonce, String piId);

}
