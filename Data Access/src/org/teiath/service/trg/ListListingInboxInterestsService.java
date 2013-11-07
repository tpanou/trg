package org.teiath.service.trg;

import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListListingInboxInterestsService {

	public SearchResults<ListingInterest> searchListingInterestsByCriteria(ListingInterestSearchCriteria criteria)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategories()
			throws ServiceException;

	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException;

	public void rejectListingInterests(Listing listing)
			throws ServiceException;

	public void approveListingInterest(ListingInterest listingInterest)
			throws ServiceException;

	public void rejectListingInterest(ListingInterest listingInterest)
			throws ServiceException;
}
