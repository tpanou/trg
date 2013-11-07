package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ProductCategoryDAO;
import org.teiath.data.dao.ProductNotificationCriteriaDAO;
import org.teiath.data.dao.ProductStatusDAO;
import org.teiath.data.dao.TransactionTypeDAO;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("editProductNotificationCriteriaService")
@Transactional
public class EditProductNotificationCriteriaServiceImpl
		implements EditProductNotificationCriteriaService {

	@Autowired
	ProductNotificationCriteriaDAO productNotificationCriteriaDAO;
	@Autowired
	ProductCategoryDAO productCategoryDAO;
	@Autowired
	TransactionTypeDAO transactionTypeDAO;
	@Autowired
	ProductStatusDAO productStatusDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveProductNotificationCriteria(ProductNotificationCriteria productNotificationCriteria)
			throws ServiceException {
		try {
			productNotificationCriteriaDAO.save(productNotificationCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
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
	public ProductNotificationCriteria getProductNotificationCriteriaById(Integer notificationId)
			throws ServiceException {
		ProductNotificationCriteria productNotificationCriteria;
		try {
			productNotificationCriteria = productNotificationCriteriaDAO.findById(notificationId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return productNotificationCriteria;
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
