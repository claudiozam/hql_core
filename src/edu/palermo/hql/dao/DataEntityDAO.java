package edu.palermo.hql.dao;

import edu.palermo.hql.bo.*;
import java.util.*;

public interface DataEntityDAO {

	public List<DataEntity> getDataEntities();
	public List<DataEntity> getDataEntities(String alias) throws Exception;
	public DataEntity findDataEntitieByAlias(String alias) throws Exception;
	public void reIndexFTS() throws InterruptedException;
}
