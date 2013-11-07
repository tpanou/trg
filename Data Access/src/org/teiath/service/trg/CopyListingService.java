package org.teiath.service.trg;

import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.*;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.image.AImage;
import org.zkoss.zul.ListModelList;

import java.util.Collection;

public interface CopyListingService {

	public void saveListing(Listing listing, ListModelList<AImage> uploadedImages,
	                        ListModelList<TradeableGood> tradeableGoods)
			throws ServiceException;

	public void deleteListingPhotos(Listing listing)
			throws ServiceException;

	public void deleteListingTradeableGoods(Listing listing)
			throws ServiceException;

	public void saveApplicationImage(ApplicationImage image)
			throws ServiceException;

	public void deleteApplicationImage(ApplicationImage image)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategories()
			throws ServiceException;

	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException;

	public Collection<ProductStatus> getProductStatuses()
			throws ServiceException;

	public Listing getListingById(Integer listingId)
			throws ServiceException;

	public Collection<ApplicationImage> getImages(Listing listing)
			throws ServiceException;

	public Collection<Currency> getCurrencies()
			throws ServiceException;

	public Collection<TradeableGood> getListingTradeableGoods(Listing listing)
			throws ServiceException;

	public Collection<ProductCategory> getProductCategoriesByParentCategoryId(Integer parentCategoryId)
			throws ServiceException;
}
