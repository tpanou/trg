package org.teiath.service.values;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.CurrencyDAO;
import org.teiath.data.dao.ProductCategoryDAO;
import org.teiath.data.dao.ProductStatusDAO;
import org.teiath.data.dao.TransactionTypeDAO;
import org.teiath.data.domain.trg.Currency;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("listValuesService")
@Transactional
public class ListValuesServiceImpl
		implements ListValuesService {

	@Autowired
	ProductCategoryDAO productCategoryDAO;
	@Autowired
	ProductStatusDAO productStatusDAO;
	@Autowired
	TransactionTypeDAO transactionTypeDAO;
	@Autowired
	CurrencyDAO currencyDAO;

	@Override
	public SearchResults<ProductCategory> searchProductCategoriesByCriteria(SearchCriteria searchCriteria)
			throws ServiceException {
		SearchResults<ProductCategory> results;

		try {
			results = productCategoryDAO.search(searchCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<Currency> getCurrencies()
			throws ServiceException {
		Collection<Currency> currencies;

		try {
			currencies = currencyDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return currencies;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteProductCategory(ProductCategory productCategory)
			throws ServiceException, DeleteViolationException {
		try {
			productCategoryDAO.delete(productCategory);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public Collection<ProductStatus> getProductStatuses()
			throws ServiceException {
		Collection<ProductStatus> productStatuses;

		try {
			productStatuses = productStatusDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return productStatuses;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteProductStatus(ProductStatus productStatus)
			throws ServiceException, DeleteViolationException {
		try {
			productStatusDAO.delete(productStatus);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteTransactionType(TransactionType transactionType)
			throws ServiceException, DeleteViolationException {
		try {
			transactionTypeDAO.delete(transactionType);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteCurrency(Currency currency)
			throws ServiceException, DeleteViolationException {
		try {
			currencyDAO.delete(currency);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
