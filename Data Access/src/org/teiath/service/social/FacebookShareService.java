package org.teiath.service.social;

import org.teiath.data.domain.trg.Listing;
import org.teiath.service.exceptions.ServiceException;

public interface FacebookShareService {

	public Listing getListingById(Integer listingId)
			throws ServiceException;
}
