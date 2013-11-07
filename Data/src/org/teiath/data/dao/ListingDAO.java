package org.teiath.data.dao;

import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.data.search.ListingSearchCriteria;

import java.util.Collection;
import java.util.Date;

public interface ListingDAO {

	public Listing findById(Integer id);

	public Listing findByCode(String code);

	public SearchResults<Listing> search(ListingSearchCriteria criteria);

	public void save(Listing listing);

	public void copy(Listing listing);

	public void delete(Listing listing);

	public SearchResults<Listing> findCommonListings(Listing listing, ListingInterest listingInterest,
	                                                 ListingInterestSearchCriteria listingInterestSearchCriteria);

	public Collection<Listing> findCreatedListings(Date dateFrom, Date dateTo);

	public Collection<Listing> findListingsByTransactionType(TransactionType transactionType);

	public Collection<Listing> findListingsByProductCategory(ProductCategory productCategory);
}
