/**
 * 
 */
package com.fourvector.apps.dabble.service.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.springframework.util.Assert;

import com.fourvector.apps.dabble.common.dto.JobDTO;
import com.fourvector.apps.dabble.common.dto.RefundDTO;
import com.fourvector.apps.dabble.dao.IJobDAO;
import com.fourvector.apps.dabble.dao.config.DAOConstants;
import com.fourvector.apps.dabble.dao.config.Status;
import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.model.job.BidModel;
import com.fourvector.apps.dabble.model.job.JobDetailModel;
import com.fourvector.apps.dabble.model.job.JobModel;
import com.fourvector.apps.dabble.model.user.UserAddressModel;
import com.fourvector.apps.dabble.model.user.UserEmailModel;
import com.fourvector.apps.dabble.model.user.UserModel;
import com.fourvector.apps.dabble.model.user.UserPhoneModel;
import com.fourvector.apps.dabble.model.user.UserRatingModel;
import com.fourvector.apps.dabble.model.user.UserSubscriptionModel;
import com.fourvector.apps.dabble.service.IJobService;
import com.fourvector.apps.dabble.service.IPaymentService;
import com.fourvector.apps.dabble.service.config.NotificationEvent;
import com.fourvector.apps.dabble.service.config.ParameterKeys;
import com.fourvector.apps.dabble.service.config.ResponseMessage;
import com.fourvector.apps.dabble.service.config.ResponseStatus;

/**
 * @author Anantha.Sharma
 */
public class JobService extends BaseService implements IJobService {
	private static final org.slf4j.Logger	LOG	= org.slf4j.LoggerFactory.getLogger(JobService.class);
	private IJobDAO							jobDAO;
	private String							imageStoreLocation, videoStoreLocation, audioStoreLocation;
	private IPaymentService					paymentService;

	/**
	 * 
	 */
	public JobService() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.service.IJobService#createJob(com.fourvector.apps.dabble.model.job.JobModel)
	 */
	@Override
	public Integer createJob(final JobDTO jobDto, String nonce) {
		LOG.trace("Method [createJob]: Called ");
		JobModel jobModel = new JobModel();
		prepareJobModelFromJobDto(jobModel, jobDto);
		UserModel userModel = (UserModel) jobDAO.findById(UserModel.class, jobDto.getPosterId());
		if (userModel == null) {
			LOG.error("User#{}, not found", jobDto.getPosterId());
			throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, "Unknown User ID provided");
		}

		UserSubscriptionModel subscriptionModel = jobDAO.getActiveSubscription(userModel.getId());
		if (subscriptionModel == null) {
			LOG.info("User {} doens't have an active subscription at the moment.", userModel.getId());
			throw new DabbleException(ResponseStatus.SUBSCRIPTION_EXPIRED, "Job create count exceeded");
		}

		jobModel.setSubscriptionModel(subscriptionModel.getSubscription());
		jobModel.setPostedBy(userModel);

		String userAddressHQL = "from " + UserAddressModel.class.getName() + " p where p.user.id=? and p.status=0";
		String userEmailHQL = "from " + UserEmailModel.class.getName() + " p where p.user.id=? and p.status=0";
		String userPhoneHQL = "from " + UserPhoneModel.class.getName() + " p where p.user.id=? and p.status=0";

		//UserPhoneModel phoneModel = (UserPhoneModel) jobDAO.runSingleResponseQuery(userPhoneHQL, jobDto.getPosterId());
		//Assert.notNull(phoneModel, "Phone Model cannot be null");
		//jobModel.setPosterPhone(phoneModel);

		UserEmailModel emailModel = (UserEmailModel) jobDAO.runSingleResponseQuery(userEmailHQL, jobDto.getPosterId());
		Assert.notNull(emailModel, "Email Model cannot be null");
		jobModel.setPosterEmail(emailModel);

