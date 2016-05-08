/**
 * 
 */
package com.fourvector.apps.dabble.dao.config;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

/**
 * @author asharma
 */
public class SQLOrder extends Order {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 706340720145125218L;
	private String				sqlFormula;

	/**
	 * Constructor for Order.
	 * 
	 * @param sqlFormula
	 *            an SQL formula that will be appended to the resulting SQL query
	 */
	protected SQLOrder(String sqlFormula) {
		super(sqlFormula, true);
		this.sqlFormula = sqlFormula;
	}

	public String toString() {
		return sqlFormula;
	}

	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
		return sqlFormula;
	}

	/**
	 * Custom order
	 *
	 * @param sqlFormula
	 *            an SQL formula that will be appended to the resulting SQL query
	 * @return Order
	 */
	public static Order sqlFormula(String sqlFormula) {
		return new SQLOrder(sqlFormula);
	}
}
