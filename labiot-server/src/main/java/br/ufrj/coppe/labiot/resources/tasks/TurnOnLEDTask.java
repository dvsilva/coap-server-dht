package br.ufrj.coppe.labiot.resources.tasks;

import java.util.TimerTask;

import br.ufrj.coppe.labiot.domain.LED;
import br.ufrj.coppe.labiot.domain.Sensor;
import br.ufrj.coppe.labiot.resources.actuators.LEDPropertieResource;
import br.ufrj.coppe.labiot.resources.actuators.LEDSensorClient;

public class TurnOnLEDTask extends TimerTask {

	private LEDPropertieResource ledResource;
	
	private LEDSensorClient sensorClient;
	private LED led;

	public TurnOnLEDTask(LEDPropertieResource ledResource) {
		this.ledResource = ledResource;
		this.sensorClient = ledResource.getSensorClient();
		this.led = ledResource.getLED();
		this.led.setState();
	}

	@Override
	public void run() {

		if (sensorClient.isConfigured() && sensorClient.hasValue()) {
			if (!ledResource.isPulsing()) {
				// System.out.println(sensorPath + " - " + blink + " " + led.getState());

				Sensor sensor = sensorClient.getSensor();
				Float value = sensor.getValue();

				if (value != null) {
					if (sensorClient.isValueHigherThan() && led.isOff()) {
						// System.out.println("acender led " + sensorPath + " - " + appConfigPath);
						led.turnOn();
						ledResource.changed();
					} 
					else if (sensorClient.isValueLowerThanMax() && led.isOn()) {
						// System.out.println("apagar led " + sensorPath + " - " + appConfigPath);
						led.turnOff();
						ledResource.changed();
					}
				}
			} 
			else {
				led.toggle();
			}
		}
	}

}
