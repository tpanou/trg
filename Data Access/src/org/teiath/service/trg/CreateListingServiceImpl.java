package org.teiath.service.trg;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.*;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.image.AImage;
import org.zkoss.zul.ListModelList;

import java.util.Collection;
import java.util.Date;

@Service("createListingService")
@Transactional
public class CreateListingServiceImpl
		implements CreateListingService {

	@Autowired
	ListingDAO listingDAO;
	@Autowired
	TransactionDAO transactionDAO;
	@Autowired
	ProductCategoryDAO productCategoryDAO;
	@Autowired
	TransactionTypeDAO transactionTypeDAO;
	@Autowired
	ProductStatusDAO productStatusDAO;
	@Autowired
	CurrencyDAO currencyDAO;
	@Autowired
	ApplicationImageDAO applicationImageDAO;
	@Autowired
	TradeableGoodDAO tradeableGoodDAO;
	@Autowired
	ProductNotificationCriteriaDAO productNotificationCriteriaDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;
	@Autowired
	UserActionDAO userActionDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveListing(Listing listing, ListModelList<AImage> uploadedImages,
	                        ListModelList<TradeableGood> tradeableGoods)
			throws ServiceException {
		Collection<ProductNotificationCriteria> productNotificationCriteria;

		try {
			listing.setListingCreationDate(new Date());
			listing.setCode(RandomStringUtils.randomAlphanumeric(15).toUpperCase());

			//save Transaction
			transactionDAO.save(listing.getTransaction());

			listingDAO.save(listing);

			//save Images
			for (AImage image : uploadedImages) {
				ApplicationImage applicationImage = new ApplicationImage();
				applicationImage.setListing(listing);
				applicationImage.setImageBytes(image.getByteData());
				applicationImageDAO.save(applicationImage);
			}

			//save TradeableGoods
			for (TradeableGood tradeableGood : tradeableGoods) {
				TradeableGood newTradeAbleGood = new TradeableGood();
				newTradeAbleGood.setListing(listing);
				newTradeAbleGood.setName(tradeableGood.getName());
				tradeableGoodDAO.save(newTradeAbleGood);
			}

			//check for notification criteria and send notifications
			if (listing.isEnabled()) {
				productNotificationCriteria = productNotificationCriteriaDAO.checkCriteria(listing);
				for (ProductNotificationCriteria matchedCriteria : productNotificationCriteria) {
					//Create and save Notification
					Notification notification = new Notification();
					notification.setUser(matchedCriteria.getUser());
					notification.setListing(listing);
					notification.setType(Notification.TYPE_GOODS);
					notification.setSentDate(new Date());
					notification.setTitle("Νεα αγγελία");
					String body = notificationProperties.getNotificationBody()
							.replace("$1", listing.getUser().getFullName()).replace("$2", "αγγελία");
					notification.setBody(body);
					notification.setNotificationCriteria(matchedCriteria);
					notificationDAO.save(notification);

					//Create and send Email
					String mailSubject = emailProperties.getRouteCreateSubject()
							.replace("$1", "[Υπηρεσία εύρεσης Αγαθών]:").replace("$2", "αγγελίας");
					StringBuilder htmlMessageBuiler = new StringBuilder();
					htmlMessageBuiler.append("<html> <body>");
					String mailBody = emailProperties.getRouteCreateBody()
							.replace("$1", listing.getUser().getFullName()).replace("$2", "αγγελία");
					htmlMessageBuiler.append(mailBody);
					htmlMessageBuiler.append("<br>");
					htmlMessageBuiler.append("<br>Κωδικός αγγελίας: " + listing.getCode());
					htmlMessageBuiler.append("<br>Κατηγορία προϊόντος: " + listing.getProductCategory().getName());
					htmlMessageBuiler.append("<br>Όνομα προϊόντος: " + listing.getProductName());
					htmlMessageBuiler.append("<br>Τύπος συναλλαγής: " + listing.getTransactionType().getName());
					htmlMessageBuiler.append("<br>Κατάσταση προϊόντος: " + listing.getProductStatus().getName());
					if (listing.getPrice() != null) {
						htmlMessageBuiler.append("<br>Τιμή: " + listing.getPrice());
					}
					if (listing.getUser().getAverageTransactionRating() != null) {
						htmlMessageBuiler.append("<br>Μέση αξιολόγηση ιδιοκτήτη: " + listing.getUser()
								.getAverageTransactionRating());
					}
					htmlMessageBuiler.append("</body> </html>");
					mailManager.sendMail(emailProperties.getFromAddress(), matchedCriteria.getUser().getEmail(),
							mailSubject, htmlMessageBuiler.toString());
				}
			}

			//create and save user action trace
			UserAction userAction = new UserAction();
			userAction.setDate(new Date());
			userAction.setUser(listing.getUser());
			userAction.setProduct(listing.getProductName());
			userAction.setTransactionType(listing.getTransactionType().getName());
			userAction.setPrice(listing.getPriceWithCurrency());
			userAction.setListingCode(listing.getCode());
			userAction.setType(UserAction.TYPE_CREATE);
			userAction.setListingDate(listing.getListingCreationDate());
			userActionDAO.save(userAction);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

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

	public Collection<ProductStatus> getProductStatuses()
			throws ServiceException {
		Collection<ProductStatus> statuses;

		try {
			statuses = productStatusDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return statuses;
	}

	public Collection<Currency> getCurrencies()
			throws ServiceException {
		Collection<Currency> currencies;

		try {
			currencies = currencyDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return currencies;
	}

	@Override
	public Currency getDefaultCurrency()
			throws ServiceException {
		Currency currency;
		try {
			currency = currencyDAO.findDefaultCurrency();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return currency;
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
