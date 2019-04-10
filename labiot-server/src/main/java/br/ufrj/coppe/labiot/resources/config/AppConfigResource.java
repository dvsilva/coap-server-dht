package br.ufrj.coppe.labiot.resources.config;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.AppConfig;
import br.ufrj.coppe.labiot.domain.EnvPropertieType;

public class AppConfigResource extends CoapResource {

	private Gson gson;
	
	private ConfigResource temperatureConfig;
	private ConfigResource humidityConfig;

	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public AppConfigResource(String name) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();
		
		// instancia dois observables que obtem configuracao da temperatura e umidade
		this.temperatureConfig = new ConfigResource(EnvPropertieType.TEMPERATURE.getName());
		this.humidityConfig = new ConfigResource(EnvPropertieType.HUMIDITY.getName());

		// seta como observables
		this.setObservable(true);
		this.getAttributes().setObservable();

		// adiciona filhos
		add(this.temperatureConfig);
		add(this.humidityConfig);
	}
	
	/**
	 * retorna mensagem com a configuração atual
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		// instancia um objeto que engloba configuracao dos dois sensores
		AppConfig appConfig = new AppConfig(this.temperatureConfig.getConfig(), this.humidityConfig.getConfig());
		// converte objeto em JSON e retorna na resposta
		String jsonInString = gson.toJson(appConfig);
		exchange.respond(ResponseCode.CONTENT, jsonInString, MediaTypeRegistry.TEXT_PLAIN);
	}

	/**
	 * salva a configuração solicitada
	 */
	@Override
	public void handlePOST(CoapExchange exchange) {
		String request = exchange.getRequestText();
		
		// converte JSON recebido em objeto
		AppConfig appConfig = gson.fromJson(request, AppConfig.class);
		
		// seta os valores
		this.temperatureConfig.setConfig(appConfig.getTemperature());
		this.humidityConfig.setConfig(appConfig.getHumidity());
		
		// notifica que recursos foram modificados
		this.temperatureConfig.changed();
		this.humidityConfig.changed();
		
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
		AppConfig appConfig = gson.fromJson(request, AppConfig.class);
		
		// seta os valores
		this.temperatureConfig.setConfig(appConfig.getTemperature());
		this.humidityConfig.setConfig(appConfig.getHumidity());
		
		// notifica que recursos foram modificados
		this.temperatureConfig.changed();
		this.humidityConfig.changed();
		
		this.changed();
		
		// retorna mensagem de sucesso
		String response = getName() + " has been successful configured";
		exchange.respond(ResponseCode.CONTENT, "{ message: " + response + " }", MediaTypeRegistry.TEXT_PLAIN);
	}

	
}