package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TradeableGood;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

@Service("createListingInterestService")
@Transactional
public class CreateListingInterestServiceImpl
		implements CreateListingInterestService {

	@Autowired
	ListingDAO listingDAO;
	@Autowired
	ListingInterestDAO listingInterestDAO;
	@Autowired
	ProductStatusDAO productStatusDAO;
	@Autowired
	TradeableGoodDAO tradeableGoodDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;

	@Override
	public Listing getListingById(Integer listingId)
			throws ServiceException {
		Listing listing;
		try {
			listing = listingDAO.findById(listingId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return listing;
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
			String mailBody = emailProperties.getRouteInterestBody()
					.replace("$1", listingInterest.getUser().getFullName()).replace("$2", "αγγελία");
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
	public Collection<TradeableGood> getListingTradeableGoods(Listing listing)
			throws ServiceException {
		Collection<TradeableGood> tradeableGoods;

		try {
			tradeableGoods = tradeableGoodDAO.findByListing(listing);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return tradeableGoods;
	}
}
