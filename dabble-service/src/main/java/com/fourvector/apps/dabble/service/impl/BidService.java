/**
 * 
 */
package com.fourvector.apps.dabble.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fourvector.apps.dabble.common.dto.BidDTO;
import com.fourvector.apps.dabble.common.dto.JobDTO;
import com.fourvector.apps.dabble.common.dto.UserDetailsDTO;
import com.fourvector.apps.dabble.dao.IJobDAO;
import com.fourvector.apps.dabble.dao.config.DAOConstants;
import com.fourvector.apps.dabble.dao.config.Status;
import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.model.job.BidModel;
import com.fourvector.apps.dabble.model.job.JobModel;
import com.fourvector.apps.dabble.model.user.UserModel;
import com.fourvector.apps.dabble.model.user.UserSubscriptionModel;
import com.fourvector.apps.dabble.service.IBidService;
import com.fourvector.apps.dabble.service.IPaymentService;
import com.fourvector.apps.dabble.service.IUserService;
import com.fourvector.apps.dabble.service.config.NotificationEvent;
import com.fourvector.apps.dabble.service.config.ParameterKeys;
import com.fourvector.apps.dabble.service.config.ResponseStatus;

/**
 * @author Anantha.Sharma
 */
public class BidService extends BaseService implements IBidService {
	private static final org.slf4j.Logger	LOG	= org.slf4j.LoggerFactory.getLogger(BidService.class);
	private IJobDAO							jobDAO;
	private IPaymentService					paymentService;
	private IUserService					userService;

	/**
	 * 
	 */
	public BidService() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BidDTO> getBidsForJob(Integer jobId) {
		LOG.debug("Method [getBidsForJob]: Called");

		List<BidDTO> result = new LinkedList<>();

		List<BidModel> bids = (List<BidModel>) jobDAO.runQuery("from " + BidModel.class.getName() + " p where p.status<>? and p.job.id=? order by p.createdOn desc", Status.INACTIVE, jobId);
		for (BidModel bidModel : bids) {
			BidDTO bidDto = new BidDTO();
			prepareBidDTO(bidDto, bidModel);
			hyderateBid(bidDto);
			result.add(bidDto);
		}

		LOG.debug("Method [getBidsForJob]: Returning {}", result);
		return result;
	}

