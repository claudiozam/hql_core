package edu.palermo.hql.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.palermo.hql.bo.DataEntity;
import edu.palermo.hql.bo.NaturalQueryCommand;
import edu.palermo.hql.dao.DataEntityDAO;
import edu.palermo.hql.dao.NaturalQueryCommandDAO;

@Service
@Transactional(rollbackFor=Exception.class)
public class NaturalQueryService {

	@Autowired
	private DataEntityDAO dataEntityDAO;
	
	@Autowired
	private NaturalQueryCommandDAO naturalQueryCommandDAO;
	
	public List<NaturalQueryCommand> getNaturalQueryCommands() {
		return naturalQueryCommandDAO.getNaturalQueryCommands();
	}
	
}
