package org.teiath.service.ntf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ProductNotificationCriteriaDAO;
import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("listNotificationsCriteriaService")
@Transactional
public class ListNotificationsCriteriaServiceImpl
		implements ListNotificationsCriteriaService {

	@Autowired
	ProductNotificationCriteriaDAO notificationCriteriaDAO;

	@Override
	public SearchResults<ProductNotificationCriteria> searchNotificationsCriteriaByCriteria(
			NotificationsCriteriaSearchCriteria criteria)
			throws ServiceException {
		SearchResults<ProductNotificationCriteria> results;

		try {
			results = notificationCriteriaDAO.search(criteria);
			for (ProductNotificationCriteria notificationCriteria : results.getData()) {
				notificationCriteria.getNotifications().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteNotificationCriteria(ProductNotificationCriteria notificationCriteria)
			throws ServiceException {
		try {
			notificationCriteriaDAO.delete(notificationCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
