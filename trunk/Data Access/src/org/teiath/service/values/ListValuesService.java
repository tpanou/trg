package org.teiath.service.values;

import org.teiath.data.domain.trg.Currency;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListValuesService {

	public SearchResults<ProductCategory> searchProductCategoriesByCriteria(SearchCriteria searchCriteria)
			throws ServiceException;

	public Collection<Currency> getCurrencies()
			throws ServiceException;

	public void deleteProductCategory(ProductCategory productCategory)
			throws ServiceException, DeleteViolationException;

	public Collection<ProductStatus> getProductStatuses()
			throws ServiceException;

	public void deleteProductStatus(ProductStatus productStatus)
			throws ServiceException, DeleteViolationException;

	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException;

	public void deleteTransactionType(TransactionType transactionType)
			throws ServiceException, DeleteViolationException;

	public void deleteCurrency(Currency currency)
			throws ServiceException, DeleteViolationException;
}

