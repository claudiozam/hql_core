package edu.palermo.hql.bo;

import org.joda.time.DateTime;

public class Language {

	private Long id;
	
	private String idioma;
	private String formatoFechaJoda;
	
	public enum LogicalOperator {
		 CONJUNCION, DISYUNCION
	}

	public enum WordType {
		VERBO, NOMBRE, ADVERVIO, DETERMINANTE, PREPOSICION, ADJETIVO, PRONOMBRE, CONJUNCION, SIGNODEPUNTUACION, NUMERO, FECHA
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	// Constructor
	public Language(String lang){
		this.idioma = lang;
		this.formatoFechaJoda = "yyyy-MM-dd";
	}
	
	public String getWordType(String shortTag){
		String tipoDePalabra = "";
		
		if (this.idioma.equalsIgnoreCase("es")){
			// -------------------------------------------
			if (shortTag.startsWith("V") ) {
				tipoDePalabra = WordType.VERBO.toString();
			} else if (shortTag.startsWith("N")) {
				tipoDePalabra = WordType.NOMBRE.toString();
			} else if(shortTag.startsWith("R")){
				tipoDePalabra = WordType.ADVERVIO.toString();
			} else if(shortTag.startsWith("D")){
				tipoDePalabra = WordType.DETERMINANTE.toString();
			} else if (shortTag.startsWith("S")) {
				tipoDePalabra = WordType.PREPOSICION.toString();
			} else if (shortTag.startsWith("A")) {
				tipoDePalabra = WordType.ADJETIVO.toString();
			} else if (shortTag.startsWith("P")) {
				tipoDePalabra = WordType.PRONOMBRE.toString();
			} else if (shortTag.startsWith("C")) {
				tipoDePalabra = WordType.CONJUNCION.toString();
			} else if (shortTag.startsWith("F")){
				tipoDePalabra = WordType.SIGNODEPUNTUACION.toString();
			} else if (shortTag.startsWith("Z")) {
				tipoDePalabra = WordType.NUMERO.toString();
			} else if (shortTag.startsWith("W")) {
				tipoDePalabra = WordType.FECHA.toString();
			} else {
				tipoDePalabra = "";
			}

			// -------------------------------------------
		} 
		return tipoDePalabra;
	}
	
	
	/* ***************************** Adjetivos ********************************** */
	public String convertAdjective(String adjective){
		String adverbio = "";
		
		if (adjective.equalsIgnoreCase("igual")) {
			adverbio = " = ";
		} else if (adjective.equalsIgnoreCase("mayor")) {
			adverbio = " > ";
		} else if (adjective.equalsIgnoreCase("menor")) {
			adverbio = " < ";
		} 
		return adverbio;
	}
	
	public String getLogicalOperation(String conjuntion){
		// OPERADORES LOGICOS
		String logicalOperation = "";
		if (conjuntion.equalsIgnoreCase("y") || conjuntion.equalsIgnoreCase("e")){
			logicalOperation = LogicalOperator.CONJUNCION.toString();
		}
		else if (conjuntion.equalsIgnoreCase("o") || conjuntion.equalsIgnoreCase("u")){
			logicalOperation = LogicalOperator.DISYUNCION.toString();
		}
		return logicalOperation;
	} 
	
	
	/* *************************** Preposiciones ******************************** */
	
	public String convertPreposition(String preposition){
		// Operadores de Comparacion
		String preposicion = " = ";
		if (preposition.equalsIgnoreCase("de")) {
			
		} else if (preposition.equalsIgnoreCase("hasta")) {
			preposicion = "<=";
		} else if (preposition.equalsIgnoreCase("desde")) {
			preposicion = ">=";
		} else if (preposition.equalsIgnoreCase("entre")) {
			preposicion = "BETWEEN";
		}
		return preposicion;
	}
	
	/* ****************************** Fechas ************************************ */
	public String convertDate(String date, String type){
		String fecha = "";
		Integer dia = 0;
		Integer mes = 0;
		Integer anio = 0;
		Integer horas = 0;
		Integer minutos = 0;
		
		DateTime dt; // Objeto JODA 
		
		if (type.equalsIgnoreCase("R") || type.equalsIgnoreCase("N")){
			// Fechas expresadas con palabras (Ej. hoy, ayer...)
			dt = new DateTime(DateTime.now());
			if (date.equalsIgnoreCase("hoy")){
			} else if (date.equalsIgnoreCase("ayer")){
				dt.plusDays(-1);	
			}
			fecha = dt.toString(this.formatoFechaJoda);
		} else if (type.equalsIgnoreCase("W")){
			// El lemma viene con corchetes, los reemplazo para poder hacer un split
			String[] arrayFecha = date.replace("([|])", "").split(":");
			// Recorro el array para encontrar la parte del lemma que tiene la fecha
			for (int i = 0; i < arrayFecha.length; i++) {
				if (i == 1) {
					// La posicion 1 tiene la fecha en formato dd/mm/yyyy
					// Si NO se informa el dia el lemma viene con formato ??/mm/yyyy 
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
			dt = new DateTime(anio, mes, dia, horas, minutos);
			fecha = dt.toString(this.formatoFechaJoda);
		} else {
			dt = null;
		}
		return fecha;
	}
	
	public boolean isAValidCommand(String preForm){
		boolean valido = false;
		
		if (preForm.equalsIgnoreCase("listar")) {
			valido = true;
		} else if (preForm.equalsIgnoreCase("graficar")) {
			valido = true;
		} else if (preForm.equalsIgnoreCase("contar")) {
			valido = true;
		} else if (preForm.equalsIgnoreCase("sumar")) {
			valido = true;
		}
		return valido;
	}

}
