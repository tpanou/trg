package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;

import java.util.Collection;

@Repository("productNotificationCriteriaDAO")
public class ProductNotificationCriteriaDAOImpl
		implements ProductNotificationCriteriaDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void save(ProductNotificationCriteria productNotificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(productNotificationCriteria);
	}

	@Override
	public void delete(ProductNotificationCriteria productNotificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(productNotificationCriteria);
	}

	@Override
	public Collection<ProductNotificationCriteria> checkCriteria(Listing listing) {
		Session session = sessionFactory.getCurrentSession();
		Collection<ProductNotificationCriteria> productNotificationCriterias;
		Criteria criteria = session.createCriteria(ProductNotificationCriteria.class);

		//User Restriction
		criteria.add(Restrictions.ne("user", listing.getUser()));

		//Transaction type restriction
		criteria.add(Restrictions
				.or(Restrictions.and(Restrictions.isNull("transactionType"), Restrictions.isNotNull("id")),
						Restrictions.eq("transactionType", listing.getTransactionType())));

		//Product category restriction
		criteria.add(Restrictions
				.or(Restrictions.and(Restrictions.isNull("productCategory"), Restrictions.isNotNull("id")),
						Restrictions.eq("productCategory", listing.getProductCategory())));

		//Product status restriction
		criteria.add(Restrictions
				.or(Restrictions.and(Restrictions.isNull("productStatus"), Restrictions.isNotNull("id")),
						Restrictions.eq("productStatus", listing.getProductStatus())));

		//Date restriction
		criteria.add(Restrictions
				.or(Restrictions.and(Restrictions.isNull("purchaseDateFrom"), Restrictions.isNotNull("id")),
						Restrictions.le("purchaseDateFrom", listing.getPurchaseDate())));
		criteria.add(Restrictions
				.or(Restrictions.and(Restrictions.isNull("purchaseDateTo"), Restrictions.isNotNull("id")),
						Restrictions.ge("purchaseDateTo", listing.getPurchaseDate())));

		//Max price restriction
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("maxPrice"), Restrictions.isNotNull("id")),
				Restrictions.ge("maxPrice", listing.getPrice())));

		//Keyword restriction
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("keywords"), Restrictions.isNotNull("id")),
				Restrictions.and(Restrictions.ne("keywords", ""),
						Restrictions.like("keywords", listing.getProductName(), MatchMode.ANYWHERE),
						Restrictions.like("keywords", listing.getProductDescription(), MatchMode.ANYWHERE))));

		productNotificationCriterias = criteria.list();

		return productNotificationCriterias;
	}

	@Override
	public ProductNotificationCriteria findById(Integer id) {
		ProductNotificationCriteria productNotificationCriteria;

		Session session = sessionFactory.getCurrentSession();
		productNotificationCriteria = (ProductNotificationCriteria) session.get(ProductNotificationCriteria.class, id);

		return productNotificationCriteria;
	}

	@Override
	public SearchResults<ProductNotificationCriteria> search(
			NotificationsCriteriaSearchCriteria notificationsCriteriaSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<ProductNotificationCriteria> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(ProductNotificationCriteria.class);

		//Type
		if (notificationsCriteriaSearchCriteria.getType() != null) {
			criteria.add(Restrictions.eq("type", notificationsCriteriaSearchCriteria.getType()));
		}

		//User
		if (notificationsCriteriaSearchCriteria.getUser() != null) {
			criteria.add(Restrictions.eq("user", notificationsCriteriaSearchCriteria.getUser()));
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(
				notificationsCriteriaSearchCriteria.getPageNumber() * notificationsCriteriaSearchCriteria
						.getPageSize());
		criteria.setMaxResults(notificationsCriteriaSearchCriteria.getPageSize());

		//Sorting
		if (notificationsCriteriaSearchCriteria.getOrderField() != null) {
			if (notificationsCriteriaSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(notificationsCriteriaSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(notificationsCriteriaSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}
}
