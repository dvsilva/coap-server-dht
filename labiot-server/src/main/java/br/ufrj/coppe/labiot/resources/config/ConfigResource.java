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

	public ConfigResource(String name) {
		super(name);
		
		this.gson = new Gson(); // Or use new GsonBuilder().create();
		this.config = new Config();
		
		this.setObservable(true);
		this.getAttributes().setObservable();
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		String jsonInString = gson.toJson(this.config);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		this.config = gson.fromJson(request, Config.class);
		
		this.changed();
		
		String jsonInString = gson.toJson(this.config);
		String response = this.getName() + " has been configured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}

	@Override
	public void handlePUT(CoapExchange exchange) {		
		String request = exchange.getRequestText();
		this.config = gson.fromJson(request, Config.class);
		
		this.changed();

		String jsonInString = gson.toJson(this.config);
		String response = this.getName() + " has been configured to " + jsonInString;
		exchange.respond(ResponseCode.CONTENT, response, MediaTypeRegistry.TEXT_PLAIN);
	}

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