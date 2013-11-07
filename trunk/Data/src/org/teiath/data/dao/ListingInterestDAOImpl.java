package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;

@Repository("listingInterestDAO")
public class ListingInterestDAOImpl
		implements ListingInterestDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public ListingInterest findById(Integer id) {
		ListingInterest listingInterest;

		Session session = sessionFactory.getCurrentSession();
		listingInterest = (ListingInterest) session.get(ListingInterest.class, id);

		return listingInterest;
	}

	@Override
	public SearchResults<ListingInterest> search(ListingInterestSearchCriteria listingInterestSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<ListingInterest> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(ListingInterest.class);
		criteria.createAlias("listing", "list");

		//Status Restriction
		if (listingInterestSearchCriteria.getStatus() != null) {
			criteria.add(Restrictions.eq("status", listingInterestSearchCriteria.getStatus()));
		}

		//Buyer Restriction
		if (listingInterestSearchCriteria.getBuyer() != null) {
			criteria.add(Restrictions.eq("user", listingInterestSearchCriteria.getBuyer()));
		}

		//Listing Creator Restriction
		if (listingInterestSearchCriteria.getUser() != null) {
			criteria.add(Restrictions.eq("list.user", listingInterestSearchCriteria.getUser()));
		}

		//Listing Code Restricition
		if ((listingInterestSearchCriteria.getListingCode() != null) && (!listingInterestSearchCriteria.getListingCode().equals(""))){
			criteria.add(Restrictions.eq("list.code", listingInterestSearchCriteria.getListingCode()));
		}

		//Parent Category Restriction
		if ((listingInterestSearchCriteria.getParentCategoryId() != null) && (listingInterestSearchCriteria.getParentCategoryId()
		!= 0)) {
			criteria.createAlias("list.productCategory", "category");
			criteria.add(Restrictions.eq("category.parentCategoryId", listingInterestSearchCriteria.getParentCategoryId()));
		}

		//ProductCategory Restriction
		if ((listingInterestSearchCriteria.getProductCategory() != null) && (listingInterestSearchCriteria
				.getProductCategory().getId() != - 1)) {

			criteria.add(Restrictions.eq("list.productCategory", listingInterestSearchCriteria.getProductCategory()));
		}

		//TransactionType Restriction
		if ((listingInterestSearchCriteria.getTransactionType() != null) && (listingInterestSearchCriteria
				.getTransactionType().getId() != - 1)) {

			criteria.add(Restrictions.eq("list.transactionType", listingInterestSearchCriteria.getTransactionType()));
		}

		//Keyword Restriction
		if (listingInterestSearchCriteria.getListingKeyword() != null) {

			criteria.add(Restrictions.or(Restrictions
					.like("list.productName", listingInterestSearchCriteria.getListingKeyword(), MatchMode.ANYWHERE),
					Restrictions.like("list.comments", listingInterestSearchCriteria.getListingKeyword(),
							MatchMode.ANYWHERE)));
		}

		//Listing Restriction
		if (listingInterestSearchCriteria.getListing() != null) {

			criteria.add(Restrictions.eq("listing.id", listingInterestSearchCriteria.getListing().getId()));
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(
				listingInterestSearchCriteria.getPageNumber() * listingInterestSearchCriteria.getPageSize());
		criteria.setMaxResults(listingInterestSearchCriteria.getPageSize());

		//Sorting
		if (listingInterestSearchCriteria.getOrderField() != null) {
			criteria.createAlias("user", "usr");
			if (listingInterestSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(listingInterestSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(listingInterestSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}

	@Override
	public void save(ListingInterest listingInterest) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(listingInterest);
	}

	@Override
	public void delete(ListingInterest listingInterest) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(listingInterest);
	}

	@Override
	public ListingInterest findByUser(Listing listing, User user) {
		ListingInterest listingInterest;

		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ListingInterest.class);

		criteria.add(Restrictions.eq("listing", listing));
		criteria.add(Restrictions.eq("user", user));

		listingInterest = (ListingInterest) criteria.uniqueResult();

		return listingInterest;
	}
}
