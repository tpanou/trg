package org.teiath.service.values;

import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.service.exceptions.ServiceException;

public interface EditProductCategoryService {

	public void saveProductCategory(ProductCategory productCategory)
			throws ServiceException;

	public ProductCategory getProductCategoryById(Integer productCategoryId)
			throws ServiceException;
}
