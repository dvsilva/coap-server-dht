package br.ufrj.coppe.labiot.resources.sensors;

import java.util.Timer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.Sensor;
import br.ufrj.coppe.labiot.resources.tasks.DTHSensorTask;

public class DTHObservableResource extends CoapResource {

	private static final long TIME_TO_UPDATE = 2000;
	
	private Sensor sensor;
	private Gson gson;
	
	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public DTHObservableResource(String name) {
		super(name);
		
		this.gson = new Gson(); // Or use new GsonBuilder().create();
		this.sensor = new Sensor();
		
		// seta sensor como observable
		this.setObservable(true);
		this.getAttributes().setObservable();

		// instancia tarefa que retorna os dados lidos dos sensores
		DTHSensorTask dthSensorTask = new DTHSensorTask(this, this.sensor);
		
		// inicia e configura periodicidade
		Timer timer = new Timer();
		timer.schedule(dthSensorTask, 0, TIME_TO_UPDATE);
	}

	/**
	 * retorna mensagem com o estado atual do sensor
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		// converte objeto atual para JSON
		String jsonInString = gson.toJson(this.sensor);
		// retorna na resposta
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	/**
	 * TEST
	 * altera estado atual do sensor
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		
		// converte JSON recebido em objeto
		this.sensor = gson.fromJson(request, Sensor.class);

		// notifica que o recurso foi modificado
		this.changed();
		
		// retorna mensagem de sucesso
		String response = getName() + " has been successful configured";
		exchange.respond(ResponseCode.CONTENT, "{ message: " + response + " }", MediaTypeRegistry.TEXT_PLAIN);
	}
	
	/**
	 * metodo sobrescrito para atualizar o recurso pai caso o filho seja alterado
	 */
	@Override
	public void changed() {
		super.changed();
		
		SensorResource parent = (SensorResource) this.getParent();
		parent.changed();
	}
	
	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

}