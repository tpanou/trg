package org.teiath.service.trg;

import org.teiath.data.domain.trg.*;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.image.AImage;
import org.zkoss.zul.ListModelList;

import java.util.Collection;

public interface CreateListingService {

	public void saveListing(Listing listing, ListModelList<AImage> uploadedImages,
	                        ListModelList<TradeableGood> tradeableGoods)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategories()
			throws ServiceException;

	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException;

	public Collection<ProductStatus> getProductStatuses()
			throws ServiceException;

	public Collection<Currency> getCurrencies()
			throws ServiceException;

	public Currency getDefaultCurrency()
			throws ServiceException;

	public Collection<ProductCategory> getProductCategoriesByParentCategoryId(Integer parentCategoryId)
			throws ServiceException;
}
