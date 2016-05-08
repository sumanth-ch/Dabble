/**
 * 
 */
package com.fourvector.apps.dabble.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.jms.core.JmsTemplate;

import com.fourvector.apps.dabble.common.dto.AddressDTO;
import com.fourvector.apps.dabble.common.dto.BidDTO;
import com.fourvector.apps.dabble.common.dto.JobDTO;
import com.fourvector.apps.dabble.common.dto.SubscriptionDTO;
import com.fourvector.apps.dabble.common.dto.UserDTO;
import com.fourvector.apps.dabble.dao.IBaseDAO;
import com.fourvector.apps.dabble.dao.config.DAOConstants;
import com.fourvector.apps.dabble.dao.config.Status;
import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.jms.DabbleMessageCreator;
import com.fourvector.apps.dabble.model.VerificationKeyModel;
import com.fourvector.apps.dabble.model.job.BidModel;
import com.fourvector.apps.dabble.model.job.JobDetailModel;
import com.fourvector.apps.dabble.model.job.JobModel;
import com.fourvector.apps.dabble.model.payment.SubscriptionModel;
import com.fourvector.apps.dabble.model.user.UserAddressModel;
import com.fourvector.apps.dabble.model.user.UserModel;
import com.fourvector.apps.dabble.model.user.UserRatingModel;
import com.fourvector.apps.dabble.model.user.UserSubscriptionModel;
import com.fourvector.apps.dabble.service.config.NotificationEvent;
import com.fourvector.apps.dabble.service.config.ParameterKeys;
import com.fourvector.apps.dabble.service.config.ResponseMessage;
import com.fourvector.apps.dabble.service.config.ResponseStatus;

/**
 * @author asharma
 */
