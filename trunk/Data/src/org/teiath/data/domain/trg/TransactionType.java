package org.teiath.data.domain.trg;

import javax.persistence.*;

@Entity
@Table(name = "trg_transaction_types")
public class TransactionType {

	public final static int TYPE_DONATION = 1;
	public final static int TYPE_EXCHANGE = 2;
	public final static int TYPE_SELL = 3;

	@Id
	@Column(name = "transaction_type_id")
	@SequenceGenerator(name = "transaction_types_seq", sequenceName = "trg_transaction_types_transaction_type_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_types_seq")
	private Integer id;

	@Column(name = "transaction_type_code", length = 50, nullable = false)
	private String code;
	@Column(name = "transaction_type_name", length = 200, nullable = false)
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == TransactionType.class && this.id
				.equals(((TransactionType) obj).getId());
	}
}
