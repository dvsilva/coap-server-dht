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

	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public ActuatorResource(String name, String ledName, String appConfigPath, String sensorsPath) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		// instancia objeto responsavel pelo gerenciamento dos LEDs
		this.ledResource = new LEDResource(ledName, appConfigPath, sensorsPath);

		// adiciona filho
		add(this.ledResource);

		// seta como observables
		this.setObservable(true);
		this.getAttributes().setObservable();
	}
	
	/**
	 * retorna mensagem com o estado atual dos atuadores
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		// instancia um objeto que retorna estado dos atuadores
		Actuator actLed = new Actuator(this.ledResource.getLED());
		// converte objeto em JSON e retorna na resposta
		String jsonInString = gson.toJson(actLed);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	/**
	 * TEST
	 * altera estado atual dos atuadores
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		
		// converte JSON recebido em objeto
		Actuator actuatorLED = gson.fromJson(request, Actuator.class);

		// seta os valores
		String temperatureLEDState = actuatorLED.getSenseLED().getTemperatureLed().getState();
		String humidityLedState = actuatorLED.getSenseLED().getHumidityLed().getState();
		this.ledResource.update(temperatureLEDState, humidityLedState);

		// notifica que o recurso foi modificado
		this.changed();

		// retorna mensagem de sucesso
		String response = getName() + " has been successful configured";
		exchange.respond(ResponseCode.CONTENT, "{ message: " + response + " }", MediaTypeRegistry.TEXT_PLAIN);
	}

}