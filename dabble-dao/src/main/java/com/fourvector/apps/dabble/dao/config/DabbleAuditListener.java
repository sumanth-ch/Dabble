/**
 * 
 */
package com.fourvector.apps.dabble.dao.config;

import java.util.Date;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

import com.fourvector.apps.dabble.model.Auditable;
import com.fourvector.apps.dabble.model.BaseModel;

/**
 * @author asharma
 */
public class DabbleAuditListener implements PreInsertEventListener, PreUpdateEventListener {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DabbleAuditListener.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 2535632571407338349L;

	/**
	 * 
	 */
	public DabbleAuditListener() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see org.hibernate.event.spi.PreInsertEventListener#onPreInsert(org.hibernate.event.spi.PreInsertEvent)
	 */
	@Override
	public boolean onPreInsert(PreInsertEvent event) {
		LOG.debug("Method [onPreInsert]: Called");

		if (event.getEntity() instanceof Auditable) {
			LOG.debug("Creating auditable logs for {}", event.getEntity().getClass());
			((BaseModel) event.getEntity()).setCreatedOn(new Date());
		}

		LOG.debug("Method [onPreInsert]: Returning.");
		return true;
	}

	@Override
	public boolean onPreUpdate(PreUpdateEvent event) {
		LOG.debug("Method [onPreUpdate]: Called");
		if (event.getEntity() instanceof Auditable) {
			LOG.debug("updating auditable logs for {}", event.getEntity().getClass());
			((BaseModel) event.getEntity()).setLastUpdatedOn(new Date());
		}
		LOG.debug("Method [onPreUpdate]: Returning.");
		return true;
	}

}
