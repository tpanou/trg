package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ProductCategoryDAO;
import org.teiath.data.dao.TransactionTypeDAO;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("selectListingReportService")
@Transactional
public class SelectListingReportServiceImpl
		implements SelectListingReportService {

	@Autowired
	TransactionTypeDAO transactionTypeDAO;
	@Autowired
	ProductCategoryDAO productCategoryDAO;

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
	public Collection<ProductCategory> getProductCategories()
			throws ServiceException {
		Collection<ProductCategory> productCategories;

		try {
			productCategories = productCategoryDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return productCategories;
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
