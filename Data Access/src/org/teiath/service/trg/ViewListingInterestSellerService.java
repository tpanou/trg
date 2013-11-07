package org.teiath.service.trg;

import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ViewListingInterestSellerService {

	public ListingInterest getListingInterestById(Integer listingInterestId)
			throws ServiceException;

	public SearchResults<Listing> findCommonListings(Listing listing, ListingInterest listingInterest,
	                                                 ListingInterestSearchCriteria listingInterestSearchCriteria)
			throws ServiceException;

	public Collection<ListingAssessment> findUserTransactionsComments(User user)
			throws ServiceException;
}

