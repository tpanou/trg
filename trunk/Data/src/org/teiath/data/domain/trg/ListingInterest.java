package org.teiath.data.domain.trg;

import org.teiath.data.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "trg_listing_interests")
public class ListingInterest
		implements Serializable {

	public final static int STATUS_PENDING = 0;
	public final static int STATUS_APPROVED = 1;
	public final static int STATUS_REJECTED = 2;

	@Id
	@Column(name = "lint_id")
	@SequenceGenerator(name = "listing_interests_seq", sequenceName = "trg_listing_interests_lint_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "listing_interests_seq")
	private Integer id;

	@Column(name = "lint_interest_date", nullable = false)
	private Date interestDate;
	@Column(name = "lint_comment", length = 4000, nullable = true)
	private String comment;
	@Column(name = "lint_status", nullable = false)
	private Integer status;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "listing_id", nullable = false)
	private Listing listing;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "tradeable_good_id", nullable = true)
	private TradeableGood tradeableGood;

	public ListingInterest() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getInterestDate() {
		return interestDate;
	}

	public void setInterestDate(Date interestDate) {
		this.interestDate = interestDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TradeableGood getTradeableGood() {
		return tradeableGood;
	}

	public void setTradeableGood(TradeableGood tradeableGood) {
		this.tradeableGood = tradeableGood;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == ListingInterest.class && this.id
				.equals(((ListingInterest) obj).getId());
	}
}
