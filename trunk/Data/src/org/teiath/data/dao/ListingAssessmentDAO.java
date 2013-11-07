package org.teiath.data.dao;

import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.ListingAssessment;

import java.util.Collection;

public interface ListingAssessmentDAO {

	public ListingAssessment findByBuyer(Integer listingId, Integer userId);

	public ListingAssessment findBySeller(Integer listingId, Integer userId);

	public Double getTransactionAverageRating(User user);

	public void save(ListingAssessment listingAssessment);

	public Collection<ListingAssessment> getUserTransactionComments(User user);
}
