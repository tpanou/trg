package org.teiath.service.ntf;

import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ListNotificationsCriteriaService {

	public SearchResults<ProductNotificationCriteria> searchNotificationsCriteriaByCriteria(
			NotificationsCriteriaSearchCriteria criteria)
			throws ServiceException;

	public void deleteNotificationCriteria(ProductNotificationCriteria notificationCriteria)
			throws ServiceException;
}
