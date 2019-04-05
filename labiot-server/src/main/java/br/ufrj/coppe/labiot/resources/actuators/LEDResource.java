package br.ufrj.coppe.labiot.resources.actuators;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.LED;
import br.ufrj.coppe.labiot.domain.Sensor;

public class LEDResource extends CoapResource {

	private static final long TIME_TO_UPDATE = 1500;
	
	private LED led;
	private Gson gson;
	
	private LEDSensorClient sensorClient;

	private String sensorPath;
	private String appConfigPath;

	private boolean blink;

	public LEDResource(String name, String sensorPath, String appConfigPath) {
		super(name);
		this.sensorPath = sensorPath;
		this.appConfigPath = appConfigPath;
		
		this.gson = new Gson(); // Or use new GsonBuilder().create();
		this.led = new LED();

		this.sensorClient = new LEDSensorClient(sensorPath, appConfigPath);
		
		Timer timer = new Timer();
		timer.schedule(new TurnOnTask(this), 0, TIME_TO_UPDATE);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		String jsonInString = gson.toJson(this.led);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		this.led = gson.fromJson(request, LED.class);
		
		this.changed();
		
		String jsonInString = gson.toJson(this.led);
		String response = this.getName() + " has been ledured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePUT(CoapExchange exchange) {		
		String request = exchange.getRequestText();
		this.led = gson.fromJson(request, LED.class);
		
		this.changed();

		String jsonInString = gson.toJson(this.led);
		String response = this.getName() + " has been ledured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}
	
	private class TurnOnTask extends TimerTask {
		
		private CoapResource mCoapRes;

		public TurnOnTask(CoapResource coapRes) {
			mCoapRes = coapRes;
		}

		@Override
		public void run() {
		
			if(sensorClient.isConfigured() && sensorClient.hasValue()) {
				if(!blink) {
					//System.out.println(sensorPath + " - " + blink + " " + led.getState());
					
					Sensor sensor = sensorClient.getSensor();
					Float value = sensor.getValue();
					
					if(value != null) {			
						if(sensorClient.isValueHigherThan() && isOff()) {
							//System.out.println("acender led " + sensorPath + " - " + appConfigPath);
							turnOn();
							mCoapRes.changed();
						}
						else if(sensorClient.isValueLowerThanMax() && isOn()) {
							//System.out.println("apagar led " + sensorPath + " - " + appConfigPath);
							turnOff();
							mCoapRes.changed();
						}
					}
				}
				else {
					if(isOff())
						turnOn();
					
					//System.out.println("blinking " + led.getState());
				}
			}
		}
	}
	
	public String getLEDJson() {
		String jsonInString = gson.toJson(this.led);
		return jsonInString;
	}

	public LED getLED() {
		return led;
	}

	public void setLED(LED led) {
		this.led = led;
	}

	public boolean isBlink() {
		return blink;
	}

	public void setBlink(boolean blink) {
		this.blink = blink;
	}

	public LEDSensorClient getSensorClient() {
		return sensorClient;
	}

	public void setSensorClient(LEDSensorClient sensorClient) {
		this.sensorClient = sensorClient;
	}

	public void turnOn() {
		//System.out.println("--> GPIO state should be: ON");
		//pin.high();
		this.led.setState("HIGH");
	}

	public void turnOff() {
		//System.out.println("--> GPIO state should be: OFF");
		//pin.low();
		this.led.setState("LOW");
	}
	
	public boolean isOn() {
		//return pin.getState() == PinState.HIGH;
		return this.led.getState() != null ? this.led.getState().equalsIgnoreCase("HIGH") : false;
	}

	public boolean isOff() {
		//return pin.getState() == PinState.LOW;
		return this.led.getState() != null ? this.led.getState().equalsIgnoreCase("LOW") : true;
	}

}