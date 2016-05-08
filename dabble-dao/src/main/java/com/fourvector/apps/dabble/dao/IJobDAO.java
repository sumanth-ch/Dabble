/**
 * 
 */
package com.fourvector.apps.dabble.dao;

import java.util.List;

import com.fourvector.apps.dabble.model.job.BidModel;
import com.fourvector.apps.dabble.model.job.JobModel;

/**
 * @author Anantha.Sharma
 */
public interface IJobDAO extends IBaseDAO {

	/**
	 * finds and returns the accepted bid for a given job, returns null otherwise.
	 * cannot throw Dabble exceptions.
	 * 
	 * @param jobId
	 * @return
	 */
	BidModel finAcceptedBidForJob(Integer jobId);

	/**
	 * finds jobs based on categories (if available).
	 * 
	 * @param userId
	 * @param lat
	 * @param lng
	 * @param rangeInMeters
	 * @param searchText
	 * @param includeCreator
	 * @return
	 */
	List<JobModel> findJobs(Integer userId, Double lat, Double lng, Integer rangeInMeters, String searchText, boolean includeCreator);

}
