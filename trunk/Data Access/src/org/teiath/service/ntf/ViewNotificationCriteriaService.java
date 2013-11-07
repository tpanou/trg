package org.teiath.service.ntf;

import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ViewNotificationCriteriaService {

	public ProductNotificationCriteria getNotificationCriteriaById(Integer notificationCriteriaId)
			throws ServiceException;
}


