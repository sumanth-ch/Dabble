/**
 * 
 */
package com.fourvector.apps.dabble.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import com.fourvector.apps.dabble.dao.IBaseDAO;
import com.fourvector.apps.dabble.dao.config.Status;
import com.fourvector.apps.dabble.model.Auditable;
import com.fourvector.apps.dabble.model.BaseModel;
import com.fourvector.apps.dabble.model.job.BidModel;
import com.fourvector.apps.dabble.model.job.JobModel;
import com.fourvector.apps.dabble.model.user.DeviceTokenModel;
import com.fourvector.apps.dabble.model.user.UserEmailModel;
import com.fourvector.apps.dabble.model.user.UserSubscriptionModel;

/**
 * @author Anantha.Sharma
 */
public class BaseDAO extends HibernateDaoSupport implements IBaseDAO {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BaseDAO.class);

	/**
	 * 
	 */
	public BaseDAO() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#saveObject(com.fourvector.apps.dabble.model.BaseModel)
	 */
	@Override
	public Serializable saveObject(BaseModel baseModel) {
		LOG.trace("Method [saveObject]: Called ({}) ", baseModel);
		if (baseModel instanceof Auditable) {
			if(baseModel.getCreatedOn() == null) {
				baseModel.setCreatedOn(new Date());
			}	
			baseModel.setLastUpdatedOn(new Date());
		}
		Serializable returnValue = getHibernateTemplate().save(baseModel);

		LOG.trace("Method [saveObject]: Returning " + returnValue);
		return returnValue;

	}

	@SuppressWarnings("unchecked")
	@Override
	public String[] getDeviceIds(Integer id) {
		LOG.debug("Method [getDeviceIds]: Called");
		String[] deviceTokens = new String[] {};
		String hql = "from " + DeviceTokenModel.class.getName() + " p where p.user.id=?";
		List<DeviceTokenModel> deviceTokenList = (List<DeviceTokenModel>) runQuery(hql, id);
		if (deviceTokenList != null) {
			deviceTokens = new String[deviceTokenList.size()];
			for (int i = 0; i < deviceTokenList.size(); i++) {
				deviceTokens[i] = deviceTokenList.get(i).getDeviceToken();
			}
		}
		LOG.debug("Method [getDeviceIds]: Returning {}", (Object[]) deviceTokens);
		return deviceTokens;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#getActiveEmail(java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getActiveEmail(Integer userId) {
		LOG.trace("Method [getActiveEmail]: Called " + userId);
		Session session = getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(UserEmailModel.class);
		criteria.add(Restrictions.eq("user.id", userId));
		criteria.add(Restrictions.eq("status", 0));
		List<UserEmailModel> userEmailList = criteria.list();
		if (userEmailList != null && !userEmailList.isEmpty()) {
			String email = userEmailList.get(0).getEmail();
			LOG.trace("Method [getActiveEmail]: Returning " + email);
			return email;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#saveObjects(java.util.List)
	 */
	@Override
	public void saveObjects(List<? extends BaseModel> baseModels) {
		LOG.trace("Method [saveObjects]: Called ");
		if (baseModels != null) {
			LOG.debug("Preparing to save {} items.", baseModels.size());
			for (BaseModel baseModel : baseModels) {
				this.saveObject(baseModel);
			}
		}
		LOG.trace("Method [saveObjects]: Returning ");

	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#updateObject(com.fourvector.apps.dabble.model.BaseModel)
	 */
	@Override
	public void updateObject(BaseModel baseModel) {
		LOG.trace("Method [updateObject]: Called ({}) ", baseModel);
		if (baseModel instanceof Auditable) {
			baseModel.setLastUpdatedOn(new Date());
		}
		getHibernateTemplate().saveOrUpdate(baseModel);

		LOG.trace("Method [updateObject]: Returning ");

	}

	@Override
	public void deleteObject(BaseModel entity) {
		LOG.debug("Method [deleteObject]: Called {}", entity);

		getHibernateTemplate().delete(entity);

		LOG.debug("Method [deleteObject]: Returning.");

	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#findAll(java.lang.Class)
	 */
	@Override
	public List<? extends BaseModel> findAll(Class<? extends BaseModel> modelClass) {
		LOG.trace("Method [findAll]: Called ({})", modelClass);
		List<? extends BaseModel> results = getHibernateTemplate().loadAll(modelClass);
		LOG.trace("Method [findAll]: Returning {} items", results.size());
		return results;

	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#findById(java.lang.Class, java.io.Serializable)
	 */
	@Override
	public <T> T findById(final Class<T> modelClass, Serializable id) {
		LOG.trace("Method [findById]: Called ({},{})", modelClass, id);

		T returnValue = null;
		if (id != null) {
			returnValue = getHibernateTemplate().get(modelClass, id);
		}

		LOG.trace("Method [findById]: Returning {}", returnValue);
		return returnValue;

	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#runQuery(java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<? extends BaseModel> runQuery(String query, Map<String, Object> params) {
		LOG.trace("Method [runQuery]: Called ({},{})", query, params.size());
		Session session = getSessionFactory().getCurrentSession();
		if (session == null) {
			session = getSessionFactory().openSession();
		}

		Query hqlQuery = session.createQuery(query);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			hqlQuery.setParameter(entry.getKey(), entry.getValue());
		}

		List<? extends BaseModel> results = hqlQuery.list();

		LOG.trace("Method [runQuery]: Returning {} items", results.size());
		return results;

	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#runQuery(java.lang.String, java.util.List)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<? extends BaseModel> runQuery(String query, List<Object> params) {
		LOG.trace("Method [runQuery]: Called ({},{})", query, params.size());

		Session session = getSessionFactory().getCurrentSession();
		if (session == null) {
			session = getSessionFactory().openSession();
		}

		Query hqlQuery = session.createQuery(query);
		for (int i = 0; i < params.size(); i++) {
			Object param = params.get(i);
			hqlQuery.setParameter(i, param);
		}

		List<? extends BaseModel> results = hqlQuery.list();

		Set<? extends BaseModel> resultsInSet = new LinkedHashSet<>(results);
		List<? extends BaseModel> resultsAsUniqueList = new ArrayList<>(resultsInSet);

		LOG.trace("Method [runQuery]: Returning {} items", resultsAsUniqueList.size());
		return resultsAsUniqueList;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#runQuery(java.lang.String, java.util.List)
	 */
	@Override
	public Integer runUpdateQuery(String query, Object... params) {
		LOG.trace("Method [runUpdateQuery]: Called ({},{})", query, Array.getLength(params));

		Session session = getSessionFactory().getCurrentSession();
		if (session == null) {
			session = getSessionFactory().openSession();
		}

		Query hqlQuery = session.createQuery(query);
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			hqlQuery.setParameter(i, param);
		}

		Integer results = hqlQuery.executeUpdate();

		LOG.trace("Method [runQuery]: Returning, {} row(s) affected", results);
		return results;

	}

	@Override
	public Serializable runSingleResponseQuery(String query, Object... params) {
		LOG.debug("Method [runSingleResponseQuery]: Called");
		Session session = getSessionFactory().getCurrentSession();
		if (session == null) {
			session = getSessionFactory().openSession();
		}

		Query hqlQuery = session.createQuery(query);
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			hqlQuery.setParameter(i, param);
		}
		Serializable result = (Serializable) hqlQuery.uniqueResult();
		LOG.debug("Method [runSingleResponseQuery]: Returning {}", result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IBaseDAO#runQuery(java.lang.String, java.util.List)
	 */
	@Override
	public List<? extends BaseModel> runQuery(String query, Object... params) {
		return runQuery(query, Arrays.asList(params));
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserSubscriptionModel getActiveSubscription(Integer userId) {
		LOG.debug("Method [getActiveSubscription]: Called");
		String hql = "from " + UserSubscriptionModel.class.getName() + " p where p.user.id=? and p.status in (?,?) and current_date() between p.subscriptionDate and p.expiryDate";
		UserSubscriptionModel result = null;
		List<UserSubscriptionModel> results = (List<UserSubscriptionModel>) getHibernateTemplate().find(hql, userId, Status.ACTIVE, Status.PENDING_VERIFICATION);

		for (UserSubscriptionModel usm : results) {
			if (usm.getStatus() == Status.PENDING_VERIFICATION && results.size() == 1) {
				// looks like the previous subscription has expired and the new one has taken effect.
				usm.setStatus(Status.ACTIVE);
				updateObject(usm);
				result = usm;
				break;
			}
			if (usm.getStatus() == Status.ACTIVE) {
				// looks like the previous subscription has expired and the new one has taken effect.
				result = usm;
				break;
			}
		}
		if (result != null) {
			result = results.get(0);
			String jobHql = " select count(p) from " + JobModel.class.getName() + " p where p.createdOn >= ? and p.status<>?";
			String bidHql = " select count(p) from " + BidModel.class.getName() + " p where p.createdOn >= ? and p.status<>?";
			Long jobCount = (Long) runSingleResponseQuery(jobHql, result.getSubscriptionDate(), Status.JOB_OR_BID_RETRACTED);
			Long bidCount = (Long) runSingleResponseQuery(bidHql, result.getSubscriptionDate(), Status.JOB_OR_BID_RETRACTED);
			result.setBidCount(bidCount.intValue());
			result.setJobCount(jobCount.intValue());
		} else {
			LOG.info("User doesnt have a valid subscription");
		}
		LOG.debug("Method [getActiveSubscription]: Returning {}", result);
		return result;
	}

}