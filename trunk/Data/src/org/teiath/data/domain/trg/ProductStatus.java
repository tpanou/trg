package org.teiath.data.domain.trg;

import javax.persistence.*;

@Entity
@Table(name = "trg_product_statuses")
public class ProductStatus {

	@Id
	@Column(name = "product_status_id")
	@SequenceGenerator(name = "product_statuses_seq", sequenceName = "trg_product_statuses_product_status_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_statuses_seq")
	private Integer id;

	@Column(name = "product_status_code", length = 50, nullable = false)
	private String code;
	@Column(name = "product_status_name", length = 200, nullable = false)
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
		return obj != null && this.id != null && obj.getClass() == ProductStatus.class && this.id
				.equals(((ProductStatus) obj).getId());
	}
}
