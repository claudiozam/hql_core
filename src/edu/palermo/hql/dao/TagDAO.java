package edu.palermo.hql.dao;

import edu.palermo.hql.bo.Tag;

public interface TagDAO {
	public Tag findTagValueByCode(String code);
}
