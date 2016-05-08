/**
 * 
 */
package com.fourvector.apps.dabble.dao.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fourvector.apps.dabble.dao.IJobDAO;
import com.fourvector.apps.dabble.dao.IMongoDAO;
import com.fourvector.apps.dabble.dao.config.SQLOrder;
import com.fourvector.apps.dabble.dao.config.Status;
import com.fourvector.apps.dabble.model.job.BidModel;
import com.fourvector.apps.dabble.model.job.JobModel;

/**
 * @author Anantha.Sharma
 */
public class JobDAO extends BaseDAO implements IJobDAO {
	private static final org.slf4j.Logger	LOG	= org.slf4j.LoggerFactory.getLogger(JobDAO.class);
	private IMongoDAO						mongoDAO;

	/**
	 * 
	 */
	public JobDAO() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BidModel finAcceptedBidForJob(Integer jobId) {
		LOG.debug("Method [finAcceptedBidForJob]: Called");
		BidModel result = null;

		String hqlQuery = "from " + BidModel.class.getName() + " p where p.status=0 and p.job.id=?";
		List<BidModel> bids = (List<BidModel>) getHibernateTemplate().find(hqlQuery, jobId);

		if (bids == null || bids.isEmpty()) {
			// No accepted bids found for Job
			LOG.info("Job with id {} doesn't have any 'accepted' bids", jobId);
		} else {
			result = bids.get(0);
		}

		LOG.debug("Method [finAcceptedBidForJob]: Returning {}", result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JobModel> findJobs(Integer userId, Double lat, Double lng, Integer rangeInMeters, String searchText, boolean includeCreator) {
		LOG.debug("Method [findJobs]: Called [user:{},lat:{},lng:{},range:{},searchText:{}]", userId, lat, lng, rangeInMeters, searchText);
		List<JobModel> result = new LinkedList<>();
		List<Integer> jobIds = null;
		Session session = getSessionFactory().getCurrentSession();
		StringBuilder builder = new StringBuilder();
		Criteria criteria = session.createCriteria(JobModel.class);
		if (rangeInMeters != null) {
			double rangeInKM = (double) (rangeInMeters / 1000d);
			String sql = "SELECT id_job, ( 6371 * acos( cos( radians(" + lat + ") ) * cos( radians( lat ) )  * cos( radians( lng ) - radians(" + lng + ") ) + sin( radians(" + lat + ") ) * sin(radians(lat)) ) ) AS distance  FROM job where endDate  > CURRENT_DATE - 1 HAVING distance < " + rangeInKM + "  ORDER BY distance asc";
			SQLQuery query = session.createSQLQuery(sql);
			List<Object[]> results = query.list();
			if (results == null || results.isEmpty()) {
				LOG.info("NO RECORDS FOUND");
				return result;
			} else {
				LOG.info("Found {} records.", results.size());
				jobIds = new LinkedList<>();
				for (Object[] res : results) {
					Integer jobId = (Integer) res[0];
					jobIds.add(jobId);
				}
				for (int i = 0; i < jobIds.size() - 1; i++) {
					Integer jobId = jobIds.get(i);
					builder.append(jobId + ", ");
				}
				builder.append(jobIds.get(jobIds.size() - 1));
			}
			if (jobIds.isEmpty()) {
				LOG.info("No jobs found for the given criteria set.");
				return result;
			} else {
				criteria.add(Restrictions.in("id", jobIds));
			}
		}

		if (!StringUtils.isEmpty(searchText)) {
			Criterion orCondition = Restrictions.or(Restrictions.ilike("jobTitle", searchText, MatchMode.ANYWHERE), Restrictions.ilike("description", searchText, MatchMode.ANYWHERE));
			criteria.add(orCondition);
		}

		criteria.add(Restrictions.not(Restrictions.in("status", new Integer[] { Status.JOB_OR_BID_RETRACTED, Status.JOB_COMPLETED })));

		if (includeCreator == false) {
			// don't show jobs created by me in my job search result.
			criteria.createAlias("postedBy", "creator");
			criteria.add(Restrictions.ne("creator.id", userId));
		} else {
			criteria.createAlias("postedBy", "creator");
			criteria.add(Restrictions.eq("creator.id", userId));
		}

		if (!CollectionUtils.isEmpty(jobIds) && includeCreator == false) {
			criteria.addOrder(SQLOrder.sqlFormula("FIELD (id_job, " + builder.toString() + " )"));
		}
		if (includeCreator) {
			criteria.addOrder(Order.asc("status"));
		}
		result = criteria.list();
		Set<JobModel> res = new LinkedHashSet<>();

		LOG.info("Retrieved {} jobs", result.size());
		for (JobModel jobModel : result) {
			//TODO Remove hard coding
			if (jobModel.getIsEvent().equals("1")) {
				int acceptedBidCount = 0;
				for (BidModel bidModel : jobModel.getBids()) {
					if (bidModel.getStatus() == Status.JOB_OR_BID_ACCEPTED) {
						acceptedBidCount++;
					}
				}
				if (jobModel.getNumberOfVolunteers() == 0 || (acceptedBidCount < jobModel.getNumberOfVolunteers())) {
					res.add(jobModel);
				}
			} else {
				res.add(jobModel);
			}
		}
		LOG.info("settled on {} jobs and events after filtering.", res.size());

		List<JobModel> jmList = new ArrayList<>(res);
		LOG.debug("Method [findJobs]: Returning {}", jmList.size());

		return jmList;
	}

	/**
	 * @return the mongoDAO
	 */
	public IMongoDAO getMongoDAO() {
		return mongoDAO;
	}

	/**
	 * @param mongoDAO
	 *            the mongoDAO to set
	 */
	public void setMongoDAO(IMongoDAO mongoDAO) {
		this.mongoDAO = mongoDAO;
	}

}
