package edu.palermo.hql.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.palermo.hql.bo.DataEntity;
import edu.palermo.hql.bo.NplRequest;
import edu.palermo.hql.bo.NplResponse;
import edu.palermo.hql.bo.Query;
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
			throws HQLException {
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
	private NplResponse process(List<Word> analyzeWords, NplRequest nplRequest) throws HQLException {

		HashMap<String, String> values = new HashMap<String, String>();

		boolean isFromMobile = GeneralUtils.checkIsMobile(nplRequest.getUserAgent());

		String mascaraActual = "";
		String comandoActual = "";
		String entidadActual = "";
		// String filtroActual = "";
		String shortTag = "";
		String form = "";

		String sqlActual = "";

		String tag = "";
		String lemma = "";


		ArrayList<String> funciones = new ArrayList<String>();
		ArrayList<String> operadores = new ArrayList<String>();
		ArrayList<String> operadoresLogicos = new ArrayList<String>();
		
		ArrayList<String> valores = new ArrayList<String>();
		ArrayList<String> entidades = new ArrayList<String>();
		ArrayList<String> camposWhere = new ArrayList<String>();

		ArrayList<String> camposOrderBy = new ArrayList<String>();
		ArrayList<String> camposGroupBy = new ArrayList<String>();
		
		Integer dia = 0;
		Integer mes = 0;
		Integer anio = 0;
		Integer horas = 0;
		Integer minutos = 0;
		
		boolean requiereOrderBy = false;

		// -----------------------------------------------------------

		NplResponse nplResponse = new NplResponse();
		long id = 12345678;
		nplResponse.setId(id);
		nplResponse.setResponseType("text");
		nplResponse.setResponseData(values);

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		// ----------------------------------------------------------------------------------
		// Comienzo del FOR
		// ----------------------------------------------------------------------------------
		for (Word w : analyzeWords) {

			tag = w.getTag();
			form = w.getForm();
			lemma = w.getLemma();
			shortTag = w.getShortTag();

			mascaraActual += shortTag + " ";
			/* ****************************** Verbos ************************************ */
			if (shortTag.startsWith("VMN")) {
				/* ----------------------------------------------------------------*/		
				/*                           COMANDOS                              */
				/* ----------------------------------------------------------------*/
				if (form.equalsIgnoreCase("listar")) {
					comandoActual = "listar";
				} else if (form.equalsIgnoreCase("graficar")) {
					comandoActual = "graficar";
				} else if (form.equalsIgnoreCase("contar")) {
					comandoActual = "contar";
					funciones.add("COUNT");
				} else if (form.equalsIgnoreCase("sumar")) {
					//comandoActual = "sumar";
					funciones.add("SUM");
				}
			} else if(shortTag.startsWith("VMP")){
				/* ----------------------------------------------------------------*/		
				/*                        FLAG ORDER BY                            */
				/* ----------------------------------------------------------------*/
				if (lemma.equalsIgnoreCase("ordenar")){
					requiereOrderBy = true;
				}
			}
			/* ****************************** Nombres *********************************** */
			else if (shortTag.startsWith("N")) {
				if (entidades.isEmpty()) {
					/* ----------------------------------------------------------------*/		
					/*                          ENTIDADES                              */
					/* ----------------------------------------------------------------*/
					entidades.add(form);
					entidadActual = entidades.get(0).toString();
				} else if (!entidades.isEmpty() && camposWhere.isEmpty() && requiereOrderBy == false){
					/* ----------------------------------------------------------------*/		
					/*                        CAMPOS WHERE                             */
					/* ----------------------------------------------------------------*/
					camposWhere.add(form);	
				} else if (!entidades.isEmpty() && !camposWhere.isEmpty() && requiereOrderBy == false){
					/* ----------------------------------------------------------------*/		
					/*                           VALORES                               */
					/* ----------------------------------------------------------------*/
					if (shortTag.startsWith("NC") && form.equalsIgnoreCase("ayer")){
						// AYER
						DateTime dt = new DateTime(DateTime.now());
						dt = dt.plusDays(-1);
						log.info("Fecha JODA: " + dt.toString("yyyy-MM-dd"));
						valores.add(dt.toString("yyyy-MM-dd"));
					} else {
						valores.add(form);
					}
				} else if (requiereOrderBy == true){
					/* ----------------------------------------------------------------*/		
					/*                      CAMPOS ORDER BY                            */
					/* ----------------------------------------------------------------*/
					// Si se proceso previamente la palabra "ordenar" empiezo a guardar los campos en otro Array
					camposOrderBy.add(form);
					// -------------------------------------------------------------
				}
			}
			/* ***************************** Advervios ********************************** */
			else if(shortTag.startsWith("R")){
				if (form.equalsIgnoreCase("hoy")){
					// --------------------------------------------------
					// HOY
					// --------------------------------------------------
					DateTime dt = new DateTime(DateTime.now());
					log.info("Fecha JODA: " + dt.toString("yyyy-MM-dd"));
					valores.add(dt.toString("yyyy-MM-dd"));
				} else if (form.equalsIgnoreCase("ayer")){
					// --------------------------------------------------
					// AYER
					// --------------------------------------------------
					DateTime dt = new DateTime(DateTime.now());
					dt.plusDays(-1);
					log.info("Fecha JODA: " + dt.toString("yyyy-MM-dd"));
					valores.add(dt.toString("yyyy-MM-dd"));
					// --------------------------------------------------
				} else if (form.equalsIgnoreCase("mes")){
					// MES
				} else if (form.equalsIgnoreCase("año")){
					// AÑO
				}
			}
			/* *************************** Preposiciones ******************************** */
			else if (shortTag.startsWith("S")) {
				/* ----------------------------------------------------------------*/		
				/*                  OPERADORES DE COMPARACION                      */
				/* ----------------------------------------------------------------*/
				// TODO: Me falta chequear cuales estan funcionando actualmente
				if (form.equalsIgnoreCase("de")) {
					if (camposWhere.size() == 1) {
						operadores.add("=");
					} else if (form.equalsIgnoreCase("entre")) {
						operadores.add("BETWEEN");
					}
				}
				
				if (tag.startsWith("SPS00")) {
					if (form.equalsIgnoreCase("a")) {
					} else if (form.equalsIgnoreCase("con")) {
					} else if (form.equalsIgnoreCase("hasta")) {
						operadores.add("<=");
					} else if (form.equalsIgnoreCase("desde")) {
						operadores.add(">=");
					} else if (form.equalsIgnoreCase("entre")) {
						operadores.add("BETWEEN");
					}
				}
			}
			/* ***************************** Adjetivos ********************************** */
			else if (shortTag.startsWith("A")) {
				if (form.equalsIgnoreCase("igual")) { 
					operadores.add(" = ");
				} else if (form.equalsIgnoreCase("mayor")) {
					operadores.add(" > ");
				} else if (form.equalsIgnoreCase("menor")) {
					operadores.add(" < ");
				}
			}
			/* **************************** Pronombres ********************************** */
			else if (shortTag.startsWith("P")) {
				// Esta parte la controlo con las mascaras, por ahora no se usa
			}
			/* *************************** Conjunciones ********************************* */
			else if (shortTag.startsWith("C")) {
				if (shortTag.startsWith("CC")) {
					/* ----------------------------------------------------------------*/		
					/*                       OPERADORES LOGICOS                        */
					/* ----------------------------------------------------------------*/
					if (form.equalsIgnoreCase("y") || form.equalsIgnoreCase("e")){
						operadoresLogicos.add(" AND ");
					}
					else if (form.equalsIgnoreCase("o") || form.equalsIgnoreCase("u")){
						operadoresLogicos.add(" OR ");
					}
					log.info("operadoresLogicos: " + form);
				}
			}
			/* *********************** Signos de Puntuacion ***************************** */
			else if (shortTag.startsWith("F")){
				// Por ahora solo usamos una sola oracion
			}
			/* ************************ Cifras y Numerales ****************************** */
			else if (shortTag.startsWith("Z")) {
				/* ----------------------------------------------------------------*/		
				/*                       VALORES NUMERICOS                         */
				/* ----------------------------------------------------------------*/
				valores.add(lemma);
			}
			/* ****************************** Fechas ************************************ */
			else if (shortTag.startsWith("W")) {
				/* ----------------------------------------------------------------*/		
				/*                      VALORES TIPO FECHA                         */
				/* ----------------------------------------------------------------*/
				// El lemma viene con corchetes, los reemplazo para poder hacer un split
				String[] arrayFecha = lemma.replace("([|])", "").split(":");	
				// Recorro el array para encontrar la parte del lemma que tiene la fecha
				for (int i = 0; i < arrayFecha.length; i++) {
					if (i == 1) {
						// La posicion 1 tiene la fecha en formato dd/mm/yyyy
						// Si NO se informa el dia el lemma viene con formato ??/mm/yyyy 
						//para evitar que pinche hacemos un replace
						String s = arrayFecha[1].replace("??", "01").toString();
						String[] f = s.split("/");
						// Guardo valores para crear la fecha JODA
						dia = Integer.parseInt(f[0].toString());
						mes = Integer.parseInt(f[1].toString());
						anio = Integer.parseInt(f[2].toString());
						// Falta esta parte
						horas = 0;
						minutos = 0;
					}
				}
				// Fecha JODA
				DateTime dt = new DateTime(anio, mes, dia, horas, minutos);
				log.info("Fecha JODA: " + dt.toString("yyyy-MM-dd"));
				
				valores.add(dt.toString("yyyy-MM-dd"));
			}
			/* ************************************************************************** */
			else {
				log.warn("No se puede procesar la palabra: " + form + " short tag: " + shortTag);
			}
		}
		// ----------------------------------------------------------------------------------
		// Fin del FOR
		// ----------------------------------------------------------------------------------
		
		// ya tengo las palabras base ahora tengo que hacer la consulta......
		// ?????????????????????????????????????????????????????????????????
		if (comandoActual != "") {
			long nplRequestId = 0; 
			DataEntity dataEntity = naturalQueryService.findDataEntitieByAlias(entidadActual);
		
			if (dataEntity != null) {
				mascaraActual = mascaraActual.trim();
				log.info("Mascara: " + mascaraActual);
				
				/* **************************** Generar SQL ********************************* */
				Query query = new Query(comandoActual, mascaraActual, dataEntity.getColummns(), 
										dataEntity.getCountColumn(),  entidadActual, camposWhere, 
										operadores, valores, camposOrderBy, camposGroupBy, 
										operadoresLogicos, funciones);
				sqlActual = query.getSql();
				log.info("SQL Generado: " + sqlActual);
				/* ************************************************************************** */
				
				if (sqlActual != null) {
					/* ----------------------------------------------------------------*/		
					/*                            CONTAR                               */
					/* ----------------------------------------------------------------*/
					if (comandoActual.equalsIgnoreCase("contar")) {
						int countEntidad = jdbcTemplate.queryForInt(sqlActual);
						values.put("simpleText", "El resultado es " + countEntidad);
						nplResponse.setResponseData(values);
					/* ----------------------------------------------------------------*/		
					/*                            LISTAR                               */
					/* ----------------------------------------------------------------*/
					} else if (comandoActual.equalsIgnoreCase("listar")) {
						nplResponse.setResponseType("list");
						nplResponse.setResponseData(jdbcTemplate.queryForList(sqlActual));
					/* ----------------------------------------------------------------*/		
					/*                           GRAFICAR                              */
					/* ----------------------------------------------------------------*/
					} else if (comandoActual.equalsIgnoreCase("graficar")) {
						nplResponse.setResponseType("pie-chart");
						sqlActual = "select " + dataEntity.getGroupColumn()
								+ ", count(" + dataEntity.getGroupColumn()
								+ ") as value" + " from "
								+ dataEntity.getTables() + " group by "
								+ dataEntity.getGroupColumn();
						if (!isFromMobile) {
							nplResponse.setResponseType("pie-chart");
							nplResponse.setResponseData(jdbcTemplate.queryForList(sqlActual));
						} else {
							nplRequestId = nplRequestDAO.saveNplRequest(nplRequest);
							nplResponse.setResponseType("link");
							values.put("simpleText", "Click para ver la lista");
							values.put("url", "/chart.html?queryId=" + nplRequestDAO);
							nplResponse.setResponseData(jdbcTemplate.queryForList(sqlActual));
							// TODO: Me falta guardar la consulta en la base
							// para ejecutar despues.....
						}
					}
					/* ----------------------------------------------------------------*/
				} 
			}	
		} 
		// ?????????????????????????????????????????????????????????????????

		log.info("Resultado del analize " + nplResponse);
		return nplResponse;
	}

	@Override
	public NplResponse analizeBySavedQuery(Long queryId) throws HQLException {
		NplRequest nplRequest = nplRequestDAO.getNplRequestById(queryId);
		nplRequest.setUserAgent("saved-query");
		return this.analize(nplRequest);
	}
}
