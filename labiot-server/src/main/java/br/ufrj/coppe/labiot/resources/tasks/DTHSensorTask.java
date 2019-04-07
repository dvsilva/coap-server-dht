package br.ufrj.coppe.labiot.resources.tasks;

import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;

import br.ufrj.coppe.labiot.domain.EnvPropertieType;
import br.ufrj.coppe.labiot.domain.Sensor;
import br.ufrj.coppe.labiot.util.ScriptPython;

public class DTHSensorTask extends TimerTask {

	private CoapResource mCoapRes;

	private ScriptPython script;
	private Sensor sensor;

	public DTHSensorTask(CoapResource coapRes, Sensor sensor) {
		this.mCoapRes = coapRes;
		this.sensor = sensor;
		
		this.script = new ScriptPython();
	}

	@Override
	public void run() {
		 //Random rand = new Random(); Float value = rand.nextFloat();

		String result = script.getResult();

		if (result != null && !result.equalsIgnoreCase("")) {
			Float value = 0.0f;
			
			//System.out.println(result);
			String resourceName = mCoapRes.getName();
			
			if (resourceName.equalsIgnoreCase(EnvPropertieType.TEMPERATURE.getName())) 
				value = getTemperature(result);
			else if (resourceName.equalsIgnoreCase(EnvPropertieType.HUMIDITY.getName())) 
				value = getHumidity(result);

			sensor.setValue(value);
			//sensor.setTimestamp(DataUtil.getFormattedTimestamp());
			sensor.setTimestamp(getTimestamp(result));
			
			mCoapRes.changed();
		}

		// System.out.println(gson.toJson(sensor));
	}

	public Float getTemperature(String result) {
		if (result == null)
			return 0f;

		String formatedFields = result.replaceAll("\\{", "").replaceAll("\\}", "");
		String[] fields = formatedFields.split(";");
		String[] temperatureWithLabel = fields[0].split(":");
		String temperatureValue = temperatureWithLabel[1].trim();

		return Float.parseFloat(temperatureValue);
	}

	public Float getHumidity(String result) {
		if (result == null)
			return 0f;

		String formatedFields = result.replaceAll("\\{", "").replaceAll("\\}", "");
		String[] fields = formatedFields.split(";");
		String[] humidityWithLabel = fields[1].split(":");
		String humidityValue = humidityWithLabel[1].trim().replaceAll("%", "");
		return Float.parseFloat(humidityValue);
	}
	
	public String getTimestamp(String result) {
		if (result == null)
			return "";

		String formatedFields = result.replaceAll("\\{", "").replaceAll("\\}", "");
		String[] fields = formatedFields.split(";");
		String[] timestampWithLabel = fields[2].split(":");
		String timestampValue = timestampWithLabel[1].trim().replaceAll("%", "");
		return timestampValue;
	}
}