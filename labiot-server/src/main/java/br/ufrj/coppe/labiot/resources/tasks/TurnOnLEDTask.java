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

	/**
	 * metodo constutor da classe
	 * 
	 * @param name - nome do recurso
	 */
	public TurnOnLEDTask(LEDPropertieResource ledResource) {
		this.ledResource = ledResource;
		this.sensorClient = ledResource.getSensorClient();
		this.led = ledResource.getLED();
		this.led.setState();
	}

	/**
	 * metodo que executa a tarefa
	 * 
	 */
	@Override
	public void run() {

		// se esta configurado e tem valor lido
		if (sensorClient.isConfigured() && sensorClient.hasValue()) {
			
			// se nao estiver configurado para piscar
			if (!ledResource.isPulsing()) {
				// System.out.println(sensorPath + " - " + blink + " " + led.getState());

				Sensor sensor = sensorClient.getSensor();
				Float value = sensor.getValue();

				if (value != null) {
					// se valor estiver acima do configurado e estiver desligado, liga o LED
					if (sensorClient.isValueHigherThan() && led.isOff()) {
						// System.out.println("acender led " + sensorPath + " - " + appConfigPath);
						led.turnOn();
						ledResource.changed();
					} 
					// se valor estiver aceitavel do configurado e estiver ligado, desliga o LED
					else if (sensorClient.isValueLowerThanMax() && led.isOn()) {
						// System.out.println("apagar led " + sensorPath + " - " + appConfigPath);
						led.turnOff();
						ledResource.changed();
					}
				}
			} 
			else {
				// se deve piscar modifica o status do LED atual
				led.toggle();
			}
		}
	}

}
