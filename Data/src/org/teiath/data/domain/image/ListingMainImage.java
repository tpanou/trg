package org.teiath.data.domain.image;

import org.teiath.data.domain.trg.Listing;
import org.zkoss.image.AImage;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Entity
@Table(name = "listing_main_images")
public class ListingMainImage
		implements Serializable {

	@Id
	@Column(name = "listing_main_image_id")
	@SequenceGenerator(name = "listing_main_images_seq", sequenceName = "listing_main_images_listing_main_image_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "listing_main_images_seq")
	private Integer id;

	@Column(name = "image_bytes", nullable = true)
	private byte[] imageBytes;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "listingMainImage")
	private Listing listing;

	private AImage image;

	public ListingMainImage() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}

	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public AImage getImage()
			throws IOException {
		return new AImage("Image", this.getImageBytes());
	}

	public void setImage(AImage image) {
		this.image = image;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == ListingMainImage.class && this.id
				.equals(((ListingMainImage) obj).getId());
	}
}
