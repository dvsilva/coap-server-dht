package br.ufrj.coppe.labiot.domain;

import com.google.gson.annotations.SerializedName;

public class SenseLED {

	@SerializedName("temperature-led")
	private LED temperatureLed;
	
	@SerializedName("humidity-led")
	private LED humidityLed;

	public SenseLED(LED temperatureLed, LED humidityLed) {
		this.temperatureLed = temperatureLed;
		this.humidityLed = humidityLed;
	}

	public LED getHumidityLed() {
		return humidityLed;
	}

	public void setHumidityLed(LED humidityLed) {
		this.humidityLed = humidityLed;
	}

	public LED getTemperatureLed() {
		return temperatureLed;
	}

	public void setTemperatureLed(LED temperatureLed) {
		this.temperatureLed = temperatureLed;
	}

}
