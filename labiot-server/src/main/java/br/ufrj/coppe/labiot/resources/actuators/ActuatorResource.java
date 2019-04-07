package br.ufrj.coppe.labiot.resources.actuators;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.Actuator;

public class ActuatorResource extends CoapResource {

	private Gson gson;

	private LEDResource ledResource;
	
	public ActuatorResource(String name, String ledName, String appConfigPath, String sensorsPath) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		this.ledResource = new LEDResource(ledName, appConfigPath, sensorsPath);
		add(this.ledResource);
		
		this.setObservable(true);
		this.getAttributes().setObservable();
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		Actuator actLed = new Actuator(this.ledResource.getLED());
		String jsonInString = gson.toJson(actLed);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		
		Actuator actuatorLED = gson.fromJson(request, Actuator.class);
		
		String temperatureLEDState = actuatorLED.getSenseLED().getTemperatureLed().getState();
		String humidityLedState = actuatorLED.getSenseLED().getHumidityLed().getState();
		this.ledResource.update(temperatureLEDState, humidityLedState);
		
		this.changed();
		
		String jsonInString = gson.toJson(actuatorLED);
		String response = getName() + " has been configured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}

}