/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author Anantha.Sharma
 */
public class UserAddressModel extends BaseModel {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6184406366690764476L;
	private UserModel			user;
	private String				addressLine1;
	private String				addressLine2;
	private String				country;
	private String				city;
	private String				state;
	private Integer				zip;

	/**
	 * 
	 */
	public UserAddressModel() {
		super();
	}

	/**
	 * @return the user
	 */
	public UserModel getUser() {
		return this.user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(final UserModel user) {
		this.user = user;
	}

	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return this.addressLine1;
	}

	/**
	 * @param addressLine1
	 *            the addressLine1 to set
	 */
	public void setAddressLine1(final String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	/**
	 * @return the addressLine2
	 */
	public String getAddressLine2() {
		return this.addressLine2;
	}

	/**
	 * @param addressLine2
	 *            the addressLine2 to set
	 */
	public void setAddressLine2(final String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(final String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(final String state) {
		this.state = state;
	}

	/**
	 * @return the zip
	 */
	public Integer getZip() {
		return this.zip;
	}

	/**
	 * @param zip
	 *            the zip to set
	 */
	public void setZip(final Integer zip) {
		this.zip = zip;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("UserAddressModel [user=%s, addressLine1=%s, addressLine2=%s, city=%s, state=%s, zip=%s, id=%s, status=%s]", DAOUtils.showEntity(this.user), this.addressLine1, this.addressLine2, this.city, this.state, this.zip, this.id, this.status);
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

}
