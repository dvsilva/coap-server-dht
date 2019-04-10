package br.ufrj.coppe.labiot.resources.actuators;

import java.util.Timer;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.google.gson.Gson;
import com.pi4j.io.gpio.RaspiPin;

import br.ufrj.coppe.labiot.domain.EnvPropertieType;
import br.ufrj.coppe.labiot.domain.SenseLED;
import br.ufrj.coppe.labiot.resources.tasks.PulseLEDTask;

public class LEDResource extends CoapResource {

	private static final long TIME_TO_UPDATE = 1000;

	private Gson gson;
	
	private LEDPropertieResource temperatureLed;
	private LEDPropertieResource humidityLed;

	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public LEDResource(String name, String appConfigPath, String sensorsPath) {
		super(name);

		this.gson = new Gson(); // Or use new GsonBuilder().create();

		// instancia dois observables - LEDs de temperatura e umidade
		this.temperatureLed = new LEDPropertieResource(RaspiPin.GPIO_01, EnvPropertieType.TEMPERATURE.getName(), sensorsPath, appConfigPath);
		this.humidityLed = new LEDPropertieResource(RaspiPin.GPIO_04, EnvPropertieType.HUMIDITY.getName(),  sensorsPath, appConfigPath);

		// seta como observables
		this.setObservable(true);
		this.getAttributes().setObservable();

		// adiciona filhos
		add(this.temperatureLed);
		add(this.humidityLed);

		// instancia tarefa que vai controlar o estado dos LEDs quanto a piscar ou nao
		PulseLEDTask blinkLEDTask = new PulseLEDTask(this);

		// inicia e configura periodicidade
		Timer timer = new Timer();
		timer.schedule(blinkLEDTask, 0, TIME_TO_UPDATE);
	}

	/**
	 * retorna mensagem com o estado atual do LED
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		// instancia um objeto que engloba os LEDs dos dois sensores
		SenseLED senseLED = new SenseLED(this.temperatureLed.getLED(), this.humidityLed.getLED());
		// converte objeto em JSON e retorna na resposta
		String jsonInString = gson.toJson(senseLED);
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
		SenseLED senseLED = gson.fromJson(request, SenseLED.class);

		// seta os valores
		String temperatureLEDState = senseLED.getTemperatureLed().getState();
		String humidityLedState = senseLED.getHumidityLed().getState();
		update(temperatureLEDState, humidityLedState);

		// notifica que recursos foram modificados
		this.changed();

		// retorna mensagem de sucesso
		String response = getName() + " has been successful configured";
		exchange.respond(ResponseCode.CONTENT, "{ message: " + response + " }", MediaTypeRegistry.TEXT_PLAIN);
	}

	/**
	 * TEST
	 * Atualiza o estado dos LEDS
	 * 
	 * @param tempLedState
	 * @param humLedState
	 */
	public void update(String tempLedState, String humLedState) {
		this.temperatureLed.update(tempLedState);
		this.humidityLed.update(humLedState);
	}
	
	/**
	 * metodo sobrescrito para atualizar o recurso pai caso o filho seja alterado
	 */
	@Override
	public void changed() {
		super.changed();
		
		ActuatorResource parent = (ActuatorResource) this.getParent();
		parent.changed();
	}

	public LEDPropertieResource getTemperatureLed() {
		return temperatureLed;
	}

	public void setTemperatureLed(LEDPropertieResource temperatureLed) {
		this.temperatureLed = temperatureLed;
	}

	public LEDPropertieResource getHumidityLed() {
		return humidityLed;
	}

	public void setHumidityLed(LEDPropertieResource humidityLed) {
		this.humidityLed = humidityLed;
	}

	/**
	 * TEST
	 * 
	 * metodo que o pai chama para atualizar os dados do filho
	 * @param senseLED
	 */
	public void setLED(SenseLED senseLED) {
		this.temperatureLed.setLED(senseLED.getTemperatureLed());
		this.humidityLed.setLED(senseLED.getHumidityLed());		

		update(this.temperatureLed.getLED().getState(), this.humidityLed.getLED().getState());
	}

	public SenseLED getLED() {
		SenseLED senseLED = new SenseLED(this.temperatureLed.getLED(), this.humidityLed.getLED());
		return senseLED;
	}

}