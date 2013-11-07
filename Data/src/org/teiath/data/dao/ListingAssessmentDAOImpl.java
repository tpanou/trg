package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.ListingAssessment;

import java.util.Collection;

@Repository("listingAssessmentDAO")
public class ListingAssessmentDAOImpl
		implements ListingAssessmentDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public ListingAssessment findByBuyer(Integer listingId, Integer userId) {
		ListingAssessment listingAssessment;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ListingAssessment.class);
		criteria.createAlias("assessedTransaction", "transaction").createAlias("transaction.listingInterest", "listint")
				.createAlias("listint.listing", "list").createAlias("listint.user", "buyer");

		criteria.add(Restrictions.and(Restrictions.eq("list.id", listingId), Restrictions.eq("buyer.id", userId)));
		criteria.add(Restrictions.ne("assessedUser.id", userId));

		listingAssessment = (ListingAssessment) criteria.uniqueResult();

		return listingAssessment;
	}

	@Override
	public ListingAssessment findBySeller(Integer listingId, Integer userId) {
		ListingAssessment listingAssessment;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ListingAssessment.class);
		criteria.createAlias("assessedTransaction", "transaction").createAlias("transaction.listingInterest", "listint")
				.createAlias("listint.listing", "list").createAlias("list.user", "seller");

		criteria.add(Restrictions.and(Restrictions.eq("list.id", listingId), Restrictions.eq("seller.id", userId)));
		criteria.add(Restrictions.ne("assessedUser.id", userId));

		listingAssessment = (ListingAssessment) criteria.uniqueResult();

		return listingAssessment;
	}

	@Override
	public void save(ListingAssessment listingAssessment) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(listingAssessment);
	}

	@Override
	public Double getTransactionAverageRating(User user) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ListingAssessment.class);

		criteria.add(Restrictions.eq("assessedUser", user));
		criteria.setProjection(Property.forName("rating").avg());
		Double avgRating = (Double) criteria.uniqueResult();

		return avgRating;
	}

	@Override
	public Collection<ListingAssessment> getUserTransactionComments(User user) {
		Collection<ListingAssessment> listingAssessments;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ListingAssessment.class);

		criteria.add(Restrictions.eq("assessedUser", user));
		criteria.add(Restrictions.ne("comment", ""));
		listingAssessments = criteria.list();

		return listingAssessments;
	}
}
