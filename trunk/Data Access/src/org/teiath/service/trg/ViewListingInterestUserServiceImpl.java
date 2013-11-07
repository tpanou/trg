package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ApplicationImageDAO;
import org.teiath.data.dao.ListingAssessmentDAO;
import org.teiath.data.dao.ListingDAO;
import org.teiath.data.dao.TradeableGoodDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.data.domain.trg.TradeableGood;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("viewListingInterestUserService")
@Transactional
public class ViewListingInterestUserServiceImpl
		implements ViewListingInterestUserService {

	@Autowired
	ListingDAO listingDAO;
	@Autowired
	ApplicationImageDAO imageDAO;
	@Autowired
	TradeableGoodDAO tradeableGoodDAO;
	@Autowired
	ListingAssessmentDAO listingAssessmentDAO;

	@Override
	public Listing getListingById(Integer listingId)
			throws ServiceException {
		Listing listing;

		try {
			listing = listingDAO.findById(listingId);
			listing.getListingInterests().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listing;
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

	@Override
	public Collection<ListingAssessment> findUserTransactionsComments(User user)
			throws ServiceException {
		Collection<ListingAssessment> listingAssessments;
		try {
			listingAssessments = listingAssessmentDAO.getUserTransactionComments(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listingAssessments;
	}

	@Override
	public Collection<TradeableGood> getListingTradeableGoods(Listing listing)
			throws ServiceException {
		Collection<TradeableGood> tradeableGoods;

		try {
			tradeableGoods = tradeableGoodDAO.findByListing(listing);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return tradeableGoods;
	}
}
