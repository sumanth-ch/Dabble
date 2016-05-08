/**
 * 
 */
package com.fourvector.apps.dabble.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

import com.fourvector.apps.dabble.common.dto.ChatDTO;
import com.fourvector.apps.dabble.dao.IMongoDAO;
import com.fourvector.apps.dabble.dao.IUserDAO;
import com.fourvector.apps.dabble.jms.DabbleMessageCreator;
import com.fourvector.apps.dabble.model.user.DeviceTokenModel;
import com.fourvector.apps.dabble.service.IChatService;
import com.fourvector.apps.dabble.service.config.NotificationEvent;
import com.fourvector.apps.dabble.service.config.ParameterKeys;

/**
 * @author asharma
 */
public class ChatService implements IChatService {
	private static final Logger	LOG	= LoggerFactory.getLogger(ChatService.class);
	private JmsTemplate			jmsTemplate;
	private IMongoDAO			mongoDAO;
	private IUserDAO			userDAO;

	/**
	 * 
	 */
	public ChatService() {
		super();
	}

	@Override
	public Long sendMessage(Integer userId, String message, Integer targetUserId) {
		LOG.debug("Method [sendMessage]: Called (sender:{}, message:{}, reciever:{})", userId, message, targetUserId);
		Long result = System.currentTimeMillis();
		ChatDTO chatDto = new ChatDTO();
		chatDto.setMessage(message);
		chatDto.setMessageTime(result);
		chatDto.setReciever(targetUserId);
		chatDto.setSender(userId);
		mongoDAO.insertDTO(chatDto);
		List<?> strList = (List<?>) userDAO.runQuery("select p.deviceToken from " + DeviceTokenModel.class.getName() + " p where p.user.id=?", targetUserId);
		Map<String, Object> params = new HashMap<>();
		if (strList != null) {
			for (Object deviceId : strList) {
				params.put(ParameterKeys.KEY_EVENT, NotificationEvent.CHAT);
				params.put(ParameterKeys.KEY_MESSAGE, message);
				params.put(ParameterKeys.KEY_SENDER, userId);
				params.put(ParameterKeys.MESSAGE_TOKEN, result);
				params.put(ParameterKeys.KEY_TARGET, deviceId.toString());
				params.put(ParameterKeys.TIME_STAMP, new Date().getTime());
				params.put(ParameterKeys.MODE, ParameterKeys.KEY_PUSH_MODE);
				jmsTemplate.send(new DabbleMessageCreator(NotificationEvent.CHAT, params));
			}
		}
		LOG.debug("Method [sendMessage]: Returning {}", result);

		return result;
	}

	@Override
	public List<ChatDTO> getMessage(Integer userId, Long token, Integer senderId) {
		LOG.debug("Method [getMessage]: Called (User:{}, token:{}, sender:{})", userId, token, senderId);

		List<ChatDTO> chats = mongoDAO.findChatsFor(userId, token, senderId);

		LOG.debug("Method [getMessage]: Returning {}", chats);

		return chats;
	}

	/**
	 * @return the jmsTemplate
	 */
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	/**
	 * @param jmsTemplate
	 *            the jmsTemplate to set
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	/**
	 * @return the mongoDAO
	 */
	public IMongoDAO getMongoDAO() {
		return mongoDAO;
	}

	/**
	 * @param mongoDAO
	 *            the mongoDAO to set
	 */
	public void setMongoDAO(IMongoDAO mongoDAO) {
		this.mongoDAO = mongoDAO;
	}

	/**
	 * @return the userDAO
	 */
	public IUserDAO getUserDAO() {
		return userDAO;
	}

	/**
	 * @param userDAO
	 *            the userDAO to set
	 */
	public void setUserDAO(IUserDAO userDAO) {
		this.userDAO = userDAO;
	}

}
