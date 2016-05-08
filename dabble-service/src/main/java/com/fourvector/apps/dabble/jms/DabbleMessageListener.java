/**
 * 
 */
package com.fourvector.apps.dabble.jms;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fourvector.apps.dabble.notification.DabbleApplePushNotifier;
import com.fourvector.apps.dabble.notification.DabbleEmailNotifier;
import com.fourvector.apps.dabble.service.config.NotificationEvent;
import com.fourvector.apps.dabble.service.config.ParameterKeys;

/**
 * @author asharma
 */

public class DabbleMessageListener implements MessageListener {
	private static final Logger LOG = LoggerFactory.getLogger(DabbleMessageListener.class);

	private DabbleEmailNotifier		emailNotifier;
	private DabbleApplePushNotifier	apnsNotifier;
	private String					templatePath;
	private Map<String, String>		emailTemplates	= new LinkedHashMap<>();

	/**
	 * 
	 */
	public DabbleMessageListener() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(Message message) {
		LOG.debug("Method [onMessage]: Called");
		TextMessage textMessage = (TextMessage) message;

		String target;
		String subject;
		String text;
		String mode;
		try {
			String event = textMessage.getStringProperty(ParameterKeys.KEY_EVENT);
			LOG.info("Recieved event {} for processing.", event);
			target = textMessage.getStringProperty(ParameterKeys.KEY_TARGET);
			subject = getSubject(null);
			mode = textMessage.getStringProperty(ParameterKeys.MODE);
			if (!StringUtils.hasText(mode)) {
				LOG.info("Mode was not specified, defaulting to email");
				mode = ParameterKeys.VALUE_EMAIL_MODE;
			}
			Map<String, String> valueMap = new LinkedHashMap<>();

			Enumeration<String> strEnum = textMessage.getPropertyNames();
			while (strEnum.hasMoreElements()) {
				String key = strEnum.nextElement();
				Object k = textMessage.getObjectProperty(key);
				String value = k == null ? "" : k.toString();
				valueMap.put(key, value);
				LOG.debug("KEY: {}, Value: {}", key, value);
			}
			StrSubstitutor s = new StrSubstitutor(valueMap);
			String templateName = emailTemplates.get(event);
			LOG.info("Found Event {}, looking up template {}", event, templateName);
			String templateFile = null;
			if (mode.equalsIgnoreCase(ParameterKeys.VALUE_EMAIL_MODE)) {
				templateFile = "/" + templatePath + templateName;
			} else {
				templateFile = "/" + templatePath + templateName + "." + mode;
			}
			InputStream is = this.getClass().getResourceAsStream(templateFile);
			if (is == null) {
				LOG.error("Unable to find ResourceStream at location {} for event {}. Returning.", templateFile, event);
				return;
			}
			LOG.info("Found ResourceStream at location {} for event {}.", templateFile, event);
			StringWriter sw = new StringWriter();
			try {
				IOUtils.copy(is, sw);
			} catch (IOException e) {
				LOG.error("Found exception IOException in method onMessage", e);
			} finally {
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(sw);
			}
			text = s.replace(sw.getBuffer());// this prepares the message.
			if (mode.equalsIgnoreCase(ParameterKeys.VALUE_EMAIL_MODE)) {
				emailNotifier.notify(target, subject, text);
			}
			if (mode.equalsIgnoreCase(ParameterKeys.KEY_PUSH_MODE)) {
				subject = valueMap.get(ParameterKeys.KEY_MESSAGE);
				try {
					apnsNotifier.notify(target, subject, text, valueMap);
				} catch (Exception e) {
					LOG.error("Found exception Exception in method onMessage", e);
				}
			}
			message.acknowledge();
		} catch (JMSException e) {
			LOG.error("Found exception Exception in method onMessage", e);
		}

		LOG.debug("Method [onMessage]: Returning.");

	}

	private String getSubject(NotificationEvent event) {
		LOG.debug("Method [getSubject]: Called");

		LOG.debug("Method [getSubject]: Returning.");

		return "Dabble Notification";
	}

	/**
	 * @return the emailNotifier
	 */
	public DabbleEmailNotifier getEmailNotifier() {
		return emailNotifier;
	}

	/**
	 * @param emailNotifier
	 *            the emailNotifier to set
	 */
	public void setEmailNotifier(DabbleEmailNotifier emailNotifier) {
		this.emailNotifier = emailNotifier;
	}

	/**
	 * @return the apnsNotifier
	 */
	public DabbleApplePushNotifier getApnsNotifier() {
		return apnsNotifier;
	}

	/**
	 * @param apnsNotifier
	 *            the apnsNotifier to set
	 */
	public void setApnsNotifier(DabbleApplePushNotifier apnsNotifier) {
		this.apnsNotifier = apnsNotifier;
	}

	/**
	 * @return the templatePath
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * @param templatePath
	 *            the templatePath to set
	 */
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	/**
	 * @return the emailTemplates
	 */
	public Map<String, String> getEmailTemplates() {
		return emailTemplates;
	}

	/**
	 * @param emailTemplates
	 *            the emailTemplates to set
	 */
	public void setEmailTemplates(Map<String, String> emailTemplates) {
		this.emailTemplates = emailTemplates;
	}

}
