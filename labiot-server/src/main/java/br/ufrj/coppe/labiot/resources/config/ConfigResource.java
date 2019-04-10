package br.ufrj.coppe.labiot.resources.config;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.Config;

public class ConfigResource extends CoapResource {

	private Config config;
	private Gson gson;

	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public ConfigResource(String name) {
		super(name);
		
		this.gson = new Gson(); // Or use new GsonBuilder().create();
		this.config = new Config();

		// seta sensor como observable
		this.setObservable(true);
		this.getAttributes().setObservable();
	}

	/**
	 * retorna mensagem com a configuração atual
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		// converte objeto atual para JSON
		String jsonInString = gson.toJson(this.config);
		// retorna na resposta
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	/**
	 * salva a configuração solicitada
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();

		// converte JSON recebido em objeto
		this.config = gson.fromJson(request, Config.class);

		// notifica que o recurso foi modificado
		this.changed();

		// retorna mensagem de sucesso
		String response = getName() + " has been successful configured";
		exchange.respond(ResponseCode.CONTENT, "{ message: " + response + " }", MediaTypeRegistry.TEXT_PLAIN);
	}

	/**
	 * altera a configuração solicitada
	 */
	@Override
	public void handlePUT(CoapExchange exchange) {		
		String request = exchange.getRequestText();
		
		// converte JSON recebido em objeto
		this.config = gson.fromJson(request, Config.class);

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
		
		AppConfigResource parent = (AppConfigResource) this.getParent();
		parent.changed();
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
	
}