package org.teiath.data.dao;

import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;

import java.util.Collection;

public interface ProductCategoryDAO {

	public Collection<ProductCategory> findAll();

	public Collection<ProductCategory> findByParentProductCategoryId(Integer parentCategoryId);

	public void save(ProductCategory productCategory);

	public ProductCategory findById(Integer id);

	public void delete(ProductCategory productCategory);

	public SearchResults<ProductCategory> search(SearchCriteria criteria);
}
