package org.teiath.data.dao;

import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.TradeableGood;

import java.util.Collection;

public interface TradeableGoodDAO {

	public Collection<TradeableGood> findByListing(Listing listing);

	//	public ApplicationImage findByUser(User user);

	public void save(TradeableGood tradeableGood);

	//	public void delete(ApplicationImage applicationImage);

	public void deleteAll(Listing listing);
}