		//UserAddressModel addressModel = (UserAddressModel) jobDAO.runSingleResponseQuery(userAddressHQL, jobDto.getPosterId());
		//Assert.notNull(addressModel, "Address Model cannot be null");
		//jobModel.setPosterAddress(addressModel);
		jobModel.setStatus(Status.INACTIVE);
		LOG.debug("Recived date from device {}", jobDto.getCreatedDate());
		jobModel.setCreatedOn(jobDto.getCreatedDate());
		Integer id = (Integer) jobDAO.saveObject(jobModel);
		double amount = 0d;
		UserSubscriptionModel usm = jobDAO.getActiveSubscription(userModel.getId());
		NotificationEvent event = NotificationEvent.CREATE_JOB_SUCCESS;
		if (usm.getSubscription().getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_PAY_AS_YOU_GO) {
			// payment is necessary
			double amountForCreateJob = usm.getSubscription().getAmountForCreateJob();
			double amountForCreateEvent = usm.getSubscription().getAmountForCreateEvent();
			amount = amountForCreateJob;
			//TODO need to remove the hard coding after talking to Chandu
			if (jobDto.getEvent().equals("1")) {
				amount = amountForCreateEvent;
			}
			if (jobModel.isVolunteers()) {
				amount = 0d;
			}
			paymentService.performSale(userModel.getId(), id, amount, nonce);
			event = NotificationEvent.CREATE_JOB_SUCCESS;
		}
		jobModel.setStatus(Status.ACTIVE);
		jobDAO.updateObject(jobModel);
		prepareJobDTO(jobDto, jobModel);
		jobDto.setJobId(id);
		if (jobDto.isEmailCommunication()) {
			Map<String, Object> params = new HashMap<>();
			params.put(ParameterKeys.KEY_JOB_ID, id);
			params.put(ParameterKeys.KEY_USER_NAME, userModel.getFirstName());
			params.put(ParameterKeys.KEY_JOB_TITLE, jobModel.getJobTitle());
			params.put(ParameterKeys.KEY_SUBSCRIPTION_AMOUNT, amount);
			params.put(ParameterKeys.KEY_BID_VALUE, jobModel.getAmount());
			sendEmailNotification(event, params, jobDAO, userModel.getId());
		} else {
			LOG.info("User has disabled email notifications for activities related to this job.");
		}
		LOG.trace("Method [createJob]: Returning Id: {}", id);
		return id;
	}

	@Override
	public JobDTO editJob(Integer userId, Integer jobId, JobDTO jobDTO) {
		LOG.debug("Method [editJob]: Called");
		JobDTO result = new JobDTO();
		UserModel userModel = jobDAO.findById(UserModel.class, userId);
		verifyUser(userModel);
		JobModel jobModel = jobDAO.findById(JobModel.class, jobId);
		verifyJob(jobModel);

		if (jobModel.getPostedBy().getId().equals(userId)) {
			if (jobModel.getStatus() != Status.ACTIVE) {
				// we cannot update a job which isnt active
				// TODO: need to add feature to handle post bid accept condition
				throw new DabbleException(ResponseStatus.UNAUTHORISED_ACCESS, "Cannot change job in this status.");
			}

			// we are in the clear.
			jobModel.setJobTitle(jobDTO.getJobTitle());
			jobModel.setDescription(jobDTO.getDescription());
			jobModel.setAmount(jobDTO.getAmount());
			jobModel.setCurrency(jobDTO.getCurrency());
			jobModel.setEmailCommunication(jobDTO.isEmailCommunication());
			jobModel.setChatCommunication(jobDTO.isChatCommunication());
			if (jobDTO.getEndDate().getTimeInMillis() < System.currentTimeMillis()) {
// throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, "Cannot mark the job end date in the past.");
			}
			if (jobDTO.getStartDate().getTimeInMillis() < System.currentTimeMillis()) {
// throw new DabbleException(ResponseStatus.INCOMPLETE_DATA, "Cannot mark the job start date in the past.");
			}
//			jobModel.setEndDate(jobDTO.getEndDate().getTime());
			jobModel.setStartDate(jobDTO.getStartDate().getTime());
			jobModel.setLat(jobDTO.getLat());
			jobModel.setLng(jobDTO.getLng());
			jobModel.setWorkHours(jobDTO.getWorkHours());
			jobModel.setNumberOfVolunteers(jobDTO.getNumberOfVolunteers());
			jobDAO.updateObject(jobModel);
			prepareJobDTO(jobDTO, jobModel);
		} else {
			throw new DabbleException(ResponseStatus.UNAUTHORISED_ACCESS, "Only job poster can change the job.");
		}
		LOG.debug("Method [editJob]: Returning {}", result);
		return jobDTO;
	}

	@Override
	public void removeFiles(Integer jobId, String resourceId) {
		LOG.debug("Method [removeFiles]: Called");

		String hql = "from " + JobDetailModel.class.getName() + " p where p.job.id=? and p.artifactUrl=?";
		JobDetailModel jdm = (JobDetailModel) jobDAO.runSingleResponseQuery(hql, jobId, resourceId);

		if (jdm == null) {
			throw new DabbleException(ResponseStatus.UNAVAILABLE, "Resource not found");
		}
		jobDAO.deleteObject(jdm);
		LOG.debug("Method [removeFiles]: Returning.");
	}

	@Override
	public File getResource(Integer jobId, String resourceId) {
		LOG.debug("Method [getResource]: Called");

		String hql = "from " + JobDetailModel.class.getName() + " p where p.job.id=? and p.artifactUrl=?";
		JobDetailModel jdm = (JobDetailModel) jobDAO.runSingleResponseQuery(hql, jobId, resourceId);

		if (jdm == null) {
			throw new DabbleException(ResponseStatus.UNAVAILABLE, "Resource not found");
		}
		String fileName = null;
		switch (jdm.getArtifactType()) {
			case DAOConstants.ARTIFACT_AUDIO:
				fileName = audioStoreLocation + "/" + jdm.getArtifactUrl();
				break;
			case DAOConstants.ARTIFACT_VIDEO:
				fileName = videoStoreLocation + "/" + jdm.getArtifactUrl();
				break;
			case DAOConstants.ARTIFACT_IMAGE:
				fileName = imageStoreLocation + "/" + jdm.getArtifactUrl();
				break;
		}
		File file = new File(fileName);
		if (!file.exists()) {
			throw new DabbleException(ResponseStatus.UNAVAILABLE, "Resource not found");
		}
		LOG.debug("Method [getResource]: Returning Stream");

		return file;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.service.IJobService#updateJob(com.fourvector.apps.dabble.model.job.JobModel)
	 */
	@Override
	public RefundDTO updateJob(final Integer jobId, final JobDTO jobDto) {
		LOG.trace("Method [updateJob]: Called ");

		RefundDTO refundDto = new RefundDTO();
		JobModel jobModel = (JobModel) jobDAO.findById(JobModel.class, jobId);
		if (jobModel == null) {
			throw new DabbleException(ResponseStatus.UNAVAILABLE, "Job not found");
		}
		if (jobModel.getStatus() != 0) {
			throw new DabbleException(ResponseStatus.INACTIVE, "Job cannot be updated");
		}
		prepareJobModelFromJobDto(jobModel, jobDto);

		jobDAO.updateObject(jobModel);
		prepareJobDTO(jobDto, jobModel);
		jobDto.setJobId(jobId);
// mongoDAO.insertDTO(jobDto);
		LOG.trace("Method [updateJob]: Returning ");
		return refundDto;

	}

	@Override
	public JobDTO retractJob(Integer jobId, Integer userId) {
		LOG.debug("Method [retractJob]: Called");
		JobModel jobModel = (JobModel) jobDAO.findById(JobModel.class, jobId);
		verifyJob(jobModel);
		if (!jobModel.getPostedBy().getId().equals(userId)) {
			throw new DabbleException(ResponseStatus.UNAUTHORISED_ACCESS, ResponseMessage.UNAUTHORISED_ACCESS);
		}

		long jobStartDateTimeInMillis = jobModel.getStartDate().getTime();
		long nowTimeInMillis = System.currentTimeMillis();

		double durationInDays = ((double) (jobStartDateTimeInMillis - nowTimeInMillis)) / ((double) (24 * 60 * 60));
		BidModel acceptedBid = jobDAO.finAcceptedBidForJob(jobId);
		JobDTO result = new JobDTO();

// if (jobModel.getStatus() == Status.JOB_OR_BID_RETRACTION_REQUESTED) {
// LOG.info("Job or bid retraction request is still pending..");
// if (userId == acceptedBid.getBidder().getId()) {
// // the bidder has accepted.
// approveRetraction(jobId, userId);
// }
// }

		LOG.info("Method [retractJob]: DurationInDays: {}, isAcceptedBidNull?: {}", durationInDays, (acceptedBid == null));
		Map<String, Object> params = new HashMap<>();
		params.put(ParameterKeys.KEY_JOB_ID, jobId);
		params.put(ParameterKeys.KEY_BID_VALUE, jobModel.getAmount());
		params.put(ParameterKeys.KEY_JOB_TITLE, jobModel.getJobTitle());

		// no takers for this job.
		if (jobModel.getEmailCommunication()) {
			//TODO remove hard coding
			NotificationEvent event = jobModel.isEvent().equals("1") ? NotificationEvent.RETRACT_EVENT_SELLER : NotificationEvent.RETRACT_JOB_SELLER;
			sendEmailNotification(event, params, jobDAO, userId);
		}
		sendPushNotification(NotificationEvent.RETRACT_JOB_SELLER, params, jobDAO, userId);

		if (jobModel.getBids() != null) {
			//TODO remove hard coding
			NotificationEvent event = jobModel.isEvent().equals("1") ? NotificationEvent.RETRACT_EVENT_SELLER : NotificationEvent.RETRACT_JOB_SELLER;
			for (BidModel bidModel : jobModel.getBids()) {
				if (bidModel.getStatus() == Status.ACTIVE) {
					params.put(ParameterKeys.KEY_BID_ID, bidModel.getId());
					sendEmailNotification(event, params, jobDAO, bidModel.getBidder().getId());
				}
			}
		}

		// if (acceptedBid != null) {
// LOG.info("Method [retractJob]: Case 2: DurationInDays: {}, isAcceptedBidNull?: {}<-- this Supersedes duration, only BidAccepted status is considered.", durationInDays,
// (acceptedBid == null));
// // case 2: Await Bidder's response.
// jobModel.setStatus(Status.JOB_OR_BID_RETRACTION_VERIFICATION_PENDING);
// acceptedBid.setStatus(Status.JOB_OR_BID_RETRACTION_VERIFICATION_PENDING);
// jobDAO.updateObject(acceptedBid);
// Integer bidderId = acceptedBid.getBidder().getId();
// Map<String, Object> params = new HashMap<>();
// params.put(ParameterKeys.KEY_BID_ID, acceptedBid.getId());
// sendEmailNotification(NotificationEvent.RETRACT_JOB_BIDDER, params, jobDAO, bidderId);
// sendPushNotification(NotificationEvent.RETRACT_JOB_BIDDER, params, jobDAO, bidderId);
// }
		jobModel.setStatus(Status.JOB_OR_BID_RETRACTED);
		jobDAO.updateObject(jobModel);
		LOG.debug("Method [retractJob]: Returning {}", result);

		return result;
	}

// private void approveRetraction(Integer jobId, Integer userId) {
// LOG.trace("Method [approveRetraction]: Called ");
// JobModel jobModel = (JobModel) jobDAO.findById(JobModel.class, jobId);
// if (jobModel.getStatus() == Status.JOB_OR_BID_RETRACTED) {
// // looks like a duplicate request.
// LOG.info("Bid already retracted, no need for any action, this perhaps is a duplicate request.");
// throw new DabbleException(ResponseStatus.DUPLICATE_REQUEST, "Job retraction has already been approved.");
// }
// if (jobModel.getStatus() == Status.JOB_OR_BID_RETRACTION_VERIFICATION_PENDING) {
// // the retraction request is pending; now, we have received an approval request.
// // lets mark this job as retracted.
// jobModel.setStatus(Status.JOB_OR_BID_RETRACTED);
// if (jobModel.getEmailCommunication()) {
// sendEmailNotification(NotificationEvent.RETRACT_BID, null, jobDAO, jobModel.getCreatedBy().getId());
// }
// sendPushNotification(NotificationEvent.RETRACT_BID, null, jobDAO, jobModel.getCreatedBy().getId());
// jobDAO.updateObject(jobModel);
// } else {
// throw new DabbleException(ResponseStatus.UNAUTHORISED_ACCESS, "Bid retraction can only be approved by job creator.");
// }
// LOG.trace("Method [approveRetraction]: Returning.");
// }

	@SuppressWarnings("unchecked")
	@Override
	public List<JobDTO> getMyJobs(Integer userId) {
		LOG.debug("Method [getMyJobs]: Called");
		List<JobDTO> result = new LinkedList<>();
		String query = "from " + JobModel.class.getName() + " p where p.postedBy.id=? and p.status != 5 order by p.startDate desc";
		//String query = "from " + JobModel.class.getName() + " p where p.postedBy.id=? and endDate > CURDATE() order by p.startDate desc";
		List<JobModel> jobs = (List<JobModel>) jobDAO.runQuery(query, userId);// findJobs(userId, 0d, 0d, Integer.MAX_VALUE, null, true);
		System.out.println("Before Sorting");
		for(JobModel job : jobs){	
			System.out.println(job.getJobTitle()+":::"+job.getStartDate()+":"+job.getStartDate());
		}

		if(jobs != null && jobs.size() > 0){
			List<JobModel> sortedJobs = ServiceUtil.sortJobs(jobs);
			
			for (JobModel jobModel : sortedJobs) {
				JobDTO dto = new JobDTO();
				prepareJobDTO(dto, jobModel);
				result.add(dto);
			}
		}
		LOG.debug("Method [getMyJobs]: Returning {}", result);
		return result;
	}
	

	@Override
	public List<JobDTO> listJobs(Integer userId, Double lat, Double lng, Integer rangeInMeters, String searchText, String timezone) {
		LOG.debug("Method [listJobs]: Called");
		List<JobDTO> result = new LinkedList<>();

		timezone = timezone.equals("PDT") ? "PST" : timezone;
		timezone = timezone.equals("ET") ? "EST" : timezone;
		timezone = timezone.equals("MT") ? "MST" : timezone;
		timezone = timezone.equals("MDT") ? "MST" : timezone;
		timezone = timezone.equals("BST") ? "GMT" : timezone;
		Calendar todayInTz = convertJodaTimezone(new Date(), timezone);
		List<JobModel> jobs = (List<JobModel>) jobDAO.findJobs(userId, lat, lng, rangeInMeters, searchText, false);
		for (JobModel jobModel : jobs) {
			JobDTO dto = new JobDTO();
			Calendar inst = new GregorianCalendar();
			inst.setTime(jobModel.getEndDate());
			LOG.info("#{} - Comparing today ({}) with endDate({})", jobModel.getId(), todayInTz.getTime(), inst.getTime());
			if (inst.after(todayInTz)) {
				// if the end Date is in the future
				prepareJobDTO(dto, jobModel);
				result.add(dto);
			}
		}
		LOG.debug("Method [listJobs]: Returning {}", result);

		return result;
	}

	public static Calendar convertJodaTimezone(Date date, String destTz) {
		LocalDateTime ldate = LocalDateTime.fromDateFields(date);
		TimeZone tz2 = TimeZone.getTimeZone(destTz);
		tz2.useDaylightTime();
		DateTime srcDateTime = ldate.toDateTime();
		DateTime dstDateTime = srcDateTime.withZone(DateTimeZone.forTimeZone(tz2));
		return dstDateTime.toLocalDateTime().toDateTime().toGregorianCalendar();
	}

	public static Calendar getOffsetDate(String tzStr) {
		TimeZone tz = TimeZone.getTimeZone(tzStr);
		tz.useDaylightTime();
		tz.setID(tzStr);
		long time = System.currentTimeMillis();
		LOG.debug(tz.getDisplayName() + " / " + tz.getOffset(time));
		time = time + tz.getOffset(time);
		Calendar cal = new GregorianCalendar(tz);
		cal.setTimeInMillis(time);
		return cal;
	}

	@Override
	public void storeFeedbackFromBidder(Integer userId, Integer jobId, Integer commenteeId, String comment, Integer rating) {
		LOG.debug("Method [storeFeedbackFromBidder]: Called");
		storeFeedback(userId, jobId, commenteeId, comment, rating);
		JobModel job = (JobModel) jobDAO.findById(JobModel.class, jobId);
		verifyJob(job);
		if (job.getStatus() != Status.JOB_OR_BID_ACCEPTED) {
			throw new DabbleException(ResponseStatus.UNAUTHORISED_ACCESS, "Cannot mark a job without any accepted bid.");
		}

		job.setStatus(Status.JOB_COMPLETION_REQUESTED);
		jobDAO.updateObject(job);

		Map<String, Object> params = new HashMap<>();
		params.put(ParameterKeys.KEY_JOB_TITLE, job.getJobTitle());
		params.put(ParameterKeys.KEY_JOB_ID, job.getId());
		params.put(ParameterKeys.KEY_BID_VALUE, job.getAmount());
		if (job.getEmailCommunication()) {
			super.sendEmailNotification(NotificationEvent.JOB_COMPLETION_REQUESTED, params, jobDAO, commenteeId);
		}
		super.sendPushNotification(NotificationEvent.JOB_COMPLETION_REQUESTED, params, jobDAO, commenteeId);
		LOG.debug("Method [storeFeedbackFromBidder]: Returning.");
	}

	@Override
	public void storeFeedbackFromSeller(Integer userId, Integer jobId, Integer commenteeId, String comment, Integer rating) {
		LOG.debug("Method [storeFeedbackFromSeller]: Called");
		storeFeedback(userId, jobId, commenteeId, comment, rating);
		JobModel job = (JobModel) jobDAO.findById(JobModel.class, jobId);
		verifyJob(job);
		if (job.getStatus() != Status.JOB_COMPLETION_REQUESTED) {
			throw new DabbleException(ResponseStatus.UNAUTHORISED_ACCESS, "Cannot approve a job which isn't awaiting completion approval.");
		}
		job.setStatus(Status.JOB_COMPLETED);
		jobDAO.updateObject(job);
		Map<String, Object> params = new HashMap<>();
		params.put(ParameterKeys.KEY_JOB_TITLE, job.getJobTitle());
		params.put(ParameterKeys.KEY_JOB_TITLE, job.getJobTitle());
		params.put(ParameterKeys.KEY_JOB_ID, job.getId());
		params.put(ParameterKeys.KEY_BID_VALUE, job.getAmount());
		super.sendEmailNotification(NotificationEvent.JOB_COMPLETED, params, jobDAO, commenteeId);
		super.sendPushNotification(NotificationEvent.JOB_COMPLETED, params, jobDAO, commenteeId);
		LOG.debug("Method [storeFeedbackFromSeller]: Returning.");
	}

	protected void storeFeedback(Integer userId, Integer jobId, Integer commenteeId, String comment, Integer rating) {
		LOG.debug("Method [storeFeedback]: Called");
		UserRatingModel userRatingModel = new UserRatingModel();
		JobModel job = (JobModel) jobDAO.findById(JobModel.class, jobId);
		verifyJob(job);
		UserModel commenter = (UserModel) jobDAO.findById(UserModel.class, userId);
		verifyUser(commenter);
		UserModel commentee = (UserModel) jobDAO.findById(UserModel.class, commenteeId);
		verifyUser(commentee);
		userRatingModel.setComment(comment);
		userRatingModel.setJob(job);
		userRatingModel.setRating(rating);
		userRatingModel.setRatingByUser(commenter);
		userRatingModel.setRatingForUser(commentee);
		jobDAO.saveObject(userRatingModel);
		LOG.debug("Method [storeFeedback]: Returning.");

	}

	@Override
	public void addFiles(Integer jobId, JobDTO jobDto) {
		LOG.debug("Method [addFiles]: Called");

		JobModel jobModel = (JobModel) jobDAO.findById(JobModel.class, jobId);
		verifyJob(jobModel);
		if (jobDto.getImages() != null) {
			if (jobDto.getImages() != null) {
				for (String item : jobDto.getImages()) {
					JobDetailModel jdm = new JobDetailModel();

					jdm.setArtifactType(DAOConstants.ARTIFACT_IMAGE);
					jdm.setArtifactUrl(item);
					jdm.setJob(jobModel);
					jdm.setStatus(Status.ACTIVE);
					jobModel.getJobDetails().add(jdm);
				}
			}
			if (jobDto.getAudios() != null) {
				for (String item : jobDto.getAudios()) {
					JobDetailModel jdm = new JobDetailModel();

					jdm.setArtifactType(DAOConstants.ARTIFACT_AUDIO);
					jdm.setArtifactUrl(item);
					jdm.setJob(jobModel);
					jdm.setStatus(Status.ACTIVE);
					jobModel.getJobDetails().add(jdm);
				}
			}
			if (jobDto.getVideos() != null) {
				for (String item : jobDto.getVideos()) {
					JobDetailModel jdm = new JobDetailModel();

					jdm.setArtifactType(DAOConstants.ARTIFACT_VIDEO);
					jdm.setArtifactUrl(item);
					jdm.setJob(jobModel);
					jdm.setStatus(Status.ACTIVE);
					jobModel.getJobDetails().add(jdm);
				}
			}
		}
		jobDAO.updateObject(jobModel);
		LOG.debug("Method [addFiles]: Returning.");
	}

	@Override
	public String saveVideoFile(InputStream inputStream, String originalFilename) {
		LOG.debug("Method [saveVideoFile]: Called");
		String result = storeFile(inputStream, originalFilename, videoStoreLocation);
		LOG.debug("Method [saveVideoFile]: Returning {}", result);
		return result;
	}

	@Override
	public String saveAudioFile(InputStream inputStream, String originalFilename) {
		LOG.debug("Method [saveAudioFile]: Called");
		String result = storeFile(inputStream, originalFilename, audioStoreLocation);
		LOG.debug("Method [saveAudioFile]: Returning {}", result);
		return result;
	}

	@Override
	public String saveImageFile(InputStream inputStream, String originalFilename) {
		LOG.debug("Method [saveImageFile]: Called");
		String result = storeFile(inputStream, originalFilename, imageStoreLocation);
		LOG.debug("Method [saveImageFile]: Returning {}", result);
		return result;
	}

	/**
	 * @return the jobDAO
	 */
	public IJobDAO getJobDAO() {
		return jobDAO;
	}

	/**
	 * @param jobDAO
	 *            the jobDAO to set
	 */
	public void setJobDAO(IJobDAO jobDAO) {
		this.jobDAO = jobDAO;
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
	 * @return the videoStoreLocation
	 */
	public String getVideoStoreLocation() {
		return videoStoreLocation;
	}

	/**
	 * @param videoStoreLocation
	 *            the videoStoreLocation to set
	 */
	public void setVideoStoreLocation(String videoStoreLocation) {
		this.videoStoreLocation = videoStoreLocation;
	}

	/**
	 * @return the audioStoreLocation
	 */
	public String getAudioStoreLocation() {
		return audioStoreLocation;
	}

	/**
	 * @param audioStoreLocation
	 *            the audioStoreLocation to set
	 */
	public void setAudioStoreLocation(String audioStoreLocation) {
		this.audioStoreLocation = audioStoreLocation;
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
