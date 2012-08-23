package edu.palermo.hql.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.palermo.hql.bo.DataEntity;

@Repository
public class DataEntityDAOImpl implements DataEntityDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	
	@Override
	public List<DataEntity> getDataEntities() {
		return sessionFactory.getCurrentSession().createQuery("from DataEntity as d order by d.name").list();
	}

	
	@Override
	public List<DataEntity> getDataEntities(String name) {
		Query query = sessionFactory.getCurrentSession().createQuery("from DataEntity as d where d.name like :name order by d.name");
		query.setString("name", "%" + name + "%");
		return query.list();
	}

	@Override
	public DataEntity findDataEntitieByAlias(String alias) {
		Query query = sessionFactory.getCurrentSession().createQuery("from DataEntity as d where d.alias like :alias");
		query.setString("alias", "%" + alias + "%");
		return (DataEntity) query.uniqueResult();
	}

}
