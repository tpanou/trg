package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ListingAssessmentDAO;
import org.teiath.data.dao.ListingDAO;
import org.teiath.data.dao.ListingInterestDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("viewListingInterestSellerService")
@Transactional
public class ViewListingInterestSellerServiceImpl
		implements ViewListingInterestSellerService {

	@Autowired
	ListingDAO listingDAO;
	@Autowired
	ListingInterestDAO listingInterestDAO;
	@Autowired
	ListingAssessmentDAO listingAssessmentDAO;

	@Override
	public ListingInterest getListingInterestById(Integer listingInterestId)
			throws ServiceException {
		ListingInterest listingInterest;
		try {
			listingInterest = listingInterestDAO.findById(listingInterestId);
			listingInterest.getUser().setAverageTransactionRating(
					listingAssessmentDAO.getTransactionAverageRating(listingInterest.getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listingInterest;
	}

	@Override
	public SearchResults<Listing> findCommonListings(Listing listing, ListingInterest listingInterest,
	                                                 ListingInterestSearchCriteria listingInterestSearchCriteria)
			throws ServiceException {
		SearchResults<Listing> results;

		try {
			results = listingDAO.findCommonListings(listing, listingInterest, listingInterestSearchCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<ListingAssessment> findUserTransactionsComments(User user)
			throws ServiceException {
		Collection<ListingAssessment> listingAssessments;
		try {
			listingAssessments = listingAssessmentDAO.getUserTransactionComments(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listingAssessments;
	}

	/*

		@Override
		public Route getRouteById(Integer routeId)
				throws ServiceException {
			Route route;
			try {
				route = routeDAO.findById(routeId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException(ServiceException.DATABASE_ERROR);
			}

			return route;
		}



		@Override
		public Collection<RouteAssessment> findPassengerRatings(User passenger, User driver)
				throws ServiceException {
			Collection<RouteAssessment> routeAssessments;
			try {
				routeAssessments = routeAssessmentDAO.getPassengerRatings(passenger, driver);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException(ServiceException.DATABASE_ERROR);
			}

			return routeAssessments;
		}    */
}
