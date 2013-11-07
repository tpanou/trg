package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.TradeableGood;

import java.util.Collection;

@Repository("tradeableGoodDAO")
public class TradeableGoodDAOImpl
		implements TradeableGoodDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<TradeableGood> findByListing(Listing listing) {

		Session session = sessionFactory.getCurrentSession();
		Collection<TradeableGood> tradeableGoods;
		Criteria criteria = session.createCriteria(TradeableGood.class);

		criteria.add(Restrictions.eq("listing.id", listing.getId()));
		tradeableGoods = criteria.list();

		return tradeableGoods;
	}

	@Override
	public void save(TradeableGood tradeableGood) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(tradeableGood);
	}

	@Override
	public void deleteAll(Listing listing) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "delete from TradeableGood as tradeableGood " + "where tradeableGood.listing.id=:listingId";
		Query query = session.createQuery(hql);
		query.setParameter("listingId", listing.getId());

		query.executeUpdate();
	}
}
