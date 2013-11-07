package org.teiath.data.dao;

import org.teiath.data.domain.trg.ProductStatus;

import java.util.Collection;

public interface ProductStatusDAO {

	public Collection<ProductStatus> findAll();

	public void save(ProductStatus productStatus);

	public ProductStatus findById(Integer id);

	public void delete(ProductStatus productStatus);
}
