/**
 * 
 */
package com.fourvector.apps.dabble.common.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Anantha.Sharma
 */
@Document(collection = "jobs")
public class JobDTO extends BaseDTO implements Cloneable {
	@Id
	private Integer			jobId;
	private String			event;
	private String			description;
	private Integer			categoryId;
	private String			jobTitle;
	private String			categoryImage;
	private String			categoryName;
	private Integer			posterId;
	private String			posterImage;
	private String			posterName;
	private Double			lat, lng;
	private Point			locationPoint;
	private Integer			durationOfWorkInDays;
	private String			workHours;
	private List<String>	videos		= new LinkedList<>();
	private List<String>	audios		= new LinkedList<>();
	private List<String>	images		= new LinkedList<>();
	private Double			amount;
	private String			currency;
	private Calendar		startDate	= new GregorianCalendar();
	private String			timezone;
	private Calendar		endDate		= new GregorianCalendar();
	private boolean			volunteers;
	private int				numberOfVolunteers;
	private boolean			emailCommunication;
	private boolean			chatCommunication;
	private Date			createdDate;
	private int			rating;
	private String 			comments;
	private List<BidDTO>	bids		= new LinkedList<>();
	
	
	/**
	 * 
	 */
	public JobDTO() {
		super();
	}

	/**
	 * @return the jobId
	 */
	public Integer getJobId() {
		return this.jobId;
	}

	/**
	 * @param jobId
	 *            the jobId to set
	 */
	public void setJobId(final Integer jobId) {
		this.jobId = jobId;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the categoryId
	 */
	public Integer getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId
	 *            the categoryId to set
	 */
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the categoryImage
	 */
	public String getCategoryImage() {
		return categoryImage;
	}

	/**
	 * @param categoryImage
	 *            the categoryImage to set
	 */
	public void setCategoryImage(String categoryImage) {
		this.categoryImage = categoryImage;
	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName
	 *            the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
	 * @return the durationOfWorkInDays
	 */
	public Integer getDurationOfWorkInDays() {
		return durationOfWorkInDays;
	}

	/**
	 * @param durationOfWorkInDays
	 *            the durationOfWorkInDays to set
	 */
	public void setDurationOfWorkInDays(Integer durationOfWorkInDays) {
		this.durationOfWorkInDays = durationOfWorkInDays;
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

	/**
	 * @return the videos
	 */
	public List<String> getVideos() {
		return videos;
	}

	/**
	 * @param videos
	 *            the videos to set
	 */
	public void setVideos(List<String> videos) {
		this.videos = videos;
	}

	/**
	 * @return the audios
	 */
	public List<String> getAudios() {
		return audios;
	}

	/**
	 * @param audios
	 *            the audios to set
	 */
	public void setAudios(List<String> audios) {
		this.audios = audios;
	}

	/**
	 * @return the images
	 */
	public List<String> getImages() {
		return images;
	}

	/**
	 * @param images
	 *            the images to set
	 */
	public void setImages(List<String> images) {
		this.images = images;
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
	 * @return the startDate
	 */
	public Calendar getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the posterId
	 */
	public Integer getPosterId() {
		return posterId;
	}

	/**
	 * @param posterId
	 *            the posterId to set
	 */
	public void setPosterId(Integer posterId) {
		this.posterId = posterId;
	}

	/**
	 * @return the posterImage
	 */
	public String getPosterImage() {
		return posterImage;
	}

	/**
	 * @param posterImage
	 *            the posterImage to set
	 */
	public void setPosterImage(String posterImage) {
		this.posterImage = posterImage;
	}

	/**
	 * @return the posterName
	 */
	public String getPosterName() {
		return posterName;
	}

	/**
	 * @param posterName
	 *            the posterName to set
	 */
	public void setPosterName(String posterName) {
		this.posterName = posterName;
	}

	/**
	 * @return the emailCommunication
	 */
	public boolean isEmailCommunication() {
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

	/**
	 * @return the bids
	 */
	public List<BidDTO> getBids() {
		return bids;
	}

	/**
	 * @param bids
	 *            the bids to set
	 */
	public void setBids(List<BidDTO> bids) {
		this.bids = bids;
	}

	/**
	 * @return the endDate
	 */
	public Calendar getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Calendar endDate) {
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
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the locationPoint
	 */
	public Point getLocationPoint() {
		return locationPoint;
	}

	/**
	 * @param locationPoint
	 *            the locationPoint to set
	 */
	public void setLocationPoint(Point locationPoint) {
		this.locationPoint = locationPoint;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "JobDTO [jobId=" + jobId + ", event=" + event + ", description=" + description + ", categoryId="
				+ categoryId + ", jobTitle=" + jobTitle + ", categoryImage=" + categoryImage + ", categoryName="
				+ categoryName + ", posterId=" + posterId + ", posterImage=" + posterImage + ", posterName="
				+ posterName + ", lat=" + lat + ", lng=" + lng + ", locationPoint=" + locationPoint
				+ ", durationOfWorkInDays=" + durationOfWorkInDays + ", workHours=" + workHours + ", videos=" + videos
				+ ", audios=" + audios + ", images=" + images + ", amount=" + amount + ", currency=" + currency
				+ ", startDate=" + startDate + ", timezone=" + timezone + ", endDate=" + endDate + ", volunteers="
				+ volunteers + ", numberOfVolunteers=" + numberOfVolunteers + ", emailCommunication="
				+ emailCommunication + ", chatCommunication=" + chatCommunication + ", createdDate=" + createdDate
				+ ", bids=" + bids + "]";
	}

	
	

}
