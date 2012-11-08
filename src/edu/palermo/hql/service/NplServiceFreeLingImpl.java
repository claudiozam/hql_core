package edu.palermo.hql.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.palermo.hql.bo.Programming;
import edu.palermo.hql.bo.DataEntity;
import edu.palermo.hql.bo.Language;
import edu.palermo.hql.bo.NaturalQueryCommand;
import edu.palermo.hql.bo.NplRequest;
import edu.palermo.hql.bo.NplResponse;
import edu.palermo.hql.bo.Query;
import edu.palermo.hql.dao.DataEntityDAO;
import edu.palermo.hql.dao.NaturalQueryCommandDAO;
import edu.palermo.hql.dao.NplRequestDAO;
import edu.palermo.hql.general.GeneralUtils;
import edu.upc.freeling.ChartParser;
import edu.upc.freeling.DepTxala;
import edu.upc.freeling.HmmTagger;
import edu.upc.freeling.ListSentence;
import edu.upc.freeling.ListWord;
import edu.upc.freeling.Maco;
import edu.upc.freeling.MacoOptions;
import edu.upc.freeling.Nec;
import edu.upc.freeling.Sentence;
import edu.upc.freeling.Splitter;
import edu.upc.freeling.Tokenizer;
import edu.upc.freeling.UkbWrap;
import edu.upc.freeling.Util;
import edu.upc.freeling.Word;

@Service
public class NplServiceFreeLingImpl implements NplService {

	private static Logger log = Logger.getLogger(NplServiceFreeLingImpl.class);

	private static final String FREELINGDIR = "/usr/local";
	private static final String DATA = FREELINGDIR + "/share/freeling/";
	private static final String LANG = "es";

	private Tokenizer tk;
	private Splitter sp;
	private Maco mf;
	private HmmTagger tg;
	private Nec neclass;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private NaturalQueryService naturalQueryService;

	@Autowired
	private NplRequestDAO nplRequestDAO;

	@Autowired
	private DataEntityDAO dataEntityDAO;
	
	@Autowired
	private NaturalQueryCommandDAO naturalQueryCommandDAO;
	
	
	public NplServiceFreeLingImpl() {
		init();
	}

	private void init() {
		log.info("Cargando freeling.....");
		
		// System.loadLibrary("libfreeling_javaAPI");
		System.load("/home/hql/myfreeling/APIs/java/libfreeling_javaAPI.so");

		Util.initLocale("default");

		MacoOptions op = new MacoOptions(LANG);

		op.setActiveModules(false, true, true, true, true, true, true, true,
				true, true, false);

		op.setDataFiles("", DATA + LANG + "/locucions.dat", DATA + LANG
				+ "/quantities.dat", DATA + LANG + "/afixos.dat", DATA + LANG
				+ "/probabilitats.dat", DATA + LANG + "/dicc.src", DATA + LANG
				+ "/np.dat", DATA + "common/punct.dat", DATA + LANG
				+ "/corrector/corrector.dat");

		tk = new Tokenizer(DATA + LANG + "/tokenizer.dat");
		sp = new Splitter(DATA + LANG + "/splitter.dat");

		mf = new Maco(op);

		tg = new HmmTagger(LANG, DATA + LANG + "/tagger.dat", true, 2);
		ChartParser parser = new ChartParser(DATA + LANG
				+ "/chunker/grammar-chunk.dat");

		DepTxala dep = new DepTxala(DATA + LANG + "/dep/dependences.dat",
				parser.getStartSymbol());

		neclass = new Nec(DATA + LANG + "/nec/nec-ab.dat");

		UkbWrap dis = new UkbWrap(DATA + LANG + "/ukb.dat");

		log.info("Freeling cargado");

	}

	@Override
	public synchronized NplResponse analize(NplRequest nplRequest)
			throws Exception {
		log.info("Ejecutando analize " + nplRequest);

		ListWord l = tk.tokenize(nplRequest.getText());
		ListSentence ls = sp.split(l, false);
		mf.analyze(ls);
		tg.analyze(ls);
		neclass.analyze(ls);

		List<Word> analyzeWords = new ArrayList<Word>();

		for (int i = 0; i < ls.size(); i++) {
			// Por el momento solo trabajamos con una sola sentence....
			Sentence s = ls.get(i);
			for (int j = 0; j < s.size(); j++) {
				Word w = s.get(j);
				log.info("Word: " + w.getForm() + " Lc: " + w.getLcForm()
						+ " Lemma: " + w.getLemma() + " Tag: " + w.getTag()
						+ " ShortTag: " + w.getShortTag());
				analyzeWords.add(w);
			}
		}
		return this.process(analyzeWords, nplRequest);
	}

