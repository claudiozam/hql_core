package edu.palermo.hql.bo;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.palermo.hql.service.NplServiceFreeLingImpl;

public class Mask {
	
	private static Logger log = Logger.getLogger(NplServiceFreeLingImpl.class);
	
	private String mascara;

	private HashMap<Integer, String> tagCollection = new HashMap<Integer, String>();
	private HashMap<Integer, String> tagAnalysis = new HashMap<Integer, String>();

	/* ******************************************************************************************** */

	public Mask(String mask){
		// Este objeto NO reconoce palabras!!!
		this.mascara = mask;
		this.analize();
	}
	
	/* ******************************************************************************************** */
		
	private void analize(){
		// Separamos la mascara en un HashMap de etiquetas
		this.tagCollection = this.convertStringInHashMap(this.mascara, " ");
		for (int i = 0; i < this.tagCollection.size(); i++){
			String tag = this.tagCollection.get(i);
			String possibleFunctions = this.convertArrayListInString(this.getPossibleFunctions(i, tag), ",");
			this.tagAnalysis.put(i, possibleFunctions);
		}
	}
	
	/* ******************************************************************************************** */

	/*
	private ArrayList<String> getOrderByPatterns(){
		ArrayList<String> mascaraCondiciones = new ArrayList<String>();

		mascaraCondiciones.add("VMP SP "); // ordenado por
		
		return mascaraCondiciones;
	}
	*/
	
	private ArrayList<String> getConditionPatterns(){
		ArrayList<String> mascaraCondiciones = new ArrayList<String>();

		mascaraCondiciones.add("SP DA NC SP "); // de la carrera de
		mascaraCondiciones.add("(PR|S) NC VSI AQ SP "); // donde carrera es igual a
		mascaraCondiciones.add("SP DA NC SP AQ CC NC "); // de la carrera de XXX y ZZZ 
		
		return mascaraCondiciones;
	}
	
	private ArrayList<String> getWhereValuePatterns(){
		ArrayList<String> mascaraCondiciones = new ArrayList<String>();

		/* 
		
		"NC" = valorNombre       | "NC CC NC" valorNombre1       o valorNombre2
		"NP" = valorNombrePropio | "NP CC NP" valorNombrePropio1 o valorNombrePropio2
		"Z"  = valorNumerico     | "Z CC Z"   valorNumerico1     o valorNumerico2
		"W"  = valorFecha        | "W CC W"   valorFecha1        o valorFecha2
		
		*/
		mascaraCondiciones.add("((NC|NC CC (NC|RG))|(NP|NP CC NP)|(Z|Z CC Z)|(W|W CC W))"); 
		
		return mascaraCondiciones;
	}
	
	/* ******************************************************************************************** */
	
	// TODO: seguir con esta parte!!!
	public ArrayList<String> getPossibleFunctions(Integer position, String tag){
		ArrayList<String> posiblesFunctions = new ArrayList<String>();
		
		if (!tag.equalsIgnoreCase("")){
			
			if (position == 0 && tag.startsWith("VM")){
				posiblesFunctions.add("comando");
			}
			
			if (position == 1 && tag.startsWith("N")){
				posiblesFunctions.add("entidad");
			}
			
			if (tag.startsWith("SP") || tag.startsWith("AQ")){
				for (int i = 0; i <this.getConditionPatterns().size(); i++){
					posiblesFunctions.add("operadorComparacion");
					// TODO: OJO CON ESTO!!!
					if (tag.startsWith("AQ")){
						posiblesFunctions.add("valorWhere");	
					}
				}
			}
			
			if (tag.startsWith("CC")){
				posiblesFunctions.add("operadoresLogicos");
			}
			
			if (tag.startsWith("Z")){
				posiblesFunctions.add("valorWhere");
			}
			
			if (tag.startsWith("W")){
				posiblesFunctions.add("valorWhere");
			}
			
			if (tag.startsWith("N")){
				for(String p : this.getConditionPatterns()){
					if (p.contains(tag)){
						posiblesFunctions.add("campoWhere");	
					}
				}
				
				for(String v : this.getWhereValuePatterns()){
					if (v.contains(tag)){
						posiblesFunctions.add("valorWhere");	
					}
				}
				
				posiblesFunctions.add("camposOrderBy");	
			}
			
			/*
			if (tag.startsWith("VMP") && this.mascara.contains("VMP SP")){
				posiblesFunctions.add("ordenamiento");
			}
			
			if (tag.startsWith("SP") && this.mascara.contains("VMP SP")){
				posiblesFunctions.add("ordenamiento");
			}
			*/
			
			
		} else {
			posiblesFunctions.add("");
		}
		
		return posiblesFunctions;
	}
	
	/* ******************************************************************************************** */
	
	private String convertArrayListInString(ArrayList<String> array, String delimiter){
		String cadena = "";
		for (int i = 0; i < array.size(); i++){
			if (cadena.equalsIgnoreCase("")){
				cadena = array.get(i);
			} else {
				cadena = cadena + delimiter + array.get(i);	
			}
		}
		return cadena;
	}
	
	private HashMap<Integer, String> convertStringInHashMap(String value, String delimiter){
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		if (value != null && delimiter != null){
			String[] s = value.split(delimiter);
			for (int i = 0; i < s.length; i++) {
				hm.put(i, s[i]);
			}
		}
		return hm;	
	}
	
	/* ******************************************************************************************** */
	
	public HashMap<Integer, String> getTagAnalysis(){
		return this.tagAnalysis;
	}
	
	public HashMap<Integer, String> getTagCollection(){
		return this.tagCollection;
	}
	
}

