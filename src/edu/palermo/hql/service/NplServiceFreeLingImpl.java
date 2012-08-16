package edu.palermo.hql.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import edu.palermo.hql.bo.DataEntity;
import edu.palermo.hql.bo.NplRequest;
import edu.palermo.hql.bo.NplResponse;
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
		return this.process(analyzeWords);
	}
	
	private NplResponse process(List<Word> analyzeWords) throws HQLException {

		String comandoActual = "";
		String entidadActual = "";
		String filtroActual = "";
		String shortTag = "";
		String form = "";
		String sqlActual = "";
		
		NplResponse nplResponse = new NplResponse();
		long id = 12345678;
		nplResponse.setId(id);
		nplResponse.setResponseType("text");
		nplResponse.addData("simpleText", "La consulta no pudo ser ejecutada");
		
		
		// connection = dataSource.getConnection();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		for (Word w : analyzeWords) {
			shortTag = w.getShortTag();
			form = w.getForm();
			if (shortTag.startsWith("VMN")) {
				if (form.equalsIgnoreCase("listar")) {
					comandoActual = "listar";
				} else if (form.equalsIgnoreCase("graficar")) {
					comandoActual = "graficar";
				} else if (form.equalsIgnoreCase("contar")) {
					comandoActual = "contar";
				}
			} else if (shortTag.startsWith("NC")) {
				entidadActual = form;
			} else if (shortTag.startsWith("W")) {

			} else {
				log.warn("No se puede procesar la palabra: " + form
						+ " short tag: " + shortTag);
			}
		}

		// ya tengo las palabras base ahora tengo que hacer la consulta......
		// ?????????????????????????????????????????????????????????????????
		if (comandoActual.equalsIgnoreCase("contar")) {
			DataEntity dataEntity = naturalQueryService
					.findDataEntitieByAlias(entidadActual);
			if (dataEntity != null) {
				sqlActual = "select count(*) from " + dataEntity.getTables();
				int countEntidad = jdbcTemplate.queryForInt(sqlActual);
				nplResponse.addData("simpleText", "El resultado es " + countEntidad);
			}

		} else if (comandoActual.equalsIgnoreCase("listar")) {
			DataEntity dataEntity = naturalQueryService
					.findDataEntitieByAlias(entidadActual);
			if (dataEntity != null) {
				sqlActual = "select count(*) from " + dataEntity.getTables();
				//int countEntidad = jdbcTemplate.queryForInt(sqlActual);
				nplResponse.addData("simpleText", "El resultado es ");
			}

		} else if (comandoActual.equalsIgnoreCase("graficar")) {
			DataEntity dataEntity = naturalQueryService
					.findDataEntitieByAlias(entidadActual);
			if (dataEntity != null) {
				sqlActual = "select count(*) from " + dataEntity.getTables();
				//int countEntidad = jdbcTemplate.queryForInt(sqlActual);
				nplResponse.addData("simpleText", "El resultado es ");
			}

		}

		
		log.info("Resultado del analize " + nplResponse);
		return nplResponse;
	}

}
