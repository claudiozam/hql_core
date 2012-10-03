package edu.palermo.hql.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import edu.palermo.hql.bo.DataEntity;
import edu.palermo.hql.bo.NplRequest;
import edu.palermo.hql.bo.NplResponse;
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
	private NplResponse process(List<Word> analyzeWords, NplRequest nplRequest)
			throws Exception {

		HashMap<String, String> values = new HashMap<String, String>();

		boolean isFromMobile = GeneralUtils.checkIsMobile(nplRequest
				.getUserAgent());

		String mascaraActual = "";
		String comandoActual = "";
		String entidadActual = "";
		// String filtroActual = "";
		String shortTag = "";
		String form = "";

		String sqlActual = "";

		String tag = "";
		String lemma = "";

		ArrayList<String> fechas = new ArrayList<String>();
		ArrayList<String> nombres = new ArrayList<String>();
		ArrayList<String> operadores = new ArrayList<String>();
		ArrayList<String> camposOrderBy = new ArrayList<String>();
		ArrayList<Integer> valoresNumericos = new ArrayList<Integer>();

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
			/*
			 *  ****************************** Verbos
			 * ************************************
			 */
			if (shortTag.startsWith("VMN")) {
				if (form.equalsIgnoreCase("listar")) {
					comandoActual = "listar";
				} else if (form.equalsIgnoreCase("graficar")) {
					comandoActual = "graficar";
				} else if (form.equalsIgnoreCase("contar")) {
					comandoActual = "contar";
				}
			} else if (shortTag.startsWith("VMP")) {
				if (lemma.equalsIgnoreCase("ordenar")) {
					// Flag para avisar que los proximos "N" son campos del
					// ORDER BY
					requiereOrderBy = true;
				}
			} else if (shortTag.startsWith("VSI")) {
				if (form.equalsIgnoreCase("es")) {
				}
			}
			/*
			 *  ****************************** Nombres
			 * ***********************************
			 */
			else if (shortTag.startsWith("N")) {
				// Si es el primer "N" entonces lo guardo como nombre de la
				// entidad
				if (nombres.isEmpty()) {
					entidadActual = form;
				}
				// Pregunto si previamente se proceso la palabra "ordenar"
				// En caso de ser TRUE empiezo a guardar los campos del ORDER BY
				// en otro ArrayList
				if (requiereOrderBy == true) {
					camposOrderBy.add(form);
				} else {
					nombres.add(form);
				}
			}
			/*
			 *  *************************** Preposiciones
			 * ********************************
			 */
			else if (shortTag.startsWith("S")) {
				if (form.equalsIgnoreCase("de")) {
					if (nombres.size() == 2) {
						operadores.add(" = ");
					}
				}
				if (tag.startsWith("SPS00")) {
					if (form.equalsIgnoreCase("a")) {
					} else if (form.equalsIgnoreCase("con")) {
					} else if (form.equalsIgnoreCase("hasta")) {
						operadores.add(" <= ");
					} else if (form.equalsIgnoreCase("desde")) {
						operadores.add(" >= ");
					} else if (form.equalsIgnoreCase("entre")) {
					} else if (form.equalsIgnoreCase("en")) {
						// operadores.add(" IN ");
					} else if (form.equalsIgnoreCase("por")) {
					}
				}
			}
			/*
			 *  ***************************** Adjetivos
			 * **********************************
			 */
			else if (shortTag.startsWith("A")) {
				// Operadores de condicion
				if (form.equalsIgnoreCase("igual")) {
					operadores.add(" = ");
				} else if (form.equalsIgnoreCase("mayor")) {
					operadores.add(" > ");
				} else if (form.equalsIgnoreCase("menor")) {
					operadores.add(" < ");
				}
			}
			/*
			 *  **************************** Pronombres
			 * **********************************
			 */
			else if (shortTag.startsWith("P")) {
				if (shortTag.startsWith("PI")) {
				} else if (shortTag.startsWith("PT")) {
				} else if (shortTag.startsWith("PR")) {
					// Pronombres Relativos
					// Posibles tipos de filtro para where (ej cuales, cuantos,
					// quienes, donde)
					if (form.equalsIgnoreCase("donde")) {
						// El "donde" es equivalenle al "de" (nos avisa que la
						// consulta debe estar filtrada)
					}
				}
			}
			/*
			 *  *************************** Conjunciones
			 * *********************************
			 */
			else if (shortTag.startsWith("C")) {
				if (shortTag.startsWith("CS")) {
				} else if (shortTag.startsWith("CC")) {
					// Operadores logicos
					if (form.equalsIgnoreCase("y")) {
						operadores.add(" AND ");
					} else if (form.equalsIgnoreCase("e")) {
						operadores.add(" AND ");
					} else if (form.equalsIgnoreCase("o")) {
						operadores.add(" OR ");
					} else if (form.equalsIgnoreCase("u")) {
						operadores.add(" OR ");
					}
				}
			}
			/*
			 *  ************************ Cifras y Numerales
			 * ******************************
			 */
			else if (shortTag.startsWith("Z")) {
				valoresNumericos.add(Integer.parseInt(lemma));
			}
			/*
			 *  ****************************** Fechas
			 * ************************************
			 */
			else if (shortTag.startsWith("W")) {
				String[] arrayFecha = lemma.replace("([|])", "").split(":");
				// Tengo que mejorar esta parte
				// No me di cuenta y borre la parte donde estaba el ejemplo de
				// SimpleDateFormat
				for (int i = 0; i < arrayFecha.length; i++) {
					if (i == 1) {

						// La posicion 1 tiene la fecha en formato ??/mm/yyyy
						String s = arrayFecha[1].replace("??", "01").toString();

						log.info("Fecha: " + s);

						String[] f = s.split("/");
						if (f[1].length() == 1) {
							f[1] = '0' + f[1];
						}
						String fechaFormateada = f[2] + "-" + f[1] + "-" + f[0];
						fechas.add(fechaFormateada);
						log.info("Fecha: " + fechas.get(0));
					}
				}
			}
			/* ************************************************************************** */
			else {
				log.warn("No se puede procesar la palabra: " + form
						+ " short tag: " + shortTag);
			}
		}
		// ----------------------------------------------------------------------------------
		// Fin del FOR
		// ----------------------------------------------------------------------------------

		/*
		 * Tipos de oraciones validas
		 * 
		 * Lista alumnos Lista alumnos donde carrera es igual a medicina Listar
		 * alumnos ordenado por legajo Contar alumnos Listar productos donde el
		 * precio es mayor a 10000 Contar inscriptos desde enero del 2012 hasta
		 * abril del 2013
		 */

		// ya tengo las palabras base ahora tengo que hacer la consulta......
		// ?????????????????????????????????????????????????????????????????
		if (comandoActual != "") {
			long nplRequestId = 0; 
			DataEntity dataEntity = naturalQueryService
					.findDataEntitieByAlias(entidadActual);
			if (dataEntity != null) {
				mascaraActual = mascaraActual.trim();
				log.info("Mascara: " + mascaraActual);
				/*
				 *  **************************** GenerateSQL
				 * *********************************
				 */
				sqlActual = this.GenerateSQL(comandoActual, mascaraActual,
						dataEntity, nombres, operadores, valoresNumericos,
						camposOrderBy, fechas);
				/* ************************************************************************** */
				if (sqlActual != null) {
					if (comandoActual.equalsIgnoreCase("contar")) {
						log.info("SQL Generado: " + sqlActual);
						int countEntidad = jdbcTemplate.queryForInt(sqlActual);
						values.put("simpleText", "El resultado es "
								+ countEntidad);
						nplResponse.setResponseData(values);
					} else if (comandoActual.equalsIgnoreCase("listar")) {
						nplResponse.setResponseType("list");
						if (!isFromMobile) {
							nplResponse.setResponseData(jdbcTemplate
									.queryForList(sqlActual));
						} else {
							nplRequestId = nplRequestDAO.saveNplRequest(nplRequest);
							nplResponse.setResponseType("link");
							values.put("simpleText", "Click para ver la lista");
							values.put("url", "/list.html?queryId=" + nplRequestId);
							// TODO: Me falta guardar la consulta en la base
							// para ejecutar despues.....
						}

					} else if (comandoActual.equalsIgnoreCase("graficar")) {

						nplResponse.setResponseType("pie-chart");
						sqlActual = "select " + dataEntity.getGroupColumn()
								+ ", count(" + dataEntity.getGroupColumn()
								+ ") as value" + " from "
								+ dataEntity.getTables() + " group by "
								+ dataEntity.getGroupColumn();
						if (!isFromMobile) {
							nplResponse.setResponseType("pie-chart");
							nplResponse.setResponseData(jdbcTemplate
									.queryForList(sqlActual));
						} else {
							nplRequestId = nplRequestDAO.saveNplRequest(nplRequest);
							nplResponse.setResponseType("link");
							values.put("simpleText", "Click para ver el grafico");
							values.put("url", "/chart.html?queryId=" + nplRequestId);
							// TODO: Me falta guardar la consulta en la base
							// para ejecutar despues.....
						}

					}
				} else {
					values.put("simpleText", "La consulta no pudo ser ejecutada");

				}
			}
		} else {
			values.put("simpleText", "La consulta no pudo ser ejecutada");

		}

		log.info("Resultado del analize " + nplResponse);
		return nplResponse;
	}

	private String GenerateSQL(String command, String mask,
			DataEntity dataEntity, ArrayList<String> nombres,
			ArrayList<String> operadores, ArrayList<Integer> valoresNumericos,
			ArrayList<String> camposOrderBy, ArrayList<String> fechas) {

		String sql = "";

		int sizeFechas = fechas.size();
		int sizeNombres = nombres.size();
		int sizeOperadores = operadores.size();
		int sizeCamposOrderBy = camposOrderBy.size();
		int sizeValoresNumericos = valoresNumericos.size();

		/* *************************** SELECT **************************** */
		// Identifico el tipo de comando
		if (command.equalsIgnoreCase("listar")) {
			sql = "SELECT " + dataEntity.getColummns() + " ";
		} else if (command.equalsIgnoreCase("contar")) {
			sql = "SELECT COUNT(" + dataEntity.getCountColumn() + ") ";
		}
		/* **************************** FROM ***************************** */
		sql += " FROM " + dataEntity.getTables();
		/* **************************** WHERE **************************** */
		// Identifico el tipo de mascara
		if (mask.matches("VMN NC Fp")) {
			// listar alumnos
			// NO hace falta modificar la variable sql debido a que se guardaria
			// lo mismo que tiene cargado
			return sql;
		} else if (mask
				.matches("VMN NC (PR|S) NC VSI AQ SP (NC|NP|Z|W) (Fp|VMP SP NC Fp)")) {
			// listar Nombre[0] donde Nombre[1] es igual a Nombre[2]
			if (sizeNombres >= 2 && sizeOperadores >= 1) {
				sql += " where " + nombres.get(1).toString() + " "
						+ operadores.get(0).toString();
				if (sizeNombres >= 3) {
					// Campo VARCHAR
					sql += " '" + nombres.get(2).toString() + "'";
				} else if (sizeValoresNumericos == 1) {
					// Campo INTEGER
					sql += " " + valoresNumericos.get(0).toString();
				} else if (sizeFechas == 1) {
					// Campo DATE
					sql += " '" + fechas.get(0).toString() + "'";
				}
			}
		} else if (mask.matches("VMN NC SP DA NC SP (NP|NC) Fp")) {
			// listar Nombre[0] de la Nombre[1] de Nombre[2]
			if (sizeNombres >= 2 && sizeOperadores >= 1) {
				// WHERE [campo] [operador]
				sql += " where " + nombres.get(1).toString() + " "
						+ operadores.get(0).toString();
				if (sizeNombres >= 3) {
					// Campo VARCHAR
					sql += " '" + nombres.get(2).toString() + "'";
				} else if (sizeValoresNumericos == 1) {
					// Campo INTEGER
					sql += " " + valoresNumericos.get(0).toString();
				} else if (sizeFechas == 1) {
					// Campo DATE
					sql += " '" + fechas.get(0).toString() + "'";
				}
			}
		} else if (mask.matches("VMN NC VMP SP NC Fp")) {
			// Listar alumnos ordenado por legajo
			if (sizeCamposOrderBy >= 1 && sizeOperadores == 0) {
				sql += " order by ";
				for (int i = 0; i < camposOrderBy.size(); i++) {
					sql += camposOrderBy.get(i).toString();
				}
			}
		} else {
			sql = null;
		}
		/* *************************** GROUP BY *************************** */

		/* *************************** ORDER BY *************************** */
		if (sizeCamposOrderBy >= 1 && sizeOperadores >= 1 && sql != null) {
			sql += " order by ";
			for (int i = 0; i < camposOrderBy.size(); i++) {
				sql += camposOrderBy.get(i).toString();
			}
		}
		/* *************************************************************** */

		log.info("SQL Generado: " + sql);

		return sql;
	}

	@Override
	public NplResponse analizeBySavedQuery(Long queryId) throws Exception {
		NplRequest nplRequest = nplRequestDAO.getNplRequestById(queryId);
		nplRequest.setUserAgent("saved-query");
		return this.analize(nplRequest);
	}
}
