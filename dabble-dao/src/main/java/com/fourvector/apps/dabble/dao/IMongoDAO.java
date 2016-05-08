/**
 * 
 */
package com.fourvector.apps.dabble.dao;

import java.util.List;

import org.springframework.data.geo.Point;

import com.fourvector.apps.dabble.common.dto.BaseDTO;
import com.fourvector.apps.dabble.common.dto.ChatDTO;

/**
 * @author asharma
 */
public interface IMongoDAO {

	public List<Integer> findJobs(Point userLocation, Integer distanceInMeters);

	public void updateDTO(BaseDTO baseDto);

	public void insertDTO(BaseDTO baseDto);
// public void insertDTO(BaseDTO baseDto);

	/**
	 * finds all chats for a given user from the given token
	 * 
	 * @param userId
	 * @param token
	 * @param senderId 
	 * @return
	 */
	public List<ChatDTO> findChatsFor(Integer userId, Long token, Integer senderId);

}
