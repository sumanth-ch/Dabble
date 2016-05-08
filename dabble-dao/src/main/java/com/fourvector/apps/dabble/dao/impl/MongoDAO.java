/**
 * 
 */
package com.fourvector.apps.dabble.dao.impl;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.fourvector.apps.dabble.common.dto.BaseDTO;
import com.fourvector.apps.dabble.common.dto.ChatDTO;
import com.fourvector.apps.dabble.dao.IMongoDAO;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * @author asharma
 */
public class MongoDAO implements IMongoDAO {
	private static final Logger LOG = LoggerFactory.getLogger(MongoDAO.class);

	private MongoTemplate mongoTemplate;

	/**
	 * 
	 */
	public MongoDAO() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IMongoDAO#findJobs(org.springframework.data.geo.Point, java.lang.Integer, java.util.List, java.util.List, java.lang.String)
	 */
	@Override
	public List<Integer> findJobs(Point userLocation, Integer distanceInMeters) {
		LOG.debug("Method [findJobs]: Called");

		String TEMPLATE = "{ locationPoint :{ $near :{ $geometry :{ type : 'Point' ,coordinates : [ %s , %s ] } ,$maxDistance : %s     } } }";
		DBObject object = (DBObject) JSON.parse(String.format(TEMPLATE, userLocation.getX(), userLocation.getY(), distanceInMeters));
		DBObject projection = BasicDBObjectBuilder.start().add("jobId", 1).get();
		DBCursor cursor = mongoTemplate.getCollection("jobs").find(object, projection);
		List<Integer> jobIds = new LinkedList<>();
		while (cursor.iterator().hasNext()) {
			jobIds.add((Integer) cursor.iterator().next().get("jobId"));
		}
		LOG.debug("Method [findJobs]: Returning {} elements", jobIds.size());

		return jobIds;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IMongoDAO#updateDTO(com.fourvector.apps.dabble.dto.BaseDTO)
	 */
	@Override
	public void updateDTO(BaseDTO baseDto) {
		// TODO Auto-generated method stub
		LOG.debug("Method [updateDTO]: Called");
// mongoTemplate.upsert(baseDto);

// Query query = Query.query(Criteria.where("objectId").equals(baseDto.))

		LOG.debug("Method [updateDTO]: Returning.");

	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IMongoDAO#insertDTO(com.fourvector.apps.dabble.dto.BaseDTO)
	 */
	@Override
	public void insertDTO(BaseDTO baseDto) {
		LOG.debug("Method [insertDTO]: Called");
		mongoTemplate.save(baseDto);
		LOG.debug("Method [insertDTO]: Returning.");

	}

	@Override
	public List<ChatDTO> findChatsFor(Integer userId, Long token, Integer senderId) {
		LOG.debug("Method [findChatsFor]: Called");

		List<ChatDTO> result = null;

		Query q = new Query();
		q.addCriteria(Criteria.where("reciever").in(userId, senderId));
		q.addCriteria(Criteria.where("sender").in(userId, senderId));
// q.addCriteria(Criteria.where("messageTime").gte(token));

		result = mongoTemplate.find(q, ChatDTO.class);

		LOG.debug("Method [findChatsFor]: Returning {}", result);

		return result;
	}

	/**
	 * @return the mongoTemplate
	 */
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	/**
	 * @param mongoTemplate
	 *            the mongoTemplate to set
	 */
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
}
