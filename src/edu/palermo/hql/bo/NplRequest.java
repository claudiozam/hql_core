package edu.palermo.hql.bo;

public class NplRequest {

	private Long id;
	private String text;
	private String userAgent;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		//Los textos tienen que terminar con punto si o si..... por el momento trabajamos con una sola oracion....
		this.text = text + (text != null && text.endsWith(".") == false ? "." : "");
	}
	
	public String getUserAgent() {
		return userAgent;
	}
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	@Override
	public String toString() {
		return "[ userAgent: " + userAgent + ", text: " + text + " ]";
	}
	
}
