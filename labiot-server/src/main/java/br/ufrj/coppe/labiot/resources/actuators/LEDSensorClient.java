package br.ufrj.coppe.labiot.resources.actuators;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.Config;
import br.ufrj.coppe.labiot.domain.Sensor;

public class LEDSensorClient implements CoapHandler {

	private Gson gson;
	
	private String sensorPath;
	private Sensor sensor;	
	
	private LEDConfigClient configClient;

	public LEDSensorClient(String sensorPath, String appConfigPath) {
		this.sensorPath = sensorPath;

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		this.sensor = new Sensor();

		this.configClient = new LEDConfigClient(appConfigPath);

		CoapClient sensorClient = new CoapClient(sensorPath);
		sensorClient.observe(this);
	}

	@Override
	public void onLoad(CoapResponse response) {
		String content = response.getResponseText();
		//System.out.println("SENSOR RESPONSE FROM " + sensorPath + ": " + content);
		this.sensor = gson.fromJson(content, Sensor.class);
	}

	@Override
	public void onError() {
		System.out.println("ERROR " + this.sensorPath);
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	
	public boolean isValueHigherThan() {
		return this.sensor.getValue() > getConfig().getMax();
	}

	public boolean isValueLowerThan() {
		return this.sensor.getValue() < getConfig().getMin();
	}

	public boolean isValueLowerThanMax() {
		return this.sensor.getValue() < getConfig().getMax();
	}
	
	public boolean isValueOk() {
		return this.sensor.getValue() > getConfig().getMin() && this.sensor.getValue() < getConfig().getMax();
	}

	public boolean isValueNotOk() {
		return this.sensor.getValue() < getConfig().getMin() || this.sensor.getValue() > getConfig().getMax();
	}

	public boolean isConfigured() {
		return getConfig().getMin() != null || getConfig().getMax() != null;
	}
	
	public boolean hasValue() {
		return this.sensor.getValue() != null;
	}

	public Config getConfig() {
		return this.configClient.getConfig();
	}

}