	@Transactional
	private NplResponse process(List<Word> analyzeWords, NplRequest nplRequest) throws Exception {

		HashMap<String, String> values = new HashMap<String, String>();

		boolean isFromMobile = GeneralUtils.checkIsMobile(nplRequest.getUserAgent());

		String tag = "";
		String form = "";
		String lemma = "";
		String shortTag = "";
		
		String sqlActual = "";
		String mascaraActual = "";
		int indicePalabraActual = 0;
		
		ArrayList<String> wordCollection = new ArrayList<String>();
		HashMap<Integer, String> wordType = new HashMap<Integer, String>();
		HashMap<Integer, String> nplAnalysis = new HashMap<Integer, String>();

		boolean tieneOrdenamiento = false;
		// -------------------------------------------------------------------------------
		// Para validar si una palabra es parecida a un campoWhere
		HashMap<String, String> possibleDataEntityColumns = new HashMap<String, String>();
		ArrayList<String> possibleEntities = new ArrayList<String>();
		// -------------------------------------------------------------------------------
		
		// -----------------------------------------------------------
		NplResponse nplResponse = new NplResponse();
		long id = 12345678;
		nplResponse.setId(id);
		nplResponse.setResponseType("text");
		nplResponse.setResponseData(values);
		// -----------------------------------------------------------

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		
		for (Word w : analyzeWords) {
			
			tag = w.getTag();
			form = w.getForm();
			lemma = w.getLemma();
			shortTag = w.getShortTag();

			mascaraActual += shortTag + " ";
			log.info("shortTag => " + shortTag);
			
			String funcion = "";
			String palabra = form;
			
			int indicePalabraAnterior = indicePalabraActual - 1;
			
			/* *************************************************************************** */
			Language idioma = new Language(LANG);
			
			// Obtener Tipo de Palabra
			wordType.put(indicePalabraActual, idioma.getWordType(shortTag));
			
			if (wordType.get(indicePalabraActual).equalsIgnoreCase("Verbo")){
				String preForm = form;
				NaturalQueryCommand naturalQueryCommand = naturalQueryCommandDAO.getNaturalQueryCommandByName(preForm);
				if(naturalQueryCommand!= null) {
					preForm = naturalQueryCommand.getName();
					log.info("FTS => Cambiando " + form + " por " + preForm);
					if (idioma.isAValidCommand(preForm)){
						funcion = "comando";
						palabra = preForm;
					} 
				}
				// Ordenamiento
				log.info("Paso 1");
				if (lemma.equalsIgnoreCase("ordenar")) {
					log.info("Paso 2");
					tieneOrdenamiento = true;
				}
			/* *************************************************************************** */
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("Nombre")){
				DataEntity dataEntity = dataEntityDAO.findDataEntitieByAlias(form); //Buscamos Fuzzy para ver si encuentra algo igual....
				if (dataEntity!=null) {
					// Es una posible Entidad
					log.info("FTS => Cambiando " + form + " por " + dataEntity.getAlias());
					
					funcion = "entidad";
					palabra = dataEntity.getAlias();
					
					possibleDataEntityColumns.put(dataEntity.getAlias(), dataEntity.getColummns());
					possibleEntities.add(dataEntity.getAlias());
					
					
					
				} else {
					log.info("Paso 3");
					if (!tieneOrdenamiento){
						//String funcion = "";
						boolean esCampoWhere = false;
						for (int i = 0; i < possibleEntities.size(); i++){
							String[] s = possibleDataEntityColumns.get(possibleEntities.get(i)).split(",");
							
							for (int j = 0; j < s.length; j++){
								String a = s[j].trim();
								String b = form.trim();
								
								if (b.equalsIgnoreCase(a)){
									
									if (funcion.equalsIgnoreCase("")){
										funcion += "campoWhere";
										esCampoWhere = true;
									} else {
										funcion += ",campoWhere";	
									}
								}		
							} // fin del for
						} // fin del for
					
						if (!nplAnalysis.containsKey(indicePalabraActual) && !esCampoWhere){
							if (funcion.equalsIgnoreCase("")){
								funcion += "valorWhere";
							} else {
								funcion += ",valorWhere";	
							}
						} 
					}
					else if (tieneOrdenamiento) {
						// Ordenamiento
						log.info("Paso 4: "  + funcion);
						
						if (funcion.equalsIgnoreCase("")){
							funcion += "camposOrderBy";
						} else {
							funcion += ",camposOrderBy";	
						}
						
					} // fin del if
					
					log.info("Paso 5: " + funcion);
				}
			/* *************************************************************************** */				
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("Adverbio")){
				palabra = idioma.convertDate(form, "R");
			/* *************************************************************************** */
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("Preposicion")){
				if (form.equalsIgnoreCase("de")) {
					if (wordType.get(indicePalabraAnterior).equalsIgnoreCase("Nombre")){
						if (nplAnalysis.containsKey(indicePalabraAnterior)){
							if (nplAnalysis.get(indicePalabraAnterior).equalsIgnoreCase("campoWhere")){
								funcion = "operadorComparacion";
								palabra = " = ";
							} 
						}
					}
				}
			/* *************************************************************************** */	
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("Adjetivo")){
				palabra = idioma.convertAdjective(form);
				int palabraAnterior = indicePalabraActual - 1;
				if (!palabra.equalsIgnoreCase("")){
					funcion = "operadorComparacion";	
				} else {
					if (shortTag.equalsIgnoreCase("AQ")){
						if (wordCollection.get(palabraAnterior).equalsIgnoreCase("de")){
							funcion = "valorWhere";		
						}
					}
				}
			/* *************************************************************************** */				
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("Determinante")){
				if (wordCollection.contains(indicePalabraAnterior)){
					if (wordCollection.get(indicePalabraAnterior).equalsIgnoreCase("de") && form.equalsIgnoreCase("la")){
					}
				}
			/* *************************************************************************** */
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("Pronombre")){
				
			/* *************************************************************************** */	
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("Conjuncion")){
				if (idioma.getLogicalOperation(form).equalsIgnoreCase("conjuncion")){
					funcion = "operadoresLogicos";
					palabra = "AND";
					
				} else if (idioma.getLogicalOperation(form).equalsIgnoreCase("disyuncion")){
					funcion = "operadoresLogicos";
					palabra = "OR";
				}
			/* *************************************************************************** */
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("SignoDePuntuacion")){
			
			/* *************************************************************************** */
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("Numero")){
				funcion = "valorWhere";
				
			/* *************************************************************************** */
			} else if (wordType.get(indicePalabraActual).equalsIgnoreCase("Fecha")){
				// Una fecha (con formato de fecha) es si o si un valor para el WHERE
				funcion = "valorWhere";
				palabra = idioma.convertDate(lemma, "W");
				//palabra = lemma;
			/* *************************************************************************** */
			} else{

			}
			
			wordCollection.add(palabra);
			nplAnalysis.put(indicePalabraActual, funcion);
			
			indicePalabraActual = indicePalabraActual + 1;
			
		} // Fin del FOR
		
		// ya tengo las palabras base ahora tengo que hacer la consulta......
		// ?????????????????????????????????????????????????????????????????

		DataEntity dataEntity = naturalQueryService.findDataEntitieByAlias(wordCollection.get(1).toString());
		
		Programming prog = new Programming(wordCollection, wordType, nplAnalysis, mascaraActual, LANG, dataEntity);

		log.info("------------------------------------------------------------------------");
		String resultado = String.format("%1$-4s","|ID") +
							String.format("%1$-6s","| Tag") +
							String.format("%1$-16s","| Word") +
							String.format("%1$-20s","| Type: ") +
							String.format("%1$-26s","| Function ") + "|";
		
		log.info(resultado);
		log.info("------------------------------------------------------------------------");
		for (int i = 0; i < prog.getAnalysisResults().size(); i++){
			log.info(prog.getAnalysisResults().get(i));
		}
		log.info("------------------------------------------------------------------------");
		
		// **************************************************************************
		
		if (prog.getData().containsKey("comando")) {
			
			long nplRequestId = 0;
			//DataEntity dataEntity = naturalQueryService.findDataEntitieByAlias(bp.getData().get("entidad"));
			//if (dataEntity != null) {
			if (!prog.getData().isEmpty()) {
				
				// *******************************************************
				// TODO: FEISIMO!!!!
				/*
				if (bp.getData().get("comando").equalsIgnoreCase("graficar")) {
					bp.getData().put("camposGroupBy", dataEntity.getGroupColumn());
					bp.getData().put("campoSelect", dataEntity.getCountColumn());
					bp.getData().put("funcAgregado", "COUNT");
				} else {
					bp.getData().put("camposSelect", dataEntity.getColummns());
				}
				*/
				//bp.getData().put("campoCount", dataEntity.getCountColumn());
				// *******************************************************
				
				log.info("------------------------------------------------------------------------");
				log.info("Mascara         : " + prog.getData().get("mascara"));
				log.info("Comando         : " + prog.getData().get("comando")); 
				log.info("Entidad         : " + prog.getData().get("entidad"));
				log.info("Campos SELECT   : " + prog.getData().get("camposSelect"));
				log.info("Condiciones     : " + prog.getData().get("condiciones"));
				log.info("Op. Logicos     : " + prog.getData().get("operadoresLogicos"));
				log.info("Campos GROUP BY : " + prog.getData().get("camposGroupBy"));
				log.info("Campos ORDER BY : " + prog.getData().get("camposOrderBy"));
				log.info("Func. Agregado  : " + prog.getData().get("funcAgregado"));
				log.info("------------------------------------------------------------------------");
				
				/* **************************** Generar SQL ********************************* */
				Query query = new Query(prog.getData());
				/* ************************************************************************** */
				
				sqlActual = query.getSql();
				log.info("SQL Generado: " + sqlActual);
				
				if (sqlActual != null && !sqlActual.equalsIgnoreCase("")) {
					/* ----------------------------------------------------------------*/		
					/*                            CONTAR                               */
					/* ----------------------------------------------------------------*/
					if (prog.getData().get("comando").equalsIgnoreCase("contar")) {
						int countEntidad = jdbcTemplate.queryForInt(sqlActual);
						values.put("simpleText", "El resultado es " + countEntidad);
						nplResponse.setResponseData(values);
					/* ----------------------------------------------------------------*/		
					/*                            LISTAR                               */
					/* ----------------------------------------------------------------*/
					} else if (prog.getData().get("comando").equalsIgnoreCase("listar")) {
						nplResponse.setResponseType("list");
						if (!isFromMobile) {
							nplResponse.setResponseData(jdbcTemplate.queryForList(sqlActual));
						} else {
							nplRequestId = nplRequestDAO.saveNplRequest(nplRequest);
							nplResponse.setResponseType("link");
							values.put("simpleText", "Click para ver el listado");
							values.put("url", "/list.html?queryId=" + nplRequestId);
						}
						
					/* ----------------------------------------------------------------*/		
					/*                           GRAFICAR                              */
					/* ----------------------------------------------------------------*/
					} else if (prog.getData().get("comando").equalsIgnoreCase("graficar")) {
						nplResponse.setResponseType("pie-chart");
						
						if (!isFromMobile) {
							nplResponse.setResponseData(jdbcTemplate.queryForList(sqlActual));
						} else {
							nplRequestId = nplRequestDAO.saveNplRequest(nplRequest);
							nplResponse.setResponseType("link");
							values.put("simpleText", "Click para ver el grafico");
							values.put("url", "/chart.html?queryId=" + nplRequestId);

							// TODO: Me falta guardar la consulta en la base
							// para ejecutar despues.....
						}
					}
					/* ----------------------------------------------------------------*/
				} else{
					log.info("No se pudo generar la consulta SQL.");			
				} 
			}	
		} 
		// ?????????????????????????????????????????????????????????????????

		log.info("Resultado del analize " + nplResponse);
		return nplResponse;
	}

	@Override
	public NplResponse analizeBySavedQuery(Long queryId) throws Exception {
		NplRequest nplRequest = nplRequestDAO.getNplRequestById(queryId);
		nplRequest.setUserAgent("saved-query");
		return this.analize(nplRequest);
	}
		
}
