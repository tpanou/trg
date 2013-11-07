package org.teiath.data.domain.trg;

import org.teiath.data.domain.Notification;
import org.teiath.data.domain.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "trg_product_notifications_criteria")
public class ProductNotificationCriteria {

	@Id
	@Column(name = "ntf_criteria_id")
	@SequenceGenerator(name = "ntf_criteria_seq", sequenceName = "notifications_criteria_ntf_criteria_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ntf_criteria_seq")
	private Integer id;

	@Column(name = "notification_criteria_title", length = 2000, nullable = false)
	private String title;
	@Column(name = "notification_criteria_description", length = 2000, nullable = true)
	private String description;
	@Column(name = "notification_criteria_type", nullable = false)
	private int type;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "notificationCriteria")
	private Set<Notification> notifications;

	@Column(name = "product_notification_criteria_purchaseDateFrom", nullable = true)
	private Date purchaseDateFrom;

	@Column(name = "product_notification_criteria_purchaseDateTo", nullable = true)
	private Date purchaseDateTo;

	@Column(name = "product_notification_criteria_maxPrice", precision = 6, scale = 2, nullable = true)
	private BigDecimal maxPrice;

	@Column(name = "product_notification_criteria_keywords", length = 2000, nullable = true)
	private String keywords;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "transaction_type_id", nullable = true)
	private TransactionType transactionType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_category_id", nullable = true)
	private ProductCategory productCategory;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_status_id", nullable = true)
	private ProductStatus productStatus;

	public ProductNotificationCriteria() {
	}

	public Date getPurchaseDateFrom() {
		return purchaseDateFrom;
	}

	public void setPurchaseDateFrom(Date purchaseDateFrom) {
		this.purchaseDateFrom = purchaseDateFrom;
	}

	public Date getPurchaseDateTo() {
		return purchaseDateTo;
	}

	public void setPurchaseDateTo(Date purchaseDateTo) {
		this.purchaseDateTo = purchaseDateTo;
	}

	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}

	public int getTotalNotifications() {
		int totalNotifications = 0;

		for (Notification notification : this.notifications) {
			totalNotifications++;
		}

		return totalNotifications;
	}
}
