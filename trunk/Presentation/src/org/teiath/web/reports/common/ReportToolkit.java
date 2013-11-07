package org.teiath.web.reports.common;

import org.teiath.data.domain.Notification;
import org.teiath.data.domain.trg.*;

import java.util.Collection;
import java.util.Date;

public class ReportToolkit {

	public static Report requestCreatedListingsReport(Date dateFrom, Date dateTo, int reportType) {
		Report report = new Report();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("createdListings" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("createdListingsReport.jasper");

		return report;
	}

	public static Report requestSellerListingsReport(Collection<Listing> listings, int reportType) {
		Report report = new Report();
		report.setListings(listings);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("listings" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("myListingsReport.jasper");

		return report;
	}

	public static Report requestNotificationsReport(Collection<Notification> notifications, int reportType) {
		Report report = new Report();
		report.setNotifications(notifications);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("notifications" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("myNotificationsReport.jasper");

		return report;
	}

	public static Report requestTransactionsReport(Collection<Transaction> transactions, int reportType) {
		Report report = new Report();
		report.setTransactions(transactions);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("transactions" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("myTransactionsReport.jasper");

		return report;
	}

	public static Report requestSearchListingsReport(Collection<Listing> listings, int reportType) {
		Report report = new Report();
		report.setListings(listings);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("listings" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("searchListingsReport.jasper");

		return report;
	}

	public static Report requestSellerListingInterestsReport(Collection<ListingInterest> listingInterests,
	                                                         int reportType) {
		Report report = new Report();
		report.setListingInterests(listingInterests);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("listingInterests" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("myListingInterestsReport.jasper");

		return report;
	}

	public static Report requestPersonalListingInterestsReport(Collection<ListingInterest> listingInterests,
	                                                           int reportType) {
		Report report = new Report();
		report.setListingInterests(listingInterests);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("listingInterests" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("personalListingInterestsReport.jasper");

		return report;
	}

	public static Report requestIncomingListingInterestsReport(Collection<ListingInterest> listingInterests,
	                                                           int reportType) {
		Report report = new Report();
		report.setListingInterests(listingInterests);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("listingInterests" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("incomingListingInterestsReport.jasper");

		return report;
	}

	public static Report requestListingsTransactionTypeReport(int transactionTypeId, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("ListingsTransactionTypeReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("ListingsTransactionTypeReport.jasper");
		report.getParameters().put("TRANSACTION_TYPE_ID", transactionTypeId);

		return report;
	}

	public static Report requestListingsTransactionTypeReportXLS(TransactionType transactionType, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("ListingsTransactionTypeReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("ListingsTransactionTypeReport.jasper");
		report.getParameters().put("TRANSACTION_TYPE", transactionType);

		return report;
	}

	public static Report requestListingsProductCategoryReport(int productCategoryId, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("ListingsProductCategoryReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("ListingsProductCategoryReport.jasper");
		report.getParameters().put("PRODUCT_CATEGORY_ID", productCategoryId);

		return report;
	}

	public static Report requestListingsProductCategoryReportXLS(ProductCategory productCategory, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("ListingsProductCategoryReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("ListingsProductCategoryReport.jasper");
		report.getParameters().put("PRODUCT_CATEGORY", productCategory);

		return report;
	}

	public static Report requestOnGoinfActionsReport(Date dateFrom, Date dateTo, int reportType) {
		Report report = new Report();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("onGoingActionsReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/actions/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("onGoingActionsReport.jasper");

		return report;
	}

	public static Report requestActionsByCategoryReport(int eventCategoryId, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("actionsByCategoryReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/actions/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("actionsByCategoryReport.jasper");
		report.getParameters().put("EVENT_CATEGORY_ID", eventCategoryId);

		return report;
	}

	public static Report requestTransactionsReport(Date dateFrom, Date dateTo, int reportType) {
		Report report = new Report();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("TransactionsReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/listings/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("TransactionsReport.jasper");

		return report;
	}

	public static Report requestAccommodationsByAccommodationTypeReport(int accommodationTypeId, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("AccommodationsByAccommodationTypeReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/rmt/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("AccommodationsByAccommodationTypeReport.jasper");
		report.getParameters().put("ACCOMMODATION_TYPE_ID", accommodationTypeId);

		return report;
	}

	public static Report requestAccommodationsByNumberOfBedroomReport(Integer numberFrom, Integer numberTo,
	                                                                  int reportType) {
		Report report = new Report();
		report.setNumberFrom(numberFrom);
		report.setNumberTo(numberTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("AccommodationsByNumberOfBedroomsReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/rmt/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("AccommodationsByNumberOfBedroomsReport.jasper");

		return report;
	}

	private static String findFileExtension(int reportType) {
		switch (reportType) {
			case ReportType.PDF:
				return ".pdf";
			case ReportType.EXCEL:
				return ".xls";
		}

		return null;
	}
}
