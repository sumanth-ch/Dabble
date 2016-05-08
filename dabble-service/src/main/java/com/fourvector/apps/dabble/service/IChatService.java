/**
 * 
 */
package com.fourvector.apps.dabble.service;

import java.util.List;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fourvector.apps.dabble.common.dto.ChatDTO;

/**
 * @author asharma
 */
public interface IChatService {
	/**
	 * sends a mesage from a given user to another user,
	 * 
	 * @param userId
	 * @param message
	 * @param targetUserId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	Long sendMessage(Integer userId, String message, Integer targetUserId);

	/**
	 * retrieves the user's message
	 * 
	 * @param userId
	 * @param token
	 * @param senderId 
	 * @return
	 */
	List<ChatDTO> getMessage(Integer userId, Long token, Integer senderId);
}
