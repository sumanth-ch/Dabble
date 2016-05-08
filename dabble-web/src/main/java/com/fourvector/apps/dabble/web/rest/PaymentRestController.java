/**
 * 
 */
package com.fourvector.apps.dabble.web.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fourvector.apps.dabble.common.dto.CreditCardDTO;
import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.service.IPaymentService;
import com.fourvector.apps.dabble.service.config.ResponseMessage;
import com.fourvector.apps.dabble.service.config.ResponseStatus;
import com.fourvector.apps.dabble.web.model.ResponseHolder;

/**
 * @author Anantha.Sharma
 */
@RestController
@RequestMapping("/v0/service/payment")
public class PaymentRestController extends BaseRestController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PaymentRestController.class);

	@Autowired
	private IPaymentService paymentService;

	/**
	 * 
	 */
	public PaymentRestController() {
		super();
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/user/{userId}/generate-token/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> generateClientToken(@PathVariable("userId") Integer userId) {
		Thread.currentThread().setName("create-client-token-for-user#" + userId);
		LOG.trace("Method [generateClientToken]: Called ");
		ResponseHolder response = null;
		try {
			String token = paymentService.getClientToken(userId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, token);
			LOG.trace("Method [generateClientToken]: Returning.");
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method generateClientToken", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method generateClientToken", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [generateClientToken]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/user/{userId}/change-payment-instrument/", method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> changePaymentInstrument(@PathVariable("userId") Integer userId, @RequestParam(value = "paymentInstrument", required = false) String piId, @RequestParam(value = "cardNumber", required = false) String cardNumber, @RequestParam(value = "expiryDate", required = false) String expiryDate, @RequestParam(value = "cvv", required = false) String cvv,
			@RequestParam(value = "nonce", required = false) String nonce) {
		Thread.currentThread().setName("change-pi-for-user#" + userId);
		LOG.trace("Method [changePaymentInstrument]: Called ");
		ResponseHolder response = null;
		try {
			String token = paymentService.addCardToCustomer(userId, cardNumber, expiryDate, cvv, nonce);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, token);
			LOG.trace("Method [changePaymentInstrument]: Returning.");
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method changePaymentInstrument", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method changePaymentInstrument", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [changePaymentInstrument]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/user/{userId}/cards/", method = RequestMethod.GET)
	public ResponseEntity<ResponseHolder> getAllCards(@PathVariable("userId") Integer userId) {
		Thread.currentThread().setName("get-all-pi-for-user#" + userId);
		LOG.trace("Method [getAllCards]: Called ");
		ResponseHolder response = null;
		try {
			List<CreditCardDTO> ccDTO = paymentService.getCardList(userId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, ccDTO);
			LOG.trace("Method [getAllCards]: Returning.");
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getAllCards", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method getAllCards", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [getAllCards]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

}
