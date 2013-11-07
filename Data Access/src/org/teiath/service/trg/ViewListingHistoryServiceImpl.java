package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ApplicationImageDAO;
import org.teiath.data.dao.ListingAssessmentDAO;
import org.teiath.data.dao.TradeableGoodDAO;
import org.teiath.data.dao.TransactionDAO;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("viewListingHistoryService")
@Transactional
public class ViewListingHistoryServiceImpl
		implements ViewListingHistoryService {

	@Autowired
	TransactionDAO transactionDAO;
	@Autowired
	ApplicationImageDAO imageDAO;
	@Autowired
	TradeableGoodDAO tradeableGoodDAO;
	@Autowired
	ListingAssessmentDAO listingAssessmentDAO;

	@Override
	public Transaction getTransactionById(Integer transactionId)
			throws ServiceException {
		Transaction transaction;

		try {
			transaction = transactionDAO.findById(transactionId);
			transaction.getListingInterest().getUser().setAverageTransactionRating(
					listingAssessmentDAO.getTransactionAverageRating(transaction.getListingInterest().getUser()));
			transaction.getListingInterest().getListing().getUser().setAverageTransactionRating(listingAssessmentDAO
					.getTransactionAverageRating(transaction.getListingInterest().getListing().getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return transaction;
	}

	@Override
	public Collection<ApplicationImage> getImages(Listing listing)
			throws ServiceException {
		Collection<ApplicationImage> images;

		try {
			images = imageDAO.findByListing(listing);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return images;
	}
}
