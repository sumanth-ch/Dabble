/**
 * 
 */
package com.fourvector.apps.dabble.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fourvector.apps.dabble.dao.IClientDAO;
import com.fourvector.apps.dabble.service.IClientService;

/**
 * @author asharma
 */
public class ClientService implements IClientService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ClientService.class);

	private IClientDAO clientDAO;

	/**
	 * 
	 */
	public ClientService() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IClientDAO#runQuery(java.lang.String)
	 */
	@Override
	public List<Object[]> runQuery(String query) {
		LOG.debug("Method [runQuery]: Called");
		if (StringUtils.isBlank(query)) {
			return new ArrayList<>();
		}
		List<Object[]> result = clientDAO.runQuery(query);
		LOG.debug("Method [runQuery]: Returning {}", result);

		return result;
	}

	/**
	 * @return the clientDAO
	 */
	public IClientDAO getClientDAO() {
		return clientDAO;
	}

	/**
	 * @param clientDAO
	 *            the clientDAO to set
	 */
	public void setClientDAO(IClientDAO clientDAO) {
		this.clientDAO = clientDAO;
	}

}
