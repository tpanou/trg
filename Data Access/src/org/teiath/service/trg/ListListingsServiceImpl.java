package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.data.search.ListingSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("listListingsService")
@Transactional
public class ListListingsServiceImpl
		implements ListListingsService {

	@Autowired
	ListingDAO listingDAO;
	@Autowired
	ProductCategoryDAO productCategoryDAO;
	@Autowired
	TransactionTypeDAO transactionTypeDAO;
	@Autowired
	ListingInterestDAO listingInterestDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;

	@Override
	public SearchResults<Listing> searchListingsByCriteria(ListingSearchCriteria criteria)
			throws ServiceException {
		SearchResults<Listing> results;

		try {
			results = listingDAO.search(criteria);
			for (Listing listing : results.getData()) {
				listing.getListingInterests().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<ProductCategory> getProductCategoriesByParentCategoryId(Integer parentCategoryId)
			throws ServiceException {
		Collection<ProductCategory> categories;

		try {
			categories = productCategoryDAO.findByParentProductCategoryId(parentCategoryId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}

	@Override
	public Collection<TransactionType> getTransactionTypes()
			throws ServiceException {
		Collection<TransactionType> transactionTypes;

		try {
			transactionTypes = transactionTypeDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return transactionTypes;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteListing(Listing listing)
			throws ServiceException {
		SearchResults<ListingInterest> results;
		ListingInterestSearchCriteria listingInterestSearchCriteria = new ListingInterestSearchCriteria();

		listingInterestSearchCriteria.setListing(listing);
		try {
			results = listingInterestDAO.search(listingInterestSearchCriteria);
			for (ListingInterest listingInterest : results.getData()) {

				//Create and send Email
				String mailSubject = emailProperties.getRouteDeleteSubject().replace("$1", "[Υπηρεσία εύρεσης Αγαθών]:")
						.replace("$2", "αγγελίας");
				StringBuilder htmlMessageBuiler = new StringBuilder();
				htmlMessageBuiler.append("<html> <body>");
				String mailBody = emailProperties.getRouteDeleteBody()
						.replace("$1", listingInterest.getListing().getUser().getFullName()).replace("$2", "αγγελία");
				htmlMessageBuiler.append(mailBody);
				htmlMessageBuiler.append("<br>");
				htmlMessageBuiler.append("<br>Κωδικός αγγελίας: " + listingInterest.getListing().getCode());
				htmlMessageBuiler.append("<br>Κατηγορία προϊόντος: " + listingInterest.getListing().getProductCategory()
						.getName());
				htmlMessageBuiler.append("<br>Όνομα προϊόντος: " + listingInterest.getListing().getProductName());
				htmlMessageBuiler
						.append("<br>Τύπος συναλλαγής: " + listingInterest.getListing().getTransactionType().getName());
				htmlMessageBuiler.append("<br>Κατάσταση προϊόντος: " + listingInterest.getListing().getProductStatus()
						.getName());
				if (listing.getPrice() != null) {
					htmlMessageBuiler.append("<br>Τιμή: " + listingInterest.getListing().getPrice());
				}
				htmlMessageBuiler.append("</body> </html>");
				mailManager
						.sendMail(emailProperties.getFromAddress(), listingInterest.getUser().getEmail(), mailSubject,
								htmlMessageBuiler.toString());
			}
			listingDAO.delete(listing);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
