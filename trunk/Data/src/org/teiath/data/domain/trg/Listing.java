package org.teiath.data.domain.trg;

import org.teiath.data.domain.Notification;
import org.teiath.data.domain.User;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.image.ListingMainImage;

import javax.persistence.*;
import javax.swing.*;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "trg_listings")
public class Listing
		implements Serializable {

	@Id
	@Column(name = "listing_id")
	@SequenceGenerator(name = "listings_seq", sequenceName = "trg_listings_listing_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "listings_seq")
	private Integer id;

	@Column(name = "listing_code", length = 2000, nullable = false)
	private String code;
	@Column(name = "listing_product_name", length = 2000, nullable = false)
	private String productName;
	@Column(name = "listing_product_brand", length = 2000, nullable = true)
	private String productBrand;
	@Column(name = "listing_product_description", length = 2000, nullable = false)
	private String productDescription;
	@Column(name = "listing_purchaseDate", nullable = true)
	private Date purchaseDate;
	@Column(name = "listing_price", precision = 6, scale = 2, nullable = true)
	private BigDecimal price;
	@Column(name = "listing_comments", length = 2000, nullable = true)
	private String comments;
	@Column(name = "listing_desiredGoods", length = 2000, nullable = true)
	private String desiredGoods;
	@Column(name = "listing_photos", nullable = true)
	private File[] photos;
	@Column(name = "listing_is_active", nullable = false)
	private Boolean active;
	@Column(name = "listing_creation_date", nullable = false)
	private Date listingCreationDate;
	@Column(name = "listing_is_enabled", nullable = true)
	private boolean enabled;
	@Column(name = "listing_send_home", nullable = true)
	private boolean sendHome;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_category_id", nullable = false)
	private ProductCategory productCategory;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_status_id", nullable = false)
	private ProductStatus productStatus;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "currency_id", nullable = false)
	private Currency currency;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "transaction_type_id", nullable = false)
	private TransactionType transactionType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "listing")
	private Set<ListingInterest> listingInterests;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "listing")
	private Set<ApplicationImage> listingImages;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "listing_main_image_id", nullable = true)
	private ListingMainImage listingMainImage;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "listing")
	private Set<Notification> notifications;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "listing")
	private List<TradeableGood> listingTradeableGoods;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "transaction_id", nullable = true)
	private Transaction transaction;

	private Integer interests;

	@javax.persistence.Transient
	private ImageIcon reportImage;

	@javax.persistence.Transient
	private String priceWithCurrency;

	public Listing() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
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

	Locale locale = Locale.ITALY;

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public File[] getPhotos() {
		return photos;
	}

	public void setPhotos(File[] photos) {
		this.photos = photos;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public ProductStatus getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(ProductStatus productStatus) {
		this.productStatus = productStatus;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<ListingInterest> getListingInterests() {
		return listingInterests;
	}

	public void setListingInterests(Set<ListingInterest> listingInterests) {
		this.listingInterests = listingInterests;
	}

	public Integer getInterests() {
		this.interests = 0;

		for (ListingInterest listingInterest : this.listingInterests) {
			this.interests++;
		}

		return this.interests;
	}

	public void setInterests(Integer interests) {
		this.interests = interests;
	}

	public Set<ApplicationImage> getListingImages() {
		return listingImages;
	}

	public void setListingImages(Set<ApplicationImage> listingImages) {
		this.listingImages = listingImages;
	}

	public Set<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getListingCreationDate() {
		return listingCreationDate;
	}

	public void setListingCreationDate(Date listingCreationDate) {
		this.listingCreationDate = listingCreationDate;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getPriceWithCurrency() {
		return this.price.toString() + " " + this.currency.getName();
	}

	public void setPriceWithCurrency(String priceWithCurrency) {
		this.priceWithCurrency = priceWithCurrency;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDesiredGoods() {
		return desiredGoods;
	}

	public void setDesiredGoods(String desiredGoods) {
		this.desiredGoods = desiredGoods;
	}

	public String getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(String productBrand) {
		this.productBrand = productBrand;
	}

	public boolean isSendHome() {
		return sendHome;
	}

	public void setSendHome(boolean sendHome) {
		this.sendHome = sendHome;
	}

	public ListingMainImage getListingMainImage() {
		return listingMainImage;
	}

	public void setListingMainImage(ListingMainImage listingMainImage) {
		this.listingMainImage = listingMainImage;
	}

	public List<TradeableGood> getListingTradeableGoods() {
		return listingTradeableGoods;
	}

	public void setListingTradeableGoods(List<TradeableGood> listingTradeableGoods) {
		this.listingTradeableGoods = listingTradeableGoods;
	}

	public ImageIcon getReportImage() {
		return reportImage;
	}

	public void setReportImage(ImageIcon reportImage) {
		this.reportImage = reportImage;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == Listing.class && this.id
				.equals(((Listing) obj).getId());
	}
}
