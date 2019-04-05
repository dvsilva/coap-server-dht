package br.ufrj.coppe.labiot.resources.config;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.AppConfig;

public class AppConfigResource extends CoapResource {

	private static final String RESOURCE_TEMP_NAME = "temperature";
	private static final String RESOURCE_HUM_NAME = "humidity";

	private Gson gson;
	
	private ConfigResource temperatureConfig;
	private ConfigResource humidityConfig;

	public AppConfigResource(String name) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		this.temperatureConfig = new ConfigResource(RESOURCE_TEMP_NAME);
		this.humidityConfig = new ConfigResource(RESOURCE_HUM_NAME);
		
		this.setObservable(true);
		this.getAttributes().setObservable();
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		AppConfig appConfig = new AppConfig(this.temperatureConfig.getConfig(), this.humidityConfig.getConfig());
		String jsonInString = gson.toJson(appConfig);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		AppConfig appConfig = gson.fromJson(request, AppConfig.class);
		
		this.temperatureConfig.setConfig(appConfig.getTemperature());
		this.humidityConfig.setConfig(appConfig.getHumidity());
		
		this.temperatureConfig.changed();
		this.humidityConfig.changed();
		
		this.changed();
		
		String jsonInString = gson.toJson(appConfig);
		String response = getName() + " has been configured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}
	@Override
	public Resource getChild(String name) {
		Resource res = null;
		
		switch (name) {
			case RESOURCE_TEMP_NAME:
				res = temperatureConfig;
				break;
			case RESOURCE_HUM_NAME:
				res = humidityConfig;
				break;
			default:
				res = this;
		}
		
		return res;
	}

}