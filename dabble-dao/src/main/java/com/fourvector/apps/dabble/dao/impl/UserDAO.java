/**
 * 
 */
package com.fourvector.apps.dabble.dao.impl;

import java.util.List;

import com.fourvector.apps.dabble.dao.IUserDAO;
import com.fourvector.apps.dabble.model.user.UserAddressModel;
import com.fourvector.apps.dabble.model.user.UserCredentialModel;
import com.fourvector.apps.dabble.model.user.UserModel;

/**
 * @author Anantha.Sharma
 */
public class UserDAO extends BaseDAO implements IUserDAO {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserDAO.class);

	/**
	 * 
	 */
	public UserDAO() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IUserDAO#addAddress(com.fourvector.apps.dabble.model.user.UserAddressModel, com.fourvector.apps.dabble.model.user.UserModel)
	 */
	@Override
	public void addAddress(UserAddressModel userAddressModel, UserModel userModel) {
		LOG.trace("Method [addAddress]: Called ({}, {})", userAddressModel, userModel);
		userAddressModel.setUser(userModel);
		saveObject(userAddressModel);
		LOG.trace("Method [addAddress]: Returning ");
	}

	@Override
	public boolean isValidUserEmail(String userEmail) {
		LOG.debug("Method [isValidUserEmail]: Called");

		boolean result = getUserCredentialByEmail(userEmail) == null ? false : true;

		LOG.debug("Method [isValidUserEmail]: Returning {}", result);

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserCredentialModel getUserCredentialByEmail(String userEmail) {
		LOG.debug("Method [getUserCredentialByEmail]: Called");
		UserCredentialModel result = null;

		String hqlQuery = "from " + UserCredentialModel.class.getName() + " p where lower(p.login) = lower(?) and p.status=0 and p.user.status=0";

		List<UserCredentialModel> elements = (List<UserCredentialModel>) getHibernateTemplate().find(hqlQuery, userEmail);
		if (elements == null || elements.isEmpty()) {
			// either the email id is wrong or it isn't active. or the user is inactive.
			LOG.info("getUserByEmail: query returned no results.");
			result = null;
		} else {
			result = elements.get(0);
		}

		LOG.debug("Method [getUserCredentialByEmail]: Returning {}", result);

		return result;
	}
}
