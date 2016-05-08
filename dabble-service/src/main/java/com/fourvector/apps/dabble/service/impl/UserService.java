/**
 * 
 */
package com.fourvector.apps.dabble.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import com.fourvector.apps.dabble.common.dto.AddressDTO;
import com.fourvector.apps.dabble.common.dto.SubscriptionDTO;
import com.fourvector.apps.dabble.common.dto.UserDTO;
import com.fourvector.apps.dabble.common.dto.UserDetailsDTO;
import com.fourvector.apps.dabble.common.dto.UserFeedbackDTO;
import com.fourvector.apps.dabble.dao.IUserDAO;
import com.fourvector.apps.dabble.dao.config.DAOConstants;
import com.fourvector.apps.dabble.dao.config.Status;
import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.model.VerificationKeyModel;
import com.fourvector.apps.dabble.model.job.BidModel;
import com.fourvector.apps.dabble.model.job.JobModel;
import com.fourvector.apps.dabble.model.payment.SubscriptionModel;
import com.fourvector.apps.dabble.model.user.DeviceTokenModel;
import com.fourvector.apps.dabble.model.user.UserAddressModel;
import com.fourvector.apps.dabble.model.user.UserCredentialModel;
import com.fourvector.apps.dabble.model.user.UserEmailModel;
import com.fourvector.apps.dabble.model.user.UserModel;
import com.fourvector.apps.dabble.model.user.UserPhoneModel;
import com.fourvector.apps.dabble.model.user.UserRatingModel;
import com.fourvector.apps.dabble.model.user.UserSubscriptionModel;
import com.fourvector.apps.dabble.service.IPaymentService;
import com.fourvector.apps.dabble.service.IUserService;
import com.fourvector.apps.dabble.service.config.NotificationEvent;
import com.fourvector.apps.dabble.service.config.ParameterKeys;
import com.fourvector.apps.dabble.service.config.ResponseMessage;
import com.fourvector.apps.dabble.service.config.ResponseStatus;

/**
 * @author Anantha.Sharma
 */
