package org.teiath.web.reports.common;

import org.teiath.data.domain.Notification;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.Transaction;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Report
		implements Serializable {

	private Date dateFrom;
	private Date dateTo;
	private Integer numberFrom;
	private Integer numberTo;
	private String displayName;
	private String outputFileName;
	private String reportLocation;
	private String rootReportFile;
	private String imagesLocation;
	private Collection<Listing> listings;
	private Collection<Transaction> transactions;
	private Collection<ListingInterest> listingInterests;
	private Collection<Notification> notifications;
	private int userId;
	private int reportType;
	private int excelReport;
	private Map<String, Object> parameters;

	public Report() {
		parameters = new HashMap<String, Object>();
	}

	public Report(int reportType) {
		parameters = new HashMap<>();
		this.reportType = reportType;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Integer getNumberFrom() {
		return numberFrom;
	}

	public void setNumberFrom(Integer numberFrom) {
		this.numberFrom = numberFrom;
	}

	public Integer getNumberTo() {
		return numberTo;
	}

	public void setNumberTo(Integer numberTo) {
		this.numberTo = numberTo;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getReportLocation() {
		return reportLocation;
	}

	public void setReportLocation(String reportLocation) {
		this.reportLocation = reportLocation;
	}

	public String getRootReportFile() {
		return rootReportFile;
	}

	public void setRootReportFile(String rootReportFile) {
		this.rootReportFile = rootReportFile;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public String getImagesLocation() {
		return imagesLocation;
	}

	public void setImagesLocation(String imagesLocation) {
		this.imagesLocation = imagesLocation;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public int getExcelReport() {
		return excelReport;
	}

	public void setExcelReport(int excelReport) {
		this.excelReport = excelReport;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Collection<Listing> getListings() {
		return listings;
	}

	public void setListings(Collection<Listing> listings) {
		this.listings = listings;
	}

	public Collection<ListingInterest> getListingInterests() {
		return listingInterests;
	}

	public void setListingInterests(Collection<ListingInterest> listingInterests) {
		this.listingInterests = listingInterests;
	}

	public Collection<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Collection<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Collection<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Collection<Notification> notifications) {
		this.notifications = notifications;
	}
}
