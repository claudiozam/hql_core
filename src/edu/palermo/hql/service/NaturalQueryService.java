package edu.palermo.hql.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.palermo.hql.bo.AutoCompleteItem;
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

	public List<AutoCompleteItem> getAutoCompleteItems(String term) throws Exception {
		List<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();
		for(NaturalQueryCommand command : naturalQueryCommandDAO.getNaturalQueryCommands(term)) {
			items.add(new AutoCompleteItem(command.getId(), command.getName(), command.getName() + " (Comando)", ""));
		}
		
		for(DataEntity dataEntity : dataEntityDAO.getDataEntities(term)) {
			items.add(new AutoCompleteItem(dataEntity.getId(), dataEntity.getAlias(), dataEntity.getAlias() + " (Entidad)", ""));
		}
				
		return items;
	}
	
	public DataEntity findDataEntitieByAlias(String alias) throws Exception {
		return dataEntityDAO.findDataEntitieByAlias(alias);
	}
	
	public void reIndexFTSAllObjets() throws InterruptedException {
		dataEntityDAO.reIndexFTS();
	}
	
}
