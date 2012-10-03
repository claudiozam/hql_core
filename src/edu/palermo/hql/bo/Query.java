package edu.palermo.hql.bo;

import java.util.ArrayList;

public class Query {
	private Long id;
	private String sql;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
	/* ------------------------------------------------------------------------------------------- */
	/* Constructor                                                                                 */
	/* ------------------------------------------------------------------------------------------- */
	public Query(String comando, String mascara, String camposSelect, String campoCount, 
			String entidadActual, ArrayList<String> camposWhere, ArrayList<String> operadores,  
			ArrayList<String> valores, ArrayList<String> camposOrderBy, ArrayList<String> camposGroupBy, 
			ArrayList<String> operadoresLogicos, ArrayList<String> funciones){
	
		String generatedSql = "";
	
		/* ----------------------------------------------------------------*/
		/*                             SELECT                              */
		/* ----------------------------------------------------------------*/
		generatedSql += this.generateSelectClause(comando, camposSelect, funciones, campoCount, camposGroupBy);
		/* ----------------------------------------------------------------*/
		/*                              FROM                               */
		/* ----------------------------------------------------------------*/
		generatedSql += this.generateFromClause(entidadActual);
		/* ----------------------------------------------------------------*/		
		/*                              WHERE                              */
		/* ----------------------------------------------------------------*/
		// Identifico el tipo de mascara
		if (mascara.matches("VMN NC Fp")) {
			// Listar alumnos
			this.sql = generatedSql;
		} else if (mascara.matches("VMN NC (PR|S) NC VSI AQ SP ((NC|NC CC (NC|RG))|(NP|NP CC NP)|(Z|Z CC Z)|(W|W CC W)) (Fp|VMP SP NC Fp)")) {
			// Listar alumnos donde carrera es igual a informatica o arquitectura 
			generatedSql += this.generateWhereClause(camposWhere, operadores, operadoresLogicos, valores);			
		} else if (mascara.matches("VMN NC SP DA NC SP (NP|NC|NC CC (NC|RG)) (Fp|VMP SP NC Fp)")) {
			// Listar alumnos de la carrera de informatica 
			generatedSql += this.generateWhereClause(camposWhere, operadores, operadoresLogicos, valores);			
		} else if(mascara.matches("VMN NC VMP SP NC Fp")) {
			// Listar alumnos ordenados por carrera 
			generatedSql += this.generateOrderByClause(camposOrderBy);
		} else if(mascara.matches("VMN NC SP NC SP ((NC|NC CC (NC|RG))|(NP|NP CC NP)|(Z|Z CC Z)|(W|W CC W)) (Fp|VMP SP NC Fp)")) {
			// Listar alumnos con promedio entre 1 y 5
			generatedSql += this.generateWhereClause(camposWhere, operadores, operadoresLogicos, valores);
		} else {
			this.sql = null;
		}
		/* ----------------------------------------------------------------*/		
		/*                            GROUP BY                             */
		/* ----------------------------------------------------------------*/
		if (!camposGroupBy.isEmpty()){
			generatedSql += this.generateGroupByClause(camposGroupBy);	
		}
		/* ----------------------------------------------------------------*/		
		/*                            ORDER BY                             */
		/* ----------------------------------------------------------------*/
		if (!camposOrderBy.isEmpty() && !camposWhere.isEmpty()){
			generatedSql += this.generateOrderByClause(camposOrderBy);
		}
		/* ----------------------------------------------------------------*/
		this.sql = generatedSql;
	}
	/* ------------------------------------------------------------------------------------------- */
	
	//*****************************************************************************************************************
	//SELECT
	//*****************************************************************************************************************
	private String generateSelectClause(String comando, String camposSelect, ArrayList<String> funciones, 
										String campoCount, ArrayList<String> camposGroupBy){
		String select = "SELECT ";
		// Identifico el tipo de comando
		if (comando.equalsIgnoreCase("listar")) {
			// Agrego esta parte porque en la parte web no se veia bien el 
			// formato de la fecha despues de que agregue la parte de la hora
			//camposSelect = this.formatDateColumns(camposSelect);			
			select += camposSelect + " ";
		} else if (comando.equalsIgnoreCase("contar")) {
			//Por ahora solo una funcion de agregado
			select += funciones.get(0).toString() + "(" + campoCount + ") ";
		} else if (funciones.get(0).equalsIgnoreCase("graficar")) {
			select += camposSelect  + " ";
		} else {
			select = "";
		}
		return select;
	}
	
	//*****************************************************************************************************************
	//FROM
	//*****************************************************************************************************************
	private String generateFromClause(String entidadActual){
		String fromClause = " FROM ";
		fromClause += entidadActual;
		return fromClause;
	}

