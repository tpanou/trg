package org.teiath.data.domain.trg;

import org.teiath.data.domain.Assessment;
import org.teiath.data.domain.User;

import javax.persistence.*;

@Entity
@Table(name = "crp_listing_assessments")
@PrimaryKeyJoinColumn(name = "assessment_id")
public class ListingAssessment
		extends Assessment {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User assessedUser;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "transaction_id", nullable = false)
	private Transaction assessedTransaction;

	public ListingAssessment() {
	}

	public Transaction getAssessedTransaction() {
		return assessedTransaction;
	}

	public void setAssessedTransaction(Transaction assessedTransaction) {
		this.assessedTransaction = assessedTransaction;
	}

	public User getAssessedUser() {
		return assessedUser;
	}

	public void setAssessedUser(User assessedUser) {
		this.assessedUser = assessedUser;
	}
}
