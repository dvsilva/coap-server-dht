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
	
	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public LEDPropertieResource(Pin gpio, String propName, String sensorPath, String appConfigPath) {
		super(propName);
		
		this.gson = new Gson(); // Or use new GsonBuilder().create();
		this.led = new LED(propName, gpio);
		
		sensorPath += "/" + propName;
		appConfigPath += "/" + propName;
		
		// instancia um cliente para obter valores dos sensores
		this.sensorClient = new LEDSensorClient(sensorPath, appConfigPath);

		// instancia tarefa que vai controlar o estado dos LEDs quanto ligar ou nao
		TurnOnLEDTask turnOnTask = new TurnOnLEDTask(this);

		// inicia e configura periodicidade
		Timer timer = new Timer();
		timer.schedule(turnOnTask, 0, TIME_TO_UPDATE);
	}

	/**
	 * retorna mensagem com o estado atual do LED
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		// converte objeto atual para JSON
		String jsonInString = gson.toJson(this.led);
		// retorna na resposta
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}
	
	/**
	 * TEST
	 * altera estado atual do LED
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		
		// converte JSON recebido em objeto
		LED led = gson.fromJson(request, LED.class);

		// seta os valores
		update(led.getState());

		// notifica que o recurso foi modificado
		this.changed();

		// retorna mensagem de sucesso
		String response = getName() + " has been successful configured";
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}
	
	/**
	 * metodo sobrescrito para atualizar o recurso pai caso o filho seja alterado
	 */
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