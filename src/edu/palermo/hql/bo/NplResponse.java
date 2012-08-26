package edu.palermo.hql.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NplResponse<T> {

	private Long id;
	private String responseType;
	private T responseData;
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	public String getResponseType() {
		return responseType;
	}
	
	public T getResponseData() {
		return responseData;
	}

	public void setResponseData(T responseData) {
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
