package br.ufrj.coppe.labiot.domain;

public enum EnvPropertieType {

	TEMPERATURE("temperature"), 
	HUMIDITY("humidity");

	private String name;

	private EnvPropertieType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
