/**
 * 
 */
package com.fourvector.apps.dabble.web.rest;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fourvector.apps.dabble.exception.DabbleException;
import com.fourvector.apps.dabble.service.IUserService;
import com.fourvector.apps.dabble.service.config.ResponseStatus;

/**
 * @author Anantha.Sharma
 */
@RestController
@RequestMapping("/verify")
public class VerificationRestController extends BaseRestController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(VerificationRestController.class);

	@Autowired
	private IUserService	userService;
	@Value("${email.template.location}")
	private String			location;

	/**
	 * 
	 */
	public VerificationRestController() {
		super();
	}

	/**
	 * 
	 */
	@RequestMapping(value = "/user/{verification-code}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
	public String verifyLink(@PathVariable("verification-code") String verificationCode) {
		Thread.currentThread().setName("verifyLink");
		LOG.trace("Method [verifyLink]: Called Data: {}", verificationCode);
		String responseText = "";
		try {
			int responseStatus = userService.verifyLink(verificationCode);
			String path = "/notification-html/verifyEmail.html";
			LOG.info("Path: {}", path);
			InputStream is = this.getClass().getResourceAsStream(path);
			switch (responseStatus) {
				case ResponseStatus.SUCCESS:
					responseText = IOUtils.toString(is);
					break;
				default:
					responseText = "<h2>Unfortunately, we were unable to verify the code, please contact support team</h2>";
			}
		} catch (DabbleException e) {
			LOG.error("Found DabbleException in method verifyLink", e);
		} catch (Exception e) {
			LOG.error("Found Exception in method verifyLink", e);
		}
		LOG.trace("Method [verifyLink]: Returning.");
		return responseText;
	}

}
