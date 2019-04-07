package br.ufrj.coppe.labiot.domain;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class LED {
	
	private transient Pin gpio;

	private transient GpioPinDigitalOutput pin;
	
	private String name;
	private String port;

	private boolean pulsing;
	
	@SuppressWarnings("unused")
	private String state;

	public LED(String name, Pin gpio) {
		this.gpio = gpio;

		// create gpio controller
		GpioController gpioController = GpioFactory.getInstance();

		// provision gpio pin #01 as an output pin and turn on
		this.pin = gpioController.provisionDigitalOutputPin(gpio, name, PinState.LOW);

		// set shutdown state for this pin
		this.pin.setShutdownOptions(true, PinState.LOW);
		
		this.name = name;
		this.setPort(gpio.getName()); 
		this.state = pin.getState().toString();
	}

	public Pin getGpio() {
		return gpio;
	}

	public void setGpio(Pin gpio) {
		this.gpio = gpio;
	}

	public GpioPinDigitalOutput getPin() {
		return pin;
	}

	public void setPin(GpioPinDigitalOutput pin) {
		this.pin = pin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void turnOn() {
		//System.out.println("--> GPIO state should be: ON");
		pin.high();
		this.setState();
	}

	public void turnOff() {
		//System.out.println("--> GPIO state should be: OFF");
		pin.low();
		this.setState();
	}
	
	public void pulse(int value) {
		// set second argument to 'true' use a blocking call
		pin.pulse(value, true);
		this.setState();
	}

	public void toggle() {
		this.pin.toggle();
		this.setState();
	}
	
	public void blink(int value) {
		pin.blink(value); 
		this.setState();
	}

	public String getState() {
		return this.state;
	}

	public void setState() {
		this.state = pin.getState().toString();
	}
	
	public boolean isOn() {
		return pin.getState() == PinState.HIGH;
	}

	public boolean isOff() {
		return pin.getState() == PinState.LOW;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void update(String newState) {
		if (newState.equalsIgnoreCase(PinState.HIGH.getName()))
			pin.high();
		else if (newState.equalsIgnoreCase(PinState.LOW.getName()))
			pin.low();
		
		setState();
	}

	public boolean isPulsing() {
		return pulsing;
	}

	public void setPulsing(boolean pulsing) {
		this.pulsing = pulsing;
	}

}