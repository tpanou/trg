package org.teiath.service.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ListingDAO;
import org.teiath.data.domain.trg.Listing;
import org.teiath.service.exceptions.ServiceException;

@Service("facebookShareService")
@Transactional
public class FacebookShareServiceImpl
		implements FacebookShareService {

	@Autowired
	ListingDAO listingDAO;

	@Override
	public Listing getListingById(Integer listingId)
			throws ServiceException {
		Listing listing;
		try {
			listing = listingDAO.findById(listingId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listing;
	}
}
