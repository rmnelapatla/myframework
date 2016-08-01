package com.adaequare.testng.adtf.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AdtfDateUtil {
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"[E] yyyy-MMM-dd hh:mm:ss a z");

	private static SimpleDateFormat randomString = new SimpleDateFormat(
			"EyyyyMMMddz");

	public static String formatAsDate(String reportFolderTime) {

		String formattedDate = formatter.format(new Date(Long
				.parseLong(reportFolderTime)));
		return formattedDate;
	}
	
	
	
	public static String randomString(String reportFolderTime) {

		String formattedDate = randomString.format(new Date(Long
				.parseLong(reportFolderTime)));
		return formattedDate;
	}
	public static String getDateString() {
		String RandomString = null;
		Calendar objCalendar = new GregorianCalendar();
		RandomString = setPrefix(objCalendar.get(Calendar.YEAR))
					 + setPrefix(objCalendar.get(Calendar.MONTH) + 1)
					 + setPrefix(objCalendar.get(Calendar.DATE))
					 + setPrefix(objCalendar.get(Calendar.HOUR_OF_DAY))
					 + setPrefix(objCalendar.get(Calendar.MINUTE))
					 + setPrefix(objCalendar.get(Calendar.SECOND))
					 + setPrefix(objCalendar.get(Calendar.MILLISECOND));

		return RandomString;
	}
	private static String setPrefix(int val){
		if(val < 10)
			return "0"+Integer.toString(val);
		else return Integer.toString(val);
	}
	
}
