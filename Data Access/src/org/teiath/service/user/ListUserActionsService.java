package org.teiath.service.user;

import org.teiath.data.domain.trg.UserAction;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.UserActionSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ListUserActionsService {

	public SearchResults<UserAction> searchUserActionsByCriteria(UserActionSearchCriteria criteria)
			throws ServiceException;
}
