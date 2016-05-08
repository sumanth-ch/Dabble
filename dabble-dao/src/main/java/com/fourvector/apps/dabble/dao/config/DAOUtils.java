/**
 * 
 */
package com.fourvector.apps.dabble.dao.config;

import java.util.Collection;
import java.util.Set;

import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author asharma
 */
public final class DAOUtils {

	/**
	 * 
	 */
	public DAOUtils() {
		super();
	}

	public static String showEntity(BaseModel entity) {
		if (entity == null) {
			return null;
		}
		return "" + entity.getId();
	}

	public static Object showEntity(Collection<? extends BaseModel> entities) {
		if (entities == null) {
			return null;
		}
		return "" + entities.size();
	}

	public static Object showEntity(Set<? extends BaseModel> entities) {
		if (entities == null) {
			return null;
		}
		return "" + entities.size();
	}

}
