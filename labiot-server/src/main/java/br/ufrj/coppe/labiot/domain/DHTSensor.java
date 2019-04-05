package br.ufrj.coppe.labiot.domain;

public class DHTSensor {

	private Sensor temperature;
	private Sensor humidity;
	private String timestamp;

	public DHTSensor(Sensor temperature, Sensor humidity, String timestamp) {
		super();
		this.temperature = temperature;
		this.humidity = humidity;
		this.timestamp = timestamp;
	}

	public Sensor getTemperature() {
		return temperature;
	}

	public void setTemperature(Sensor temperature) {
		this.temperature = temperature;
	}

	public Sensor getHumidity() {
		return humidity;
	}

	public void setHumidity(Sensor humidity) {
		this.humidity = humidity;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
