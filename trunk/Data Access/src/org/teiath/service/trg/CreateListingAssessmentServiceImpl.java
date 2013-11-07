package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ListingAssessmentDAO;
import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.service.exceptions.ServiceException;

@Service("createListingAssessmentService")
@Transactional
public class CreateListingAssessmentServiceImpl
		implements CreateListingAssessmentService {

	@Autowired
	ListingAssessmentDAO listingAssessmentDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveAssessment(ListingAssessment listingAssessment)
			throws ServiceException {
		try {
			listingAssessmentDAO.save(listingAssessment);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public ListingAssessment getListingAssessmentBuyer(Integer listingId, Integer userId)
			throws ServiceException {
		ListingAssessment listingAssessment;
		try {
			listingAssessment = listingAssessmentDAO.findByBuyer(listingId, userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listingAssessment;
	}

	@Override
	public ListingAssessment getListingAssessmentSeller(Integer listingId, Integer userId)
			throws ServiceException {
		ListingAssessment listingAssessment;
		try {
			listingAssessment = listingAssessmentDAO.findBySeller(listingId, userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listingAssessment;
	}
}
