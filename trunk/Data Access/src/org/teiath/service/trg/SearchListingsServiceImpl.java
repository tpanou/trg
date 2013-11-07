package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.trg.*;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.data.search.ListingSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

@Service("searchListingsService")
@Transactional
public class SearchListingsServiceImpl
		implements SearchListingsService {

	@Autowired
	ListingDAO listingDAO;
	@Autowired
	ListingInterestDAO listingInterestDAO;
	@Autowired
	ProductCategoryDAO productCategoryDAO;
	@Autowired
	TransactionTypeDAO transactionTypeDAO;
	@Autowired
	ProductStatusDAO productStatusDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;

	@Override
	public SearchResults<Listing> searchListings(ListingSearchCriteria criteria)
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
	public Collection<ProductCategory> getProductCategories()
			throws ServiceException {
		Collection<ProductCategory> categories;

		try {
			categories = productCategoryDAO.findAll();
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
	public Collection<ProductStatus> getProductStatuses()
			throws ServiceException {
		Collection<ProductStatus> productStatuses;

		try {
			productStatuses = productStatusDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return productStatuses;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveListingInterest(ListingInterest listingInterest)
			throws ServiceException {
		try {
			listingInterestDAO.save(listingInterest);

			//Create and save Notification
			Notification notification = new Notification();
			notification.setUser(listingInterest.getListing().getUser());
			notification.setListing(listingInterest.getListing());
			notification.setType(Notification.TYPE_GOODS);
			notification.setSentDate(new Date());
			notification.setTitle("Νεα εκδήλωση ενδιαφέροντος για αγγελία");
			String body = notificationProperties.getNotificationInterestBody()
					.replace("$1", listingInterest.getUser().getFullName()).replace("$2", "αγγελία");
			notification.setBody(body);
			notificationDAO.save(notification);

			//Create and send Email
			String mailSubject = emailProperties.getRouteInterestSubject().replace("$1", "[Υπηρεσία εύρεσης Αγαθών]:")
					.replace("$2", "αγγελίας");
			StringBuilder htmlMessageBuiler = new StringBuilder();
			htmlMessageBuiler.append("<html> <body>");
			htmlMessageBuiler.append("<br>Κωδικός αγγελίας: " + listingInterest.getListing().getCode());
			String mailBody = emailProperties.getRouteInterestBody()
					.replace("$1", listingInterest.getUser().getFullName()).replace("$2", "αγγελία");
			htmlMessageBuiler.append(mailBody);
			htmlMessageBuiler.append("<br>");
			htmlMessageBuiler
					.append("<br>Κατηγορία προϊόντος: " + listingInterest.getListing().getProductCategory().getName());
			htmlMessageBuiler.append("<br>Όνομα προϊόντος: " + listingInterest.getListing().getProductName());
			htmlMessageBuiler
					.append("<br>Τύπος συναλλαγής: " + listingInterest.getListing().getTransactionType().getName());
			htmlMessageBuiler
					.append("<br>Κατάσταση προϊόντος: " + listingInterest.getListing().getProductStatus().getName());
			if (listingInterest.getListing().getPrice() != null) {
				htmlMessageBuiler.append("<br>Τιμή: " + listingInterest.getListing().getPrice());
			}
			if (listingInterest.getUser().getAverageTransactionRating() != null) {
				htmlMessageBuiler.append("<br>Μέση αξιολόγηση ενδιαφερόμενου: " + listingInterest.getUser()
						.getAverageTransactionRating());
			}
			htmlMessageBuiler.append("</body> </html>");
			mailManager.sendMail(emailProperties.getFromAddress(), listingInterest.getListing().getUser().getEmail(),
					mailSubject, htmlMessageBuiler.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
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
}
