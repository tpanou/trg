package org.teiath.service.trg;

import org.teiath.data.domain.trg.*;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface SearchListingsService {

	public SearchResults<Listing> searchListings(ListingSearchCriteria criteria)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategories()
			throws ServiceException;

	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException;

	public Collection<ProductStatus> getProductStatuses()
			throws ServiceException;

	public void saveListingInterest(ListingInterest listingInterest)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategoriesByParentCategoryId(Integer parentCategoryId)
			throws ServiceException;
}
