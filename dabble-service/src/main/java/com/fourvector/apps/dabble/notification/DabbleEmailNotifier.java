/**
 * 
 */
package com.fourvector.apps.dabble.notification;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * @author asharma
 */
public class DabbleEmailNotifier {
	private static final Logger LOG = LoggerFactory.getLogger(DabbleEmailNotifier.class);

	private JavaMailSender		mailSender;
	private SimpleMailMessage	templateMessage;
	private String				from;

	/**
	 * 
	 */
	public DabbleEmailNotifier() {
		super();
	}

	public void notify(final String target, final String subject, final String text) {
		LOG.debug("Method [notify]: Called, {} , {}", target, subject);

		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(target);
			helper.setFrom(from);
			helper.setSubject(subject);
			helper.setText(text, true);
			mailSender.send(helper.getMimeMessage());
		} catch (MailException | MessagingException e) {
			LOG.error("Found exception Exception in method notify", e);
		}
		LOG.debug("Method [notify]: Returning.");
	}

	/**
	 * @return the mailSender
	 */
	public MailSender getMailSender() {
		return mailSender;
	}

	/**
	 * @param mailSender
	 *            the mailSender to set
	 */
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * @return the templateMessage
	 */
	public SimpleMailMessage getTemplateMessage() {
		return templateMessage;
	}

	/**
	 * @param templateMessage
	 *            the templateMessage to set
	 */
	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}

}