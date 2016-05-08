/**
 * 
 */
package com.fourvector.apps.dabble.notification;

import java.util.Calendar;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.fourvector.apps.dabble.service.config.NotificationEvent;
import com.fourvector.apps.dabble.service.config.ParameterKeys;
import com.fourvector.apps.dabble.service.config.TargetEnvironment;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.PayloadBuilder;

/**
 * @author asharma
 */
public class DabbleApplePushNotifier implements InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(DabbleApplePushNotifier.class);

	private String				pathToCertificate;
	private String				certificatePassword;
	private TargetEnvironment	targetEnvironment;
	private ApnsService			apnsService;
	private boolean				serviceEnabled;

	/**
	 * 
	 */
	public DabbleApplePushNotifier() {
		super();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.debug("Method [afterPropertiesSet]: Called");
		Assert.notNull(pathToCertificate, "Certificate path is null");
		Assert.notNull(certificatePassword, "Certificate Password is null");
		if (serviceEnabled) {
			if (targetEnvironment == null) {
				LOG.info("TargetEnvironment is null, defaulting to SANDBOX");
				targetEnvironment = TargetEnvironment.SANDBOX;
			}
			ApnsServiceBuilder builder = APNS.newService().withCert(pathToCertificate, certificatePassword);
			switch (targetEnvironment) {
				case PRODUCTION:
					builder.withProductionDestination();
					break;
				case SANDBOX:
					builder.withSandboxDestination();
					break;
			}
			apnsService = builder.build();
			apnsService.start();
		} else {
			LOG.warn("APNS Service disabled in configuration.");
		}
		LOG.debug("Method [afterPropertiesSet]: Returning.");
	}

	public void notify(String target, String subject, String message, Map<String, String> params) {
		LOG.debug("Method [notify]: Called, {} , {}, {}", target, subject, message);
		String event = params.get(ParameterKeys.KEY_EVENT);
		NotificationEvent ne = NotificationEvent.valueOf(event);
		PayloadBuilder builder = APNS.newPayload();
		builder.customField(ParameterKeys.KEY_EVENT, event);
		builder.customField(ParameterKeys.TIME_STAMP, System.currentTimeMillis());
		switch (ne) {
			case CHAT:
				builder.customField("senderId", params.get(ParameterKeys.KEY_SENDER));
				builder.actionKey("Reply");
				break;
			default:
				break;
		}
		String payload = builder.alertBody(message).build();
		LOG.info("Sending message {} ", payload);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, 5);
		apnsService.push(target, payload, c.getTime());
		LOG.debug("Method [notify]: Returning.");
	}

	/**
	 * @return the pathToCertificate
	 */
	public String getPathToCertificate() {
		return pathToCertificate;
	}

	/**
	 * @param pathToCertificate
	 *            the pathToCertificate to set
	 */
	public void setPathToCertificate(String pathToCertificate) {
		this.pathToCertificate = pathToCertificate;
	}

	/**
	 * @return the certificatePassword
	 */
	public String getCertificatePassword() {
		return certificatePassword;
	}

	/**
	 * @param certificatePassword
	 *            the certificatePassword to set
	 */
	public void setCertificatePassword(String certificatePassword) {
		this.certificatePassword = certificatePassword;
	}

	/**
	 * @return the targetEnvironment
	 */
	public TargetEnvironment getTargetEnvironment() {
		return targetEnvironment;
	}

	/**
	 * @param targetEnvironment
	 *            the targetEnvironment to set
	 */
	public void setTargetEnvironment(TargetEnvironment targetEnvironment) {
		this.targetEnvironment = targetEnvironment;
	}

	/**
	 * @return the serviceEnabled
	 */
	public Boolean getServiceEnabled() {
		return serviceEnabled;
	}

	/**
	 * @param serviceEnabled
	 *            the serviceEnabled to set
	 */
	public void setServiceEnabled(Boolean serviceEnabled) {
		this.serviceEnabled = serviceEnabled;
	}

}