public class BaseService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BaseService.class);

	@Autowired
	private JmsTemplate	jmsTemplate;
	protected String	verificationLink;

	/**
	 * 
	 */
	public BaseService() {
		super();
	}

	protected String getLast4(String cardNumber) {
		int len = cardNumber.length();
		return cardNumber.substring(len - 4, len);
	}

	/**
	 * @param d
	 * @return
	 */
	protected double round(double d) {
		return Math.round(d * 100) / 100.0d;
	}

	/**
	 * verifies bid model
	 * 
	 * @param bidModel
	 */
	protected void verifyBidModel(BidModel bidModel) {
		LOG.debug("Method [verifyBidModel]: Called");
		if (bidModel == null) {
			throw new DabbleException(ResponseStatus.UNAVAILABLE, "Bid not found.");
		}
		if (bidModel.getStatus() == Status.JOB_OR_BID_RETRACTED) {
			// looks like a duplicate request.
			LOG.info("Bid already retracted, no need for any action, this perhaps is a duplicate request.");
			throw new DabbleException(ResponseStatus.UNAVAILABLE, "Bid is not available.");

		}
		LOG.debug("Method [verifyBidModel]: Returning.");
	}

	/**
	 * stores a file in the given location and returns the URL
	 * 
	 * @param is
	 * @param fileName
	 * @param location
	 * @return
	 */
	protected String storeFile(InputStream is, String fileName, String location) {
		LOG.debug("Method [storeFile]: Called");
		if (is == null) {

		}
		String newName = UUID.randomUUID().toString();
		OutputStream os = null;
		String extn = FilenameUtils.getExtension(fileName);
		String file = location + newName + "." + extn;
		File filePath = new File(file);
		try {
			os = FileUtils.openOutputStream(filePath);
			long size = IOUtils.copyLarge(is, os);
			LOG.debug("File Stored: name: {}, size: {}KB.", filePath.getAbsolutePath(), (size / 1024d));
		} catch (IOException e) {
			LOG.error("Found exception IOException in method createUser", e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}

		LOG.debug("Method [storeFile]: Returning");
		return filePath.getName();
	}

	protected void sendPushNotification(NotificationEvent event, Map<String, Object> params, IBaseDAO dao, Integer userId) {
		LOG.debug("Method [sendPushNotification]: Called");
		if (params == null) {
			params = new HashMap<>();
		}
		params.put(ParameterKeys.MODE, ParameterKeys.KEY_PUSH_MODE);
		String[] deviceIds = dao.getDeviceIds(userId);
		for (String deviceId : deviceIds) {
			params.put(ParameterKeys.KEY_TARGET, deviceId);
			notifyUserOfEvent(event, params);
		}
		LOG.debug("Method [sendPushNotification]: Returning.");
	}

	protected void sendEmailNotification(NotificationEvent event, Map<String, Object> params, IBaseDAO dao, Integer userId) {
		LOG.debug("Method [sendEmailNotification]: Called");
		if (params == null) {
			params = new HashMap<>();
		}
		UserModel um = (UserModel) dao.findById(UserModel.class, userId);

		params.put(ParameterKeys.KEY_USER_NAME, um.getFirstName());
		params.put(ParameterKeys.MODE, ParameterKeys.VALUE_EMAIL_MODE);
// if (params.get(ParameterKeys.KEY_TARGET) == null) {
		String email = dao.getActiveEmail(userId);
		LOG.info("Sending user#{} email@{} for event {}", userId, email, event.name());
		params.put(ParameterKeys.KEY_TARGET, email);
// }
		notifyUserOfEvent(event, params);

		LOG.debug("Method [sendEmailNotification]: Returning.");

	}

	/**
	 * prepares a jobDTO
	 * 
	 * @param dto
	 * @param jobModel
	 */
	protected void prepareJobDTO(JobDTO dto, JobModel jobModel) {
// LOG.debug("Method [prepareJobDTO]: Called");

		dto.setAmount(jobModel.getAmount());
		dto.setCreatedDate(jobModel.getCreatedOn());
		dto.setEvent(jobModel.isEvent());	
		dto.setCurrency(jobModel.getCurrency());
		dto.setJobTitle(jobModel.getJobTitle());
		dto.getEndDate().setTimeInMillis(jobModel.getEndDate().getTime());
		dto.getEndDate().set(Calendar.HOUR, 0);
		dto.getEndDate().set(Calendar.MINUTE, 0);
		dto.getEndDate().set(Calendar.SECOND, 0);
		dto.setNumberOfVolunteers(jobModel.getNumberOfVolunteers());
		dto.setVolunteers(jobModel.isVolunteers());
		dto.setDescription(jobModel.getDescription());
		dto.setJobId(jobModel.getId());
		dto.setLat(jobModel.getLat());
		dto.setLng(jobModel.getLng());
		dto.setPosterId(jobModel.getPostedBy().getId());
		dto.setPosterImage(jobModel.getPostedBy().getPhotoUrl());
		dto.setPosterName(jobModel.getPostedBy().getFirstName() + " " + jobModel.getPostedBy().getLastName());
		dto.getStartDate().setTimeInMillis(jobModel.getStartDate().getTime());
		dto.setWorkHours(jobModel.getWorkHours());
		dto.setEmailCommunication(jobModel.getEmailCommunication());
		dto.setChatCommunication(jobModel.getChatCommunication());
		dto.setStatus(jobModel.getStatus());
		if (jobModel.getBids() != null) {
			for (BidModel bm : jobModel.getBids()) {
				BidDTO bidDto = new BidDTO();
				if (bm.getStatus() == Status.JOB_OR_BID_RETRACTED) {
					continue;
				}
				prepareBidDTO(bidDto, bm);
				dto.getBids().add(bidDto);
			}
		}
		
		if (jobModel.getRatings() != null) {
			for (UserRatingModel userRatingModel : jobModel.getRatings()) {
				LOG.error("Searching for ratings and comments for user :"+jobModel.getPostedBy().getId());
				if(userRatingModel.getRatingForUser().getId().equals(jobModel.getPostedBy().getId())){
					dto.setComments(userRatingModel.getComment());
					dto.setRating(userRatingModel.getRating());
				}
			}
		}

		Iterator<JobDetailModel> jdmIterator = jobModel.getJobDetails().iterator();
		while (jdmIterator.hasNext()) {
			JobDetailModel jdm = jdmIterator.next();
			if (jdm.getArtifactType() != null) {
				switch (jdm.getArtifactType()) {
					case DAOConstants.ARTIFACT_IMAGE:
						dto.getImages().add(jdm.getArtifactUrl());
						break;
					case DAOConstants.ARTIFACT_AUDIO:
						dto.getAudios().add(jdm.getArtifactUrl());
						break;
					case DAOConstants.ARTIFACT_VIDEO:
						dto.getVideos().add(jdm.getArtifactUrl());
						break;
					default:
						LOG.error("***********JobArtifact has an unknown type, DATA INCONSISTENCY*********(jobId: {}, jobDetailId:{}, type: {})", jobModel.getId(), jdm.getId(), jdm.getArtifactType());
				}
			} else {
				LOG.error("***********JobArtifact doesn't have a type, DATA INCONSISTENCY*********(jobId: {}, jobDetailId:{})", jobModel.getId(), jdm.getId());
			}
		}
// LOG.debug("Method [prepareJobDTO]: Called");

	}

	protected void prepareBidDTO(BidDTO bidDto, BidModel bidModel) {
// LOG.debug("Method [prepareBidDTO]: Called");
		bidDto.setAmount(bidModel.getAmount());
		bidDto.setComment(bidModel.getComment());
		bidDto.setCurrency(bidModel.getCurrency());
		bidDto.setBidderId(bidModel.getBidder().getId());
		bidDto.setBidderName(bidModel.getBidder().getFirstName() + " " + bidModel.getBidder().getLastName());
		bidDto.setBidderProfilePic(bidModel.getBidder().getPhotoUrl());
		bidDto.setBidId(bidModel.getId());
		bidDto.setJobId(bidModel.getJob().getId());
		bidDto.setStatus(bidModel.getStatus());
		bidDto.setCreatedDate(bidModel.getCreatedOn());
// LOG.debug("Method [prepareBidDTO]: Returning.");
	}

	/**
	 * creates a new verification key
	 * 
	 * @param maxLength
	 * @param userEmail
	 * @return
	 */
	protected VerificationKeyModel createVerificationKey(int maxLength, String userEmail) {
		LOG.debug("Method [createVerificationKey]: Called, maxLength: {}", maxLength);

		VerificationKeyModel vkm = new VerificationKeyModel();
		vkm.setEmail(userEmail);
		vkm.setKey(UUID.randomUUID().toString().substring(0, maxLength).replaceAll("-", "d"));
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DATE, 1);
		vkm.setExpiryDate(calendar.getTime());

		LOG.debug("Method [createVerificationKey]: Returning, {}", vkm);
		return vkm;

	}

	private void notifyUserOfEvent(final NotificationEvent event, final Map<String, Object> params) {
		LOG.debug("Method [notifyUserOfEvent]: Called for Event {}", event);
		jmsTemplate.send(new DabbleMessageCreator(event, params));
		LOG.debug("Method [notifyUserOfEvent]: Returning.");
	}

	protected void prepareUserAddressDTO(AddressDTO address, UserAddressModel uam) {
// LOG.debug("Method [prepareUserAddressDTO]: Called");

		address.setId(uam.getId());
		address.setAddressLine1(uam.getAddressLine1());
		address.setAddressLine2(uam.getAddressLine2());
		address.setCity(uam.getCity());
		address.setState(uam.getState());
		address.setZip(uam.getZip());
		address.setCountry(uam.getCountry());
		address.setStatus(uam.getStatus());

// LOG.debug("Method [prepareUserAddressDTO]: Returning.");
	}

	protected void prepareUserDTO(UserDTO result, UserModel userModel) {
// LOG.debug("Method [prepareUserDTO]: Called");

		result.setFirstName(userModel.getFirstName());
		result.setMiddleName(userModel.getMiddleName());
		result.setLastName(userModel.getLastName());
		result.setDob(userModel.getDob());
		result.setTimezone(userModel.getTimezone());
		result.setAboutMe(userModel.getAboutMe());
		result.setAge(userModel.getAge());
		result.setPhotoUrl(userModel.getPhotoUrl());
		result.setUserId(userModel.getId());
		result.setUserTypeCode(userModel.getUserTypeCode());
		result.setGender(userModel.getGender());
		result.setStatus(userModel.getStatus());
		result.setUserReference(userModel.getBraintreeReference());

// LOG.debug("Method [prepareUserDTO]: Returning.");

	}

	protected void prepareUserModel(UserModel userModel, UserDTO userDto) {
		LOG.debug("Method [prepareUserModel]: Called");

		userModel.setAge(userDto.getAge());
		userModel.setDob(userDto.getDob());
		userModel.setTimezone(userDto.getTimezone());
		userModel.setFirstName(userDto.getFirstName());
		userModel.setLastName(userDto.getLastName());
		userModel.setAboutMe(userDto.getAboutMe());
		userModel.setGender(userDto.getGender());
		if (userDto.getUserTypeCode() == null) {
			throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, "UserType value is mandatory");
		}
		userModel.setUserTypeCode(userDto.getUserTypeCode());

		LOG.debug("Method [prepareUserModel]: Returning.");

	}

	protected void prepareUserAddressModel(UserAddressModel userAddressModel, AddressDTO userAddressDto) {
		LOG.debug("Method [prepareUserAddressModel]: Called");

		userAddressModel.setAddressLine1(userAddressDto.getAddressLine1());
		userAddressModel.setAddressLine2(userAddressDto.getAddressLine2());
		userAddressModel.setState(userAddressDto.getState());
		userAddressModel.setCity(userAddressDto.getCity());
		userAddressModel.setZip(userAddressDto.getZip());
		userAddressModel.setCountry(userAddressDto.getCountry());

		LOG.debug("Method [prepareUserAddressModel]: Returning.");

	}

	protected Date asTimeZone(Calendar inst, String timezone) {
// Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timezone));
// c.setTimeInMillis(inst.getTimeInMillis());
		return inst.getTime();
	}

	protected void prepareJobModelFromJobDto(JobModel jobModel, JobDTO jobDto) {
		LOG.debug("Method [prepareJobModelFromJobDto]: Called");

		jobModel.setAmount(jobDto.getAmount());
		jobModel.setCurrency(jobDto.getCurrency());
		jobModel.setJobTitle(jobDto.getJobTitle());
		jobModel.setDescription(jobDto.getDescription());
		jobModel.setEvent(jobDto.getEvent());
		jobModel.setEndDate(asTimeZone(jobDto.getEndDate(), jobDto.getTimezone()));
		jobModel.setNumberOfVolunteers(jobDto.getNumberOfVolunteers());
		jobModel.setVolunteers(jobDto.isVolunteers());
		jobModel.setEmailCommunication(jobDto.isEmailCommunication());
		jobModel.setChatCommunication(jobDto.isChatCommunication());
		Set<JobDetailModel> jobDetails = new LinkedHashSet<>();
		for (String artifact : jobDto.getAudios()) {
			JobDetailModel jdm = new JobDetailModel();
			jdm.setArtifactUrl(artifact);
			jdm.setStatus(Status.ACTIVE);
			jdm.setJob(jobModel);
			jdm.setArtifactType(DAOConstants.ARTIFACT_AUDIO);
			jobDetails.add(jdm);
		}
		for (String artifact : jobDto.getImages()) {
			JobDetailModel jdm = new JobDetailModel();
			jdm.setArtifactUrl(artifact);
			jdm.setStatus(Status.ACTIVE);
			jdm.setJob(jobModel);
			jdm.setArtifactType(DAOConstants.ARTIFACT_IMAGE);
			jobDetails.add(jdm);
		}
		for (String artifact : jobDto.getVideos()) {
			JobDetailModel jdm = new JobDetailModel();
			jdm.setArtifactUrl(artifact);
			jdm.setStatus(Status.ACTIVE);
			jdm.setJob(jobModel);
			jdm.setArtifactType(DAOConstants.ARTIFACT_VIDEO);
			jobDetails.add(jdm);
		}
		jobModel.setJobDetails(jobDetails);
		jobModel.setLat(jobDto.getLat());
		jobModel.setLng(jobDto.getLng());
		jobModel.setStartDate(asTimeZone(jobDto.getStartDate(), jobDto.getTimezone()));
		jobModel.setWorkHours(jobDto.getWorkHours());
		jobDto.setLocationPoint(new Point(jobDto.getLat(), jobDto.getLng()));
		LOG.debug("Method [prepareJobModelFromJobDto]: Returning.");

	}

	protected void verifyUser(UserModel userModel) {
		LOG.debug("Method [verifyUser]: Called");
		if (userModel == null) {
			throw new DabbleException(ResponseStatus.UNAVAILABLE, ResponseMessage.USER_UNAVAILABLE);
		}
		if (userModel.getStatus() > 0) {
			throw new DabbleException(ResponseStatus.INACTIVE, ResponseMessage.USER_INACTIVE);
		}

		LOG.debug("Method [verifyUser]: Returning.");
	}

	protected void verifyJob(JobModel jobModel) {
		LOG.debug("Method [verifyJob]: Called");
		if (jobModel == null) {
			throw new DabbleException(ResponseStatus.UNAVAILABLE, "Job not found");
		}
		if (jobModel.getStatus() == Status.JOB_COMPLETED) {
			throw new DabbleException(ResponseStatus.UNAVAILABLE, "Job has been completed already");
		}

		LOG.debug("Method [verifyJob]: Returning.");
	}

	/**
	 * @return the verificationLink
	 */
	public String getVerificationLink() {
		return verificationLink;
	}

	/**
	 * @param verificationLink
	 *            the verificationLink to set
	 */
	public void setVerificationLink(String verificationLink) {
		this.verificationLink = verificationLink;
	}

	public void prepareSubscriptionDTO(SubscriptionDTO dto, UserSubscriptionModel userSubscriptionModel) {
		LOG.debug("Method [prepareSubscriptionDTO]: Called");

		SubscriptionModel model = userSubscriptionModel.getSubscription();
		dto.setBidderExcempt(model.isBidderExcempt());
		dto.setDescription(model.getDescription());
		dto.setDurationInDays(model.getDurationInDays());
		dto.setStartDate(userSubscriptionModel.getSubscriptionDate());
		dto.setEndDate(userSubscriptionModel.getExpiryDate());
		dto.setFreeTier(model.getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_FREE);
		dto.setMonthlyTier(model.getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_MONTHLY);
		dto.setPayAsYouGoTier(model.getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_PAY_AS_YOU_GO);
		dto.setMaxBidCount(model.getMaxBidCount());
		dto.setMaxJobCount(model.getMaxJobCount());
		dto.setName(model.getName());
		dto.setAmountForSubscription(model.getAmountForSubscription());
		dto.setAmountForCreateEvent(model.getAmountForCreateEvent());
		dto.setAmountForCreateJob(model.getAmountForCreateJob());
		dto.setStatus(model.getStatus());
		dto.setSubscriptionId(model.getId());
		dto.setUserTypeCode(model.getUserTypeCode());

		LOG.debug("Method [prepareSubscriptionDTO]: Returning.");
	}

	public void prepareSubscriptionDTO(SubscriptionDTO dto, SubscriptionModel model) {
		LOG.debug("Method [prepareSubscriptionDTO]: Called");
		dto.setBidderExcempt(model.isBidderExcempt());
		dto.setDescription(model.getDescription());
		dto.setDurationInDays(model.getDurationInDays());
		dto.setFreeTier(model.getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_FREE);
		dto.setMonthlyTier(model.getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_MONTHLY);
		dto.setPayAsYouGoTier(model.getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_PAY_AS_YOU_GO);
		dto.setMaxBidCount(model.getMaxBidCount());
		dto.setMaxJobCount(model.getMaxJobCount());
		dto.setName(model.getName());
		dto.setAmountForSubscription(model.getAmountForSubscription());
		dto.setAmountForCreateEvent(model.getAmountForCreateEvent());
		dto.setAmountForCreateJob(model.getAmountForCreateJob());
		dto.setStatus(model.getStatus());
		dto.setSubscriptionId(model.getId());
		dto.setUserTypeCode(model.getUserTypeCode());

		LOG.debug("Method [prepareSubscriptionDTO]: Returning.");
	}

}
