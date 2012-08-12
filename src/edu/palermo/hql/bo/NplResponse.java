package edu.palermo.hql.bo;

import java.util.HashMap;

public class NplResponse {

	private Long id;
	private String responseType;
	private HashMap<String, String> responseData = new HashMap<String, String>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	
	public HashMap<String, String> getResponseData() {
		return responseData;
	}
	public void setResponseData(HashMap<String, String> responseData) {
		this.responseData = responseData;
	}
	
	public void addData(String key, String value) {
		responseData.put(key, value);
	}
	
	@Override
	public String toString() {
		return "[ responseType: " + responseType + ", responseData: " + responseData + " ]";
	}
	
}