	private void hyderateBid(BidDTO bidDto) {
		UserDetailsDTO dto = userService.getUserDetails(bidDto.getBidderId());
		bidDto.setUserDetails(dto);
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.service.IJobService#placeBid(java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.Double, java.lang.String)
	 */
	@Override
	public Integer placeBid(final Integer bidderId, final String comment, final Integer jobId, final Double amount, final String currency) {
		LOG.trace("Method [placeBid]: Called ");
		Serializable result = null;
		JobModel jobModel = (JobModel) jobDAO.findById(JobModel.class, jobId);
		verifyJob(jobModel);
		BidModel bidModel = new BidModel();
		UserModel bidder = (UserModel) jobDAO.findById(UserModel.class, bidderId);
		verifyUser(bidder);
		bidModel.setBidder(bidder);
		bidModel.setAmount(amount);
		bidModel.setCurrency(currency);
		bidModel.setComment(comment);
		bidModel.setStatus(Status.ACTIVE);
		bidModel.setJob(jobModel);
		String hql = "update " + BidModel.class.getName() + " p set p.status=? where p.bidder.id=? and p.job.id=?";
		int count = jobDAO.runUpdateQuery(hql, Status.INACTIVE, bidderId, jobId);
		LOG.info("{} bids have been disabled.", count);

		result = jobDAO.saveObject(bidModel);

		LOG.info("added new bid{} for job{}", result, jobId);
		Map<String, Object> params = new HashMap<>();

		params.put(ParameterKeys.KEY_BID_ID, result);
		params.put(ParameterKeys.KEY_JOB_ID, jobId);
		params.put(ParameterKeys.KEY_JOB_POSTER, jobModel.getPostedBy().getFirstName() + " " + jobModel.getPostedBy().getLastName());
		params.put(ParameterKeys.KEY_JOB_TITLE, jobModel.getJobTitle());
		params.put(ParameterKeys.KEY_BID_VALUE, bidModel.getAmount());
		params.put(ParameterKeys.KEY_BID_CURRENCY, bidModel.getCurrency());
		
		//TODO remove hard coding
		if (jobModel.isEvent().equals("1")) {
			// this is an event.
			sendEmailNotification(NotificationEvent.EVENT_BID_PLACED_BIDDER, params, jobDAO, bidder.getId());
			if (bidModel.getJob().getEmailCommunication()) {
				params.remove(ParameterKeys.KEY_TARGET);
				sendEmailNotification(NotificationEvent.EVENT_BID_PLACED_SELLER, params, jobDAO, jobModel.getPostedBy().getId());
			}
		} else {
			sendEmailNotification(NotificationEvent.BID_PLACED_BIDDER, params, jobDAO, bidder.getId());
			params.remove(ParameterKeys.KEY_TARGET);
			sendEmailNotification(NotificationEvent.BID_PLACED_SELLER, params, jobDAO, jobModel.getPostedBy().getId());
		}
		LOG.trace("Method [placeBid]: Returning " + result);
		return (Integer) result;

	}

	@Override
	public BidDTO retractBid(Integer bidId, Integer userId, String comment) {
		LOG.debug("Method [retractBid]: Called");
		BidDTO result = new BidDTO();
		BidModel bidModel = (BidModel) jobDAO.findById(BidModel.class, bidId);
		verifyBidModel(bidModel);
		Integer jobCreatorId = bidModel.getJob().getPostedBy().getId();
		//TODO remove the hard coding
		if (bidModel.getJob().isEvent().equals("1")) {
			// this is an event.
			// we can simply retract.
			bidModel.setStatus(Status.JOB_OR_BID_RETRACTED);
			LOG.info("One of the bids#{} for the job#{} has been retracted, which makes the event available in the market (if it wasnt already available it will be now)", bidId, bidModel.getJob().getId());
			bidModel.getJob().setStatus(Status.ACTIVE);
			jobDAO.updateObject(bidModel.getJob());
		}

		if (bidModel.getStatus() == Status.JOB_OR_BID_ACCEPTED) {
			// this bid has been accepted, so require job creators permission to retract.
			LOG.info("bid#{} is accepted; waiting for job creator's approval", bidId);
			bidModel.setStatus(Status.JOB_OR_BID_RETRACTION_REQUESTED);
			sendEmailNotification(NotificationEvent.RETRACT_BID_REQUESTED, null, jobDAO, jobCreatorId);
			sendPushNotification(NotificationEvent.RETRACT_BID_REQUESTED, null, jobDAO, jobCreatorId);
		} else if (bidModel.getStatus() == Status.JOB_OR_BID_RETRACTION_REQUESTED) {
			// this bid has been accepted, so require job creators permission to retract.
			LOG.info("bid#{} retraction approved by user#{}", bidId, jobCreatorId);
			if (userId.equals(jobCreatorId)) {
				approveRetraction(bidId, userId);
			}
		} else if (bidModel.getStatus() != Status.JOB_OR_BID_ACCEPTED && bidModel.getStatus() != Status.JOB_OR_BID_RETRACTION_REQUESTED) {
			// this bid is neither accepted nor is it waiting for approval.
			// we can just retract.placeBid
			LOG.info("bid#{} is neither accepted nor is it waiting for approval, retracting...", bidId);
			bidModel.setStatus(Status.JOB_OR_BID_RETRACTED);
			// the requester here is the bidder.
			Map<String, Object> params = new HashMap<>();
			params.put(ParameterKeys.KEY_BID_ID, bidId);
			params.put(ParameterKeys.KEY_JOB_ID, bidModel.getJob().getId());
			params.put(ParameterKeys.KEY_JOB_TITLE, bidModel.getJob().getJobTitle());
			params.put(ParameterKeys.KEY_BID_VALUE, bidModel.getAmount());
			params.put(ParameterKeys.KEY_BID_CURRENCY, bidModel.getCurrency());

			sendEmailNotification(NotificationEvent.BID_RETRACTED_BIDDER, params, jobDAO, bidModel.getBidder().getId());
			if (bidModel.getJob().getEmailCommunication()) {
				params.remove(ParameterKeys.KEY_TARGET);
				sendEmailNotification(NotificationEvent.BID_RETRACTED_SELLER, params, jobDAO, bidModel.getJob().getPostedBy().getId());
			}
			params.remove(ParameterKeys.KEY_TARGET);
			sendPushNotification(NotificationEvent.BID_RETRACTED_BIDDER, params, jobDAO, bidModel.getBidder().getId());
			params.remove(ParameterKeys.KEY_TARGET);
			sendPushNotification(NotificationEvent.BID_RETRACTED_SELLER, params, jobDAO, bidModel.getJob().getPostedBy().getId());
		}
		jobDAO.updateObject(bidModel);
		prepareBidDTO(result, bidModel);
		LOG.debug("Method [retractBid]: Returning {}", result);
		return result;
	}

	/**
	 * can be called by bidder or jobCreator.
	 * 
	 * @param bidId
	 * @param userId
	 */
	public void approveRetraction(Integer bidId, Integer userId) {
		LOG.trace("Method [approveRetraction]: Called ");
		BidModel bidModel = (BidModel) jobDAO.findById(BidModel.class, bidId);
		verifyBidModel(bidModel);
		if (bidModel.getStatus() == Status.JOB_OR_BID_RETRACTED) {
			// looks like a duplicate request.
			LOG.info("Bid already retracted, no need for any action, this perhaps is a duplicate request.");
			throw new DabbleException(ResponseStatus.DUPLICATE_REQUEST, "Bid retraction has already been approved.");
		}
		JobModel jobModel = bidModel.getJob();
		if (bidModel.getStatus() == Status.JOB_OR_BID_RETRACTION_VERIFICATION_PENDING) {
			if (jobModel.getStatus().equals(Status.JOB_OR_BID_RETRACTION_VERIFICATION_PENDING)) {
				// this is a request which started with the job creator and ended up here.. lets allow the job to be retracted as well.
				bidModel.setStatus(Status.JOB_OR_BID_RETRACTED);
				jobModel.setStatus(Status.JOB_OR_BID_RETRACTED);
				jobDAO.updateObject(bidModel);
				jobDAO.updateObject(jobModel);
				Map<String, Object> params = new HashMap<>();
				params.put(ParameterKeys.KEY_JOB_ID, jobModel.getId());
				if (bidModel.getJob().getEmailCommunication()) {
					params.remove(ParameterKeys.KEY_TARGET);
					sendEmailNotification(NotificationEvent.RETRACT_JOB_SELLER, params, jobDAO, jobModel.getCreatedBy().getId());
				}
				params.remove(ParameterKeys.KEY_TARGET);
				sendPushNotification(NotificationEvent.RETRACT_JOB_SELLER, params, jobDAO, jobModel.getCreatedBy().getId());
			}
			// the retraction request is pending; now, we have received an approval request.
			// lets mark this bid as retracted.
			bidModel.setStatus(Status.JOB_OR_BID_RETRACTED);
			jobDAO.updateObject(bidModel);
			Map<String, Object> params = new HashMap<>();
			params.put(ParameterKeys.KEY_BID_ID, bidModel.getId());
			sendEmailNotification(NotificationEvent.RETRACT_BID, params, jobDAO, bidModel.getBidder().getId());
			params.remove(ParameterKeys.KEY_TARGET);
			sendPushNotification(NotificationEvent.RETRACT_BID, params, jobDAO, bidModel.getBidder().getId());
		}
		LOG.trace("Method [approveRetraction]: Returning.");
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.service.IJobService#acceptBid(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public String acceptBid(Integer userId, Integer bidId, String paymentMethodNonce) {
		LOG.trace("Method [acceptBid]: Called ");
		String result = null;
		BidModel bidModel = (BidModel) jobDAO.findById(BidModel.class, bidId);
		verifyBidModel(bidModel);
		JobModel jobModel = bidModel.getJob();
		if (bidModel.getStatus() == Status.JOB_OR_BID_ACCEPTED) {
			// looks like a duplicate request.
			LOG.info("Bid already accepted, no need for any action, this perhaps is a duplicate request.");
			throw new DabbleException(ResponseStatus.DUPLICATE_REQUEST, "Bid has already been accepted");
		}
		if (bidModel.getStatus() == Status.JOB_OR_BID_RETRACTED) {
			// looks like a duplicate request.
			LOG.info("Bid already retracted, no need for any action, this perhaps is a duplicate request.");
			throw new DabbleException(ResponseStatus.UNAVAILABLE, "Retracted bid cannot be accepted.");
		}
		double amount = 0d;
		Integer bidderId = bidModel.getBidder().getId();
		Integer jobId = jobModel.getId();
		UserSubscriptionModel usm = jobDAO.getActiveSubscription(bidderId);
		boolean isPayAsYouGo = usm.getSubscription().getSubscriptionType() == DAOConstants.SUBSCRIPTION_TYPE_PAY_AS_YOU_GO;
		UserSubscriptionModel usm_jobCreator = jobDAO.getActiveSubscription(jobModel.getPostedBy().getId());
		boolean isBidderExcempt = usm_jobCreator.getSubscription().isBidderExcempt();
		if (!isBidderExcempt) {
			if (isPayAsYouGo) {
				//TODO Remove Hard coding
				amount = jobModel.isEvent().equals("1") ? usm.getSubscription().getAmountForAcceptBid() : usm.getSubscription().getAmountForAcceptEvent();
				if (!jobModel.isVolunteers()) {
					LOG.info("Volunteer work, not charging customer");
					amount = 0d;
				}
				paymentService.performSale(bidderId, jobId, amount, paymentMethodNonce);
			}
			if (isPayAsYouGo) {
				//TODO Remove Hard coding
				amount = jobModel.isEvent().equals("1") ? usm.getSubscription().getAmountForAcceptBid() : usm.getSubscription().getAmountForAcceptEvent();
				if (!jobModel.isVolunteers()) {
					LOG.info("Volunteer work, not charging customer");
					amount = 0d;
				}
				paymentService.performSale(bidderId, jobId, amount, paymentMethodNonce);
			}

		}
		bidModel.setStatus(Status.JOB_OR_BID_ACCEPTED);

		boolean markJobAccepted = true;
		//TODO Remove Hard coding
		if (jobModel.getIsEvent().equals("1") || jobModel.isVolunteers() == true) {
			String query = "select count(p) from " + BidModel.class.getName() + " p where  p.job.id=? and p.status=?";
			Long acceptedBids = (Long) jobDAO.runSingleResponseQuery(query, jobId, Status.JOB_OR_BID_ACCEPTED);
			if (acceptedBids.intValue() != jobModel.getNumberOfVolunteers()) {
				markJobAccepted = false;
			}
		}
		if (markJobAccepted) {
			jobModel.setStatus(Status.JOB_OR_BID_ACCEPTED);
		}
		jobDAO.updateObject(bidModel);
		jobDAO.updateObject(bidModel.getJob()); // this needs to be updated to accommodate the pricing model

		Map<String, Object> params = new HashMap<>();
		params.put(ParameterKeys.KEY_TRANSACTION_ID, result);
		params.put(ParameterKeys.KEY_JOB_ID, bidModel.getJob().getId());
		params.put(ParameterKeys.KEY_JOB_TITLE, bidModel.getJob().getJobTitle());
		params.put(ParameterKeys.KEY_JOB_POSTER, jobModel.getPostedBy().getFirstName() + " " + jobModel.getPostedBy().getLastName());
		params.put(ParameterKeys.KEY_BID_ID, bidId);
		params.put(ParameterKeys.KEY_BID_VALUE, bidModel.getAmount());
		params.put(ParameterKeys.KEY_BID_CURRENCY, bidModel.getCurrency());
		params.put(ParameterKeys.KEY_SUBSCRIPTION_AMOUNT, amount);
		//TODO Remove Hard coding
		if (jobModel.isEvent().equals("1")) {
			// since this is an event, the bidder needs to pay.
			params.remove(ParameterKeys.KEY_TARGET);
			if (bidModel.getJob().getEmailCommunication()) {
				sendEmailNotification(NotificationEvent.EVENT_ACCEPT_BID_BIDDER, params, jobDAO, bidModel.getBidder().getId());
			}
			params.remove(ParameterKeys.KEY_TARGET);
			sendPushNotification(NotificationEvent.EVENT_ACCEPT_BID_BIDDER, params, jobDAO, bidModel.getBidder().getId());
		} else {
			if (bidModel.getJob().getEmailCommunication()) {
				params.remove(ParameterKeys.KEY_TARGET);
				sendEmailNotification(NotificationEvent.ACCEPT_BID_SELLER, params, jobDAO, bidModel.getJob().getPostedBy().getId());
			}
			params.remove(ParameterKeys.KEY_TARGET);
			sendEmailNotification(NotificationEvent.ACCEPT_BID_BIDDER, params, jobDAO, bidModel.getBidder().getId());
		}
		LOG.trace("Method [acceptBid]: Returning transaction Reference: {}", result);
		return result;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JobDTO> getMyBids(Integer userId) {
		LOG.debug("Method [getMyBids]: Called");
		List<JobDTO> result = new LinkedList<>();

		List<BidModel> bids = (List<BidModel>) jobDAO.runQuery("from " + BidModel.class.getName() + " p where p.bidder.id=? and p.status not in (?,?) and p.job.status<>? order by p.createdOn desc", userId, Status.INACTIVE, Status.JOB_OR_BID_RETRACTED, Status.JOB_OR_BID_RETRACTED);
		Set<JobModel> jobs = new LinkedHashSet<>();
		for (BidModel bidModel : bids) {
			jobs.add(bidModel.getJob());
		}
		List<JobModel> jobsList = new ArrayList<>(jobs);
		Collections.sort(jobsList, Collections.reverseOrder(new Comparator() {
		    public int compare(Object j1, Object j2) {
		        return ((JobModel)j1).getStartDate().compareTo(((JobModel)j2).getStartDate());
		}}));
		
		List<JobModel> sortedJobs = ServiceUtil.sortJobs(jobsList);
		
		for (JobModel jobModel : sortedJobs) {
			JobDTO dto = new JobDTO();
			prepareJobDTO(dto, jobModel);
			for (BidDTO bid : dto.getBids()) {
				if (bid != null) {
					hyderateBid(bid);
				}
			}
			result.add(dto);
		}
				
		int size = 0;
		size = ((result == null) ? 0 : result.size());
		LOG.debug("Method [getMyBids]: Returning {} entries", size);
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

	/**
	 * @return the userService
	 */
	public IUserService getUserService() {
		return userService;
	}

	/**
	 * @param userService
	 *            the userService to set
	 */
	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

}
