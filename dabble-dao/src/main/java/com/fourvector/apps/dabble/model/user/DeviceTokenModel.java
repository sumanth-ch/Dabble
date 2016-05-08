/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author asharma
 */
public class DeviceTokenModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8676476778855949829L;

	private String		deviceToken;
	private Integer		deviceType;
	private UserModel	user;

	/**
	 * 
	 */
	public DeviceTokenModel() {
		super();
	}

	/**
	 * @param deviceToken
	 * @param deviceType
	 */
	public DeviceTokenModel(String deviceToken, Integer deviceType, UserModel user) {
		super();
		this.deviceToken = deviceToken;
		this.deviceType = deviceType;
		this.user = user;
	}

	/**
	 * @return the deviceToken
	 */
	public String getDeviceToken() {
		return deviceToken;
	}

	/**
	 * @param deviceToken
	 *            the deviceToken to set
	 */
	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	/**
	 * @return the deviceType
	 */
	public Integer getDeviceType() {
		return deviceType;
	}

	/**
	 * @param deviceType
	 *            the deviceType to set
	 */
	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * @return the user
	 */
	public UserModel getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(UserModel user) {
		this.user = user;
	}

}
