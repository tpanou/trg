package org.teiath.data.dao;

import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ProductNotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;

import java.util.Collection;

public interface ProductNotificationCriteriaDAO {

	public void save(ProductNotificationCriteria productNotificationCriteria);

	public void delete(ProductNotificationCriteria productNotificationCriteria);

	public Collection<ProductNotificationCriteria> checkCriteria(Listing listing);

	public ProductNotificationCriteria findById(Integer id);

	public SearchResults<ProductNotificationCriteria> search(NotificationsCriteriaSearchCriteria criteria);
}
