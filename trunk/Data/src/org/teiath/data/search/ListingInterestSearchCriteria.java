package org.teiath.data.search;

import org.teiath.data.domain.User;
import org.teiath.data.domain.trg.Listing;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.domain.trg.ProductStatus;
import org.teiath.data.domain.trg.TransactionType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ListingInterestSearchCriteria
		extends SearchCriteria
		implements Serializable {

	private Listing listing;
	private ProductCategory productCategory;
	private TransactionType transactionType;
	private String listingKeyword;
	private Date date;
	private boolean active;
	private ProductStatus productStatus;
	private Date dateFrom;
	private Date dateTo;
	private BigDecimal maxAmount;
	private User buyer;
	private Integer status;
	private String listingCode;
	private Integer parentCategoryId;

	public ListingInterestSearchCriteria() {
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

	public String getListingKeyword() {
		return listingKeyword;
	}

	public void setListingKeyword(String listingKeyword) {
		this.listingKeyword = listingKeyword;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ProductStatus getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(ProductStatus productStatus) {
		this.productStatus = productStatus;
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

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public User getBuyer() {
		return buyer;
	}

	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
