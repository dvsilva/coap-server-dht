package br.ufrj.coppe.labiot.resources.tasks;

import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;

import br.ufrj.coppe.labiot.resources.actuators.LEDPropertieResource;
import br.ufrj.coppe.labiot.resources.actuators.LEDResource;
import br.ufrj.coppe.labiot.resources.actuators.LEDSensorClient;

public class PulseLEDTask extends TimerTask {

	private CoapResource mCoapRes;
	
	private LEDPropertieResource temperatureLedResource;
	private LEDPropertieResource humidityLedResource;

	private LEDSensorClient temperatureSensorClient;
	private LEDSensorClient humiditySensorClient;

	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public PulseLEDTask(LEDResource ledResource) {
		this.mCoapRes = ledResource;
		this.temperatureLedResource = ledResource.getTemperatureLed();
		this.humidityLedResource = ledResource.getHumidityLed();
		
		this.temperatureSensorClient = this.temperatureLedResource.getSensorClient();
		this.humiditySensorClient = this.humidityLedResource.getSensorClient();
	}

	/**
	 * metodo que executa a tarefa
	 * 
	 */
	@Override
	public void run() {

		// verifica se foi realizada a configuracao e se os valores foram colhidos
		if (temperatureSensorClient.isConfigured() && humiditySensorClient.isConfigured()
				&& temperatureSensorClient.hasValue() && humiditySensorClient.hasValue()) {

			// verifica se estao pulsando
			if (!temperatureLedResource.isPulsing() && !humidityLedResource.isPulsing()) {
				boolean tempValueNotOk = temperatureSensorClient.isValueNotOk();
				boolean humValueNotOk = humiditySensorClient.isValueNotOk();

				// verifica os dois valores estao abaixo ou acima
				// se estiver modifica os LEDs para piscarem
				if (tempValueNotOk && humValueNotOk) {
					temperatureLedResource.setPulsing(true);
					humidityLedResource.setPulsing(true);
					mCoapRes.changed();
				}
			} 
			else {
				boolean tempValueOk = temperatureSensorClient.isValueOk();
				boolean humValueOk = humiditySensorClient.isValueOk();
				
				// se valores estao ok desabilita o piscar dos LEDs
				if (tempValueOk || humValueOk) {
					temperatureLedResource.setPulsing(false);
					humidityLedResource.setPulsing(false);
					mCoapRes.changed();
				}
			}
		}
	}
}