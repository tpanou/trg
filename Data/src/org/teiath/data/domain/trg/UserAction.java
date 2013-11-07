package org.teiath.data.domain.trg;

import org.teiath.data.domain.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "trg_user_actions")
public class UserAction {
	public final static int TYPE_CREATE = 0;
/*	public final static int TYPE_GOODS = 1;
	public final static int TYPE_ACTIONS = 2;
	public final static int TYPE_ROOMMATES = 3;*/

	@Id
	@Column(name = "user_action_id")
	@SequenceGenerator(name = "user_actions_seq", sequenceName = "trg_user_actions_user_action_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_actions_seq")
	private Integer id;

	@Column(name = "user_action_date", nullable = false)
	private Date date;
	@Column(name = "user_action_type", nullable = false)
	private int type;
	@Column(name = "product_name", length = 2000, nullable = true)
	private String product;
	@Column(name = "transaction_type", length = 2000, nullable = true)
	private String transactionType;
	@Column(name = "product_price", length = 2000, nullable = true)
	private String price;
	@Column(name = "listing_creation_date", nullable = true)
	private Date listingDate;
	@Column(name = "listing_code", length = 2000, nullable = true)
	private String listingCode;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public UserAction() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getListingCode() {
		return listingCode;
	}

	public void setListingCode(String listingCode) {
		this.listingCode = listingCode;
	}

	public Date getListingDate() {
		return listingDate;
	}

	public void setListingDate(Date listingDate) {
		this.listingDate = listingDate;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == UserAction.class && this.id
				.equals(((UserAction) obj).getId());
	}
}
