package org.teiath.webservices.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingAssessment;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.ListingSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.trg.SearchListingsService;
import org.teiath.service.trg.ViewListingInterestSellerService;
import org.teiath.service.trg.ViewListingSellerService;
import org.teiath.webservices.model.ServiceProduct;
import org.teiath.webservices.model.ServiceProductList;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Controller
public class ProductsController {

	@Autowired
	private SearchListingsService searchListingsService;
	@Autowired
	private ViewListingInterestSellerService viewListingInterestSellerService;
	@Autowired
	private ViewListingSellerService viewListingSellerService;

	private static final Logger logger_c = Logger.getLogger(ProductsController.class);

	@RequestMapping(value = "/services/products", method = RequestMethod.GET)
	public ServiceProductList searchProducts(String transactionType, String category, String status, String pDateFrom,
	                                         String pDateTo, Double price, String keyword) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		ServiceProductList serviceProductList = new ServiceProductList();
		serviceProductList.setServiceProducts(new ArrayList<ServiceProduct>());
		ServiceProduct serviceProduct = null;

		ListingSearchCriteria criteria = new ListingSearchCriteria();
		try {
			criteria.setTransactionTypeName(transactionType);
			criteria.setProductCategoryName(category);
			criteria.setProductStatusName(status);
			criteria.setDateFrom(pDateFrom != null ? sdf.parse(pDateFrom) : null);
			criteria.setDateTo(pDateTo != null ? sdf.parse(pDateTo) : null);
			criteria.setMaxAmount(price != null ? new BigDecimal(price) : null);
			criteria.setListingKeyword(keyword);

			criteria.setPageNumber(0);
			criteria.setPageSize(Integer.MAX_VALUE);

			SearchResults<Listing> results = searchListingsService.searchListings(criteria);

			for (Listing listing : results.getData()) {
				serviceProduct = new ServiceProduct();
				serviceProduct.setTransactionTypeName(listing.getTransactionType().getName());
				serviceProduct.setProductName(listing.getProductName());
				serviceProduct.setDescription(listing.getProductDescription());
				serviceProduct.setProductCategoryName(listing.getProductCategory().getName());
				serviceProduct.setProductStatusName(listing.getProductStatus().getName());
				serviceProduct.setPurchaseDate(listing.getPurchaseDate());
				serviceProduct.setPrice(listing.getPrice());
				serviceProduct.setOwnerName(listing.getUser().getFullName());
				serviceProduct.setListingCreationDate(listing.getListingCreationDate());
				serviceProduct.setCode(listing.getCode());
				serviceProduct.setOwnerRating(
						listing.getUser().getAverageTransactionRating() != null ? listing.getUser()
								.getAverageTransactionRating().toString() : "-");
				for (ListingAssessment listingAssessment : viewListingInterestSellerService
						.findUserTransactionsComments(listing.getUser())) {
					serviceProduct.getOwnerComments().add(listingAssessment.getComment());
				}
				serviceProduct.setComments(listing.getComments());

				serviceProductList.getServiceProducts().add(serviceProduct);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		logger_c.debug("Returing Routes: " + serviceProduct);
		return serviceProductList;
	}

	@RequestMapping(value = "/services/product", method = RequestMethod.GET)
	public ServiceProduct searchProductByCode(String code) {

		ServiceProduct serviceProduct = new ServiceProduct();
		try {

			Listing listing = viewListingSellerService.getListingByCode(code);
			serviceProduct.setTransactionTypeName(listing.getTransactionType().getName());
			serviceProduct.setProductName(listing.getProductName());
			serviceProduct.setDescription(listing.getProductDescription());
			serviceProduct.setProductCategoryName(listing.getProductCategory().getName());
			serviceProduct.setProductStatusName(listing.getProductStatus().getName());
			serviceProduct.setPurchaseDate(listing.getPurchaseDate());
			serviceProduct.setPrice(listing.getPrice());
			serviceProduct.setOwnerName(listing.getUser().getFullName());
			serviceProduct.setListingCreationDate(listing.getListingCreationDate());
			serviceProduct.setOwnerRating(
					listing.getUser().getAverageTransactionRating() != null ? listing.getUser()
							.getAverageTransactionRating().toString() : "-");
			for (ListingAssessment listingAssessment : viewListingInterestSellerService
					.findUserTransactionsComments(listing.getUser())) {
				serviceProduct.getOwnerComments().add(listingAssessment.getComment());
			}
			serviceProduct.setComments(listing.getComments());
			serviceProduct.setCode(code);

		} catch (ServiceException e) {
			e.printStackTrace();
		}

		logger_c.debug("Returing Product: " + serviceProduct);

		return serviceProduct;
	}
}
