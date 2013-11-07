package org.teiath.service.trg;

import org.teiath.data.domain.User;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.TradeableGood;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ViewListingSearchService {

	public Listing getListingById(Integer listingId)
			throws ServiceException;

	public Collection<ApplicationImage> getImages(Listing listing)
			throws ServiceException;

	public Collection<ListingAssessment> findUserTransactionsComments(User user)
			throws ServiceException;

	public ListingInterest getListingInterestByUser(Listing listing, User user)
			throws ServiceException;

	public void saveListingInterest(ListingInterest listingInterest)
			throws ServiceException;

	public Collection<TradeableGood> getListingTradeableGoods(Listing listing)
			throws ServiceException;
}


