package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ProductStatusDAO;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.service.exceptions.ServiceException;

@Service("editProductStatusService")
@Transactional
public class EditProductStatusServiceImpl
		implements EditProductStatusService {

	@Autowired
	ProductStatusDAO productStatusDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveProductStatus(ProductStatus productStatus)
			throws ServiceException {
		try {
			productStatusDAO.save(productStatus);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public ProductStatus getProductStatusById(Integer productStatusId)
			throws ServiceException {
		ProductStatus productStatus;
		try {
			productStatus = productStatusDAO.findById(productStatusId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return productStatus;
	}
}
