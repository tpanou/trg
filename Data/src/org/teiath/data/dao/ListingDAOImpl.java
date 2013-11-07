package org.teiath.data.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.fts.FullTextSearch;
import org.teiath.data.fts.OrderBySqlFormula;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.data.search.ListingSearchCriteria;

import java.util.*;

@Repository("listingDAO")
public class ListingDAOImpl
		implements ListingDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Listing findById(Integer id) {
		Listing listing;

		Session session = sessionFactory.getCurrentSession();
		listing = (Listing) session.get(Listing.class, id);

		return listing;
	}

	@Override
	public Listing findByCode(String code) {
		Listing listing;

		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(Listing.class);

		criteria.add(Restrictions.eq("code", code));
		listing = (Listing) criteria.uniqueResult();

		return listing;
	}

	@Override
	public SearchResults<Listing> search(ListingSearchCriteria listingSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<Listing> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(Listing.class);
		criteria.createAlias("transactionType", "transaction");
		criteria.createAlias("productCategory", "category");
		criteria.createAlias("productStatus", "status");

		//User Restriction
		if (listingSearchCriteria.getUser() != null) {
			criteria.add(Restrictions.eq("user", listingSearchCriteria.getUser()));
		}

		//Code Restriction
		if ((listingSearchCriteria.getCode() != null) && !(listingSearchCriteria.getCode().equals(""))) {
			criteria.add(Restrictions.eq("code", listingSearchCriteria.getCode()));
		}

		//TransactionType Restriction
		if ((listingSearchCriteria.getTransactionType() != null) && (listingSearchCriteria.getTransactionType()
				.getId() != - 1)) {

			criteria.add(Restrictions.eq("transactionType", listingSearchCriteria.getTransactionType()));
		}

		//TransactionType Restriction by name
		if (listingSearchCriteria.getTransactionTypeName() != null) {
			criteria.add(Restrictions.eq("transaction.name", listingSearchCriteria.getTransactionTypeName()));
		}

		//Parent Category restriction
		if ((listingSearchCriteria.getParentCategoryId() != null) && (listingSearchCriteria.getParentCategoryId() != 0)) {
			criteria.add(Restrictions.eq("category.parentCategoryId", listingSearchCriteria.getParentCategoryId()));
		}

		//ProductCategory Restriction
		if ((listingSearchCriteria.getProductCategory() != null) && (listingSearchCriteria.getProductCategory()
				.getId() != - 1)) {

			criteria.add(Restrictions.eq("productCategory", listingSearchCriteria.getProductCategory()));
		}

		//ProductCategory Restriction by name
		if (listingSearchCriteria.getProductCategoryName() != null) {
			criteria.add(Restrictions.eq("category.name", listingSearchCriteria.getProductCategoryName()));
		}

		//ProductStatus Restriction
		if ((listingSearchCriteria.getProductStatus() != null) && (listingSearchCriteria.getProductStatus()
				.getId() != - 1)) {

			criteria.add(Restrictions.eq("productStatus", listingSearchCriteria.getProductStatus()));
		}

		//ProductStatus Restriction by name
		if (listingSearchCriteria.getProductStatusName() != null) {
			criteria.add(Restrictions.eq("status.name", listingSearchCriteria.getProductStatusName()));
		}

		//Date restriction
		if ((listingSearchCriteria.getDateFrom() != null) && (listingSearchCriteria.getDateTo() != null)) {
			criteria.add(Restrictions.ge("purchaseDate", listingSearchCriteria.getDateFrom()));
			criteria.add(Restrictions.le("purchaseDate", listingSearchCriteria.getDateTo()));
		}

		//Price restriction
		if (listingSearchCriteria.getMaxAmount() != null) {
			criteria.add(Restrictions.le("price", listingSearchCriteria.getMaxAmount()));
		}

		//Active restriction
		if (listingSearchCriteria.isActive()) {
			criteria.add(Restrictions.eq("active", true));
		}

		//Enabled restriction
		if (listingSearchCriteria.isEnabled()) {
			criteria.add(Restrictions.eq("enabled", true));
		}

		//Keyword
		if (listingSearchCriteria.getListingKeyword() != null) {
			FullTextSearch fullTextSearch = new FullTextSearch();
			Collection<String> keywords = fullTextSearch.transformKeyword(listingSearchCriteria.getListingKeyword());

			if (! keywords.isEmpty()) {
				StringBuffer productNameQuery = new StringBuffer();
				StringBuffer productDescriptionQuery = new StringBuffer();
				int threshold;
				for (String keyword : keywords) {
					threshold = (int) Math.ceil(keyword.length() * 50 / 100); // 50% distance
					productNameQuery
							.append("SELECT distinct link, levenshtein(value, '" + keyword + "') as rank FROM indx_listing_name WHERE levenshtein(value, '" + keyword + "') <= " + threshold + " AND substring(value, 1,3) = substring('" + keyword + "', 1,3)");
					productNameQuery.append(" UNION ");

					productDescriptionQuery
							.append("SELECT distinct link, levenshtein(value, '" + keyword + "') as rank FROM indx_listing_description WHERE levenshtein(value, '" + keyword + "') <= " + threshold + " AND substring(value, 1,3) = substring('" + keyword + "', 1,3)");
					productDescriptionQuery.append(" UNION ");
				}
				productNameQuery = productNameQuery.replace(productNameQuery.lastIndexOf(" UNION "), productNameQuery.length(), "");
				productNameQuery.insert(0, "(");
				productNameQuery.append(")");

				productDescriptionQuery = productDescriptionQuery.replace(productDescriptionQuery.lastIndexOf(" UNION "), productDescriptionQuery.length(), "");
				productDescriptionQuery.insert(0, "(");
				productDescriptionQuery.append(")");

				String q = "(" + productNameQuery.toString() + " UNION " + productDescriptionQuery.toString() + ") ORDER BY rank ASC";
				System.out.println(q);

				List productIndexList = session.createSQLQuery(q).list();

				List<Integer> resultset = new ArrayList<>();

				Iterator iterator = productIndexList.iterator();
				while (iterator.hasNext()) {
					Object[] row = (Object[])iterator.next();
					resultset.add((Integer) row[0]);
				}

				if (! resultset.isEmpty()) {
					criteria.add(Restrictions.in("id", resultset.toArray()));
					if (listingSearchCriteria.getSortOrder() == 1) { // Text Relevance
						String sortOrder = "";
						for (Integer id: resultset) {
							sortOrder += "this_.listing_id = " + id + ",";
						}
						sortOrder = sortOrder.substring(0, sortOrder.length()-1);
						criteria.addOrder(OrderBySqlFormula.sqlFormula(sortOrder));
					} else { // Creation Date
						criteria.addOrder(Order.desc("listingCreationDate"));
					}
				} else {
					criteria.add(Restrictions.in("id", new Object[] {-1})); // display nothing
				}
			}
		} else {
			//Sorting
			if (listingSearchCriteria.getOrderField() != null) {
				if (listingSearchCriteria.getOrderDirection().equals("ascending")) {
					criteria.addOrder(Order.asc(listingSearchCriteria.getOrderField()));
				} else {
					criteria.addOrder(Order.desc(listingSearchCriteria.getOrderField()));
				}
			}
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(listingSearchCriteria.getPageNumber() * listingSearchCriteria.getPageSize());
		criteria.setMaxResults(listingSearchCriteria.getPageSize());

		//Fetch data
		results.setData(criteria.list());

		return results;
	}

	@Override
	public void save(Listing listing) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(listing);
	}

	@Override
	public void copy(Listing listing) {
		Session session = sessionFactory.getCurrentSession();
		session.save(listing);
	}

	@Override
	public void delete(Listing listing) {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
		session.delete(listing);
	}

	@Override
	public SearchResults<Listing> findCommonListings(Listing listing, ListingInterest listingInterest,
	                                                 ListingInterestSearchCriteria listingInterestSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<Listing> results = new SearchResults<>();
		String orderField;
		String orderDirection;

		//sorting
		if (listingInterestSearchCriteria.getOrderField() != null) {

			orderField = listingInterestSearchCriteria.getOrderField();
			if (listingInterestSearchCriteria.getOrderDirection().equals("ascending")) {
				orderDirection = "ASC";
			} else {
				orderDirection = "DESC";
			}
		} else {
			orderField = "id";
			orderDirection = "ASC";
		}

		String hql = "select listing " +
				"from Listing as listing, ListingInterest as listingInterest " +
				"where listing.user.id=:sellerId " +
				"and listing.id = listingInterest.listing.id " +
				"and listingInterest.user.id=:buyerId " +
				"and listingInterest.status=:routeInterestStatus " +
				"order by listing." + orderField + " " + orderDirection;

		Query query = session.createQuery(hql);
		query.setParameter("sellerId", listing.getUser().getId());
		query.setParameter("buyerId", listingInterest.getUser().getId());
		query.setParameter("routeInterestStatus", listingInterest.STATUS_APPROVED);

		//Total records
		results.setTotalRecords(query.list().size());

		//paging
		query.setFirstResult(
				listingInterestSearchCriteria.getPageNumber() * listingInterestSearchCriteria.getPageSize());
		query.setMaxResults(listingInterestSearchCriteria.getPageSize());

		//Fetch data
		results.setData(query.list());

		return results;
	}

	@Override
	public Collection<Listing> findCreatedListings(Date dateFrom, Date dateTo) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Listing> listings;
		Criteria criteria = session.createCriteria(Listing.class);

		criteria.add(Restrictions.ge("listingCreationDate", dateFrom));
		criteria.add(Restrictions.le("listingCreationDate", dateTo));
		listings = criteria.list();

		return listings;
	}

	@Override
	public Collection<Listing> findListingsByTransactionType(TransactionType transactionType) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Listing> listings;
		Criteria criteria = session.createCriteria(Listing.class);

		criteria.add(Restrictions.eq("transactionType", transactionType));
		listings = criteria.list();

		return listings;
	}

	@Override
	public Collection<Listing> findListingsByProductCategory(ProductCategory productCategory) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Listing> listings;
		Criteria criteria = session.createCriteria(Listing.class);

		criteria.add(Restrictions.eq("productCategory", productCategory));
		listings = criteria.list();

		return listings;
	}

}
