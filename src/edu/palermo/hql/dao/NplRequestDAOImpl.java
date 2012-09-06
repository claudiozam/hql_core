package edu.palermo.hql.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.palermo.hql.bo.DataEntity;
import edu.palermo.hql.bo.NplRequest;

@Repository
public class NplRequestDAOImpl implements NplRequestDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public NplRequest getNplRequestById(Long id) {
		return (NplRequest) sessionFactory.getCurrentSession().get(NplRequest.class, id);
	}

	@Override
	public Long saveNplRequest(NplRequest nplRequest) {
		return (Long) sessionFactory.getCurrentSession().save(nplRequest);
	}
	
	
}
