/**
 * 
 */
package com.fourvector.apps.dabble.jms;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.MessageCreator;

import com.fourvector.apps.dabble.service.config.NotificationEvent;
import com.fourvector.apps.dabble.service.config.ParameterKeys;

/**
 * @author asharma
 */
public class DabbleMessageCreator implements MessageCreator {
	private static final Logger	LOG	= LoggerFactory.getLogger(DabbleMessageCreator.class);
	private NotificationEvent	event;
	private Map<String, Object>	params;

	/**
	 * 
	 */
	public DabbleMessageCreator() {
	}

	/**
	 * @param event
	 * @param params
	 */
	public DabbleMessageCreator(NotificationEvent event, Map<String, Object> params) {
		super();
		this.event = event;
		this.params = params;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		LOG.debug("Method [createMessage]: Called");

		TextMessage message = session.createTextMessage();
		message.setStringProperty(ParameterKeys.KEY_EVENT, event.name());
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			Object val = entry.getValue();
			if (val instanceof Enum<?>) {
				val = ((Enum<?>) val).name();
			}
			LOG.trace("Setting property {} with value {}", entry.getKey(), val);
			message.setObjectProperty(entry.getKey(), val);
		}

		LOG.debug("Method [createMessage]: Returning JMS Message");
		return message;
	}

}
