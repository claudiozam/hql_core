package edu.palermo.hql.dao;

import java.util.List;

import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
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
	public List<DataEntity> getDataEntities(String alias) throws Exception {
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
		
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity(DataEntity.class).get();

		org.apache.lucene.search.Query query 
			= qb.keyword().fuzzy().onField("alias").matching(alias).createQuery();

		org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(
				query, DataEntity.class);

		List<DataEntity> result = hibQuery.list();
		return result;
		
	}

	@Override
	public DataEntity findDataEntitieByAlias(String alias) throws Exception {
	
	FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
		
		QueryBuilder qb = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity(DataEntity.class).get();

		org.apache.lucene.search.Query query 
			= qb.keyword().fuzzy().onField("alias").matching(alias).createQuery();

		org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(
				query, DataEntity.class);
		hibQuery.setMaxResults(1);
		hibQuery.setFirstResult(0);
		
		List<DataEntity> result = hibQuery.list();
		
		
		if(result.size() >= 1) {
			return result.get(0);
		}
		return null;
	}


	@Override
	public void reIndexFTS() throws InterruptedException {
		System.out.println("Ejecutando reIndexFTS");
		FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
		
		MassIndexer massIndexer = fullTextSession.createIndexer();
		massIndexer.startAndWait();
	}

}
