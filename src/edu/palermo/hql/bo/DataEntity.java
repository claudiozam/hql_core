package edu.palermo.hql.bo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DataEntity {

	private Long id;
	private String tables;
	private String colummns;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTables() {
		return tables;
	}
	
	public void setTables(String tables) {
		this.tables = tables;
	}
	
	public String getColummns() {
		return colummns;
	}
	
	public void setColummns(String colummns) {
		this.colummns = colummns;
	}
	
}
