package org.teiath.data.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.trg.TransactionType;

import java.util.Collection;

@Repository("transactionTypeDAO")
public class TransactionTypeDAOImpl
		implements TransactionTypeDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<TransactionType> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<TransactionType> transactionTypes;

		String hql = "from TransactionType";
		Query query = session.createQuery(hql);

		transactionTypes = query.list();

		return transactionTypes;
	}

	@Override
	public void save(TransactionType transactionType) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(transactionType);
	}

	@Override
	public TransactionType findById(Integer id) {
		TransactionType transactionType;

		Session session = sessionFactory.getCurrentSession();
		transactionType = (TransactionType) session.get(TransactionType.class, id);

		return transactionType;
	}

	@Override
	public void delete(TransactionType transactionType) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(transactionType);
		session.flush();
	}
}
