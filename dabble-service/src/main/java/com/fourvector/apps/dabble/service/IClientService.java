/**
 * 
 */
package com.fourvector.apps.dabble.service;

import java.util.List;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author asharma
 */
public interface IClientService {

	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public List<Object[]> runQuery(String query);

}
