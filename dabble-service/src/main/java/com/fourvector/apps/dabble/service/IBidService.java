/**
 * 
 */
package com.fourvector.apps.dabble.service;

import java.util.List;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fourvector.apps.dabble.common.dto.BidDTO;
import com.fourvector.apps.dabble.common.dto.JobDTO;

/**
 * @author Anantha.Sharma
 */
public interface IBidService {

	/**
	 * places a new Bid.
	 * 
	 * @param bidderId
	 * @param comment
	 * @param jobId
	 * @param amount
	 * @param currency
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	Integer placeBid(Integer bidderId, String comment, Integer jobId, Double amount, String currency);

	/**
	 * retracts a bid
	 * 
	 * @param bidderId
	 * @param bidId
	 * @param comment
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	BidDTO retractBid(Integer bidId, Integer bidderId, String comment);

	/**
	 * accepts a bid.
	 * 
	 * @param userId
	 * @param bidId
	 * @param paymentMethodNonce
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	String acceptBid(Integer userId, Integer bidId, String paymentMethodNonce);

	/**
	 * returns a list of all bids created by a given user.
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	List<JobDTO> getMyBids(Integer userId);

	/**
	 * returns all the bids placed for a particular job.
	 * 
	 * @param jobId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	List<BidDTO> getBidsForJob(Integer jobId);

}
