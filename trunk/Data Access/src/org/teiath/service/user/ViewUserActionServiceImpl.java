package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ListingDAO;
import org.teiath.data.dao.UserActionDAO;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.UserAction;
import org.teiath.service.exceptions.ServiceException;

@Service("viewUserActionService")
@Transactional
public class ViewUserActionServiceImpl
		implements ViewUserActionService {

	@Autowired
	UserActionDAO userActionDAO;
	@Autowired
	ListingDAO listingDAO;

	@Override
	public UserAction getUserActionById(Integer userActionId)
			throws ServiceException {
		UserAction userAction;
		try {
			userAction = userActionDAO.findById(userActionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return userAction;
	}

	@Override
	public Listing getListingByCode(String code)
			throws ServiceException {
		Listing listing;

		try {
			listing = listingDAO.findByCode(code);
			if (listing != null)
				listing.getListingInterests().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listing;
	}
}
