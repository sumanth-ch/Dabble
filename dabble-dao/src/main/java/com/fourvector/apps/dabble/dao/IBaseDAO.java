/**
 * 
 */
package com.fourvector.apps.dabble.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fourvector.apps.dabble.model.BaseModel;
import com.fourvector.apps.dabble.model.user.UserSubscriptionModel;

/**
 * @author Anantha.Sharma
 */
public interface IBaseDAO {
	/**
	 * saves a given object into the database.
	 * 
	 * @param baseModel
	 * @return
	 */
	Serializable saveObject(final BaseModel baseModel);

	/**
	 * saves a collection of objects into the database.
	 * 
	 * @param baseModels
	 */
	void saveObjects(final List<? extends BaseModel> baseModels);

	/**
	 * updates an object into the database.
	 * 
	 * @param baseModel
	 */
	void updateObject(final BaseModel baseModel);

	/**
	 * returns all objects of a particular entity
	 * 
	 * @param modelClass
	 * @return
	 */
	List<? extends BaseModel> findAll(final Class<? extends BaseModel> modelClass);

	/**
	 * finds objects of type modelClass by their id
	 * 
	 * @param modelClass
	 * @param id
	 * @return
	 */
	<T> T findById(final Class<T> modelClass, Serializable id);

	/**
	 * executes a HQL query and returns the results.
	 * <strong> uses named parameters </strong>
	 * 
	 * @param query
	 * @param params
	 * @return
	 */
	List<? extends BaseModel> runQuery(String query, Map<String, Object> params);

	/**
	 * executes a HQL query and returns the results.
	 * <strong> uses named parameters </strong>
	 * 
	 * @param query
	 * @param params
	 * @return
	 */
	List<? extends BaseModel> runQuery(String query, Object... params);

	/**
	 * executes a HQL query and returns the results.
	 * <strong> uses positional parameters </strong>
	 * 
	 * @param query
	 * @param params
	 * @return
	 */
	List<? extends BaseModel> runQuery(String query, List<Object> params);

	/**
	 * fires an update query.
	 * 
	 * @param query
	 * @param params
	 * @return
	 */
	Integer runUpdateQuery(String query, Object... params);

	/**
	 * executes a HQL query and returns a single result.
	 * 
	 * @param query
	 * @param params
	 * @return
	 */
	Serializable runSingleResponseQuery(String query, Object... params);

	/**
	 * returns the active Email ID for a user. or <code>null </code> otherwise
	 * 
	 * @param userId
	 * @return
	 */
	String getActiveEmail(Integer userId);

	/**
	 * returns the given user's active subscription
	 * 
	 * @param userId
	 * @return
	 */
	UserSubscriptionModel getActiveSubscription(Integer userId);

	void deleteObject(BaseModel vkm);

	/**
	 * @param id
	 * @return
	 */
	String[] getDeviceIds(Integer id);

}
