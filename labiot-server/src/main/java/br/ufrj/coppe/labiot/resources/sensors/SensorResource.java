package br.ufrj.coppe.labiot.resources.sensors;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.DHTSensor;
import br.ufrj.coppe.labiot.util.DataUtil;

public class SensorResource extends CoapResource {

	private static final String RESOURCE_TEMP_NAME = "temperature";
	private static final String RESOURCE_HUM_NAME = "humidity";

	private Gson gson;
	
	private SensorObservableResource temperature;
	private SensorObservableResource humidity;

	public SensorResource(String name) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		this.temperature = new SensorObservableResource(RESOURCE_TEMP_NAME);
		this.humidity = new SensorObservableResource(RESOURCE_HUM_NAME);
		
		this.setObservable(true);
		this.getAttributes().setObservable();
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
	
	@Override
	public Resource getChild(String name) {
		Resource res = null;
		
		switch (name) {
			case RESOURCE_TEMP_NAME:
				res = temperature;
				break;
			case RESOURCE_HUM_NAME:
				res = humidity;
				break;
			default:
				res = this;
		}
		
		return res;
	}

}