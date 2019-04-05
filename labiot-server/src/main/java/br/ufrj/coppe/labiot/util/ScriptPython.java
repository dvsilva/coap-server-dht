package br.ufrj.coppe.labiot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ScriptPython implements Runnable {

	private static final String SCRIPT_PYTHON = "/home/pi/Desktop/trabalho/AdafruitDHT.py";
	private static final String TYPE_SENSOR = "11";
	private static final String GPIO_PIN = "4";

	private String result;
	private Process mProcess;

	public ScriptPython() {
		Thread th = new Thread(this);
		th.start();
	}

	@Override
	public void run() {
		Process process;

		try {
			process = Runtime.getRuntime().exec(new String[] { SCRIPT_PYTHON, TYPE_SENSOR, GPIO_PIN });
			mProcess = process;
		} catch (Exception e) {
			System.out.println("Exception Raised" + e.toString());
		}

		InputStream stdout = mProcess.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));

		String line;

		try {
			while ((line = reader.readLine()) != null) {
				this.result = line;
				//System.out.println("RESULT ===== " + this.result);
			}
		} catch (IOException e) {
			System.out.println("Exception in reading output" + e.toString());
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}