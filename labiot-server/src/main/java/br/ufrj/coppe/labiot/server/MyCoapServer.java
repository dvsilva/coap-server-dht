package br.ufrj.coppe.labiot.server;

import org.eclipse.californium.core.CoapServer;

import br.ufrj.coppe.labiot.resources.actuators.ActuatorResource;
import br.ufrj.coppe.labiot.resources.config.AppConfigResource;
import br.ufrj.coppe.labiot.resources.sensors.SensorResource;

public class MyCoapServer extends CoapServer {

	public static String coap_path = "coap://192.168.1.2:5683/";
	//public static String coap_path = "coap://10.0.0.230:5683/";
	//public static final String coap_path = "coap://127.0.0.1:5683/";
	
	public static final String resource_app_config = "app-config";
	public static final String resource_sensors = "sensors";
	public static final String resource_actuators = "actuators";
	public static final String resource_led = "led";
	
	public static final String app_config_path = coap_path + resource_app_config;
	public static final String sensors_path = coap_path + resource_sensors;
	
	/**
	 * metodo que inicia o servidor
	 * @param args
	 */
	public static void main(String[] args) {

		MyCoapServer server = new MyCoapServer();

		// adiciona recurso de configuracao
		AppConfigResource configRes = new AppConfigResource(resource_app_config);
		server.add(configRes);

		// adiciona recurso de sensores
		SensorResource sensorsResource = new SensorResource(resource_sensors);
		server.add(sensorsResource);
		
		// adiciona recurso de atuadores
		ActuatorResource actuatorResource = new ActuatorResource(resource_actuators, resource_led, app_config_path, sensors_path);
		server.add(actuatorResource);
		
		server.start();
	}

}