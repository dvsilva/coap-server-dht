package br.ufrj.coppe.labiot.domain;

public class AppConfig {

	private Config temperature;
	private Config humidity;

	public AppConfig(Config temperatureConfig, Config humidityConfig) {
		this.temperature = temperatureConfig;
		this.humidity = humidityConfig;
	}

	public Config getTemperature() {
		return temperature;
	}

	public void setTemperature(Config temperature) {
		this.temperature = temperature;
	}

	public Config getHumidity() {
		return humidity;
	}

	public void setHumidity(Config humidity) {
		this.humidity = humidity;
	}

}
