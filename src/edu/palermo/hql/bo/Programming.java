package edu.palermo.hql.bo;

import edu.palermo.hql.bo.Mask;
import edu.palermo.hql.service.NplServiceFreeLingImpl;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Programming {

	private static Logger log = Logger.getLogger(NplServiceFreeLingImpl.class);
	
	ArrayList<String> wordCollection = new ArrayList<String>();
	
	private HashMap<Integer, String> wordType = new HashMap<Integer, String>();
	private HashMap<Integer, String> nplAnalysis = new HashMap<Integer, String>();
	private HashMap<Integer, String> tagAnalysis = new HashMap<Integer, String>();
	private HashMap<Integer, String> tagCollection = new HashMap<Integer, String>();
	private HashMap<Integer, String> compareFunctions = new HashMap<Integer, String>();
	
	private HashMap<String, String> data = new HashMap<String, String>();
	
	private HashMap<Integer, String> analysisResults = new HashMap<Integer, String>();
	
	public Programming(ArrayList<String> wordCollection, 
						HashMap<Integer, String> wordType, 
						HashMap<Integer, String> nplAnalysis, 
						String mascara, 
						String lang, 
						DataEntity dataEntity){
		
		// Se busca una obtener una idea aproximada del comportamiento de cada palabra 
		// de acuerdo a los resultados del analisis de la mascara y del servicio
		
		Mask mascaraActual = new Mask(mascara);
		//Language idioma = new Language(lang);
		
		this.wordType = wordType;
		this.nplAnalysis = nplAnalysis;
		this.wordCollection = wordCollection;
		this.tagAnalysis = mascaraActual.getTagAnalysis();
		this.tagCollection = mascaraActual.getTagCollection();
		
		this.compareFunctions = this.compareFunctions();
		
		// *****************************************************************************
		// this.analize();
		// *****************************************************************************
		
		for (int i = 0; i < this.wordCollection.size(); i++){
			
			String palabra = this.wordCollection.get(i);
			String tipoPalabra = this.wordType.get(i);
			String funcion = this.compareFunctions.get(i);
			String etiqueta = this.tagCollection.get(i);
			
			String resultado = "";
			
			resultado += String.format("%1$-4s","| " + i) +
						String.format("%1$-6s","| " + etiqueta) +
						String.format("%1$-16s","| " + palabra) +
						String.format("%1$-20s","| " + tipoPalabra) + 
						String.format("%1$-26s","| {" + funcion + "} ") + "|"; 
			
			this.analysisResults.put(i, resultado);	
		}
		// *****************************************************************************
		// this.completeData(mascara, dataEntity);		
		// *****************************************************************************
		ArrayList<String> condiciones = new ArrayList<String>();
		String condicion = "";
		
		String campoWhere = "";
		String valorWhere = "";
		String operadorComparacion = "";
		ArrayList<String> camposOrderBy = new ArrayList<String>();
		ArrayList<String> camposGroupBy = new ArrayList<String>();
		ArrayList<String> operadoresLogicos = new ArrayList<String>(); 
	
		String campoWhereAnterior = "";
		//String palabraAnterior = "";
		
		this.data.put("mascara", mascara);
		
		for (int i = 0; i < this.compareFunctions.size(); i++){
			
			String funcion = this.compareFunctions.get(i); 
			String palabra = this.wordCollection.get(i);
			
			if (funcion.equalsIgnoreCase("comando")){
				this.data.put(funcion, palabra);

			} else if (funcion.equalsIgnoreCase("entidad")) {
				this.data.put(funcion, palabra);
				
				if (dataEntity != null) {

					log.info(this.data.get("comando").toString());
					if (this.data.get("comando").equalsIgnoreCase("graficar")){
						camposGroupBy.add(dataEntity.getGroupColumn());		
						this.data.put("camposSelect", dataEntity.getGroupColumn());
						this.data.put("funcAgregado", "COUNT");						
					} else {
						this.data.put("camposSelect", dataEntity.getColummns());
						log.info("camposSelect: " + dataEntity.getColummns());	
					}
				}
			} else if (funcion.equalsIgnoreCase("operadoresLogicos")) {
				operadoresLogicos.add(palabra);
			} else if (funcion.equalsIgnoreCase("campoWhere")) {
				campoWhere = "";
				campoWhere += palabra;
				condicion = "";
			} else if (funcion.equalsIgnoreCase("operadorComparacion")) {
				operadorComparacion = "";
				operadorComparacion += palabra;
			} else if (funcion.equalsIgnoreCase("valorWhere")) {
				if (!operadoresLogicos.isEmpty() && campoWhere.equalsIgnoreCase(campoWhereAnterior)){
					if (operadoresLogicos.contains("AND")){
						operadoresLogicos.remove(operadoresLogicos.lastIndexOf("AND"));
						operadoresLogicos.add("OR");
					}
				} 
				/*
				 * else if (palabraAnterior.equalsIgnoreCase(",") && campoWhere.equalsIgnoreCase(campoWhereAnterior)) {
					operadoresLogicos.add("OR");
				}
				*/
				valorWhere = "";
				valorWhere += palabra;
				// TEST
				if (operadorComparacion.equalsIgnoreCase(" BETWEEN ")){
					if (campoWhere.equalsIgnoreCase(campoWhereAnterior)){
						if (operadoresLogicos.contains("OR")){
							operadoresLogicos.remove(operadoresLogicos.lastIndexOf("OR"));
							operadoresLogicos.add("AND");
						}
						condicion = campoWhere + " <= " + valorWhere;
					} else {
						condicion = campoWhere + " >= " + valorWhere;
					}
				} else {
					condicion = campoWhere + operadorComparacion + valorWhere;
				}				
				//condicion = campoWhere + operadorComparacion + valorWhere;
				campoWhereAnterior = campoWhere;

				condiciones.add(condicion);
			}
			else if (funcion.equalsIgnoreCase("camposGroupBy")) {
				camposGroupBy.add(palabra);
			} 
			else if (funcion.equalsIgnoreCase("camposOrderBy")) {
				camposOrderBy.add(palabra);
			} else {
				// Nada...
			}
			
		}
		
		// ***************************************************************************
		
		String r = this.convertArrayListInString(operadoresLogicos, ",");
		this.data.put("operadoresLogicos", r);
		
		String s = this.convertArrayListInString(condiciones, ",");
		this.data.put("condiciones", s);
		
		String t = this.convertArrayListInString(camposOrderBy, ",");
		this.data.put("camposOrderBy", t);
		
		String u = this.convertArrayListInString(camposGroupBy, ",");
		this.data.put("camposGroupBy", u);
		
		// ***************************************************************************
	}
	
	private HashMap<Integer, String> compareFunctions(){
		
		HashMap<Integer, String> coincidencia = new HashMap<Integer, String>();
		
		// Validar que los 2 hashmap tengan el mismo tamaño
		if (this.nplAnalysis.size() == this.tagAnalysis.size()){
			for (int i = 0; i < nplAnalysis.size(); i++){
				coincidencia.put(i, "");
				// Validar que los 2 hashmap tengan valores para la misma Key (posicion en la oracion)
				if (this.nplAnalysis.containsKey(i) && this.tagAnalysis.containsKey(i)){
					
					if (!this.nplAnalysis.get(i).equalsIgnoreCase("") && !this.tagAnalysis.get(i).equalsIgnoreCase("")){
						String nplPossibleFunctions[] = this.nplAnalysis.get(i).split(",");
						String tagPossibleFunctions[] = this.tagAnalysis.get(i).split(",");
						
						// -----------------------------------------------------------------------------
						// TODO: Mejorar comparacion (buscar forma de hacer round robin)
						for (int j = 0; j < nplPossibleFunctions.length; j++){
							for (int k = 0; k < tagPossibleFunctions.length; k++){
								if (nplPossibleFunctions[j].equalsIgnoreCase(tagPossibleFunctions[k])){
									//Si coincide con alguna de la opciones del analisis de los tags entonces se considera como coincidencia
									
									String func = nplPossibleFunctions[j].trim();
									
									// TODO: falta agregar los casos donde haya mas de una coincidencia
									coincidencia.put(i, func);
								} else {
									//coincidencia.put(i, "");
								}
							}
						}
						// -----------------------------------------------------------------------------
					}
				} else {
					// alguno de los 2 hashmap no tiene una de las claves 
				}
			}
		} else {
			// los hashmap no tienen la misma 
		}

		return coincidencia;
	}

	/* ******************************************************************************************** */
	
	private String convertArrayListInString(ArrayList<String> array, String delimiter){
		String cadena = "";
		for (int i = 0; i < array.size(); i++){
			if (i == 0){
				cadena += array.get(i);
			} else {
				cadena += delimiter + array.get(i);	
			}
		}
		return cadena;
	}
	
	/* ******************************************************************************************** */
	
	public HashMap<Integer, String> getAnalysisResults(){
		return this.analysisResults;
	}
	
	public HashMap<String, String> getData(){
		return this.data;
	}
}
