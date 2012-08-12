package edu.palermo.hql.mvc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.palermo.hql.bo.AutoCompleteItem;
import edu.palermo.hql.bo.NaturalQueryCommand;
import edu.palermo.hql.bo.NplRequest;
import edu.palermo.hql.bo.NplResponse;
import edu.palermo.hql.service.HQLException;
import edu.palermo.hql.service.NaturalQueryService;
import edu.palermo.hql.service.NplService;

@Controller
@RequestMapping(value="/hqlmain")
public class HqlMainController {

	private static Logger log = Logger.getLogger(HqlMainController.class);
	
	@Autowired
	private NplService nplService;
	
	@Autowired
	private NaturalQueryService naturalQueryService;
	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index() {
		return "/hqlmain/index";
	}
	

	@ResponseBody
	@RequestMapping(value="/autocomplete_natural_query_commands", method=RequestMethod.GET)
	public List<AutoCompleteItem> autoCompleteNaturalQueryCommands(String term) throws HQLException {
		log.info("Ejecutando autoCompleteNaturalQueryCommands para el termino: " + term);
		//Ver si filtramos comandos duplicados y de damos algo de logica al autocomplete para generar las consultas
		List<AutoCompleteItem> items = new ArrayList<AutoCompleteItem>();
		for(NaturalQueryCommand command : naturalQueryService.getNaturalQueryCommands()) {
			items.add(new AutoCompleteItem(command.getId(), command.getName(), command.getName(), ""));
		}
		return items;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/analize", method=RequestMethod.GET)
	public NplResponse analize(NplRequest nplRequest) throws HQLException {
		log.info("Ejecutando analize");
		return nplService.analize(nplRequest);
	}
	
}
