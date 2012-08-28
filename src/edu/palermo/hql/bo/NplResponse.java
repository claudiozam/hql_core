package edu.palermo.hql.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NplResponse {

	private Long id;
	private String responseType;
	private Object responseData;
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public String getResponseType() {
		return responseType;
	}
	
	public Object getResponseData() {
		return responseData;
	}

	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}

	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	
	@Override
	public String toString() {
		return "[ responseType: " + responseType + ", responseData: " + responseData + " ]";
	}
	
}
