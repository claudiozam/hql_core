package edu.palermo.hql.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.palermo.hql.bo.DataEntity;
import edu.palermo.hql.bo.NaturalQueryCommand;

@Repository
public class NaturalQueryCommandDAOImpl implements NaturalQueryCommandDAO {

	@Autowired
	private SessionFactory sessionFactory;
	

	@Override
	public List<NaturalQueryCommand> getNaturalQueryCommands() {
		return sessionFactory.getCurrentSession().createQuery("from NaturalQueryCommand as n order by n.name").list();

	}

	@Override
	public List<NaturalQueryCommand> getNaturalQueryCommands(String type) {
		Query query = sessionFactory.getCurrentSession().createQuery("from NaturalQueryCommand as n where n.type = :type order by n.name");
		query.setString("type", type);
		return query.list();
	}

}
