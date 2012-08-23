package edu.palermo.hql.dao;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.palermo.hql.bo.Tag;

@Repository
public class TagDAOImpl implements TagDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Tag findTagValueByCode(String code){
		Query query = sessionFactory.getCurrentSession().createQuery("from position1 as p where p.value like :code");
		query.setString("code", "%" + code.substring(1,1) + "%");
		return (Tag) query.uniqueResult();
	}

}
