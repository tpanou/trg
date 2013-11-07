package org.teiath.data.dao;

import org.teiath.data.domain.trg.Currency;

import java.util.Collection;

public interface CurrencyDAO {

	public Collection<Currency> findAll();

	public void save(Currency currency);

	public void delete(Currency currency);

	public Currency findDefaultCurrency();

	public Currency findById(Integer id);
}
