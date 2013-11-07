package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ListingDAO;
import org.teiath.data.dao.TransactionDAO;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

@Service("listingsExcelService")
@Transactional
public class ListingsExcelServiceImpl
		implements ListingsExcelService {

	@Autowired
	ListingDAO listingDAO;
	@Autowired
	TransactionDAO transactionDAO;

	@Override
	public Collection<Listing> getCreatedListings(Date dateFrom, Date dateTo)
			throws ServiceException {
		Collection<Listing> listings;

		try {
			listings = listingDAO.findCreatedListings(dateFrom, dateTo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listings;
	}

	@Override
	public Collection<Listing> getListingsByTransactionType(TransactionType transactionType)
			throws ServiceException {
		Collection<Listing> listings;

		try {
			listings = listingDAO.findListingsByTransactionType(transactionType);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listings;
	}

	@Override
	public Collection<Listing> getListingsByProductCategory(ProductCategory productCategory)
			throws ServiceException {
		Collection<Listing> listings;

		try {
			listings = listingDAO.findListingsByProductCategory(productCategory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listings;
	}

	@Override
	public Collection<Transaction> getTransactions(Date dateFrom, Date dateTo)
			throws ServiceException {
		Collection<Transaction> transactions;

		try {
			transactions = transactionDAO.findTransactionsByDates(dateFrom, dateTo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return transactions;
	}
}
