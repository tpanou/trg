package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.trg.*;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

@Service("listListingInboxInterestsService")
@Transactional
public class ListListingInboxInterestsServiceImpl
		implements ListListingInboxInterestsService {

	@Autowired
	ListingInterestDAO listingInterestDAO;
	@Autowired
	ListingAssessmentDAO listingAssessmentDAO;
	@Autowired
	ProductCategoryDAO productCategoryDAO;
	@Autowired
	TransactionTypeDAO transactionTypeDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	ListingDAO listingDAO;
	@Autowired
	TransactionDAO transactionDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;

	@Override
	public SearchResults<ListingInterest> searchListingInterestsByCriteria(ListingInterestSearchCriteria criteria)
			throws ServiceException {
		SearchResults<ListingInterest> results;

		try {
			results = listingInterestDAO.search(criteria);
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
	public void rejectListingInterests(Listing listing)
			throws ServiceException {
		try {
			ListingInterestSearchCriteria listingInterestSearchCriteria = new ListingInterestSearchCriteria();
			listingInterestSearchCriteria.setListing(listing);
			SearchResults<ListingInterest> results;
			results = listingInterestDAO.search(listingInterestSearchCriteria);
			Collection<ListingInterest> listingInterests = results.getData();

			for (ListingInterest interest : listingInterests) {
				interest.setStatus(ListingInterest.STATUS_REJECTED);
				listingInterestDAO.save(interest);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void approveListingInterest(ListingInterest listingInterest)
			throws ServiceException {
		try {

			listingInterest.setStatus(ListingInterest.STATUS_APPROVED);
			listingInterestDAO.save(listingInterest);
			listingInterest.getListing().setActive(false);
			listingDAO.save(listingInterest.getListing());

			Transaction transaction = new Transaction();
			transaction.setTransactionDate(new Date());
			transaction.setListingInterest(listingInterest);
			transaction.setCompleted(true);
			transaction.setSeller(listingInterest.getListing().getUser());
			transaction.setListing(listingInterest.getListing());
			transactionDAO.save(transaction);

			Notification notification = new Notification();
			notification.setUser(listingInterest.getUser());
			notification.setListing(listingInterest.getListing());
			notification.setType(Notification.TYPE_GOODS);
			notification.setSentDate(new Date());
			notification.setTitle("Αποδοχή εκδήλωσης ενδιαφέροντος");
			String body = notificationProperties.getNotificationApproveBody()
					.replace("$1", listingInterest.getListing().getUser().getFullName()).replace("$2", "αγγελία");
			notification.setBody(body);
			notificationDAO.save(notification);

			//Create and send Email
			String mailSubject = emailProperties.getRouteInterestApproveSubject()
					.replace("$1", "[Υπηρεσία εύρεσης Αγαθών]:").replace("$2", "αγγελίας");
			StringBuilder htmlMessageBuiler = new StringBuilder();
			htmlMessageBuiler.append("<html> <body>");
			String mailBody = emailProperties.getRouteInterestApproveBody()
					.replace("$1", listingInterest.getListing().getUser().getFullName()).replace("$2", "αγγελία");
			htmlMessageBuiler.append(mailBody);
			htmlMessageBuiler.append("<br>");
			htmlMessageBuiler.append("<br>Κωδικός αγγελίας: " + listingInterest.getListing().getCode());
			htmlMessageBuiler
					.append("<br>Κατηγορία προϊόντος: " + listingInterest.getListing().getProductCategory().getName());
			htmlMessageBuiler.append("<br>Όνομα προϊόντος: " + listingInterest.getListing().getProductName());
			htmlMessageBuiler
					.append("<br>Τύπος συναλλαγής: " + listingInterest.getListing().getTransactionType().getName());
			htmlMessageBuiler
					.append("<br>Κατάσταση προϊόντος: " + listingInterest.getListing().getProductStatus().getName());
			htmlMessageBuiler.append("<br>Τιμή: " + listingInterest.getListing().getPrice());
			htmlMessageBuiler.append("</body> </html>");
			mailManager.sendMail(emailProperties.getFromAddress(), listingInterest.getUser().getEmail(), mailSubject,
					htmlMessageBuiler.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void rejectListingInterest(ListingInterest listingInterest)
			throws ServiceException {
		try {
			listingInterest.setStatus(ListingInterest.STATUS_REJECTED);
			listingInterestDAO.save(listingInterest);

			Notification notification = new Notification();
			notification.setUser(listingInterest.getUser());
			notification.setListing(listingInterest.getListing());
			notification.setType(Notification.TYPE_GOODS);
			notification.setSentDate(new Date());
			notification.setTitle("Απορριψη εκδήλωσης ενδιαφέροντος");
			String body = notificationProperties.getNotificationRejectBody()
					.replace("$1", listingInterest.getListing().getUser().getFullName()).replace("$2", "αγγελία");
			notification.setBody(body);
			notificationDAO.save(notification);

			//Create and send Email
			String mailSubject = emailProperties.getRouteInterestRejectSubject()
					.replace("$1", "[Υπηρεσία εύρεσης Αγαθών]:").replace("$2", "αγγελίας");
			StringBuilder htmlMessageBuiler = new StringBuilder();
			htmlMessageBuiler.append("<html> <body>");
			String mailBody = emailProperties.getRouteInterestRejectBody()
					.replace("$1", listingInterest.getListing().getUser().getFullName()).replace("$2", "αγγελία");
			htmlMessageBuiler.append(mailBody);
			htmlMessageBuiler.append("<br>");
			htmlMessageBuiler.append("<br>Κωδικός αγγελίας: " + listingInterest.getListing().getCode());
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
			htmlMessageBuiler.append("</body> </html>");
			mailManager.sendMail(emailProperties.getFromAddress(), listingInterest.getUser().getEmail(), mailSubject,
					htmlMessageBuiler.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
