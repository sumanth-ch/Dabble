/**
 * 
 */
package com.fourvector.apps.dabble.web.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fourvector.apps.dabble.common.dto.BidDTO;
import com.fourvector.apps.dabble.common.dto.JobDTO;
import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.service.IBidService;
import com.fourvector.apps.dabble.service.config.ResponseMessage;
import com.fourvector.apps.dabble.service.config.ResponseStatus;
import com.fourvector.apps.dabble.web.model.ResponseHolder;

/**
 * @author Anantha.Sharma
 */
@RestController
@RequestMapping("/v0/service/bid")
public class BidRestController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BidRestController.class);

	@Autowired
	private IBidService bidService;

	/**
	 * 
	 */
	public BidRestController() {
		super();
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/user/{userId}/job/{jobId}/create/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> createBid(@PathVariable("userId") Integer bidderId, @RequestHeader("comment") String comment, @PathVariable("jobId") Integer jobId, @RequestHeader("amount") Double amount, @RequestHeader("currency") String currency) {
		Thread.currentThread().setName("create-bid-user#" + bidderId + ";job#" + jobId);
		LOG.trace("Method [createBid]: Called ");
		ResponseHolder response = null;
		try {
			Integer result = bidService.placeBid(bidderId, comment, jobId, amount, currency);
			Object bidList = bidService.getBidsForJob(jobId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, bidList);
			LOG.trace("Method [createBid]: Returning {}", result);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method createBid", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method createBid", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [createBid]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/my-bids/user/{userId}/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> getMyBids(@PathVariable("userId") Integer userId) {
		Thread.currentThread().setName("get-bid-for-user#" + userId);
		LOG.trace("Method [getMyBids]: Called Data: {}", userId);
		ResponseHolder response = null;
		try {
			List<JobDTO> resp = bidService.getMyBids(userId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, resp);
			LOG.trace("Method [getMyBids]: Returning {}", resp.size());
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getMyBids", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method getMyBids", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [getMyBids]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}
	

	@RequestMapping(value = "/forJob/{jobId}/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> getBidsForJob(@PathVariable("jobId") Integer jobId) {
		Thread.currentThread().setName("get-bid-for-job#" + jobId);
		LOG.trace("Method [getBidsForJob]: Called Data: {}", jobId);
		ResponseHolder response = null;
		try {
			List<BidDTO> resp = bidService.getBidsForJob(jobId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, resp);
			LOG.trace("Method [getBidsForJob]: Returning {}", resp);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getBidsForJob", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method getBidsForJob", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [getBidsForJob]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/{bidId}/user/{userId}/retract/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> retractBid(@PathVariable("userId") Integer bidderId, @PathVariable("bidId") Integer bidId) {
		Thread.currentThread().setName("retract-bid#" + bidId + "-by-user#" + bidderId);
		LOG.trace("Method [retractBid]: Called ");
		ResponseHolder response = null;
		try {
			BidDTO result = bidService.retractBid(bidId, bidderId, null);
			Object bidList = bidService.getBidsForJob(result.getJobId());
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, bidList);
			LOG.trace("Method [retractBid]: Returning {}", result);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method retractBid", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method retractBid", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [retractBid]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/{bidId}/user/{userId}/accept/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> acceptBid(@PathVariable("userId") Integer userId, @PathVariable("bidId") Integer bidId, @RequestHeader("nonce") String nonce) {
		Thread.currentThread().setName("accept-bid#" + bidId + "-by-user#" + userId);
		LOG.trace("Method [acceptBid]: Called ");
		ResponseHolder response = null;
		try {
			String resp = bidService.acceptBid(userId, bidId, nonce);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, resp);
			LOG.trace("Method [acceptBid]: Returning.");
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method acceptBid", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method acceptBid", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [acceptBid]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

}
