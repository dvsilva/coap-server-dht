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

	public LEDConfigClient(String appConfigPath) {
		this.appConfigPath = appConfigPath;
		
		this.gson = new Gson(); // Or use new GsonBuilder().create();
		this.config = new Config();
		
		CoapClient configClient = new CoapClient(appConfigPath);
		configClient.observe(this);
	}

	@Override 
	public void onLoad(CoapResponse response) {
		String content = response.getResponseText();
		//System.out.println("CONFIGURATION RESPONSE FROM " + appConfigPath + ": " + content);
		this.config = gson.fromJson(content, Config.class);
	}

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
