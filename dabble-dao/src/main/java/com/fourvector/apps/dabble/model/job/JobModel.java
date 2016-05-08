/**
 * 
 */
package com.fourvector.apps.dabble.model.job;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.fourvector.apps.dabble.dao.config.DAOUtils;
import com.fourvector.apps.dabble.model.BaseModel;
import com.fourvector.apps.dabble.model.payment.SubscriptionModel;
import com.fourvector.apps.dabble.model.user.UserAddressModel;
import com.fourvector.apps.dabble.model.user.UserEmailModel;
import com.fourvector.apps.dabble.model.user.UserModel;
import com.fourvector.apps.dabble.model.user.UserPhoneModel;
import com.fourvector.apps.dabble.model.user.UserRatingModel;

/**
 * @author Anantha.Sharma
 */
public class JobModel extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4442874974597307679L;

	private UserModel			postedBy;
	private UserEmailModel		posterEmail;
	private UserPhoneModel		posterPhone;
	private UserAddressModel	posterAddress;
	private Double				amount;
	private String				currency;
	private String				jobTitle;
	private String				description;
	private Set<JobDetailModel>	jobDetails;
	private Double				lat, lng;
	private Date				startDate, endDate;
	private String				workHours;
	private SubscriptionModel	subscriptionModel;
	private String				isEvent;

	private boolean volunteers;

	private boolean	emailCommunication;
	private boolean	chatCommunication;

	private int				numberOfVolunteers;
	private Set<BidModel>	bids	= new HashSet<>();
	
	private Set<UserRatingModel> ratings = new HashSet<>();

	/**
	 * 
	 */
	public JobModel() {
		super();
	}

	/**
	 * @return the postedBy
	 */
	public UserModel getPostedBy() {
		return postedBy;
	}

	/**
	 * @param postedBy
	 *            the postedBy to set
	 */
	public void setPostedBy(UserModel postedBy) {
		this.postedBy = postedBy;
	}

	public String getIsEvent() {
		return isEvent();
	}

	/**
	 * @return the posterAddress
	 */
	public UserAddressModel getPosterAddress() {
		return posterAddress;
	}

	/**
	 * @param posterAddress
	 *            the posterAddress to set
	 */
	public void setPosterAddress(UserAddressModel posterAddress) {
		this.posterAddress = posterAddress;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the jobDetails
	 */
	public Set<JobDetailModel> getJobDetails() {
		if (jobDetails == null) {
			jobDetails = new HashSet<>();
		}
		return jobDetails;
	}

	/**
	 * @param jobDetails
	 *            the jobDetails to set
	 */
	public void setJobDetails(Set<JobDetailModel> jobDetails) {
		this.jobDetails = jobDetails;
	}

	/**
	 * @return the lat
	 */
	public Double getLat() {
		return lat;
	}

	/**
	 * @param lat
	 *            the lat to set
	 */
	public void setLat(Double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lng
	 */
	public Double getLng() {
		return lng;
	}

	/**
	 * @param lng
	 *            the lng to set
	 */
	public void setLng(Double lng) {
		this.lng = lng;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the workHours
	 */
	public String getWorkHours() {
		return workHours;
	}

	/**
	 * @param workHours
	 *            the workHours to set
	 */
	public void setWorkHours(String workHours) {
		this.workHours = workHours;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("JobModel [postedBy=%s, postedEmail=%s, postedPhone=%s, posterAddress=%s, amount=%s, currency=%s, description=%s, jobDetails.length=%s, lat=%s, lng=%s, startDate=%s, workHours=%s, subscriptionModel=%s]", DAOUtils.showEntity(postedBy), DAOUtils.showEntity(posterEmail), DAOUtils.showEntity(posterPhone), DAOUtils.showEntity(posterAddress), amount, currency, description,
				DAOUtils.showEntity(jobDetails), lat, lng, startDate, workHours, DAOUtils.showEntity(subscriptionModel));
	}

	/**
	 * @return the posterEmail
	 */
	public UserEmailModel getPosterEmail() {
		return posterEmail;
	}

	/**
	 * @param posterEmail
	 *            the posterEmail to set
	 */
	public void setPosterEmail(UserEmailModel posterEmail) {
		this.posterEmail = posterEmail;
	}

	/**
	 * @return the posterPhone
	 */
	public UserPhoneModel getPosterPhone() {
		return posterPhone;
	}

	/**
	 * @param posterPhone
	 *            the posterPhone to set
	 */
	public void setPosterPhone(UserPhoneModel posterPhone) {
		this.posterPhone = posterPhone;
	}

	/**
	 * @return the emailCommunication
	 */
	public boolean isEmailCommunication() {
		return emailCommunication;
	}

	/**
	 * @return the emailCommunication
	 */
	public boolean getEmailCommunication() {
		return emailCommunication;
	}

	/**
	 * @param emailCommunication
	 *            the emailCommunication to set
	 */
	public void setEmailCommunication(boolean emailCommunication) {
		this.emailCommunication = emailCommunication;
	}

	/**
	 * @return the chatCommunication
	 */
	public boolean isChatCommunication() {
		return chatCommunication;
	}

	/**
	 * @return the chatCommunication
	 */
	public boolean getChatCommunication() {
		return chatCommunication;
	}

	/**
	 * @param chatCommunication
	 *            the chatCommunication to set
	 */
	public void setChatCommunication(boolean chatCommunication) {
		this.chatCommunication = chatCommunication;
	}

	/**
	 * @return the jobTitle
	 */
	public String getJobTitle() {
		return jobTitle;
	}

	/**
	 * @param jobTitle
	 *            the jobTitle to set
	 */
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public boolean equals(Object obj) {
		return this.getId().equals(((JobModel) obj).getId());
	}

	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}

	/**
	 * @return the bids
	 */
	public Set<BidModel> getBids() {
		return bids;
	}

	/**
	 * @param bids
	 *            the bids to set
	 */
	public void setBids(Set<BidModel> bids) {
		this.bids = bids;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the volunteers
	 */
	public boolean isVolunteers() {
		return volunteers;
	}

	/**
	 * @param volunteers
	 *            the volunteers to set
	 */
	public void setVolunteers(boolean volunteers) {
		this.volunteers = volunteers;
	}

	/**
	 * @return the numberOfVolunteers
	 */
	public int getNumberOfVolunteers() {
		return numberOfVolunteers;
	}

	/**
	 * @param numberOfVolunteers
	 *            the numberOfVolunteers to set
	 */
	public void setNumberOfVolunteers(int numberOfVolunteers) {
		this.numberOfVolunteers = numberOfVolunteers;
	}

	/**
	 * @return the subscriptionModel
	 */
	public SubscriptionModel getSubscriptionModel() {
		return subscriptionModel;
	}

	/**
	 * @param subscriptionModel
	 *            the subscriptionModel to set
	 */
	public void setSubscriptionModel(SubscriptionModel subscriptionModel) {
		this.subscriptionModel = subscriptionModel;
	}

	/**
	 * @return the isEvent
	 */
	public String isEvent() {
		return isEvent;
	}

	/**
	 * @param isEvent
	 *            the isEvent to set
	 */
	public void setEvent(String isEvent) {
		this.isEvent = isEvent;
	}

	/**
	 * @param isEvent
	 *            the isEvent to set
	 */
	public void setIsEvent(String isEvent) {
		this.isEvent = isEvent;
	}
	
	public Set<UserRatingModel> getRatings() {
		return ratings;
	}

	public void setRatings(Set<UserRatingModel> ratings) {
		this.ratings = ratings;
	}

	public static Comparator<JobModel> COMPARE_BY_EVENT = new Comparator<JobModel>() {
        public int compare(JobModel one, JobModel other) {
            return one.getIsEvent().compareTo(other.getIsEvent());
        }
    };
}
