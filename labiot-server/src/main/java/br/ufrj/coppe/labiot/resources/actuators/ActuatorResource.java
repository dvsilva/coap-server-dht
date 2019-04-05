package br.ufrj.coppe.labiot.resources.actuators;


import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.SenseLED;

public class ActuatorResource extends CoapResource {

	private static final long TIME_TO_UPDATE = 1000;
	
	private static final String RESOURCE_TEMP_NAME = "temperature";
	private static final String RESOURCE_HUM_NAME = "humidity";

	public static final String RESOURCE_LED_PATH = "/led/";

	private static final String TEMPERATURE_LED_PATH = RESOURCE_LED_PATH + RESOURCE_TEMP_NAME;
	private static final String HUMIDITY_LED_PATH = RESOURCE_LED_PATH+ RESOURCE_HUM_NAME;
	
	private Gson gson;
	
	private LEDResource temperatureLed;
	private LEDResource humidityLed;

	public ActuatorResource(String name, String appConfigPath, String sensorsPath) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		String sensorTempPath = sensorsPath + "/" + RESOURCE_TEMP_NAME;
		String appConfTempPath = appConfigPath + "/" + RESOURCE_TEMP_NAME;
		this.temperatureLed = new LEDResource(TEMPERATURE_LED_PATH, sensorTempPath, appConfTempPath);
		
		String sensorHumPath = sensorsPath + "/" + RESOURCE_HUM_NAME;
		String appConfHumPath = appConfigPath + "/" + RESOURCE_HUM_NAME;
		this.humidityLed = new LEDResource(HUMIDITY_LED_PATH, sensorHumPath, appConfHumPath);
		
		this.setObservable(true);
		this.getAttributes().setObservable();
		
		Timer timer = new Timer();
		timer.schedule(new BlinkLEDTask(this), 0, TIME_TO_UPDATE);
	}
	
	@Override
	public void handleGET(CoapExchange exchange) {
		SenseLED dht = new SenseLED(this.temperatureLed.getLED(), this.humidityLed.getLED());
		String jsonInString = gson.toJson(dht);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		SenseLED senseLED = gson.fromJson(request, SenseLED.class);
		
		this.temperatureLed.setLED(senseLED.getTemperatureLed());
		this.humidityLed.setLED(senseLED.getHumidityLed());
		
		this.changed();
		
		String jsonInString = gson.toJson(senseLED);
		String response = getName() + " has been configured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}
	
	private class BlinkLEDTask extends TimerTask {
		
		private CoapResource mCoapRes;

		public BlinkLEDTask(CoapResource coapRes) {
			mCoapRes = coapRes;
		}

		@Override
		public void run() {
			
			if(temperatureLed.getSensorClient().isConfigured() && humidityLed.getSensorClient().isConfigured()
					&& temperatureLed.getSensorClient().hasValue() && humidityLed.getSensorClient().hasValue()) {
				
				if(!temperatureLed.isBlink() && !humidityLed.isBlink()) {
					boolean tempValueNotOk = temperatureLed.getSensorClient().isValueNotOk();
					boolean humValueNotOk = humidityLed.getSensorClient().isValueNotOk();
					
					if(tempValueNotOk && humValueNotOk) {
						temperatureLed.setBlink(true);
						humidityLed.setBlink(true);
						mCoapRes.changed();
					}
				}
				else {
					boolean tempValueOk = temperatureLed.getSensorClient().isValueOk();
					boolean humValueOk = humidityLed.getSensorClient().isValueOk();
					
					if(tempValueOk || humValueOk) {
						temperatureLed.setBlink(false);
						humidityLed.setBlink(false);
						mCoapRes.changed();
					}
				}
			}
		}
	}
	
	@Override
	public Resource getChild(String name) {
		Resource res = null;
		
		switch (name) {
			case RESOURCE_TEMP_NAME:
				res = temperatureLed;
				break;
			case RESOURCE_HUM_NAME:
				res = humidityLed;
				break;
			default:
				res = this;
		}
		
		return res;
	}

}