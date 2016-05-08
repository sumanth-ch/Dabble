/**
 * 
 */
package com.fourvector.apps.dabble.service;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fourvector.apps.dabble.common.dto.JobDTO;
import com.fourvector.apps.dabble.common.dto.RefundDTO;

/**
 * @author Anantha.Sharma
 */
public interface IJobService {

	/**
	 * creates a new Job
	 * 
	 * @param jobDto
	 * @param nonce
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	Integer createJob(final JobDTO jobDto, String nonce);

	/**
	 * updates an existing Job
	 * 
	 * @param jobId
	 * @param jobDto
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	RefundDTO updateJob(Integer jobId, JobDTO jobDto);

	/**
	 * @param jobId
	 * @param userId
	 * @param reasonCode
	 * @param comment
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	JobDTO retractJob(Integer jobId, Integer userId);

	/**
	 * lists jobs based on criteria, if no criteria provided then it will return all jobs in the current area.
	 * 
	 * @param userId
	 * @param lat
	 * @param lng
	 * @param categories
	 * @param rangeInMiles
	 * @param searchText
	 * @param timezone 
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	List<JobDTO> listJobs(Integer userId, Double lat, Double lng, Integer rangeInMiles, String searchText, String timezone);

	/**
	 * stores feedback for an individual user.
	 * 
	 * @param userId
	 * @param jobId
	 * @param commenteeId
	 * @param comment
	 * @param rating
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void storeFeedbackFromBidder(Integer userId, Integer jobId, Integer commenteeId, String comment, Integer rating);

	/**
	 * stores feedback for an individual user.
	 * 
	 * @param userId
	 * @param jobId
	 * @param commenteeId
	 * @param comment
	 * @param rating
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void storeFeedbackFromSeller(Integer userId, Integer jobId, Integer commenteeId, String comment, Integer rating);

	/**
	 * saves a video file
	 * 
	 * @param inputStream
	 * @param originalFilename
	 * @return
	 */
	String saveVideoFile(InputStream inputStream, String originalFilename);

	/**
	 * saves a audio file
	 * 
	 * @param inputStream
	 * @param originalFilename
	 * @return
	 */
	String saveAudioFile(InputStream inputStream, String originalFilename);

	/**
	 * saves a image file
	 * 
	 * @param inputStream
	 * @param originalFilename
	 * @return
	 */
	String saveImageFile(InputStream inputStream, String originalFilename);

	/**
	 * adds new files to existing job.
	 * 
	 * @param jobId
	 * @param jobDto
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void addFiles(Integer jobId, JobDTO jobDto);

	/**
	 * @param jobId
	 * @param resourceId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	File getResource(Integer jobId, String resourceId);

	/**
	 * returns a list of all jobs created by a given user.
	 * 
	 * @param userId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	List<JobDTO> getMyJobs(Integer userId);

	/**
	 * saves changes made to an existing valid job.
	 * 
	 * @param userId
	 * @param jobId
	 * @param jobDTO
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	JobDTO editJob(Integer userId, Integer jobId, JobDTO jobDTO);

	/**
	 * removes existing files from existing job.
	 * 
	 * @param jobId
	 * @param resourceId
	 * @return
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = false)
	void removeFiles(Integer jobId, String resourceId);
}
