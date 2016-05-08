/**
 * 
 */
package com.fourvector.apps.dabble.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.jdbc.Work;

import com.fourvector.apps.dabble.dao.IClientDAO;

/**
 * @author asharma
 */
public class ClientDAO extends BaseDAO implements IClientDAO {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ClientDAO.class);

	/**
	 * 
	 */
	public ClientDAO() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see com.fourvector.apps.dabble.dao.IClientDAO#runQuery(java.lang.String)
	 */
	@Override
	public List<Object[]> runQuery(final String query) {
		LOG.debug("Method [runQuery]: Called");

		final List<Object[]> result = new LinkedList<>();
		try {
			getSessionFactory().getCurrentSession().doWork(new Work() {
				@Override
				public void execute(Connection connection) throws SQLException {
					// TODO Auto-generated method stub
					LOG.debug("Method [execute]: Called");
					PreparedStatement ps = connection.prepareStatement(query);
					ps.execute();
					ResultSet rs = ps.getResultSet();
					if (rs == null) {
						// we didn't get any results.
						LOG.error("No Resultset object found");
						result.add(new String[] { "No results." });
						return;
					}

					ResultSetMetaData rsmd = rs.getMetaData();
					int colCount = rsmd.getColumnCount();
					Object[] arr_fields = new Object[colCount];
					for (int i = 0; i < colCount; i++) {
						arr_fields[i] = rsmd.getColumnLabel(i + 1);
					}
					result.add(arr_fields);
					rs.first();
					while (rs.next() && !rs.isLast()) {
						Object[] arr_data = new Object[colCount];
						for (int i = 0; i < colCount; i++) {
							arr_data[i] = rs.getString(i + 1).toString();
						}
						result.add(arr_data);
					}
					LOG.debug("Method [execute]: Returning.");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOG.debug("Method [runQuery]: Returning {}", result);

		return result;
	}

}
