package br.ufrj.coppe.labiot.resources.sensors;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.DHTSensor;
import br.ufrj.coppe.labiot.domain.EnvPropertieType;
import br.ufrj.coppe.labiot.util.DataUtil;

public class SensorResource extends CoapResource {

	private Gson gson;
	
	private DTHObservableResource temperature;
	private DTHObservableResource humidity;

	public SensorResource(String name) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		this.temperature = new DTHObservableResource(EnvPropertieType.TEMPERATURE.getName());
		this.humidity = new DTHObservableResource(EnvPropertieType.HUMIDITY.getName());
		
		this.setObservable(true);
		this.getAttributes().setObservable();
		
		add(this.temperature);
		add(this.humidity);
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		DHTSensor dht = new DHTSensor(this.temperature.getSensor(), this.humidity.getSensor(), DataUtil.getFormattedTimestamp());
		String jsonInString = gson.toJson(dht);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		DHTSensor dht = gson.fromJson(request, DHTSensor.class);
		
		this.temperature.setSensor(dht.getTemperature());
		this.humidity.setSensor(dht.getHumidity());

		this.temperature.changed();
		this.humidity.changed();
		
		this.changed();
		
		String jsonInString = gson.toJson(dht);
		String response = getName() + " has been configured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}

	public DTHObservableResource getTemperature() {
		return temperature;
	}

	public void setTemperature(DTHObservableResource temperature) {
		this.temperature = temperature;
	}

	public DTHObservableResource getHumidity() {
		return humidity;
	}

	public void setHumidity(DTHObservableResource humidity) {
		this.humidity = humidity;
	}
	
}