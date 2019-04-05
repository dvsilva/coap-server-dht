package br.ufrj.coppe.labiot.server;

import org.eclipse.californium.core.CoapServer;

import br.ufrj.coppe.labiot.resources.actuators.ActuatorResource;
import br.ufrj.coppe.labiot.resources.config.AppConfigResource;
import br.ufrj.coppe.labiot.resources.sensors.SensorResource;

public class MyCoapServer extends CoapServer {

	//public static String coap_path = "coap://192.168.1.2:5683/";
	//public static String coap_path = "coap://10.0.0.230:5683/";
	public static final String coap_path = "coap://127.0.0.1:5683/";
	
	public static final String resource_app_config = "app-config";
	
	public static final String resource_sensors = "sensors";
	public static final String resource_actuators = "actuators";
	
	public static final String app_config_path = coap_path + resource_app_config;
	public static final String sensors_path = coap_path + resource_sensors;
	
	public static void main(String[] args) {

		MyCoapServer server = new MyCoapServer();

		// all -- {"temperature":{"min":"15","max":"35"},"humidity":{"min":"15","max":"35"}}
		// foreach --{min: 20; max:30 }
		AppConfigResource configRes = new AppConfigResource(resource_app_config);
		server.add(configRes);

		// all -- {"temperature":{"value":"48","timestamp":"2019-04-05 10:44:55"},"humidity":{"value":"29","timestamp":"2019-04-05 10:44:55"},"timestamp":"2019-04-05 10:44:57"}
		// foreach -- {"value":"26","timestamp":"2019-04-05 10:45:31"}
		SensorResource sensorsResource = new SensorResource(resource_sensors);
		server.add(sensorsResource);
		
		// all - {"temperature-led":{"state":"LOW"},"humidity-led":{"state":"HIGH"}}
		// foreach -- {"state":"LOW"}
		ActuatorResource actuatorResource = new ActuatorResource(resource_actuators, app_config_path, sensors_path);
		server.add(actuatorResource);
		
		server.start();
	}

}