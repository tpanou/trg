package org.teiath.service.ntf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ProductNotificationCriteriaDAO;
import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("viewNotificationCriteriaService")
@Transactional
public class ViewNotificationCriteriaServiceImpl
		implements ViewNotificationCriteriaService {

	@Autowired
	ProductNotificationCriteriaDAO notificationCriteriaDAO;

	@Override
	public ProductNotificationCriteria getNotificationCriteriaById(Integer notificationId)
			throws ServiceException {
		ProductNotificationCriteria notificationCriteria;
		try {
			notificationCriteria = notificationCriteriaDAO.findById(notificationId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return notificationCriteria;
	}
}
