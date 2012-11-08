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
	
	public Programming(ArrayList<String> wordCollection, HashMap<Integer, String> wordType, 
							HashMap<Integer, String> nplAnalysis, String mascara, String lang, DataEntity dataEntity){
		
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
		
		this.analize();
		this.completeData(mascara, dataEntity);
	}


	private void analize(){
		
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
	}
	
	private void completeData(String mascara, DataEntity dataEntity){
		
		ArrayList<String> condiciones = new ArrayList<String>();
		String condicion = "";
		
		String campoWhere = "";
		String valorWhere = "";
		String operadorComparacion = "";
		ArrayList<String> operadoresLogicos = new ArrayList<String>(); 
		//String operadorLogico = "";
		
		String campoWhereAnterior = "";
		//boolean esOtraCondicion = false;
		
		this.data.put("mascara", mascara);
		
		for (int i = 0; i < this.compareFunctions.size(); i++){
			
			String funcion = this.compareFunctions.get(i); 
			String palabra = this.wordCollection.get(i);
			
			if (funcion.equalsIgnoreCase("comando")){
				this.data.put(funcion, palabra);
				if (palabra.equalsIgnoreCase("graficar")){
					this.data.put("funcAgregado", "COUNT");
				}
			} else if (funcion.equalsIgnoreCase("entidad")) {
				this.data.put(funcion, palabra);
				
				if (dataEntity != null) {
				
					if (this.data.get("comando").equalsIgnoreCase("graficar")){
						
						this.data.put("camposGroupBy", dataEntity.getGroupColumn());
						this.data.put("camposSelect", dataEntity.getGroupColumn());
						this.data.put("funcAgregado", "COUNT");
						
						log.info("===============================================");
						log.info("camposGroupBy: " + dataEntity.getGroupColumn());
						log.info("camposSelect: " + dataEntity.getGroupColumn());
						log.info("funcAgregado: " + "COUNT");	
						
					} else {

						this.data.put("camposSelect", dataEntity.getColummns());
						log.info("camposSelect: " + dataEntity.getColummns());	
					}
					
					
				}
				/*
			} else if (funcion.equalsIgnoreCase("camposSelect")) {
				this.data.put(funcion, palabra);
				*/
			} else if (funcion.equalsIgnoreCase("operadoresLogicos")) {
				//this.data.put(funcion, palabra);
				operadoresLogicos.add(palabra);
				//operadorLogico += palabra;
			} else if (funcion.equalsIgnoreCase("campoWhere")) {
				campoWhere = "";
				campoWhere += palabra;
				condicion = "";
				//condicion += palabra;
			} else if (funcion.equalsIgnoreCase("operadorComparacion")) {
				//condicion += palabra;
				operadorComparacion = "";
				operadorComparacion += palabra;
			} else if (funcion.equalsIgnoreCase("valorWhere")) {
			
				if (!operadoresLogicos.isEmpty() && campoWhere.equalsIgnoreCase(campoWhereAnterior)){
					if (operadoresLogicos.contains("AND")){
						operadoresLogicos.remove(operadoresLogicos.lastIndexOf("AND"));
						operadoresLogicos.add("OR");
					}
				} 
				
				valorWhere = "";
				valorWhere += palabra;

				condicion = campoWhere + operadorComparacion + valorWhere;
								
				campoWhereAnterior = campoWhere;
				condiciones.add(condicion);
			}
			/*
			 else if (funcion.equalsIgnoreCase("camposGroupBy")) {
				this.data.put(funcion, palabra);
			} 
			*/
			else if (funcion.equalsIgnoreCase("camposOrderBy")) {
				log.info("Paso 6: " + funcion);
				this.data.put(funcion, palabra);
			} else {
				
			}
			
		}

		// Condiciones
		String s = this.convertArrayListInString(condiciones, ",");
		this.data.put("condiciones", s);
		
		String r = this.convertArrayListInString(operadoresLogicos, ",");
		this.data.put("operadoresLogicos", r);
		// ***************************************************************************
		
		log.info("Paso 7: " + this.data.get("camposOrderBy"));
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
