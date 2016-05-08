/**
 * 
 */
package com.fourvector.apps.dabble.dao.config;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;

import com.fourvector.apps.dabble.model.Auditable;

/**
 * @author asharma
 */

public class AuditInterceptor extends EmptyInterceptor {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AuditInterceptor.class);

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3088022154014753934L;
	private int					updates;
	private int					creates;
	private int					loads;

	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		// do nothing
	}

	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {

		if (entity instanceof Auditable) {
			LOG.debug("Updating auditable logs for {}", entity.getClass());
			updates++;
			for (int i = 0; i < propertyNames.length; i++) {
				if ("lastUpdatedOn".equals(propertyNames[i])) {
					currentState[i] = new Date();
					return true;
				}
			}
		}
		return false;
	}

	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		if (entity instanceof Auditable) {
			loads++;
		}
		return false;
	}

	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

		if (entity instanceof Auditable) {
			creates++;
			for (int i = 0; i < propertyNames.length; i++) {
				if ("createdOn".equals(propertyNames[i])) {
					state[i] = new Date();
					return true;
				}
			}
		}
		return false;
	}

	public void afterTransactionCompletion(Transaction tx) {
		if (tx.wasCommitted()) {
			LOG.info("Creations: {}, Updates: {}, Loads: {}", creates, updates, loads);
		}
		updates = 0;
		creates = 0;
		loads = 0;
	}

}