package org.teiath.service.trg;

import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListingsProductCategoryDialogService {

	public Collection<ProductCategory> getProductCategories()
			throws ServiceException;
}
