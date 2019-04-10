package br.ufrj.coppe.labiot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ScriptPython implements Runnable {

	private static final String PYTHON_SHELL = "python";
	private static final String SCRIPT_PYTHON = "/home/pi/Desktop/trabalho/AdafruitDHT.py";
	private static final String TYPE_SENSOR = "11";
	private static final String GPIO_PIN = "4";

	private String result;
	private Process mProcess;

	public ScriptPython() {
		// inicia uma thread para obter os dados
		Thread th = new Thread(this);
		th.start();
	}

	@Override
	public void run() {
		Process process;

		try {
			// inicia um processo que executa o arquivo python para obter temperatura e umidade
			process = Runtime.getRuntime().exec(new String[] { PYTHON_SHELL, SCRIPT_PYTHON, TYPE_SENSOR, GPIO_PIN });
			mProcess = process;
		} catch (Exception e) {
			System.out.println("Exception Raised" + e.toString());
		}

		// obtem input do processo
		InputStream stdout = mProcess.getInputStream();
		// inicia um buffer para ler os valores retornados
		BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));

		String line;

		try {
			// quando algum valor e lido altera o valor da variavel resultado
			while ((line = reader.readLine()) != null) {
				this.result = line;
				//System.out.println("RESULT ===== " + this.result);
			}
		} 
		catch (IOException e) {
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