package org.teiath.data.domain.image;

import org.teiath.data.domain.trg.Listing;
import org.zkoss.image.AImage;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;

@Entity
@Table(name = "application_images")
public class ApplicationImage
		implements Serializable {

	@Id
	@Column(name = "application_image_id")
	@SequenceGenerator(name = "application_images_seq", sequenceName = "application_images_application_image_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_images_seq")
	private Integer id;

	@Column(name = "image_bytes", nullable = true)
	private byte[] imageBytes;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "listing", nullable = true)
	private Listing listing;

	private AImage image;

	public ApplicationImage() {
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
		return obj != null && this.id != null && obj.getClass() == ApplicationImage.class && this.id
				.equals(((ApplicationImage) obj).getId());
	}
}
