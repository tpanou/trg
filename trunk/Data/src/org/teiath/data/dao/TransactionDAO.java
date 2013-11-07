package org.teiath.data.dao;

import org.teiath.data.domain.trg.Transaction;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.TransactionSearchCriteria;

import java.util.Collection;
import java.util.Date;

public interface TransactionDAO {

	public Transaction findById(Integer id);

	public SearchResults<Transaction> search(TransactionSearchCriteria criteria);

	public void save(Transaction transaction);

	public void delete(Transaction transaction);

	public Collection<Transaction> findTransactionsByDates(Date dateFrom, Date dateTo);
}
