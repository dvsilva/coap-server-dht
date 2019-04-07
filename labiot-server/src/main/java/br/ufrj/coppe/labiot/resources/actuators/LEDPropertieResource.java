package br.ufrj.coppe.labiot.resources.actuators;

import java.util.Timer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.pi4j.io.gpio.Pin;

import br.ufrj.coppe.labiot.domain.LED;
import br.ufrj.coppe.labiot.resources.tasks.TurnOnLEDTask;

public class LEDPropertieResource extends CoapResource {

	private static final long TIME_TO_UPDATE = 1500;
	
	private LED led;
	private Gson gson;
	
	private LEDSensorClient sensorClient;

	public LEDPropertieResource(Pin gpio, String propName, String sensorPath, String appConfigPath) {
		super(propName);
		
		this.gson = new Gson(); // Or use new GsonBuilder().create();
		this.led = new LED(propName, gpio);
		
		sensorPath += "/" + propName;
		appConfigPath += "/" + propName;
		
		this.sensorClient = new LEDSensorClient(sensorPath, appConfigPath);

		TurnOnLEDTask turnOnTask = new TurnOnLEDTask(this);
		
		Timer timer = new Timer();
		timer.schedule(turnOnTask, 0, TIME_TO_UPDATE);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		String jsonInString = gson.toJson(this.led);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}
	
	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		LED led = gson.fromJson(request, LED.class);
		
		update(led.getState());
		
		this.changed();
		
		String jsonInString = gson.toJson(led);
		String response = getName() + " led has been configured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}
	
	@Override
	public void changed() {
		super.changed();
		LEDResource parent = (LEDResource) this.getParent();
		parent.changed();
	}

	public void update(String state) {
		this.led.update(state);
	}
	
	public LED getLED() {
		return led;
	}

	public void setLED(LED led) {
		this.led = led;
	}

	public LEDSensorClient getSensorClient() {
		return sensorClient;
	}

	public void setSensorClient(LEDSensorClient sensorClient) {
		this.sensorClient = sensorClient;
	}

	public boolean isPulsing() {
		return this.led.isPulsing();
	}

	public void setPulsing(boolean pulsing) {
		this.led.setPulsing(pulsing);
	}

}