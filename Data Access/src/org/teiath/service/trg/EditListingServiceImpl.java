package org.teiath.service.trg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.trg.*;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.data.search.ListingInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.image.AImage;
import org.zkoss.zul.ListModelList;

import java.util.Collection;
import java.util.Date;

@Service("editListingService")
@Transactional
public class EditListingServiceImpl
		implements EditListingService {

	@Autowired
	ListingDAO listingDAO;
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveListing(Listing listing, ListModelList<AImage> uploadedImages,
	                        ListModelList<TradeableGood> tradeableGoods)
			throws ServiceException {

		SearchResults<ListingInterest> results;
		ListingInterestSearchCriteria listingInterestSearchCriteria = new ListingInterestSearchCriteria();

		listingInterestSearchCriteria.setListing(listing);

		try {
			listingDAO.save(listing);

			//save Images
			for (AImage image : uploadedImages) {
				ApplicationImage applicationImage = new ApplicationImage();
				applicationImage.setListing(listing);
				applicationImage.setImageBytes(image.getByteData());
				applicationImageDAO.save(applicationImage);
			}

			//save TradeableGoods
			if (tradeableGoods != null) {
				for (TradeableGood tradeableGood : tradeableGoods) {
					TradeableGood newTradeAbleGood = new TradeableGood();
					newTradeAbleGood.setListing(listing);
					newTradeAbleGood.setName(tradeableGood.getName());
					tradeableGoodDAO.save(newTradeAbleGood);
				}
			}

			results = listingInterestDAO.search(listingInterestSearchCriteria);
			for (ListingInterest listingInterest : results.getData()) {
				//Create and save Notification
				Notification notification = new Notification();
				notification.setUser(listingInterest.getUser());
				notification.setListing(listing);
				notification.setType(Notification.TYPE_GOODS);
				notification.setSentDate(new Date());
				notification.setTitle("Τροποποίηση Αγγελίας");
				String body = notificationProperties.getNotificationEditBody()
						.replace("$1", listing.getUser().getFullName()).replace("$2", "αγγελία");
				notification.setBody(body);
				notificationDAO.save(notification);

				//Create and send Email
				String mailSubject = emailProperties.getRouteEditSubject().replace("$1", "[Υπηρεσία εύρεσης Αγαθών]:")
						.replace("$2", "αγγελίας");
				StringBuilder htmlMessageBuiler = new StringBuilder();
				htmlMessageBuiler.append("<html> <body>");
				String mailBody = emailProperties.getRouteEditBody().replace("$1", listing.getUser().getFullName())
						.replace("$2", "αγγελία");
				htmlMessageBuiler.append(mailBody);
				htmlMessageBuiler.append("<br>");
				htmlMessageBuiler.append("<br>Κωδικός αγγελίας: " + listing.getCode());
				htmlMessageBuiler.append("<br>Κατηγορία προϊόντος: " + listing.getProductCategory().getName());
				htmlMessageBuiler.append("<br>Όνομα προϊόντος: " + listing.getProductName());
				htmlMessageBuiler.append("<br>Τύπος συναλλαγής: " + listing.getTransactionType().getName());
				htmlMessageBuiler.append("<br>Κατάσταση προϊόντος: " + listing.getProductStatus().getName());
				if (listing.getPrice() != null) {
					htmlMessageBuiler.append("<br>Τιμή: " + listingInterest.getListing().getPrice());
				}
				if (listing.getUser().getAverageTransactionRating() != null) {
					htmlMessageBuiler.append("<br>Μέση αξιολόγηση ιδιοκτήτη: " + listing.getUser()
							.getAverageTransactionRating());
				}
				htmlMessageBuiler.append("</body> </html>");
				mailManager
						.sendMail(emailProperties.getFromAddress(), listingInterest.getUser().getEmail(), mailSubject,
								htmlMessageBuiler.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteListingPhotos(Listing listing)
			throws ServiceException {
		try {
			applicationImageDAO.deleteAll(listing);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void deleteListingTradeableGoods(Listing listing)
			throws ServiceException {
		try {
			tradeableGoodDAO.deleteAll(listing);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveApplicationImage(ApplicationImage image)
			throws ServiceException {
		//Collection<RouteNotificationCriteria> routeNotificationCriteria;
		try {
			applicationImageDAO.save(image);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteApplicationImage(ApplicationImage image)
			throws ServiceException {
		try {
			applicationImageDAO.delete(image);
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
	public Collection<ApplicationImage> getImages(Listing listing)
			throws ServiceException {
		Collection<ApplicationImage> images;

		try {
			images = applicationImageDAO.findByListing(listing);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return images;
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
