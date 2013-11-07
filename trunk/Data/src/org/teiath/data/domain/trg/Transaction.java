package org.teiath.data.domain.trg;

import org.teiath.data.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "trg_transactions")
public class Transaction
		implements Serializable {

	@Id
	@Column(name = "transaction_id")
	@SequenceGenerator(name = "transactions_seq", sequenceName = "trg_transactions_transaction_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_seq")
	private Integer id;

	@Column(name = "transaction_date", nullable = true)
	private Date transactionDate;

	@Column(name = "transaction_is_completed", nullable = false)
	private Boolean completed;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "lint_id", nullable = true)
	private ListingInterest listingInterest;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "listing_id", nullable = true)
	private Listing listing;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User seller;

	public Transaction() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public ListingInterest getListingInterest() {
		return listingInterest;
	}

	public void setListingInterest(ListingInterest listingInterest) {
		this.listingInterest = listingInterest;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public User getSeller() {
		return seller;
	}

	public void setSeller(User seller) {
		this.seller = seller;
	}

	public Listing getListing() {
		return listing;
	}

	public void setListing(Listing listing) {
		this.listing = listing;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == Transaction.class && this.id
				.equals(((Transaction) obj).getId());
	}
}
