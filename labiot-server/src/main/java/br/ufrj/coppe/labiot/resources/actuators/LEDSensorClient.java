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

	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public LEDSensorClient(String sensorPath, String appConfigPath) {
		this.sensorPath = sensorPath;

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		this.sensor = new Sensor();

		// instancia um cliente para obter valores de configuracao dos sensores
		this.configClient = new LEDConfigClient(appConfigPath);

		// instancia um cliente para obter valores do sensor
		CoapClient sensorClient = new CoapClient(sensorPath);
		sensorClient.observe(this);
	}

	/**
	 * metodo chamado quando o recurso requerido for atualizado
	 */
	@Override
	public void onLoad(CoapResponse response) {
		String content = response.getResponseText();
		//System.out.println("SENSOR RESPONSE FROM " + sensorPath + ": " + content);
		this.sensor = gson.fromJson(content, Sensor.class);
	}
	
	/**
	 * metodo chamado quando ocorre um erro
	 */
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
	
	// metodo que retorna true se valor lido e maior que o valor maximo configurado
	public boolean isValueHigherThan() {
		return this.sensor.getValue() > getConfig().getMax();
	}

	// metodo que retorna true se valor lido e menor que o valor minimo configurado
	public boolean isValueLowerThan() {
		return this.sensor.getValue() < getConfig().getMin();
	}

	// metodo que retorna true se valor lido e menor que o valor maximo configurado
	public boolean isValueLowerThanMax() {
		return this.sensor.getValue() < getConfig().getMax();
	}
	
	// metodo que retorna true se valor lido estiver entre o minimo e o maximo
	public boolean isValueOk() {
		return this.sensor.getValue() > getConfig().getMin() && this.sensor.getValue() < getConfig().getMax();
	}

	// metodo que retorna true se valor lido estiver menor que o minimo ou maior que o maximo
	public boolean isValueNotOk() {
		return this.sensor.getValue() < getConfig().getMin() || this.sensor.getValue() > getConfig().getMax();
	}

	// retorna true se a configuracao estiver realizada
	public boolean isConfigured() {
		return getConfig().getMin() != null || getConfig().getMax() != null;
	}
	
	// retorna true se tiver algum valor lido no sensor
	public boolean hasValue() {
		return this.sensor.getValue() != null;
	}

	// retorna a configuracao realizada
	public Config getConfig() {
		return this.configClient.getConfig();
	}

}