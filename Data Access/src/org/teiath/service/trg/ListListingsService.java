package org.teiath.service.trg;

import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListListingsService {

	public SearchResults<Listing> searchListingsByCriteria(ListingSearchCriteria criteria)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategoriesByParentCategoryId(Integer parentCategoryId)
			throws ServiceException;

	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException;

	public void deleteListing(Listing listing)
			throws ServiceException;


	/*	 public void deleteRouteInterests(Route route)
				 throws ServiceException;
	 */
}

