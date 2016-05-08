/**
 * 
 */
package com.fourvector.apps.dabble.web.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fourvector.apps.dabble.common.dto.AddressDTO;
import com.fourvector.apps.dabble.common.dto.SubscriptionDTO;
import com.fourvector.apps.dabble.common.dto.UserDTO;
import com.fourvector.apps.dabble.common.dto.UserDetailsDTO;
import com.fourvector.apps.dabble.common.dto.UserFeedbackDTO;
import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.service.IChatService;
import com.fourvector.apps.dabble.service.IUserService;
import com.fourvector.apps.dabble.service.config.ResponseMessage;
import com.fourvector.apps.dabble.service.config.ResponseStatus;
import com.fourvector.apps.dabble.web.model.ResponseHolder;
import com.fourvector.apps.dabble.web.util.FILE;

/**
 * @author Anantha.Sharma
 */
@RestController
@RequestMapping("/v0/service/user")
public class UserRestController extends BaseRestController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserRestController.class);

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IChatService chatService;

	/**
	 * 
	 */
	public UserRestController() {
		super();
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/subscriptions/by-type/{userTypeCode}/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> getSubscriptionsByUserType(@PathVariable("userTypeCode") Integer userTypeCode) {
		Thread.currentThread().setName("get-subscriptions-by-userType#" + userTypeCode);
		LOG.trace("Method [getSubscriptionsByUserType]: Called Data: {}", userTypeCode);
		ResponseHolder response = null;
		try {
			List<SubscriptionDTO> result = userService.getSubscriptionsByUserType(userTypeCode);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, result);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getSubscriptionsByUserType", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method getSubscriptionsByUserType", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [getSubscriptionsByUserType]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/{userId}/subscription/{subscriptionId}/subscribe/using/pi/{piId}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> createSubscription(@PathVariable("userId") Integer userId, @PathVariable("subscriptionId") Integer subscriptionId, @RequestParam("nonce") String nonce, @PathVariable("piId") String piId) {
		Thread.currentThread().setName("create-subscription#" + subscriptionId + "-for-user#" + userId);
		LOG.trace("Method [createSubscription]: Called Data: (user:{},subscriptionId:{})", userId, subscriptionId);
		ResponseHolder response = null;
		try {
			UserDTO result = userService.createSubscription(userId, subscriptionId, nonce, piId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, result);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method createSubscription", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
			response.setPayload(e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method createSubscription", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [createSubscription]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/profile-image/{userId}/", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, 
			produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void getProfileImage(@PathVariable("userId") Integer userId, HttpServletResponse response) {
		Thread.currentThread().setName("get-profile-image-for-user#" + userId);
		LOG.trace("Method [getProfileImage]: Called Data: {}", userId);

		InputStream is = null;
		try {
			is = userService.getProfileImage(userId);
			String content_type = HttpURLConnection.guessContentTypeFromStream(is);
			response.setContentType(content_type);
			IOUtils.copyLarge(is, response.getOutputStream());
		} catch (IOException e) {
			LOG.error("Found exception IOException in method getProfileImage", e);
			IOUtils.closeQuietly(is);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getProfileImage", e);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			try {
				response.getOutputStream().println(gson.toJson(new ResponseHolder(e.getCode(), e.getMessage(), null)));
			} catch (IOException e1) {
				LOG.error("Found exception IOException in method getProfileImage", e1);
			}
		}
		LOG.trace("Method [getProfileImage]: Returning.");
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/create/", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> createUser(@RequestHeader("payload") String userDtoJson, @RequestParam(value = "photo", required = true) MultipartFile photo) {
		Thread.currentThread().setName("create-user");
		LOG.trace("Method [createUser]: Called Data: {}", userDtoJson);
		UserDTO userDto = gson.fromJson(userDtoJson, UserDTO.class);
		InputStream is = null;
		if (photo != null) {
			try {
				is = photo.getInputStream();
			} catch (IOException e) {
				LOG.error("Found exception IOException in method createUser", e);
			}
		}
		ResponseHolder response = null;
		try {
			Integer result = userService.createUser(userDto, is);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, result);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method createUser", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method createUser", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [createUser]: Returning {}", userDto);
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/update/{userId}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> updateUser(@RequestHeader("payload") String userDtoJson, @PathVariable("userId") Integer userId, @RequestParam(value = "photo", required = false) MultipartFile photo) {
		Thread.currentThread().setName("update-user#" + userId);
		LOG.trace("Method [updateUser]: Called Data: (userId: {}, userData: {})", userId, userDtoJson);
		InputStream is = null;
		if (photo == null) {
			LOG.info("UserUpdate: No Pic Change detected, updating remaining fields");
		} else {
			try {
				is = photo.getInputStream();
			} catch (IOException e) {
				LOG.error("Found exception IOException in method updateUser", e);
			}
		}
		UserDTO userDto = gson.fromJson(userDtoJson, UserDTO.class);
		ResponseHolder response = null;
		try {
			userService.updateUser(userId, userDto, is);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, null);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method updateUser", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method updateUser", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}

		LOG.trace("Method [updateUser]: Returning {}", userDto);
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/set-email-active/userId/{userId}/email/{emailId}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> activateUserEmail(@PathVariable("userId") Integer userId, @PathVariable("emailId") String emailId) {
		Thread.currentThread().setName("activate-email-for-user#" + userId);
		LOG.trace("Method [activateUserEmail]: Called Data: (userId: {}, userEmail: {})", userId, emailId);
		ResponseHolder response = null;
		try {
			userService.activateEmail(userId, emailId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, null);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method activateUserEmail", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method activateUserEmail", e);

			response = new ResponseHolder(500, e.getMessage(), null);
		}

		LOG.trace("Method [activateUserEmail]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/{userId}/add-ios-device/device/{deviceToken}/device/{deviceType}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> addDeviceToken(@PathVariable("userId") Integer userId, @PathVariable("deviceToken") String deviceToken, @PathVariable("deviceType") Integer deviceType) {
		Thread.currentThread().setName("add-device-for-user#" + userId);
		LOG.trace("Method [addDeviceToken]: Called Data: (user: {}, token: {})", userId, deviceToken);
		ResponseHolder response = null;
		try {
			userService.addToken(userId, deviceToken, deviceType);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, null);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method addDeviceToken", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method addDeviceToken", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}

		LOG.trace("Method [addDeviceToken]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/authenticate/login/{login}/password/{password}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> authenticate(@PathVariable("login") String login, @PathVariable("password") String password) {
		Thread.currentThread().setName("authenticate-login#" + login);
		LOG.trace("Method [authenticate]: Called Data: (login: {}, password: ****)", login);
		ResponseHolder response = null;
		try {
			UserDTO dto = userService.authenticate(login, password);
			Map<String, Object> responseMap = new HashMap<>();
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, responseMap);
			if (dto == null) {
				response.setCode(ResponseStatus.AUTHENTICATION_FAILED);
				response.setMessage(ResponseMessage.AUTHENTICATION_FAILED);
			} else {
				UserDetailsDTO userDetails = userService.getUserDetails(dto.getUserId());
				responseMap.put("user", dto);
				responseMap.put("userDetails", userDetails);
			}
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method authenticate", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method authenticate", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}

		LOG.trace("Method [authenticate]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/facebook-authenticate/id/{fbId}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> facebookAuthenticate(@PathVariable("fbId") String fbId) {
		Thread.currentThread().setName("authenticate-facebook#" + fbId);
		LOG.trace("Method [facebookAuthenticate]: Called Data: (facebook ID: {})", fbId);
		ResponseHolder response = null;
		try {
			UserDTO dto = userService.fbAuthenticate(fbId);
			Map<String, Object> responseMap = new HashMap<>();
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, responseMap);
			if (dto == null) {
				response.setCode(ResponseStatus.AUTHENTICATION_FAILED);
				response.setMessage(ResponseMessage.AUTHENTICATION_FAILED);
			} else {
				UserDetailsDTO userDetails = userService.getUserDetails(dto.getUserId());
				responseMap.put("user", dto);
				responseMap.put("userDetails", userDetails);
			}
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method facebookAuthenticate", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method facebookAuthenticate", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}

		LOG.trace("Method [facebookAuthenticate]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/add-address/user/{userId}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> addAddress(@PathVariable("userId") Integer userId, @RequestHeader("payload") String addressDtoJson) {
		Thread.currentThread().setName("add-addr-for-user#" + userId);
		LOG.trace("Method [addAddress]: Called Data: (userId: {}, address: {})", userId, addressDtoJson);
		AddressDTO addressDto = gson.fromJson(addressDtoJson, AddressDTO.class);
		ResponseHolder response = null;
		try {
			userService.addAddress(addressDto, userId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, null);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method addAddress", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method addAddress", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}

		LOG.trace("Method [createUser]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/details/{userId}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> getUserDetails(@PathVariable("userId") Integer userId) {
		Thread.currentThread().setName("user_details-for-user#" + userId);
		LOG.trace("Method [getUserDetails]: Called Data: (userId: {})", userId);
		ResponseHolder response = null;
		try {
			UserDetailsDTO userDetails = userService.getUserDetails(userId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, userDetails);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getUserDetails", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method getUserDetails", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}

		LOG.trace("Method [getUserDetails]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	* 
	*/
	@RequestMapping(value = "/forgot-password/email/{userEmail}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> forgotPassword(@PathVariable("userEmail") String userEmail) {
		Thread.currentThread().setName("forgot-password-for-user#" + userEmail);
		LOG.trace("Method [forgotPassword]: Called Data: (userEmail: {})", userEmail);
		ResponseHolder response = null;
		try {
			userService.forgotPassword(userEmail);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, null);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method forgotPassword", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method forgotPassword", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}

		LOG.trace("Method [forgotPassword]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	* 
	*/
	@RequestMapping(value = "/forgot-password/verify/email/{userEmail}/pin/{pin}/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> forgotPasswordVerifyPin(@PathVariable("userEmail") String userEmail, @PathVariable("pin") String pin, @RequestHeader("newPassword") String newPassword) {
		Thread.currentThread().setName("forgot-password-verify-for-user#" + userEmail);
		LOG.trace("Method [forgotPasswordVerifyPin]: Called Data: (userEmail: {}, Pin: {}, NewPassword.length: )", userEmail, pin, newPassword.length());
		ResponseHolder response = null;
		try {
			userService.verifyAndSetPassword(userEmail, pin, newPassword);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, null);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method forgotPasswordVerifyPin", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method forgotPasswordVerifyPin", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [forgotPasswordVerifyPin]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}

	/**
	* 
	*/
	@RequestMapping(value = "/{userId}/get-feedback/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHolder> getUserFeedback(@PathVariable("userId") Integer userId) {
		Thread.currentThread().setName("get-feedback-for-user#" + userId);
		LOG.trace("Method [getUserFeedback]: Called Data: (userId: {})", userId);
		ResponseHolder response = null;
		try {
			List<UserFeedbackDTO> feedbackList = userService.getUserFeedback(userId);
			response = new ResponseHolder(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, feedbackList);
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method getUserFeedback", e);
			response = new ResponseHolder(e.getCode(), e.getMessage(), e.getOptionalResponse());
		} catch (Exception e) {
			LOG.error("Found Exception in method getUserFeedback", e);
			response = new ResponseHolder(500, e.getMessage(), null);
		}
		LOG.trace("Method [getUserFeedback]: Returning.");
		return new ResponseEntity<ResponseHolder>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/agreement", method = RequestMethod.GET)
	@ResponseBody
	public String getUserAgreementText() {
		String templateContent = null;
		try {
			templateContent = FILE.text(Paths.get(UserRestController.class.getResource("/userAgreement.html").toURI()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return templateContent;
	}

	@RequestMapping(value = "/pushNotif/{senderId}/{userId}", method = RequestMethod.GET)
	@ResponseBody
	public void sendPushNotification(@PathVariable("senderId") Integer senderId,@PathVariable("userId") Integer userId) {
		chatService.sendMessage(senderId, "You got a notification for dabble app", userId);
	}
	
}
