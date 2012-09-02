package edu.palermo.hql.service;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import edu.palermo.hql.bo.DataEntity;
import edu.palermo.hql.bo.NplRequest;
import edu.palermo.hql.bo.NplResponse;
import edu.palermo.hql.general.GeneralUtils;
import edu.upc.freeling.*;

@Component
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
	
	public NplServiceFreeLingImpl() {
		init();
	}
	
	private void init() {
		log.info("Cargando freeling.....");
		//System.loadLibrary("libfreeling_javaAPI");
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
	public synchronized NplResponse analize(NplRequest nplRequest) throws HQLException {
		log.info("Ejecutando analize " + nplRequest);
		
		ListWord l = tk.tokenize(nplRequest.getText());
		ListSentence ls = sp.split(l, false);
		mf.analyze(ls);
		tg.analyze(ls);
		neclass.analyze(ls);
		
		List<Word> analyzeWords = new ArrayList<Word>();
		
		for (int i = 0; i < ls.size(); i++) {
			//Por el momento solo trabajamos con una sola sentence....
			Sentence s = ls.get(i);
			for (int j = 0; j < s.size(); j++) {
				Word w = s.get(j);
				log.info("Word: " + w.getForm() + " Lc: " + w.getLcForm() + " Lemma: " + w.getLemma() +  " Tag: " + w.getTag() + " ShortTag: " + w.getShortTag());
				analyzeWords.add(w);
			}
		}
		return this.process(analyzeWords, nplRequest);
	}
	
	private NplResponse process(List<Word> analyzeWords, NplRequest nplRequest) throws HQLException {

		HashMap<String , String> values = new HashMap<String, String>();

		boolean isFromMobile = GeneralUtils.checkIsMobile(nplRequest.getUserAgent());
	
		String mascaraActual = "";
		String comandoActual = "";
		String entidadActual = "";
		String filtroActual = "";
		String shortTag = "";
		String form = "";
		
		String sqlActual = "";
		
		String tag = "";
		String lemma = "";
		ArrayList<String> oracion = new ArrayList<String>();
		
		/*
		Array con palabras de tipo nombres 
		
		Ej. "contar [alumnos] de la [carrera] de [informatica]."
		
		nombres.get(0) = entidad (alumnos)
		nombres.get(1) = campo where  (carrera)
		nombres.get(2) = valor buscado (informatica)
		*/ 
		ArrayList<String> nombres = new ArrayList<String>();

		
		NplResponse nplResponse = new NplResponse();
		long id = 12345678;
		nplResponse.setId(id);
		nplResponse.setResponseType("text");
		values.put("simpleText", "La consulta no pudo ser ejecutada");
		nplResponse.setResponseData(values);
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
				
		/* 
			String desc = (String) jdbcTemplate
				    .queryForObject("select p.desc from position1 p where p.value = ?", 
				    		new Object[]{tag.substring(0, 1)}, String.class);
		*/
		
		for (Word w : analyzeWords) {
			shortTag = w.getShortTag();
			form = w.getForm();
			
			tag = w.getTag();
			lemma = w.getLemma();
			
			mascaraActual += shortTag + " ";
			/* ****************************** Verbos ************************************ */
			
			if (shortTag.startsWith("VMN")) {
				if (form.equalsIgnoreCase("listar")) {
					comandoActual = "listar";
				} else if (form.equalsIgnoreCase("graficar")) {
					comandoActual = "graficar";
				} else if (form.equalsIgnoreCase("contar")) {
					comandoActual = "contar";
				}				
			}
			
			/* ****************************** Preposiciones ************************************ */
			
			else if (shortTag.startsWith("S")) {
				if (tag.startsWith("SPS00")){
					// "de" = separador de nombres
					
				}
			}
			
			/* ****************************** Nombres ************************************ */
			
			else if (shortTag.startsWith("N")) {

				if (shortTag.startsWith("NC")){
					// Nombres Comunes
					// Posibles entidades!!!

					// Si el Array esta vacio entonces es el primer "NC" y por lo tanto es la entidad
					// Tambien deberiamos considerar que no haya un "de" delante (Ej. alumnos [de] la facultad...)
					if (nombres.isEmpty()){
						// Guardo el nombre de la entidad
						entidadActual = form;
					}

				} else if (shortTag.startsWith("NP")){
					// Nombres Propios
					// Posibles filtros del WHERE
					
					// NP000G0    Lugar (ej. Barcelona) 
					// NP000P0    Nombre propio (ej. Pedro)
					// NP000O0    organizacion (ej. UNICEF)
				}
				
				// Agrego todas las palabras de tipo N al Array de nombres 
				nombres.add(form);
			}
			
			/* ****************************** Pronombres ************************************ */
			
			else if (shortTag.startsWith("P")){

				if (shortTag.startsWith("PI")){
					// Pronombres Indefinidos					
				}
				else if (shortTag.startsWith("PT")){
					// Pronombres Interrogativos
				}
				else if (shortTag.startsWith("PR")){
					// Pronombres Relativos
					// Posibles tipos de filtro para where (ej cuales, cuantos, quienes, donde)
					
				}
				
			}
			 
			/* ****************************** Conjunciones ************************************ */
			
			else if (shortTag.startsWith("C")) {

				if (shortTag.startsWith("CS")) {
					
				}
				else if (shortTag.startsWith("CC")){
					if (form.equalsIgnoreCase("y")){
						// Agregar un AND a la consulta
					}
					else if (form.equalsIgnoreCase("o")){
						// Agregar un OR a la consulta
					}
				}
			}
			

			/* ****************************** Cifras y numerales ************************************ */
			
			else if (shortTag.startsWith("Z")){

			}
			
			/* ****************************** Fechas ************************************ */
			
			else if (shortTag.startsWith("W")) {
				// [V:26:09:1992:03.00:pm]
				String regex = "[\\[LMJVSD]:\\d{2}:\\d{2}:\\d{4}:\\d{2}.\\d{2}:[a-pm]$";
				//ArrayList<String> fecha = new ArrayList<String>();
				String fecha[] = null; 
				if (lemma.matches(regex)){
					fecha = lemma.split(regex);
				}
				
				// Create a pattern to match breaks
				//Pattern p = Pattern.compile("[,\\s]+");
				// Split input with the pattern
				//String[] result = p.split("one,two, three   four ,  five");
				
				/*SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
				try {
					Date fechaFinal = formatoDeFecha.parse("02/12/2012");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				values.put("simpleText", "El resultado es " + fecha);
				nplResponse.setResponseData(values);

				
				
			} else {
				log.warn("No se puede procesar la palabra: " + form
						+ " short tag: " + shortTag);
			}
		}
		// Fin del FOR
		//------------------------------------------------------------

		//Ejemplo que como usar REGEX
		mascaraActual = mascaraActual.trim();
		log.info("Mascara: " + mascaraActual);
		if(mascaraActual.matches("(VMN|XXX|ETCETCETC) NC SP W Fp")) {
			log.info("MATCH de la regex");
		}
		
		
		
		// Preguntar si la oracion es valida
		if (oracion.size() > 0){
		

			/* Premisas
			
			*La oracion tiene que tener un verbo "V"
			*La oracion tiene que tener al menos una palabra de tipo nombre "N"
			*Si la oracion tiene un "CC" igual a la conjuncion "y" entonces hay que agregar a la consulta un AND
			*Si la oracion tiene un "CC" igual a la conjuncion "o" entonces hay que agregar a la consulta un OR
			*La preposicion "SPS00" ("de") es nuestro separador de nombres "N" 
			
			 for (String palabra : oracion) {
				
			}
			*/
			 
			
		}
		
		// ya tengo las palabras base ahora tengo que hacer la consulta......
		// ?????????????????????????????????????????????????????????????????
				
		if (comandoActual.equalsIgnoreCase("contar")) {
			DataEntity dataEntity = naturalQueryService
					.findDataEntitieByAlias(entidadActual);
			if (dataEntity != null) {
				int sizeArray = nombres.size();
				
				sqlActual = "select count(" + dataEntity.getCountColumn() + ") from " + dataEntity.getTables();
				if (sizeArray >= 3 ){
					sqlActual += " where " + nombres.get(1).toString() + " = '" + nombres.get(2).toString() + "'";
				} 
				log.info("SQL Generado: " + sqlActual);
				int countEntidad = jdbcTemplate.queryForInt(sqlActual);
				
				values.put("simpleText", "El resultado es " + countEntidad);
				nplResponse.setResponseData(values);

			}
		} else if (comandoActual.equalsIgnoreCase("listar")) {
			DataEntity dataEntity = naturalQueryService.findDataEntitieByAlias(entidadActual);
			if (dataEntity != null) {
				sqlActual = "select " + dataEntity.getColummns()  + " from " + dataEntity.getTables();
				if(!isFromMobile) {
					nplResponse.setResponseType("list");
					nplResponse.setResponseData(jdbcTemplate.queryForList(sqlActual));
				} else {
					nplResponse.setResponseType("link");
					values.put("simpleText","Click para ver el Grafico");
					values.put("url", "/list.html?queryId=" + 34355);
					//TODO: Me falta guardar la consulta en la base para ejecutar despues.....
				}
				
				//ResultSet resultSet = statement.executeQuery("SELECT * FROM NaturalQueryCommand");
				//nplResponse.setResponseData(GeneralUtils.resultSetToObjectList(resultSet));
				
				
			}

		} else if (comandoActual.equalsIgnoreCase("graficar")) {
			
			DataEntity dataEntity = naturalQueryService.findDataEntitieByAlias(entidadActual);
			if (dataEntity != null) {
				sqlActual = "select " + dataEntity.getGroupColumn() + ", count(" + dataEntity.getGroupColumn() +") as value"  + " from " + dataEntity.getTables() + " group by " + dataEntity.getGroupColumn();
				if(!isFromMobile) {
					nplResponse.setResponseType("pie-chart");
					nplResponse.setResponseData(jdbcTemplate.queryForList(sqlActual));
				} else {
					nplResponse.setResponseType("link");
					values.put("simpleText","Click para ver la lista");
					values.put("url", "/chart.html?queryId=" + 34355);
					nplResponse.setResponseData(jdbcTemplate.queryForList(sqlActual));
					//TODO: Me falta guardar la consulta en la base para ejecutar despues.....
				}
				
			}
			
		}
		 		
		log.info("Resultado del analize " + nplResponse);
		return nplResponse;
	}

}
