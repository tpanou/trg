package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ProductCategoryDAO;
import org.teiath.data.dao.TransactionDAO;
import org.teiath.data.dao.TransactionTypeDAO;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.TransactionSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("listListingHistoryService")
@Transactional
public class ListListingHistoryServiceImpl
		implements ListListingHistoryService {

	@Autowired
	TransactionDAO transactionDAO;
	@Autowired
	ProductCategoryDAO productCategoryDAO;
	@Autowired
	TransactionTypeDAO transactionTypeDAO;

	@Override
	public SearchResults<Transaction> searchTransactionsByCriteria(TransactionSearchCriteria criteria)
			throws ServiceException {
		SearchResults<Transaction> results;

		try {
			results = transactionDAO.search(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<ProductCategory> getProductCategories()
			throws ServiceException {
		Collection<ProductCategory> categories;

		try {
			categories = productCategoryDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}

	@Override
	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException {
		Collection<TransactionType> transactionTypes;

		try {
			transactionTypes = transactionTypeDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return transactionTypes;
	}

	@Override
	public Collection<ProductCategory> getProductCategoriesByParentCategoryId(Integer parentCategoryId)
			throws ServiceException {
		Collection<ProductCategory> categories;

		try {
			categories = productCategoryDAO.findByParentProductCategoryId(parentCategoryId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}
}
