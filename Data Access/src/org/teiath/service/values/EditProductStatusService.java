package org.teiath.service.values;

import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.service.exceptions.ServiceException;

public interface EditProductStatusService {

	public void saveProductStatus(ProductStatus productStatus)
			throws ServiceException;

	public ProductStatus getProductStatusById(Integer productStatusId)
			throws ServiceException;
}
