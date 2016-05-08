package com.fourvector.apps.dabble.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.service.ITChatService;
import com.fourvector.apps.dabble.service.config.ResponseMessage;
import com.fourvector.apps.dabble.service.config.ResponseStatus;
import com.fourvector.apps.dabble.service.impl.TChatService;
import com.fourvector.apps.dabble.web.model.ResponseHolder;

@RestController
@RequestMapping("/v0/service/tchat")
public class TChatRestController extends BaseRestController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TChatRestController.class);

	public TChatRestController() {
		super();
	}

	@RequestMapping(value = "/gettoken/user/{firstName}/{userId}/", produces = MediaType.APPLICATION_JSON_VALUE, 
			method = RequestMethod.GET)
	public ResponseEntity<ResponseHolder> getToken(@PathVariable("firstName") String firstName,
			@PathVariable("userId") String userId) {
		Thread.currentThread().setName("chat-send-from#" + userId);
		LOG.trace("Method [sendMessage]: Called (from:{}, to:{})", userId);
		ResponseHolder response = null;
		String result = null;
		try {
			ITChatService tchatService = new TChatService();
			result = tchatService.getToken(userId, firstName);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, result);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method sendMessage", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method sendMessage", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [sendMessage]: Returning {}", result);
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

}
