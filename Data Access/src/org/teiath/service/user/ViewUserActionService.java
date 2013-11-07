package org.teiath.service.user;

import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.UserAction;
import org.teiath.service.exceptions.ServiceException;

public interface ViewUserActionService {

	public UserAction getUserActionById(Integer userActionId)
			throws ServiceException;

	public Listing getListingByCode(String code)
			throws ServiceException;
}


