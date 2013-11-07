package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ProductCategoryDAO;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.service.exceptions.ServiceException;

@Service("editProductCategoryService")
@Transactional
public class EditProductCategoryServiceImpl
		implements EditProductCategoryService {

	@Autowired
	ProductCategoryDAO productCategoryDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveProductCategory(ProductCategory productCategory)
			throws ServiceException {
		try {
			productCategoryDAO.save(productCategory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public ProductCategory getProductCategoryById(Integer productCategoryId)
			throws ServiceException {
		ProductCategory productCategory;
		try {
			productCategory = productCategoryDAO.findById(productCategoryId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return productCategory;
	}
}
