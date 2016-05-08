/**
 * 
 */
package com.fourvector.apps.dabble.common.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author asharma
 */
@Document(collection = "chats")
public class ChatDTO extends BaseDTO {

	@Id
	private String	id;
	private Long	messageTime;
	private Integer	sender;
	private Integer	reciever;
	private String	message;

	/**
	 * 
	 */
	public ChatDTO() {
		super();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		if (id == null) {
			id = this.sender + "-" + this.messageTime;
		}
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the messageTime
	 */
	public Long getMessageTime() {
		return messageTime;
	}

	/**
	 * @param messageTime
	 *            the messageTime to set
	 */
	public void setMessageTime(Long messageTime) {
		this.messageTime = messageTime;
	}

	/**
	 * @return the sender
	 */
	public Integer getSender() {
		return sender;
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public void setSender(Integer sender) {
		this.sender = sender;
	}

	/**
	 * @return the reciever
	 */
	public Integer getReciever() {
		return reciever;
	}

	/**
	 * @param reciever
	 *            the reciever to set
	 */
	public void setReciever(Integer reciever) {
		this.reciever = reciever;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
