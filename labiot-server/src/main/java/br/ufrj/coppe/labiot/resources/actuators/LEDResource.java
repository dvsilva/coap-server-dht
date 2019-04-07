package br.ufrj.coppe.labiot.resources.actuators;

import java.util.Timer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.pi4j.io.gpio.RaspiPin;

import br.ufrj.coppe.labiot.domain.EnvPropertieType;
import br.ufrj.coppe.labiot.domain.SenseLED;
import br.ufrj.coppe.labiot.resources.tasks.PulseLEDTask;

public class LEDResource extends CoapResource {

	private static final long TIME_TO_UPDATE = 1000;

	private Gson gson;
	
	private LEDPropertieResource temperatureLed;
	private LEDPropertieResource humidityLed;

	public LEDResource(String name, String appConfigPath, String sensorsPath) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		this.temperatureLed = new LEDPropertieResource(RaspiPin.GPIO_01, EnvPropertieType.TEMPERATURE.getName(), sensorsPath, appConfigPath);
		this.humidityLed = new LEDPropertieResource(RaspiPin.GPIO_04, EnvPropertieType.HUMIDITY.getName(),  sensorsPath, appConfigPath);
		
		this.setObservable(true);
		this.getAttributes().setObservable();
		
		add(this.temperatureLed);
		add(this.humidityLed);

		PulseLEDTask blinkLEDTask = new PulseLEDTask(this);
		
		Timer timer = new Timer();
		timer.schedule(blinkLEDTask, 0, TIME_TO_UPDATE);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		SenseLED senseLED = new SenseLED(this.temperatureLed.getLED(), this.humidityLed.getLED());
		String jsonInString = gson.toJson(senseLED);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		SenseLED senseLED = gson.fromJson(request, SenseLED.class);
		
		String temperatureLEDState = senseLED.getTemperatureLed().getState();
		String humidityLedState = senseLED.getHumidityLed().getState();
		update(temperatureLEDState, humidityLedState);
		
		this.changed();
		
		String jsonInString = gson.toJson(senseLED);
		String response = getName() + " has been configured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}

	public void update(String tempLedState, String humLedState) {
		this.temperatureLed.update(tempLedState);
		this.humidityLed.update(humLedState);
	}
	
	@Override
	public void changed() {
		super.changed();
		
		ActuatorResource parent = (ActuatorResource) this.getParent();
		parent.changed();
	}

	public LEDPropertieResource getTemperatureLed() {
		return temperatureLed;
	}

	public void setTemperatureLed(LEDPropertieResource temperatureLed) {
		this.temperatureLed = temperatureLed;
	}

	public LEDPropertieResource getHumidityLed() {
		return humidityLed;
	}

	public void setHumidityLed(LEDPropertieResource humidityLed) {
		this.humidityLed = humidityLed;
	}

	public void setLED(SenseLED senseLED) {
		this.temperatureLed.setLED(senseLED.getTemperatureLed());
		this.humidityLed.setLED(senseLED.getHumidityLed());		

		update(this.temperatureLed.getLED().getState(), this.humidityLed.getLED().getState());
	}

	public SenseLED getLED() {
		SenseLED senseLED = new SenseLED(this.temperatureLed.getLED(), this.humidityLed.getLED());
		return senseLED;
	}

}