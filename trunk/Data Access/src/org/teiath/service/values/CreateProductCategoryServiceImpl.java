package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ProductCategoryDAO;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.service.exceptions.ServiceException;

@Service("createProductCategoryService")
@Transactional
public class CreateProductCategoryServiceImpl
		implements CreateProductCategoryService {

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
}
