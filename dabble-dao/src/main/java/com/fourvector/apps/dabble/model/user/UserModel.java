/**
 * 
 */
package com.fourvector.apps.dabble.model.user;

import java.util.Date;
import java.util.Set;

import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author Anantha.Sharma
 */
public class UserModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5897352289347336078L;

	private Integer				userTypeCode;
	private String				firstName;
	private String				middleName;
	private String				lastName;
	private String				aboutMe;
	private Integer				gender;
	private String				deviceToken;
	private Date				dob;
	private Integer				age;
	private String				photoUrl;
	private Set<UserEmailModel>	emails;
	private Set<UserPhoneModel>	phones;
	private String				braintreeReference;
	private String				timezone;

	/**
	 * 
	 */
	public UserModel() {
		super();
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return this.middleName;
	}

	/**
	 * @param middleName
	 *            the middleName to set
	 */
	public void setMiddleName(final String middleName) {
		this.middleName = middleName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the gender
	 */
	public Integer getGender() {
		return this.gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(final Integer gender) {
		this.gender = gender;
	}

	/**
	 * @return the dob
	 */
	public Date getDob() {
		return this.dob;
	}

	/**
	 * @param dob
	 *            the dob to set
	 */
	public void setDob(final Date dob) {
		this.dob = dob;
	}

	/**
	 * @return the age
	 */
	public Integer getAge() {
		return this.age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(final Integer age) {
		this.age = age;
	}

	/**
	 * @return the photoUrl
	 */
	public String getPhotoUrl() {
		return this.photoUrl;
	}

	/**
	 * @param photoUrl
	 *            the photoUrl to set
	 */
	public void setPhotoUrl(final String photoUrl) {
		this.photoUrl = photoUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("UserModel [userTypeCode=%s, firstName=%s, middleName=%s, lastName=%s, gender=%s, dob=%s, age=%s, photoUrl=%s, id=%s, status=%s]", this.userTypeCode, this.firstName, this.middleName, this.lastName, this.gender, this.dob, this.age, this.photoUrl, this.id, this.status);
	}

	/**
	 * @return the aboutMe
	 */
	public String getAboutMe() {
		return aboutMe;
	}

	/**
	 * @param aboutMe
	 *            the aboutMe to set
	 */
	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	/**
	 * @return the emails
	 */
	public Set<UserEmailModel> getEmails() {
		return emails;
	}

	/**
	 * @param emails
	 *            the emails to set
	 */
	public void setEmails(Set<UserEmailModel> emails) {
		this.emails = emails;
	}

	/**
	 * @return the phones
	 */
	public Set<UserPhoneModel> getPhones() {
		return phones;
	}

	/**
	 * @param phones
	 *            the phones to set
	 */
	public void setPhones(Set<UserPhoneModel> phones) {
		this.phones = phones;
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
	 * @return the userTypeCode
	 */
	public Integer getUserTypeCode() {
		return userTypeCode;
	}

	/**
	 * @param userTypeCode
	 *            the userTypeCode to set
	 */
	public void setUserTypeCode(Integer userTypeCode) {
		this.userTypeCode = userTypeCode;
	}

	/**
	 * @return the braintreeReference
	 */
	public String getBraintreeReference() {
		return braintreeReference;
	}

	/**
	 * @param braintreeReference
	 *            the braintreeReference to set
	 */
	public void setBraintreeReference(String braintreeReference) {
		this.braintreeReference = braintreeReference;
	}

	/**
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}

	/**
	 * @param timezone the timezone to set
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

}
