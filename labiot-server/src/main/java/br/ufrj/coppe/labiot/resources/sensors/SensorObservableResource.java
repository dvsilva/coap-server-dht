package br.ufrj.coppe.labiot.resources.sensors;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.DHTSensor;
import br.ufrj.coppe.labiot.domain.Sensor;
import br.ufrj.coppe.labiot.util.DataUtil;

public class SensorObservableResource extends CoapResource {

	private static final long TIME_TO_UPDATE = 2000;
	
	private Sensor sensor;
	private Gson gson;

	public SensorObservableResource(String name) {
		super(name);
		
		this.gson = new Gson(); // Or use new GsonBuilder().create();
		this.sensor = new Sensor();
		
		this.setObservable(true);
		this.getAttributes().setObservable();
		
		//Timer timer = new Timer();
		//timer.schedule(new UpdateTask(this), 0, TIME_TO_UPDATE);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		String jsonInString = gson.toJson(this.sensor);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		
		this.sensor = gson.fromJson(request, Sensor.class);
		
		this.changed();
		
		String jsonInString = gson.toJson(this.sensor);
		String response = getName() + " has been configured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}
	
	private class UpdateTask extends TimerTask {
		private CoapResource mCoapRes;

		public UpdateTask(CoapResource coapRes) {
			mCoapRes = coapRes;
		}

		@Override
		public void run() {
			Random rand = new Random();
			Float value = rand.nextFloat();
			//Float value = 40.0f;
			
			sensor.setValue(value);
			sensor.setTimestamp(DataUtil.getFormattedTimestamp());
			
			//System.out.println(gson.toJson(sensor));
			mCoapRes.changed();
		}
	}

	public String getSensorJson() {
		String jsonInString = gson.toJson(this.sensor);
		return jsonInString;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	
}