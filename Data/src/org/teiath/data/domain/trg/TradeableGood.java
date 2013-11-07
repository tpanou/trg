package org.teiath.data.domain.trg;

import org.teiath.data.domain.image.ListingMainImage;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "trg_tradeable_goods")
public class TradeableGood {

	@Id
	@Column(name = "tradeable_good_id")
	@SequenceGenerator(name = "tradeable_goods_seq", sequenceName = "trg_tradeable_goods_tradeable_good_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tradeable_goods_seq")
	private Integer id;

	@Column(name = "tradeable_good_name", length = 2000, nullable = false)
	private String name;
	@Column(name = "tradeable_good_description", length = 2000, nullable = true)
	private String description;
	@Column(name = "tradeable_good_brand", length = 2000, nullable = true)
	private String brand;
	@Column(name = "tradeable_good_purchaseDate", nullable = true)
	private Date purchaseDate;
	@Column(name = "tradeable_good_comments", length = 2000, nullable = true)
	private String comments;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_status_id", nullable = true)
	private ProductStatus productStatus;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "main_image_id", nullable = true)
	private ListingMainImage listingMainImage;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "listing_id", nullable = true)
	private Listing listing;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "tradeableGood")
	private ListingInterest listingInterest;

	public TradeableGood() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public ProductStatus getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(ProductStatus productStatus) {
		this.productStatus = productStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public ListingMainImage getListingMainImage() {
		return listingMainImage;
	}

	public void setListingMainImage(ListingMainImage listingMainImage) {
		this.listingMainImage = listingMainImage;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public ListingInterest getListingInterest() {
		return listingInterest;
	}

	public void setListingInterest(ListingInterest listingInterest) {
		this.listingInterest = listingInterest;
	}
}
