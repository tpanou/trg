package org.teiath.service.values;

import org.teiath.data.domain.trg.Currency;
import org.teiath.service.exceptions.ServiceException;

public interface CreateCurrencyService {

	public void saveCurrency(Currency currency)
			throws ServiceException;

	public void toggleDefaultCurrency(Currency currency)
			throws ServiceException;

	public Currency getDefaultCurrency()
			throws ServiceException;
}