public class UserService extends BaseService implements IUserService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserService.class);

	private IUserDAO		userDAO;
	private String			imageStoreLocation;
	private IPaymentService	paymentService;

	/**
	 * 
	 */
	public UserService() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SubscriptionDTO> getSubscriptionsByUserType(final Integer userTypeCode) {
		LOG.debug("Method [SubscriptionsByUserType]: Called ({})", userTypeCode);
		List<SubscriptionDTO> result = new LinkedList<SubscriptionDTO>();

		List<SubscriptionModel> subscriptions = (List<SubscriptionModel>) userDAO.runQuery("from " + SubscriptionModel.class.getName() + " p where p.userTypeCode=? and p.subscriptionType<>?", new Object[] { userTypeCode, DAOConstants.SUBSCRIPTION_TYPE_FREE });
		if (subscriptions != null) {
			LOG.info("found {} subscriptions for userType {}", subscriptions.size(), userTypeCode);
		}
		for (SubscriptionModel subscriptionModel : subscriptions) {
			SubscriptionDTO dto = new SubscriptionDTO();
			prepareSubscriptionDTO(dto, subscriptionModel);
			result.add(dto);
		}
		LOG.debug("Method [SubscriptionsByUserType]: Returning.");
		return result;
	}

	@Override
	public int verifyLink(String key) {
		LOG.debug("Method [verifyLink]: Called");

		String hql = "from " + VerificationKeyModel.class.getName() + " p where p.key=? and current_date() <= p.expiryDate";

		VerificationKeyModel vm = (VerificationKeyModel) userDAO.runSingleResponseQuery(hql, key);
		if (vm == null) {
			LOG.info("Key {} already verified", key);
			return ResponseStatus.DUPLICATE_REQUEST;
		}
		String userEmail = vm.getEmail();

		UserModel userModel = (UserModel) userDAO.runSingleResponseQuery("select p.user from " + UserEmailModel.class.getName() + " p where p.email=?", userEmail);

		String updateCredentialsStatusHQL = "update " + UserCredentialModel.class.getName() + " p set p.status=0 where p.user.id = ?";
		String updateAddressStatusHQL = "update " + UserAddressModel.class.getName() + " p set p.status=0 where p.user.id = ?";
		String updateEmailStatusHQL = "update " + UserEmailModel.class.getName() + " p set p.status=0 where p.user.id = ?";
		String updatePhoneStatusHQL = "update " + UserPhoneModel.class.getName() + " p set p.status=0 where p.user.id = ?";
		String updateUserStatusHQL = "update " + UserModel.class.getName() + " p set p.status=0 where p.id = ?";

		userDAO.runUpdateQuery(updateCredentialsStatusHQL, userModel.getId());
		userDAO.runUpdateQuery(updateAddressStatusHQL, userModel.getId());
		userDAO.runUpdateQuery(updateEmailStatusHQL, userModel.getId());
		userDAO.runUpdateQuery(updatePhoneStatusHQL, userModel.getId());
		userDAO.runUpdateQuery(updateUserStatusHQL, userModel.getId());

		userDAO.deleteObject(vm);

		LOG.debug("Method [verifyLink]: Returning.");
		return ResponseStatus.SUCCESS;
	}

	@Override
	public void addToken(Integer userId, String deviceToken, Integer deviceType) {
		LOG.debug("Method [addToken]: Called");

		List<?> deviceTokens = (List<?>) userDAO.runQuery("from " + DeviceTokenModel.class.getName() + " p where p.user.id = ? and lower(p.deviceToken) = lower(?)", userId, deviceToken);

		if (CollectionUtils.isEmpty(deviceTokens)) {
			UserModel userModel = (UserModel) userDAO.findById(UserModel.class, userId);
			verifyUser(userModel);
			DeviceTokenModel dtm = new DeviceTokenModel(deviceToken, deviceType, userModel);
			LOG.info("Adding new Token : .{} ", userDAO.saveObject(dtm));
		} else {
			LOG.info("Token already exists.");
		}
		LOG.debug("Method [addToken]: Returning.");
	}

	@Override
	public UserDTO createSubscription(Integer userId, Integer subscriptionId, String nonce, String piId) {
		LOG.debug("Method [createSubscription]: Called");
		UserDTO result = new UserDTO();
		SubscriptionModel subscriptionModel = userDAO.findById(SubscriptionModel.class, subscriptionId);
		if (subscriptionModel == null) {
			throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, "Subscription model#" + subscriptionId + " doesn't exist");
		}
		UserModel userModel = userDAO.findById(UserModel.class, userId);
		verifyUser(userModel);
		String hql = "select count(p) from " + UserSubscriptionModel.class.getName() + " p where p.user.id=?";
		Long resp = (Long) userDAO.runSingleResponseQuery(hql, userId);
		if (resp != null && resp > 0) {
			// this is not a new customer, and is not allowed to opt for a free trial setup.
			if (subscriptionModel.getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_FREE) {
				// cannot allow this.
				LOG.info("Customer cannot opt for free trial");
				throw new DabbleException(ResponseStatus.UNAVAILABLE, "Free tier is unavailable for this customer");
			}
		}
		UserSubscriptionModel currentSubscription = userDAO.getActiveSubscription(userId);
		UserSubscriptionModel userSubscriptionModel = new UserSubscriptionModel();
		userSubscriptionModel.setSubscription(subscriptionModel);
		Calendar date = new GregorianCalendar();
		date.add(Calendar.DATE, subscriptionModel.getDurationInDays());
		LOG.info("Setting expiry date as {} for user {}", date.getTime(), userId);
		userSubscriptionModel.setExpiryDate(date.getTime());
		userSubscriptionModel.setSubscriptionDate(new Date());
		if (currentSubscription != null && currentSubscription.getSubscription().getSubscriptionType() != DAOConstants.SUBSCRIPTION_TYPE_PAY_AS_YOU_GO) {
			// the user is switching from any to monthly subscription.
			if (currentSubscription.getExpiryDate().getTime() > System.currentTimeMillis()) {
				// we still have time.
				userSubscriptionModel.setStatus(Status.PENDING_VERIFICATION);
			}
		} else {
			userSubscriptionModel.setStatus(Status.ACTIVE);
		}
		userSubscriptionModel.setUser(userModel);
		paymentService.createCustomer(userId);

