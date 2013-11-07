package org.teiath.service.trg;

import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListingsTransactionTypeDialogService {

	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException;
}
