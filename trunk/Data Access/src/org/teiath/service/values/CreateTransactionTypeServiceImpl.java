package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.TransactionTypeDAO;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.service.exceptions.ServiceException;

@Service("createTransactionTypeService")
@Transactional
public class CreateTransactionTypeServiceImpl
		implements CreateTransactionTypeService {

	@Autowired
	TransactionTypeDAO transactionTypeDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveTransactionType(TransactionType transactionType)
			throws ServiceException {
		try {
			transactionTypeDAO.save(transactionType);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
