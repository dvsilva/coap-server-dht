package br.ufrj.coppe.labiot.domain;

public class Sensor {
	
	private Float value;
	private String timestamp;

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
}
