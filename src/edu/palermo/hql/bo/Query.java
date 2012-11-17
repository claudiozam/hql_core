package edu.palermo.hql.bo;

import java.util.HashMap;

import org.apache.log4j.Logger;
import edu.palermo.hql.service.NplServiceFreeLingImpl;

public class Query {
	private Long id;
	private String sql;
		
	private static Logger log = Logger.getLogger(NplServiceFreeLingImpl.class);
	
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
	
	// Constructor
	public Query(HashMap<String, String> datosQuery){
		
		String generatedSql = "";
		
		String comando = datosQuery.get("comando");
		String entidad = datosQuery.get("entidad");
		String funcion = datosQuery.get("funcAgregado");
		String campoCount = datosQuery.get("campoCount");
		String condiciones = datosQuery.get("condiciones");
		String camposSelect = datosQuery.get("camposSelect");
		String camposOrderBy = datosQuery.get("camposOrderBy");
		String camposGroupBy = datosQuery.get("camposGroupBy");
		String operadoresLogicos = datosQuery.get("operadoresLogicos");
	
		/* ------------------------------------------------------------------------------------------- */
		// SELECT
		generatedSql += this.generateSelectClause(comando, funcion, camposSelect, campoCount);
		// FROM 
		generatedSql += this.generateFromClause(entidad);
		// WHERE
		generatedSql += this.generateWhereClause(condiciones, operadoresLogicos);
		// GROUP BY
		generatedSql += this.generateGroupByClause(camposGroupBy);	
		// ORDER BY
		generatedSql += this.generateOrderByClause(camposOrderBy);
		/* ------------------------------------------------------------------------------------------- */

		this.sql = generatedSql;
	}

	/* ******************************************************************************************** */

	private String generateSelectClause(String command, String function, String camposSelect, String campoCount){
		String select = "SELECT ";
		if (command.equalsIgnoreCase("listar")) {
			select += camposSelect + " ";
		} else if (command.equalsIgnoreCase("contar")) {
			//Por ahora solo una funcion de agregado
			select += function + "(" + campoCount + ") ";
		} else if (command.equalsIgnoreCase("graficar")) {
			log.info("=============================================");
			log.info("camposSelect" + camposSelect);
			
			select += camposSelect + ", " + function + "(" + camposSelect + ") as value ";
		} else {
			select = "";
		}
		return select;
	}
	
	/* ******************************************************************************************** */

	private String generateFromClause(String entity){
		String fromClause = "";
		if (!entity.equalsIgnoreCase("")){
			fromClause = " FROM " + entity;
		}
		return fromClause;
	}
	
	/* ******************************************************************************************** */

	private String generateWhereClause(String condiciones, String operadoresLogicos){
		String whereClause = "";
		
		String[] s = condiciones.split(",");
		String[] r = operadoresLogicos.split(",");
		
		if (condiciones != null){
			if (!condiciones.equalsIgnoreCase("")){
				// -----------------------------------------------------------------------------------------
				for (int i = 0; i < s.length; i++){
					
					if (i == 0){
						if (r[i] != null) {
							if (r[i].equalsIgnoreCase("OR")) {
								whereClause += " WHERE (" + this.generateCondition(s[i]);
							} else {
								whereClause += " WHERE " + this.generateCondition(s[i]);	
							}	
						}
					} else {
					 	// Concatenar por operador logico
						if (operadoresLogicos != null){
							if (!operadoresLogicos.equalsIgnoreCase("")){
								// Siempre hay 1 elemento menos en el array de operadores logicos
								if (r[i-1] != null) {
									if (r[i-1].equalsIgnoreCase("OR")) {
										whereClause += " " + r[i-1] + " " + this.generateCondition(s[i]) + ") ";
									} else {
										whereClause += " " + r[i-1] + " " + this.generateCondition(s[i]);
									}
								}
							}
						}
					}
				} // fin del for
				// -----------------------------------------------------------------------------------------				
			}	
		}
		
		return whereClause;

	}
	/* ******************************************************************************************** */
	
	private String generateOrderByClause(String camposOrderBy){
		String orderByClause = "";
		if (camposOrderBy != null){
			if (!camposOrderBy.equalsIgnoreCase("")){
				String s[] = camposOrderBy.split(",");			
				for (int i = 0; i < s.length; i++){
					if (i == 0){
						orderByClause += " ORDER BY " + s[i].toString();
					} else {
						orderByClause += ", " + s[i].toString();	
					}	
				}	
			}
		}
		return orderByClause;
	}	
	
	/* ******************************************************************************************** */

	private String generateGroupByClause(String camposGroupBy){
		String groupByClause = "";
		if (camposGroupBy != null){
			if (!camposGroupBy.equalsIgnoreCase("")){
				String s[] = camposGroupBy.split(",");			
				for (int i = 0; i < s.length; i++){
					if (i == 0){
						groupByClause += " GROUP BY " + s[i].toString();
					} else {
						groupByClause += ", " + s[i].toString();	
					}	
				}	
			}
		}
		return groupByClause;
	}
	
	/* ******************************************************************************************** */
	
	private String generateCondition(String condicion){

		String condition = "";
		String[] s = condicion.split(" ");
		boolean hasOperator = false;
		
		for (int i = 0; i < s.length; i++){
			String parte = s[i].trim();
			if (i == 0){
				condition += parte + " ";
			} else if (i == 1) {
				if (this.isOperator(parte)){
					hasOperator = true;
					condition += parte;
				} 
			} else if (i == 2) {
				if (hasOperator){
					if (this.isInteger(parte)){
						condition += " " + parte + " ";
					} else {
						condition += " '" + parte + "'";
					}
				}
			}
		}
		return condition;
	}
	
	/* ******************************************************************************************** */
	
	private boolean isOperator(String operator){
		boolean check = false;
		if (operator.equalsIgnoreCase("=")){
			check = true;
		} else if (operator.equalsIgnoreCase("<")){
			check = true;
		} else if (operator.equalsIgnoreCase(">")){
			check = true;
		} else if (operator.equalsIgnoreCase("<=")){
			check = true;
		} else if (operator.equalsIgnoreCase(">=")){
			check = true;
		}
		return check;
	}

	private boolean isInteger(String valor){
		boolean isInteger;
		isInteger = valor.matches("^-?\\d+$");
		return isInteger;
	}
}
