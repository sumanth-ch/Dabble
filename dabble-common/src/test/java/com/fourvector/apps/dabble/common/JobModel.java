/**
 * 
 */
package com.fourvector.apps.dabble.common;

import java.util.Comparator;
import java.util.Date;

/**
 * @author Anantha.Sharma
 */
public class JobModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4442874974597307679L;

	private Double				amount;
	private String				currency;
	private String				jobTitle;
	private String				description;
	private Double				lat, lng;
	private Date				startDate, endDate;
	private String				workHours;
	private String				isEvent;

	private boolean volunteers;

	@Override
	public String toString() {
		return "JobModel [startDate=" + startDate + ", isEvent=" + isEvent + "]";
	}

	private boolean	emailCommunication;
	private boolean	chatCommunication;

	private int				numberOfVolunteers;
	
	/**
	 * 
	 */
	public JobModel() {
		super();
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getWorkHours() {
		return workHours;
	}

	public void setWorkHours(String workHours) {
		this.workHours = workHours;
	}

	public String getIsEvent() {
		return isEvent;
	}

	public void setIsEvent(String isEvent) {
		this.isEvent = isEvent;
	}

	public boolean isVolunteers() {
		return volunteers;
	}

	public void setVolunteers(boolean volunteers) {
		this.volunteers = volunteers;
	}

	public boolean isEmailCommunication() {
		return emailCommunication;
	}

	public void setEmailCommunication(boolean emailCommunication) {
		this.emailCommunication = emailCommunication;
	}

	public boolean isChatCommunication() {
		return chatCommunication;
	}

	public void setChatCommunication(boolean chatCommunication) {
		this.chatCommunication = chatCommunication;
	}

	public int getNumberOfVolunteers() {
		return numberOfVolunteers;
	}

	public void setNumberOfVolunteers(int numberOfVolunteers) {
		this.numberOfVolunteers = numberOfVolunteers;
	}
	
	public static Comparator<JobModel> COMPARE_BY_EVENT = new Comparator<JobModel>() {
        public int compare(JobModel one, JobModel other) {
            return one.isEvent.compareTo(other.isEvent);
        }
    };

}
