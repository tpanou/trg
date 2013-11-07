package org.teiath.data.dao;

import org.teiath.data.domain.trg.TransactionType;

import java.util.Collection;

public interface TransactionTypeDAO {

	public Collection<TransactionType> findAll();

	public void save(TransactionType transactionType);

	public TransactionType findById(Integer id);

	public void delete(TransactionType transactionType);
}
