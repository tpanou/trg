package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.data.fts.FullTextSearch;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.TransactionSearchCriteria;

import java.util.*;

@Repository("transactionDAO")
public class TransactionDAOImpl
		implements TransactionDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Transaction findById(Integer id) {
		Transaction transaction;

		Session session = sessionFactory.getCurrentSession();
		transaction = (Transaction) session.get(Transaction.class, id);

		return transaction;
	}

	@Override
	public SearchResults<Transaction> search(TransactionSearchCriteria transactionSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<Transaction> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(Transaction.class);
		criteria.createAlias("listingInterest", "listint", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("listing", "list", JoinType.LEFT_OUTER_JOIN);

		//User Restriction
		if (transactionSearchCriteria.getUser() != null) {
			criteria.add(Restrictions.or(Restrictions.eq("seller", transactionSearchCriteria.getUser()),
					Restrictions.eq("listint.user", transactionSearchCriteria.getUser())));
		}

		//Completed Restriction
		if (transactionSearchCriteria.getCompleted() != null) {
			if (transactionSearchCriteria.getCompleted()) {
				criteria.add(Restrictions.eq("completed", true));
			} else {
				criteria.add(Restrictions.eq("completed", false));
			}
		}

		//Active Restriction
		if (transactionSearchCriteria.getListingEnabled() != null) {
			if (transactionSearchCriteria.getListingEnabled()) {
				criteria.add(Restrictions.eq("list.enabled", true));
			} else {
				criteria.add(Restrictions.eq("list.enabled", false));
			}
		}

		//Listing Code Restricition
		if ((transactionSearchCriteria.getListingCode() != null) &&(!transactionSearchCriteria.getListingCode().equals(""))) {
			criteria.add(Restrictions.eq("list.code", transactionSearchCriteria.getListingCode()));
		}


		//Parent Category Restriction
		if ((transactionSearchCriteria.getParentCategoryId() != null) && (transactionSearchCriteria.getParentCategoryId() != 0)) {
			criteria.createAlias("list.productCategory", "category");
			criteria.add(Restrictions.eq("category.parentCategoryId", transactionSearchCriteria.getParentCategoryId()));
		}

		//ProductCategory Restriction
		if ((transactionSearchCriteria.getProductCategory() != null) && (transactionSearchCriteria.getProductCategory()
				.getId() != - 1)) {

			criteria.add(Restrictions.eq("list.productCategory", transactionSearchCriteria.getProductCategory()));
		}

		//TransactionType Restriction
		if ((transactionSearchCriteria.getTransactionType() != null) && (transactionSearchCriteria.getTransactionType()
				.getId() != - 1)) {

			criteria.add(Restrictions.eq("list.transactionType", transactionSearchCriteria.getTransactionType()));
		}

		//Keyword
		if (transactionSearchCriteria.getListingKeyword() != null) {
			FullTextSearch fullTextSearch = new FullTextSearch();
			Collection<String> keywords = fullTextSearch.transformKeyword(transactionSearchCriteria.getListingKeyword());

			if (! keywords.isEmpty()) {
				StringBuffer productNameQuery = new StringBuffer();
				StringBuffer productDescriptionQuery = new StringBuffer();
				int threshold;
				for (String keyword : keywords) {
					threshold = (int) Math.ceil(keyword.length() * 40 / 100); // 40% distance
					productNameQuery
							.append("SELECT distinct link FROM indx_listing_name WHERE levenshtein(value, '" + keyword + "') <= " + threshold + " AND substring(value, 1,3) = substring('" + keyword + "', 1,3)");
					productNameQuery.append(" INTERSECT ");

					productDescriptionQuery
							.append("SELECT distinct link FROM indx_listing_description WHERE levenshtein(value, '" + keyword + "') <= " + threshold + " AND substring(value, 1,3) = substring('" + keyword + "', 1,3)");
					productDescriptionQuery.append(" INTERSECT ");
				}
				productNameQuery = productNameQuery
						.replace(productNameQuery.lastIndexOf(" INTERSECT "), productNameQuery.length(), "");
				productDescriptionQuery = productDescriptionQuery
						.replace(productDescriptionQuery.lastIndexOf(" INTERSECT "), productDescriptionQuery.length(),
								"");

				List productNameList = session.createSQLQuery(productNameQuery.toString()).list();
				List productDescriptionList = session.createSQLQuery(productDescriptionQuery.toString()).list();

				Set<Integer> resultset = new HashSet<>();
				resultset.addAll(productNameList);
				resultset.addAll(productDescriptionList);
				if (! resultset.isEmpty()) {
					criteria.add(Restrictions.in("list.id", resultset.toArray()));
				} else {
					criteria.add(Restrictions.in("list.id", new Object[] {- 1})); // display nothing
				}
			}


//			criteria.add(Restrictions.or(Restrictions
//					.like("list.productName", transactionSearchCriteria.getListingKeyword(), MatchMode.ANYWHERE),
//					Restrictions
//							.like("list.comments", transactionSearchCriteria.getListingKeyword(), MatchMode.ANYWHERE)));
		}

		//Date restriction
		if ((transactionSearchCriteria.getDateFrom() != null) && (transactionSearchCriteria.getDateTo() != null)) {
			criteria.add(Restrictions.ge("transactionDate", transactionSearchCriteria.getDateFrom()));
			criteria.add(Restrictions.le("transactionDate", transactionSearchCriteria.getDateTo()));
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(transactionSearchCriteria.getPageNumber() * transactionSearchCriteria.getPageSize());
		criteria.setMaxResults(transactionSearchCriteria.getPageSize());

		//Sorting
		if (transactionSearchCriteria.getOrderField() != null) {
			if (transactionSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(transactionSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(transactionSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}

	@Override
	public void save(Transaction transaction) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(transaction);
	}

	@Override
	public void delete(Transaction transaction) {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
		session.delete(transaction);
	}

	@Override
	public Collection<Transaction> findTransactionsByDates(Date dateFrom, Date dateTo) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Transaction> transactions;
		Criteria criteria = session.createCriteria(Transaction.class);

		criteria.add(Restrictions.ge("transactionDate", dateFrom));
		criteria.add(Restrictions.le("transactionDate", dateTo));
		transactions = criteria.list();

		return transactions;
	}
}
