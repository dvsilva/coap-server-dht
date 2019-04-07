package br.ufrj.coppe.labiot.domain;

import com.google.gson.annotations.SerializedName;

public class Actuator {

	@SerializedName("led")
	private SenseLED senseLED;

	public Actuator(SenseLED senseLED) {
		this.senseLED = senseLED;
	}

	public SenseLED getSenseLED() {
		return senseLED;
	}

	public void setSenseLED(SenseLED senseLED) {
		this.senseLED = senseLED;
	}

}
