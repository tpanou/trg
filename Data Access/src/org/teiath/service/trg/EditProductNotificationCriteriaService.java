package org.teiath.service.trg;

import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface EditProductNotificationCriteriaService {

	public void saveProductNotificationCriteria(ProductNotificationCriteria productNotificationCriteria)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategories()
			throws ServiceException;

	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException;

	public Collection<ProductStatus> getProductStatuses()
			throws ServiceException;

	public ProductNotificationCriteria getProductNotificationCriteriaById(Integer notificationCriteriaId)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategoriesByParentCategoryId(Integer parentCategoryId)
			throws ServiceException;
}
