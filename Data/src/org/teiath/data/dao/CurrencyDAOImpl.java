package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.trg.Currency;

import java.util.Collection;

@Repository("currencyDAO")
public class CurrencyDAOImpl
		implements CurrencyDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<Currency> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<Currency> currencies;

		String hql = "from Currency currency order by currency.code asc";
		Query query = session.createQuery(hql);

		currencies = query.list();

		return currencies;
	}

	@Override
	public void save(Currency currency) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(currency);
	}

	@Override
	public void delete(Currency currency) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(currency);
		session.flush();
	}

	@Override
	public Currency findDefaultCurrency() {
		Session session = sessionFactory.getCurrentSession();
		Currency currency;

		Criteria criteria = session.createCriteria(Currency.class);

		criteria.add(Restrictions.eq("defaultCurrency", true));
		currency = (Currency) criteria.uniqueResult();

		return currency;
	}

	@Override
	public Currency findById(Integer id) {
		Currency currency;

		Session session = sessionFactory.getCurrentSession();
		currency = (Currency) session.get(Currency.class, id);

		return currency;
	}
}