// String token = paymentService.addCardToCustomer(userId, cardNumber, expiryDate, amount, currency, cvv, nonce);
// userSubscriptionModel.setPaymentInstrument(token);
		if (subscriptionModel.getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_MONTHLY) {
			// need to charge the customers credit card.
			String id = paymentService.setupRecurringPayments(userModel.getId(), piId, subscriptionModel, nonce);
			userSubscriptionModel.setSubscriptionReference(id);

			Map<String, Object> params = new HashMap<>();
			params.put(ParameterKeys.KEY_TRANSACTION_ID, id);
			params.put(ParameterKeys.KEY_SUBSCRIPTION_AMOUNT, subscriptionModel.getAmountForSubscription());
			params.put(ParameterKeys.KEY_SUBSCRIPTION_NAME, subscriptionModel.getName());
			params.put(ParameterKeys.KEY_USER_NAME, userModel.getFirstName());
			params.put(ParameterKeys.KEY_DATE, new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
			params.put(ParameterKeys.KEY_TIME, new SimpleDateFormat("hh:MM:ss").format(new Date()));

			sendEmailNotification(NotificationEvent.SUBSCRIBE_TO_MONTHLY_PAYMENT, params, userDAO, userId);

		}
		if (subscriptionModel.getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_PAY_AS_YOU_GO) {
			// need to check if the customer is currently a monthly subscriber.
			if (userSubscriptionModel.getSubscriptionReference() != null) {
				// this means we have an active subscription for the customer.
				// we need to cancel this and set the field to null.
				paymentService.cancelSubscription(userModel, userSubscriptionModel.getSubscriptionReference());
			}
			userSubscriptionModel.setSubscriptionReference(null);
			userDAO.updateObject(userSubscriptionModel);
			userSubscriptionModel.setSubscription(subscriptionModel);
			Map<String, Object> params = new HashMap<>();
			params.put(ParameterKeys.KEY_TRANSACTION_ID, "NA");
			params.put(ParameterKeys.KEY_SUBSCRIPTION_AMOUNT, subscriptionModel.getAmountForSubscription());
			params.put(ParameterKeys.KEY_SUBSCRIPTION_NAME, subscriptionModel.getName());
			params.put(ParameterKeys.KEY_SUBSCRIPTION_DESCRIPTION, subscriptionModel.getDescription());
			params.put(ParameterKeys.KEY_USER_NAME, userModel.getFirstName());

			Calendar calendar = new GregorianCalendar();
			String timezoneStr = userModel.getTimezone();
			timezoneStr = timezoneStr == null ? "PST" : timezoneStr;
			calendar.setTimeZone(TimeZone.getTimeZone(timezoneStr));
			SimpleDateFormat sdf_date = new SimpleDateFormat("MM/dd/yyyy");
			SimpleDateFormat sdf_time = new SimpleDateFormat("hh:MM:ss");
			sdf_date.setTimeZone(TimeZone.getTimeZone(timezoneStr));
			sdf_time.setTimeZone(TimeZone.getTimeZone(timezoneStr));

			params.put(ParameterKeys.KEY_DATE, sdf_date.format(calendar.getTime()));
			params.put(ParameterKeys.KEY_TIME, sdf_time.format(calendar.getTime()));

			sendEmailNotification(NotificationEvent.SUBSCRIBE_TO_PAY_AS_YOU_GO, params, userDAO, userId);
		}
		Serializable id = userDAO.saveObject(userSubscriptionModel);
		LOG.debug("Subscription saved#{}", id);

		userDAO.runUpdateQuery("update " + UserSubscriptionModel.class.getName() + " p set p.status=? where p.status=? and p.user.id=? and p.id<>?", Status.INACTIVE, Status.ACTIVE, userId, id);

		prepareUserDTO(result, userModel);
		enrichUserDTO(result, userSubscriptionModel);
		LOG.debug("Method [createSubscription]: Returning {}", result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.fourvector.apps.dabble.service.IUserService#createUser(com.fourvector
	 * .apps.dabble.model.user.UserModel)
	 */
	@Override
	public Integer createUser(final UserDTO userDto, InputStream imageStream) {
		LOG.trace("Method [createUser]: Called ");
		if (imageStream == null) {
			LOG.error("Unable to complete request, no image found");
			throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, ResponseMessage.INCOMPLETE_DATA);
		}

		if (!org.springframework.util.StringUtils.hasText(userDto.getPassword())) {
			LOG.error("Unable to complete request, no password supplied");
			throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, ResponseMessage.INCOMPLETE_DATA + " / Password Not Supplied");
		}

		String fileName = storeFile(imageStream, "userphoto.png", imageStoreLocation);

		UserModel userModel = new UserModel();
		File file = new File(imageStoreLocation + fileName);
		prepareUserModel(userModel, userDto);
		userModel.setPhotoUrl(file.getName());
		userModel.setStatus(Status.ACTIVE);

		UserEmailModel userEmailModel = new UserEmailModel();
		userEmailModel.setEmail(userDto.getEmail());
		userEmailModel.setStatus(Status.ACTIVE);
		UserCredentialModel userCredentialModel = new UserCredentialModel();
		userCredentialModel.setLogin(userDto.getEmail());
		userCredentialModel.setPassword(DigestUtils.md5DigestAsHex(userDto.getPassword().getBytes()));
		userCredentialModel.setUser(userModel);
		userCredentialModel.setType("password");
		userCredentialModel.setStatus(Status.PENDING_VERIFICATION);
		UserCredentialModel userCredentialModel2 = null;

		if (userDto.getFacebookId() != null) {
			userCredentialModel2 = new UserCredentialModel();
			userCredentialModel2.setLogin(userDto.getFacebookId());
			userCredentialModel2.setPassword(DigestUtils.md5DigestAsHex(userDto.getFacebookId().getBytes()));
			userCredentialModel2.setUser(userModel);
			userCredentialModel2.setType("facebook");
			userCredentialModel2.setStatus(Status.PENDING_VERIFICATION);
		}

		userEmailModel.setStatus(Status.ACTIVE);

		UserAddressModel userAddressModel = new UserAddressModel();
		prepareUserAddressModel(userAddressModel, userDto.getAddress());
		userAddressModel.setStatus(Status.ACTIVE);

		Long count = (Long) userDAO.runSingleResponseQuery("select count(p) from " + UserEmailModel.class.getName() + " p where p.email=?", userDto.getEmail());

		if (count > 0) {
			throw new DabbleException(ResponseStatus.USER_ALREADY_EXISTS, "User already exists");
		}

		Integer response = (Integer) userDAO.saveObject(userModel);
		userAddressModel.setUser(userModel);
		userEmailModel.setUser(userModel);

		userDAO.saveObject(userAddressModel);
		userDAO.saveObject(userEmailModel);
		userDAO.saveObject(userCredentialModel);
		if (userCredentialModel2 != null) {
			userDAO.saveObject(userCredentialModel2);
		}
		UserSubscriptionModel usm = new UserSubscriptionModel();
		SubscriptionModel sm = (SubscriptionModel) userDAO.runSingleResponseQuery("from " + SubscriptionModel.class.getName() + " p where p.subscriptionType=? and p.userTypeCode=?", DAOConstants.SUBSCRIPTION_TYPE_FREE, userModel.getUserTypeCode());
		if (sm == null) {
			LOG.info("Unable to find Free Subscription for user type {}", userModel.getUserTypeCode());
		}
		paymentService.createCustomer(userModel, userEmailModel.getEmail(), userDto.getPhone());
		usm.setSubscription(sm);
		usm.setSubscriptionDate(new Date());
		usm.setStatus(Status.ACTIVE);
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, sm.getDurationInDays());
		usm.setExpiryDate(cal.getTime());
		usm.setUser(userModel);
		userDAO.saveObject(usm);

		if (!StringUtils.isBlank(userDto.getPhone())) {
			UserPhoneModel userPhoneModel = new UserPhoneModel();
			userPhoneModel.setPhone(userDto.getPhone());
			userPhoneModel.setStatus(Status.ACTIVE);
			userPhoneModel.setUser(userModel);
			userDAO.saveObject(userPhoneModel);
		}

		VerificationKeyModel vkm = createVerificationKey(8, userDto.getEmail());
		userDAO.saveObject(vkm);
		String key = vkm.getKey();
		Map<String, Object> params = new HashMap<>();
		params.put(ParameterKeys.KEY_VERIFICATION_PIN, key);
		params.put(ParameterKeys.KEY_TARGET, userDto.getEmail());
		params.put(ParameterKeys.KEY_USER_NAME, userDto.getFirstName());
		params.put(ParameterKeys.KEY_VERIFICATION_LINK, verificationLink + key);

		sendEmailNotification(NotificationEvent.USER_EMAIL_VERIFICATION, params, userDAO, response);

		LOG.trace("Method [createUser]: Returning " + response);
		return response;

	}

	@Override
	public void updateUser(Integer userId, UserDTO userDto, InputStream is) {
		LOG.debug("Method [updateUser]: Called");

		UserModel userModel = (UserModel) userDAO.findById(UserModel.class, userId);
		verifyUser(userModel);
		String fileName = storeFile(is, "userphoto.png", imageStoreLocation);

		File file = new File(imageStoreLocation + fileName);
		super.prepareUserModel(userModel, userDto);

		if (StringUtils.isNotEmpty(userDto.getPhone())) {

			LOG.info("Phone number has been updated, Storing Changes.");
			userDAO.runUpdateQuery("UPDATE " + UserPhoneModel.class.getName() + " p set p.status = ? where p.status=0 and p.user.id=? ", Status.INACTIVE, userId);
			UserPhoneModel upm = new UserPhoneModel();
			upm.setPhone(userDto.getPhone());
			upm.setStatus(Status.ACTIVE);
			upm.setUser(userModel);
			userDAO.saveObject(upm);
			LOG.info("Phone number has been updated, Changes Saved.");

		}

		userModel.setPhotoUrl(file.getName());
		userDAO.updateObject(userModel);
		LOG.debug("Method [updateUser]: Returning.");

	}

	@Override
	public InputStream getProfileImage(Integer userId) {
		LOG.debug("Method [getProfileImage]: Called");
		UserModel userModel = (UserModel) userDAO.findById(UserModel.class, userId);
		verifyUser(userModel);
		InputStream is = null;
		try {
			File f = new File(imageStoreLocation + "/" + userModel.getPhotoUrl());
			if (!f.exists()) {
				// this shouldn't happen
				LOG.error("Profile Image not found.");
				throw new DabbleException(ResponseStatus.UNAVAILABLE, ResponseMessage.RESOURCE_UNAVAILABLE);
			}
			is = FileUtils.openInputStream(f);
		} catch (IOException e) {
			LOG.error("Found exception IOException in method getProfileImage", e);
		}

		LOG.debug("Method [getProfileImage]: Returning Stream.");

		return is;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.fourvector.apps.dabble.service.IUserService#addAddress(com.fourvector
	 * .apps.dabble.model.user.UserAddressModel)
	 */
	@Override
	public void addAddress(final AddressDTO address, final Integer userId) {

		LOG.trace("Method [addAddress]: Called ");

		UserModel userModel = (UserModel) userDAO.findById(UserModel.class, userId);
		verifyUser(userModel);
		UserAddressModel userAddressModel = new UserAddressModel();
		this.prepareUserAddressModel(userAddressModel, address);
		userAddressModel.setStatus(Status.ACTIVE);
		userDAO.runUpdateQuery("UPDATE " + UserAddressModel.class.getName() + " p set p.status = ? where p.status=0 and p.user.id=? ", Status.INACTIVE, userId);
		userAddressModel.setUser(userModel);
		userDAO.saveObject(userAddressModel);

		LOG.trace("Method [addAddress]: Returning ");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.fourvector.apps.dabble.service.IUserService#setEmailAsPrimary(com.
	 * fourvector.apps.dabble.model.user.UserModel, java.lang.String)
	 */
	@Override
	public void setEmailAsPrimary(final UserModel userModel, final String email) {
		LOG.trace("Method [setEmailAsPrimary]: Called ");

		LOG.trace("Method [setEmailAsPrimary]: Returning ");

		throw new IllegalStateException("Implementation Not Found");
	}

	@Override
	public void activateEmail(Integer userId, String emailId) {
		LOG.debug("Method [activateEmail]: Called");

		String hql = "from " + UserEmailModel.class.getName() + " p where lower(p.email) = lower(?) and p.user.id = ? and p.user.status = 0";
		UserEmailModel user = (UserEmailModel) userDAO.runSingleResponseQuery(hql, emailId, userId);
		if (user == null) {
			LOG.info("Email {} for User {}; not found", emailId, userId);
			throw new DabbleException(ResponseStatus.UNAVAILABLE, ResponseMessage.USER_EMAIL_UNAVAILABLE);
		}
		String updateEmailStatusHQL = "update " + UserEmailModel.class.getName() + " p set p.status=? where p.user.userId = ?";
		userDAO.runUpdateQuery(updateEmailStatusHQL, Status.INACTIVE, userId);
		user.setStatus(0);
		userDAO.updateObject(user);

		LOG.debug("Method [activateEmail]: Returning.");
	}

	@Override
	public UserDTO authenticate(String login, String password) {
		LOG.debug("Method [authenticate]: Called");

		UserDTO result = new UserDTO();
		String passwordDigest = DigestUtils.md5DigestAsHex(password.getBytes());
		UserCredentialModel model = (UserCredentialModel) userDAO.runSingleResponseQuery("from " + UserCredentialModel.class.getName() + " p where lower(p.login)=? and p.type='password'", login.toLowerCase());
		if (model == null) {
			throw new DabbleException(ResponseStatus.AUTHENTICATION_FAILED, "unknown user id.");
		}
		if (model.getStatus() != Status.ACTIVE) {
			throw new DabbleException(ResponseStatus.INACTIVE, ResponseMessage.USER_INACTIVE);
		}
		verifyUser(model.getUser());

		if (model.getPassword().equalsIgnoreCase(passwordDigest)) {
			// the user is available and the password is correct.
			populateUserDTO(result, model.getUser());
		} else {
			// incorrect password.
			throw new DabbleException(ResponseStatus.INCORRECT_PIN, "Password incorrect.");
		}

		enrichUserDTO(result, userDAO.getActiveSubscription(model.getUser().getId()));

		LOG.debug("Method [authenticate]: Returning {}", result);
		return result;
	}

	public void enrichUserDTO(UserDTO userDTO, UserSubscriptionModel userSubscriptionModel) {
		if (userSubscriptionModel == null) {
			// no subscription exists.
			throw new DabbleException(ResponseStatus.SUBSCRIPTION_EXPIRED, "User subscription is no longer valid", userDTO);
		} else {
			SubscriptionDTO dto = new SubscriptionDTO();
			prepareSubscriptionDTO(dto, userSubscriptionModel);
			userDTO.setSubscription(dto);
		}
	}

	@Override
	public UserDetailsDTO getUserDetails(Integer userId) {
		LOG.debug("Method [getUserDetails]: Called");
		UserDetailsDTO result = new UserDetailsDTO();

		Long jobsBidAndCompleted, jobsBidAndInProgress, jobsBidFor, jobsCreated, jobsCreatedAndCompleted, jobsCreatedInProgress, jobsWon, userTotalRating, userRatingsCount;
		double userRating;

		String hqlJobsBidFor = "select count(distinct p) from " + BidModel.class.getName() + " p where p.bidder.id=? and p.job.status <> ? and p.status not in (?, ?)";
		jobsBidFor = (Long) userDAO.runSingleResponseQuery(hqlJobsBidFor, userId, Status.JOB_OR_BID_RETRACTED, Status.JOB_OR_BID_RETRACTED, Status.INACTIVE);

		String hqlJobsWon = "select count(p) from " + BidModel.class.getName() + " p where p.bidder.id=? and p.status=?";
		jobsWon = (Long) userDAO.runSingleResponseQuery(hqlJobsWon, userId, Status.JOB_OR_BID_ACCEPTED);

		String hqlJobsBidAndCompleted = "select count(p) from " + BidModel.class.getName() + " p where p.bidder.id=? and p.status=? and p.job.status=?";
		jobsBidAndCompleted = (Long) userDAO.runSingleResponseQuery(hqlJobsBidAndCompleted, userId, Status.JOB_OR_BID_ACCEPTED, Status.JOB_COMPLETED);

		String hqlJobsBidAndInProgress = "select count(p) from " + BidModel.class.getName() + " p where p.bidder.id=? and p.status=? and p.job.status=?";
		jobsBidAndInProgress = (Long) userDAO.runSingleResponseQuery(hqlJobsBidAndInProgress, userId, Status.JOB_OR_BID_ACCEPTED, Status.JOB_OR_BID_ACCEPTED);

		String hqlJobsCreated = "select count(p) from " + JobModel.class.getName() + " p where p.postedBy.id=? and endDate > CURDATE()";
		jobsCreated = (Long) userDAO.runSingleResponseQuery(hqlJobsCreated, userId);

		String hqlJobsCreatedAndCompleted = "select count(p) from " + JobModel.class.getName() + " p where p.postedBy.id=? and p.status=?";
		jobsCreatedAndCompleted = (Long) userDAO.runSingleResponseQuery(hqlJobsCreatedAndCompleted, userId, Status.JOB_COMPLETED);

		String hqlJobsCreatedInProgress = "select count(p) from " + JobModel.class.getName() + " p where p.postedBy.id=? and p.status = ?";
		jobsCreatedInProgress = (Long) userDAO.runSingleResponseQuery(hqlJobsCreatedInProgress, userId, Status.JOB_OR_BID_ACCEPTED);

		String hqlRating = "select sum(p.rating), count(p.rating) from " + UserRatingModel.class.getName() + " p where p.ratingForUser.id = ?";
		Object[] resp = (Object[]) userDAO.runSingleResponseQuery(hqlRating, userId);

		userTotalRating = (Long) resp[0];
		userRatingsCount = (Long) resp[1];
		userRating = 0d;
		if (userTotalRating != null && userRatingsCount != null) {
			userRating = ((double) userTotalRating) / ((double) userRatingsCount);
		}
		result.setJobsBidFor(getInt(jobsBidFor));
		result.setJobsWon(getInt(jobsWon));
		result.setJobsBidAndCompleted(getInt(jobsBidAndCompleted));
		result.setJobsBidAndInProgress(getInt(jobsBidAndInProgress));
		result.setJobsCreated(getInt(jobsCreated));
		result.setJobsCreatedAndCompleted(getInt(jobsCreatedAndCompleted));
		result.setJobsCreatedInProgress(getInt(jobsCreatedInProgress));
		result.setUserRating(round(userRating));

		LOG.debug("Method [getUserDetails]: Returning {}", result);
		return result;
	}

	private Integer getInt(Long number) {
		if (number == null) {
			return 0;
		}
		return number.intValue();
	}

	@Override
	public UserDTO fbAuthenticate(String facebookId) {
		LOG.debug("Method [fbAuthenticate]: Called");

		UserDTO result = new UserDTO();

		String hql = "from " + UserCredentialModel.class.getName() + " p where p.login=? and p.type=?";
		UserCredentialModel model = (UserCredentialModel) userDAO.runSingleResponseQuery(hql, facebookId, "facebook");
		if (model == null) {
			throw new DabbleException(ResponseStatus.AUTHENTICATION_FAILED, "unknown user id.");
		}
		if (model.getStatus() != Status.ACTIVE) {
			throw new DabbleException(ResponseStatus.INACTIVE, ResponseMessage.USER_INACTIVE);
		} else {
			populateUserDTO(result, model.getUser());
		}
		verifyUser(model.getUser());

		enrichUserDTO(result, userDAO.getActiveSubscription(model.getUser().getId()));

		LOG.debug("Method [fbAuthenticate]: Returning {}", result);
		return result;
	}

	private void populateUserDTO(UserDTO userDto, UserModel model) {
		if (model != null) {
			// We have success
			prepareUserDTO(userDto, model);
			UserAddressModel uam = (UserAddressModel) userDAO.runSingleResponseQuery("from " + UserAddressModel.class.getName() + " p where p.user.id=? and p.status=?", model.getId(), Status.ACTIVE);
			prepareUserAddressDTO(userDto.getAddress(), uam);
			String email = (String) userDAO.runSingleResponseQuery("select p.email from " + UserEmailModel.class.getName() + " p where p.user.id=? and p.status=?", model.getId(), Status.ACTIVE);
			userDto.setEmail(email);
			String phone = (String) userDAO.runSingleResponseQuery("select p.phone from " + UserPhoneModel.class.getName() + " p where p.user.id=? and p.status=?", model.getId(), Status.ACTIVE);
			userDto.setPhone(phone);
		}
	}

	@Override
	public void forgotPassword(String userEmail) {
		LOG.debug("Method [forgotPassword]: Called");
		if (userDAO.isValidUserEmail(userEmail)) {
			// everything looks good so far.
			VerificationKeyModel vkm = createVerificationKey(6, userEmail);
			String pin = vkm.getKey();
			userDAO.saveObject(vkm);

			Map<String, Object> params = new LinkedHashMap<>();
			params.put(ParameterKeys.KEY_VERIFICATION_PIN, pin);
			params.put(ParameterKeys.KEY_TARGET, userEmail);
			Integer userId = userDAO.getUserCredentialByEmail(userEmail).getUser().getId();
			sendEmailNotification(NotificationEvent.USER_FORGOT_PASSWORD, params, userDAO, userId);
		} else {
			throw new DabbleException(ResponseStatus.UNAVAILABLE, ResponseMessage.USER_EMAIL_UNAVAILABLE);
		}
		LOG.debug("Method [forgotPassword]: Returning.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void verifyAndSetPassword(String userEmail, String pin, String newPassword) {
		LOG.debug("Method [verifyAndSetPassword]: Called");
		String hql = "from " + VerificationKeyModel.class.getName() + " p where p.email=? and p.key=? and p.expiryDate > current_date()";
		List<VerificationKeyModel> vkmList = (List<VerificationKeyModel>) userDAO.runQuery(hql, userEmail, pin);

		if (vkmList == null || vkmList.isEmpty()) {
			LOG.error("userEmail {} doesn't have a valid pin registered but recieved a call to reset password with Pin {}", userEmail, pin);
			throw new DabbleException(ResponseStatus.UNAUTHORISED_ACCESS, ResponseMessage.UNAUTHORISED_ACCESS);
		}
		VerificationKeyModel vkm = vkmList.get(0);
		if (vkm.getKey().equalsIgnoreCase(pin)) {
			LOG.info("Entry found, deleting it so that the user cannot use the same pin again.");
			userDAO.deleteObject(vkm);
			UserCredentialModel credentialsModel = (UserCredentialModel) userDAO.getUserCredentialByEmail(userEmail);
			credentialsModel.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
			userDAO.updateObject(credentialsModel);
			Map<String, Object> params = new HashMap<>();
			params.put(ParameterKeys.KEY_USER_NAME, credentialsModel.getUser().getFirstName());
			sendEmailNotification(NotificationEvent.PASSWORD_CHANGE_SUCCESS, params, userDAO, credentialsModel.getUser().getId());
		} else {
			LOG.info("User Pin incorrect, user:{}, provided Pin: {}", userEmail, pin);
			throw new DabbleException(ResponseStatus.INCORRECT_PIN, ResponseMessage.INCORRECT_PIN);
		}
		LOG.debug("Method [verifyAndSetPassword]: Returning.");

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserFeedbackDTO> getUserFeedback(Integer userId) {
		LOG.debug("Method [getUserFeedback]: Called");

		List<UserFeedbackDTO> result = new LinkedList<>();

		String hql = "from " + UserRatingModel.class.getName() + " p where p.ratingForUser.id=?";

		List<UserRatingModel> urmList = (List<UserRatingModel>) userDAO.runQuery(hql, userId);
		if (urmList != null && urmList.isEmpty() == false) {
			for (UserRatingModel userRatingModel : urmList) {
				UserFeedbackDTO dto = new UserFeedbackDTO();
				dto.setFeedback(userRatingModel.getComment());
				dto.setRating(userRatingModel.getRating());
				dto.setJobId(userRatingModel.getJob().getId());
				dto.setJobTitle(userRatingModel.getJob().getJobTitle());
				dto.setJobPosterFirstName(userRatingModel.getJob().getPostedBy().getFirstName());
				dto.setJobPosterMiddleName(userRatingModel.getJob().getPostedBy().getMiddleName());
				dto.setJobPosterLastName(userRatingModel.getJob().getPostedBy().getLastName());
				dto.setFeedbackDate(userRatingModel.getCreatedOn());
				result.add(dto);
			}
		}
		int count = 0;
		if (result != null) {
			count = result.size();
		}
		LOG.debug("Method [getUserFeedback]: Returning {}", count);
		return result;
	}

	/**
	 * @return the userDAO
	 */
	public IUserDAO getUserDAO() {
		return userDAO;
	}

	/**
	 * @param userDAO
	 *            the userDAO to set
	 */
	public void setUserDAO(IUserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * @return the imageStoreLocation
	 */
	public String getImageStoreLocation() {
		return imageStoreLocation;
	}

	/**
	 * @param imageStoreLocation
	 *            the imageStoreLocation to set
	 */
	public void setImageStoreLocation(String imageStoreLocation) {
		this.imageStoreLocation = imageStoreLocation;
	}

	/**
	 * @return the paymentService
	 */
	public IPaymentService getPaymentService() {
		return paymentService;
	}

	/**
	 * @param paymentService
	 *            the paymentService to set
	 */
	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}
}
