package edu.palermo.hql.dao;

import edu.palermo.hql.bo.*;
import java.util.*;

public interface DataEntityDAO {

	public List<DataEntity> getDataEntities();
	public List<DataEntity> getDataEntities(String name);
	public DataEntity findDataEntitieByAlias(String alias);
}
