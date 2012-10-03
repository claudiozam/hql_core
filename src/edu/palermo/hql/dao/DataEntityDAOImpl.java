package edu.palermo.hql.dao;

import java.util.List;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.FuzzyQuery;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
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
		System.out.println("Ejecutando FTS: " + alias);
		FullTextSession fullTextSession = Search.createFullTextSession(sessionFactory.getCurrentSession());
		
		org.apache.lucene.queryParser.QueryParser parser = new QueryParser("alias", new StandardAnalyzer() );

		org.apache.lucene.search.Query luceneQuery = parser.parse("alias:" + alias + "~");
		org.hibernate.Query fullTextQuery = fullTextSession.createFullTextQuery( luceneQuery, DataEntity.class); 
		
		List<DataEntity> result = fullTextQuery.list(); //return a list of managed objects
		return result;
		
	}

	@Override
	public DataEntity findDataEntitieByAlias(String alias) throws Exception {
	
	
		List<DataEntity> elements = getDataEntities(alias);
		//Query query = sessionFactory.getCurrentSession().createQuery("from DataEntity as d where d.alias like :alias");
		//query.setString("alias", "%" + alias + "%");
		if(elements.size() >= 1) {
			return elements.get(0);
		}
		return null;
	}


	@Override
	public void reIndexFTS() {
		System.out.println("Ejecutando reIndexFTS");
		FullTextSession fullTextSession = Search.createFullTextSession(sessionFactory.getCurrentSession());
		
		Query query = fullTextSession.createQuery("from DataEntity");
		List<DataEntity> elements = query.list();
		for (DataEntity dataEntity : elements) {
			fullTextSession.update(dataEntity);
		}
	}

}
