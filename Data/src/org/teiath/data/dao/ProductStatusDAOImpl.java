package org.teiath.data.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.trg.ProductStatus;

import java.util.Collection;

@Repository("productStatusDAO")
public class ProductStatusDAOImpl
		implements ProductStatusDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<ProductStatus> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<ProductStatus> statuses;

		String hql = "from ProductStatus productStatus order by productStatus.id asc ";
		Query query = session.createQuery(hql);

		statuses = query.list();

		return statuses;
	}

	@Override
	public void save(ProductStatus productStatus) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(productStatus);
	}

	@Override
	public ProductStatus findById(Integer id) {
		ProductStatus productStatus;

		Session session = sessionFactory.getCurrentSession();
		productStatus = (ProductStatus) session.get(ProductStatus.class, id);

		return productStatus;
	}

	@Override
	public void delete(ProductStatus productStatus) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(productStatus);
		session.flush();
	}
}
