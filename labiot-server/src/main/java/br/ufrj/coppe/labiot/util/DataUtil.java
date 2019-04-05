package br.ufrj.coppe.labiot.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {

	public static String getFormattedTimestamp() {
		Date now = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(now);
	}
}
