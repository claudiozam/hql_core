package edu.palermo.hql.service;

import edu.palermo.hql.bo.NplRequest;
import edu.palermo.hql.bo.NplResponse;

public interface NplService {

	public NplResponse analize(NplRequest nplRequest) throws Exception;
	public NplResponse analizeBySavedQuery(Long queryId) throws Exception;
	
}
