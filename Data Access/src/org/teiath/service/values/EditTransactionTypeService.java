package org.teiath.service.values;

import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;

public interface EditTransactionTypeService {

	public void saveTransactionType(TransactionType transactionType)
			throws ServiceException;

	public TransactionType getTransactionTypeById(Integer transactionTypeId)
			throws ServiceException;
}
