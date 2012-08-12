package edu.palermo.hql.bo;

public class AutoCompleteItem {

	private Long id;
	private String value;
	private String label;
	private String extraData;
	
	public AutoCompleteItem(Long id, String value, String label, String extraData) {
		this.id = id;
		this.value = value;
		this.label = label;
		this.extraData = extraData;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getExtraData() {
		return extraData;
	}
	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}
	
}