	//*****************************************************************************************************************
	//WHERE
	//*****************************************************************************************************************
	private String generateWhereClause(ArrayList<String> camposWhere, ArrayList<String> operadoresDeComparacion, 
										ArrayList<String> operadoresLogicos, ArrayList<String> valores){
		String whereClause = " WHERE ";
		ArrayList<String> conditions = new ArrayList<String>();
		boolean existeBetween = false;
		// Generar Condiciones
		for (int i = 0; i < camposWhere.size(); i++){
			// TODO: falta agregar comparaciones por mas de un campoWhere
			if (operadoresDeComparacion.get(i).equalsIgnoreCase("BETWEEN")){
				String campoWhere = camposWhere.get(i).toString();
				String operadorDeComparacion = operadoresDeComparacion.get(i).toString();
				String valorD = valores.get(0).toString();
				String valorH = valores.get(1).toString();
				// Agrego elementos al Array de condiciones para armar el WHERE
				conditions.add(this.generateCondition(campoWhere, operadorDeComparacion, valorD, valorH));
				existeBetween = true;
			} else {
				for (int j = 0; j < valores.size(); j++){
					String campoWhere = camposWhere.get(i).toString();
					String operadorDeComparacion = operadoresDeComparacion.get(i).toString();
					String valor = valores.get(j).toString();
					// Nueva Condicion				
					conditions.add(this.generateCondition(campoWhere, operadorDeComparacion, valor));
				}	
			}
		}
		// Concatenar por operador logico
		for (int k = 0; k < conditions.size(); k++) {
			whereClause += conditions.get(k).toString();
			// Si existe mas de una condicion entonces existe al menos un operador logico (AND/OR)
			for (int l = 0; l < operadoresLogicos.size(); l++) {
				if (l == k && !existeBetween){
					whereClause += " " + operadoresLogicos.get(l).toString() + " ";	
				}
			}
		}
		return whereClause;
	}
	
	//*****************************************************************************************************************
	//ORDER BY
	//*****************************************************************************************************************
	private String generateOrderByClause(ArrayList<String> camposOrderBy){
		String orderByClause = " ORDER BY ";
		for (int i = 0; i < camposOrderBy.size(); i++) {
			orderByClause += camposOrderBy.get(i).toString();
		}
		return orderByClause;
	}
	
	//*****************************************************************************************************************
	//GROUP BY
	//*****************************************************************************************************************
	private String generateGroupByClause(ArrayList<String> camposGroupBy){
		String groupByClause = "GROUP BY ";
		for (int i = 0; i < camposGroupBy.size(); i++) {
			sql += camposGroupBy.get(i).toString();
		}
		return groupByClause;
	}

	//*****************************************************************************************************************
	
	//-----------------------------------------------------------------------------------------------------------------
	//Sobrecargas del metodo generateCondition
	//-----------------------------------------------------------------------------------------------------------------
	
	private String generateCondition(String campoWhere, String operadorDeComparacion, String valor){
		String condition = " " + campoWhere + " " + operadorDeComparacion + " " ;
		boolean isInteger = valor.matches("^-?\\d+$");
		// Si el valor es un numero NO agrego comillas
		if (isInteger){
			condition += " " + valor + " ";
		} else {
			condition += " '" + valor + "'";
		}
		return condition;
	}
	 
	private String generateCondition(String campoWhere, String operadorDeComparacion, String valorDesde, String valorHasta){
		String condition = " " + campoWhere + " " + operadorDeComparacion + " ";
		boolean isIntegerD = valorDesde.matches("^-?\\d+$"); 
		boolean isIntegerH = valorHasta.matches("^-?\\d+$");
		// Si el valor es un numero NO agrego comillas
		if (isIntegerD && isIntegerH){
			condition += " " + valorDesde + " AND " + valorHasta + " ";
		} else {
			condition += " '" + valorDesde + "' AND '" + valorHasta + "' ";
		}
		return condition;
	}
	//-----------------------------------------------------------------------------------------------------------------
	
	private String formatDateColumns(String camposSelect){
		String camposSelectFormateados = camposSelect;
		String[] s = camposSelectFormateados.split(", ");
		String format = "%Y-%m-%d %h:%i:%s";
		// Si existe un campo con un nombre que contenga "fecha" hago un DATE_FORMAT
		for (int i = 0; i < s.length; i++){
			// TODO: mejorar esta parte!!!
			if (s[i].toString().contains("fecha")){
				String campoFechaConFuncion = "DATE_FORMAT(" + s[i].toString() + ", '" + format + "') as fecha ingreso";
				camposSelectFormateados = camposSelect.replace(s[i].toString(), campoFechaConFuncion);
			}
		}
		return camposSelectFormateados;
	}
}
