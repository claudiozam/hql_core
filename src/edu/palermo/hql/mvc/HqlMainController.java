package edu.palermo.hql.mvc;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.palermo.hql.bo.AutoCompleteItem;
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
	public String index() throws InterruptedException {
		naturalQueryService.reIndexFTSAllObjets();
		return "/hqlmain/index";
	}
	
	@RequestMapping(value="/chart", method=RequestMethod.GET)
	public String chart(@RequestParam("queryId") Long queryId, Model model) {
		model.addAttribute("queryId", queryId);
		return "/hqlmain/chart";
	}
	
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public String list(@RequestParam("queryId") Long queryId, Model model) {
		model.addAttribute("queryId", queryId);
		return "/hqlmain/list";
	}

	@ResponseBody
	@RequestMapping(value="/autocomplete_natural_query_commands", method=RequestMethod.GET)
	public List<AutoCompleteItem> autoCompleteNaturalQueryCommands(String term) throws Exception {
		log.info("Ejecutando autoCompleteNaturalQueryCommands para el termino: " + term);
		//Ver si filtramos comandos duplicados y de damos algo de logica al autocomplete para generar las consultas
		List<AutoCompleteItem> items = naturalQueryService.getAutoCompleteItems(term);
	
		return items;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/analize", method=RequestMethod.GET)
	public NplResponse analize(NplRequest nplRequest) throws Exception {
		log.info("Ejecutando analize");
		return nplService.analize(nplRequest);
	}
	
	@ResponseBody
	@RequestMapping(value="/get_query", method=RequestMethod.GET)
	public NplResponse getQuery(@RequestParam("queryId") Long queryId) throws Exception {
		log.info("Ejecutando getQuery para el queryId: " + queryId);
		return nplService.analizeBySavedQuery(queryId);
	}
	
}



