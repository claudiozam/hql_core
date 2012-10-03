package edu.palermo.hql.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
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
		return sessionFactory.getCurrentSession()
				.createQuery("from NaturalQueryCommand as n order by n.name")
				.list();
	}


	@Override
	public List<NaturalQueryCommand> getNaturalQueryCommands(String name) {
		// Query query =
		// sessionFactory.getCurrentSession().createQuery("from NaturalQueryCommand as n where n.name like :name order by n.name");
		// query.setString("name", "%" + name + "%");
		// return query.list();
		FullTextSession fullTextSession = Search
				.getFullTextSession(sessionFactory.getCurrentSession());

		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity(NaturalQueryCommand.class).get();

		org.apache.lucene.search.Query query = qb.keyword().fuzzy()
				.onField("name").matching(name).createQuery();

		org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(
				query, NaturalQueryCommand.class);

		List<NaturalQueryCommand> result = hibQuery.list();
		
		return result;
	}


	@Override
	public NaturalQueryCommand getNaturalQueryCommandByName(String name) {
		FullTextSession fullTextSession = Search
				.getFullTextSession(sessionFactory.getCurrentSession());

		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity(NaturalQueryCommand.class).get();

		org.apache.lucene.search.Query query = qb.keyword().fuzzy()
				.onField("name").matching(name).createQuery();

		org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(
				query, NaturalQueryCommand.class);
		hibQuery.setMaxResults(1);
		hibQuery.setFirstResult(0);
		
		List<NaturalQueryCommand> result = hibQuery.list();
		
		if(result.size() >= 1) {
			return result.get(0);
		}
		return null;
	}

}
