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

	public PulseLEDTask(LEDResource ledResource) {
		this.mCoapRes = ledResource;
		this.temperatureLedResource = ledResource.getTemperatureLed();
		this.humidityLedResource = ledResource.getHumidityLed();
		
		this.temperatureSensorClient = this.temperatureLedResource.getSensorClient();
		this.humiditySensorClient = this.humidityLedResource.getSensorClient();
	}

	@Override
	public void run() {

		if (temperatureSensorClient.isConfigured() && humiditySensorClient.isConfigured()
				&& temperatureSensorClient.hasValue() && humiditySensorClient.hasValue()) {

			if (!temperatureLedResource.isPulsing() && !humidityLedResource.isPulsing()) {
				boolean tempValueNotOk = temperatureSensorClient.isValueNotOk();
				boolean humValueNotOk = humiditySensorClient.isValueNotOk();

				if (tempValueNotOk && humValueNotOk) {
					temperatureLedResource.setPulsing(true);
					humidityLedResource.setPulsing(true);
					mCoapRes.changed();
				}
			} 
			else {
				boolean tempValueOk = temperatureSensorClient.isValueOk();
				boolean humValueOk = humiditySensorClient.isValueOk();

				if (tempValueOk || humValueOk) {
					temperatureLedResource.setPulsing(false);
					humidityLedResource.setPulsing(false);
					mCoapRes.changed();
				}
			}
		}
	}
}