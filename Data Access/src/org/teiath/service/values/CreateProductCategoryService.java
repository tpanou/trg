package org.teiath.service.values;

import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.service.exceptions.ServiceException;

public interface CreateProductCategoryService {

	public void saveProductCategory(ProductCategory productCategory)
			throws ServiceException;
}
