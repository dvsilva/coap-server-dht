package br.ufrj.coppe.labiot.resources.actuators;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import com.google.gson.Gson;

import br.ufrj.coppe.labiot.domain.Config;

public class LEDConfigClient implements CoapHandler {

	private Gson gson;
	
	private String appConfigPath;
	private Config config;

	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public LEDConfigClient(String appConfigPath) {
		this.appConfigPath = appConfigPath;
		
		this.gson = new Gson(); // Or use new GsonBuilder().create();
		this.config = new Config();

		// instancia um cliente para obter valores das configuracoes
		CoapClient configClient = new CoapClient(appConfigPath);
		configClient.observe(this);
	}
	
	/**
	 * metodo chamado quando o recurso requerido for atualizado
	 */
	@Override 
	public void onLoad(CoapResponse response) {
		// converte objeto atual para JSON
		String content = response.getResponseText();
		//System.out.println("CONFIGURATION RESPONSE FROM " + appConfigPath + ": " + content);
		// retorna na resposta
		this.config = gson.fromJson(content, Config.class);
	}

	/**
	 * metodo chamado quando ocorre um erro
	 */
	@Override
	public void onError() {
		System.out.println("ERROR " + this.appConfigPath);
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
	
}