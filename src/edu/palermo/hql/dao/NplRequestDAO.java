package edu.palermo.hql.dao;

import edu.palermo.hql.bo.*;
import java.util.*;

public interface NplRequestDAO {

	public NplRequest getNplRequestById(Long id);
	public Long saveNplRequest(NplRequest nplRequest);
	
}
