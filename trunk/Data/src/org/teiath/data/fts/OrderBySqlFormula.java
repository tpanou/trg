package org.teiath.data.fts;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

public class OrderBySqlFormula extends Order {
	private String sqlFormula;

	/**
	 * Constructor for Order.
	 * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
	 */
	protected OrderBySqlFormula(String sqlFormula) {
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
	 * @param sqlFormula an SQL formula that will be appended to the resulting SQL query
	 * @return Order
	 */
	public static Order sqlFormula(String sqlFormula) {
		return new OrderBySqlFormula(sqlFormula);
	}
}
