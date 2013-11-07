package org.teiath.service.trg;

import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.Transaction;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

public interface ListingsExcelService {

	public Collection<Listing> getCreatedListings(Date dateFrom, Date dateTo)
			throws ServiceException;

	public Collection<Listing> getListingsByTransactionType(TransactionType transactionType)
			throws ServiceException;

	public Collection<Listing> getListingsByProductCategory(ProductCategory productCategory)
			throws ServiceException;

	public Collection<Transaction> getTransactions(Date dateFrom, Date dateTo)
			throws ServiceException;
}
