package org.teiath.data.dao;

import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;

public interface ListingInterestDAO {

	public ListingInterest findById(Integer id);

	public SearchResults<ListingInterest> search(ListingInterestSearchCriteria criteria);

	public void save(ListingInterest listingInterest);

	public void delete(ListingInterest listingInterest);

	public ListingInterest findByUser(Listing listing, User user);
}
