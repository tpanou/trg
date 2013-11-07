package org.teiath.data.search;

import org.teiath.data.domain.trg.ListingInterest;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.TransactionType;

import java.io.Serializable;
import java.util.Date;

public class TransactionSearchCriteria
		extends SearchCriteria
		implements Serializable {

	private ListingInterest listingInterest;
	private Date transactionDate;
	private String listingKeyword;
	private Date dateFrom;
	private Date dateTo;
	private ProductCategory productCategory;
	private TransactionType transactionType;
	private Boolean completed;
	private Boolean listingEnabled;
	private String listingCode;
	private Integer parentCategoryId;

	public TransactionSearchCriteria() {
	}

	public ListingInterest getListingInterest() {
		return listingInterest;
	}

	public void setListingInterest(ListingInterest listingInterest) {
		this.listingInterest = listingInterest;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getListingKeyword() {
		return listingKeyword;
	}

	public void setListingKeyword(String listingKeyword) {
		this.listingKeyword = listingKeyword;
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

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Boolean getListingEnabled() {
		return listingEnabled;
	}

	public void setListingEnabled(Boolean listingEnabled) {
		this.listingEnabled = listingEnabled;
	}

	public String getListingCode() {
		return listingCode;
	}

	public void setListingCode(String listingCode) {
		this.listingCode = listingCode;
	}

	public Integer getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Integer parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}
}
