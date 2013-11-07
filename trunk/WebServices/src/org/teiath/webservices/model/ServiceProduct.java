package org.teiath.webservices.model;

import org.codehaus.jackson.map.annotate.JsonRootName;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

@XmlRootElement(name = "product")
@JsonRootName(value = "product")
public class ServiceProduct {

	private String transactionTypeName;
	private String productName;
	private String description;
	private String productCategoryName;
	private String productStatusName;
	private Date purchaseDate;
	private BigDecimal price;
	private String ownerName;
	private String ownerRating;
	private Collection<String> ownerComments;
	private String comments;
	private Date listingCreationDate;
	private String code;

	public ServiceProduct() {
	}

	public String getTransactionTypeName() {
		return transactionTypeName;
	}

	public void setTransactionTypeName(String transactionTypeName) {
		this.transactionTypeName = transactionTypeName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public String getProductStatusName() {
		return productStatusName;
	}

	public void setProductStatusName(String productStatusName) {
		this.productStatusName = productStatusName;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerRating() {
		return ownerRating;
	}

	public void setOwnerRating(String ownerRating) {
		this.ownerRating = ownerRating;
	}

	public Collection<String> getOwnerComments() {
		return ownerComments;
	}

	public void setOwnerComments(Collection<String> ownerComments) {
		this.ownerComments = ownerComments;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getListingCreationDate() {
		return listingCreationDate;
	}

	public void setListingCreationDate(Date listingCreationDate) {
		this.listingCreationDate = listingCreationDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
