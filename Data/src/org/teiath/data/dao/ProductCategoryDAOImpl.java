package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.trg.ProductCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;

import java.util.Collection;

@Repository("productCategotyDAO")
public class ProductCategoryDAOImpl
		implements ProductCategoryDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<ProductCategory> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<ProductCategory> categories;

		String hql = "from ProductCategory";
		Query query = session.createQuery(hql);

		categories = query.list();

		return categories;
	}

	@Override
	public Collection<ProductCategory> findByParentProductCategoryId(Integer parentCategoryId) {
		Session session = sessionFactory.getCurrentSession();
		Collection<ProductCategory> categories;

		String hql = "from ProductCategory productCategory where productCategory.parentCategoryId = :parentCategoryId";

		Query query = session.createQuery(hql).setParameter("parentCategoryId", parentCategoryId);

		categories = query.list();

		return categories;
	}

	@Override
	public void save(ProductCategory productCategory) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(productCategory);
	}

	@Override
	public ProductCategory findById(Integer id) {
		ProductCategory productCategory;

		Session session = sessionFactory.getCurrentSession();
		productCategory = (ProductCategory) session.get(ProductCategory.class, id);

		return productCategory;
	}

	@Override
	public void delete(ProductCategory productCategory) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(productCategory);
		session.flush();
	}

	@Override
	public SearchResults<ProductCategory> search(SearchCriteria searchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<ProductCategory> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(ProductCategory.class);

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(searchCriteria.getPageNumber() * searchCriteria.getPageSize());
		criteria.setMaxResults(searchCriteria.getPageSize());

		//Sorting
		if (searchCriteria.getOrderField() != null) {
			if (searchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(searchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(searchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}
}
