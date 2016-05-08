/**
 * 
 */
package com.fourvector.apps.dabble.dao;

import java.util.List;

/**
 * @author asharma
 */
public interface IClientDAO {
	public List<Object[]> runQuery(String query);
}
