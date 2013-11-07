package org.teiath.service.trg;

import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.service.exceptions.ServiceException;

public interface CreateListingAssessmentService {

	public void saveAssessment(ListingAssessment listingAssessment)
			throws ServiceException;

	public ListingAssessment getListingAssessmentBuyer(Integer listingId, Integer userId)
			throws ServiceException;

	public ListingAssessment getListingAssessmentSeller(Integer listingId, Integer userId)
			throws ServiceException;
}
