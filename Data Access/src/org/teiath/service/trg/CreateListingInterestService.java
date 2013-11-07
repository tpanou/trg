package org.teiath.service.trg;

import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TradeableGood;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface CreateListingInterestService {

	public Listing getListingById(Integer listingId)
			throws ServiceException;

	public Collection<ProductStatus> getProductStatuses()
			throws ServiceException;

	public void saveListingInterest(ListingInterest listingInterest)
			throws ServiceException;

	public Collection<TradeableGood> getListingTradeableGoods(Listing listing)
			throws ServiceException;
}
