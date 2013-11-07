package org.teiath.data.domain.trg;

import javax.persistence.*;

@Entity
@Table(name = "trg_product_categories")
public class ProductCategory {

	@Id
	@Column(name = "product_category_id")
	@SequenceGenerator(name = "product_categories_seq", sequenceName = "trg_product_categories_product_category_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_categories_seq")
	private Integer id;

	@Column(name = "product_category_code", length = 50, nullable = false)
	private String code;
	@Column(name = "product_category_name", length = 200, nullable = false)
	private String name;
	@Column(name = "parent_category_id", nullable = false)
	private Integer parentCategoryId;

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

	public Integer getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(Integer parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == ProductCategory.class && this.id
				.equals(((ProductCategory) obj).getId());
	}
}
