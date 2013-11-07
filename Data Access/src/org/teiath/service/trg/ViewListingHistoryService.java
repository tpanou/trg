package org.teiath.service.trg;

import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ViewListingHistoryService {

	public Transaction getTransactionById(Integer transcationId)
			throws ServiceException;

	public Collection<ApplicationImage> getImages(Listing listing)
			throws ServiceException;
}


