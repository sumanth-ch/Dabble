/**
 * 
 */
package com.fourvector.apps.dabble.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.service.IChatService;
import com.fourvector.apps.dabble.service.config.ResponseMessage;
import com.fourvector.apps.dabble.service.config.ResponseStatus;
import com.fourvector.apps.dabble.web.model.ResponseHolder;

/**
 * @author Anantha.Sharma
 */
@RestController
@RequestMapping("/v0/service/chat")
public class ChatRestController extends BaseRestController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChatRestController.class);

	@Autowired
	private IChatService chatService;

	/**
	 * 
	 */
	public ChatRestController() {
		super();
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/send/user/{userId}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> sendMessage(@PathVariable("userId") Integer userId, @RequestHeader("message") String message, @RequestHeader("recipient") Integer recipientId) {
		Thread.currentThread().setName("chat-send-from#" + userId + "-to-user#" + recipientId);
		LOG.trace("Method [sendMessage]: Called (from:{}, to:{})", userId, recipientId);
		ResponseHolder response = null;
		Long result = null;
		try {
			result = chatService.sendMessage(userId, message, recipientId);
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

	/**
	 * 
	 */
	@RequestMapping(value = "/get/user/{userId}/token/{token}/sender/{senderId}/", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public ResponseEntity<ResponseHolder> getMessage(@PathVariable("userId") Integer userId, @PathVariable("token") Long token, @PathVariable("senderId") Integer senderId) {
		Thread.currentThread().setName("chat-read-of-user#" + userId);
		LOG.trace("Method [getMessage]: Called (user:{}, token:{}, sender:{})", userId, token, senderId);
		ResponseHolder response = null;
		try {
			Object result = chatService.getMessage(userId, token, senderId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, result);
			LOG.trace("Method [makePayment]: Returning {}", result);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getMessage", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method getMessage", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [getMessage]: Returning.");

		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

}
