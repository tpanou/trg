package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ProductCategoryDAO;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("listingsProductCategoryDialogService")
@Transactional
public class ListingsProductCategoryDialogServiceImpl
		implements ListingsProductCategoryDialogService {

	@Autowired
	ProductCategoryDAO productCategoryDAO;

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
}
