package org.teiath.service.trg;

import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.TransactionSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListListingHistoryService {

	public SearchResults<Transaction> searchTransactionsByCriteria(TransactionSearchCriteria criteria)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategories()
			throws ServiceException;

	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException;

	public Collection<ProductCategory> getProductCategoriesByParentCategoryId(Integer parentCategoryId)
			throws ServiceException;
}
