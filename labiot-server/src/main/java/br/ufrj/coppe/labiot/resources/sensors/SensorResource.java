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
	
	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public SensorResource(String name) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		// instancia dois observables que obtem a temperatura e umidade
		this.temperature = new DTHObservableResource(EnvPropertieType.TEMPERATURE.getName());
		this.humidity = new DTHObservableResource(EnvPropertieType.HUMIDITY.getName());
		
		// seta sensors como observables
		this.setObservable(true);
		this.getAttributes().setObservable();
		
		// adiciona filhos
		add(this.temperature);
		add(this.humidity);
	}

	/**
	 * retorna mensagem com o estado atual dos sensores
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		// instancia um objeto que engloba os dois sensores
		DHTSensor dht = new DHTSensor(this.temperature.getSensor(), this.humidity.getSensor(), DataUtil.getFormattedTimestamp());
		// converte objeto em JSON e retorna na resposta
		String jsonInString = gson.toJson(dht);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	/**
	 * TEST
	 * altera estado atual dos sensores
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		
		// converte JSON recebido em objeto
		DHTSensor dht = gson.fromJson(request, DHTSensor.class);
		
		// seta os valores
		this.temperature.setSensor(dht.getTemperature());
		this.humidity.setSensor(dht.getHumidity());

		// notifica que recursos foram modificados
		this.temperature.changed();
		this.humidity.changed();
		
		this.changed();
		
		// retorna mensagem de sucesso
		String response = getName() + " has been successful configured";
		exchange.respond(ResponseCode.CONTENT, "{ message: " + response + " }", MediaTypeRegistry.TEXT_PLAIN);
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