package com.infoway.banking.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataUtils {
	
	public static String converterParaString(Date date, Locale locale) {
		return new SimpleDateFormat(formatoData(locale)).format(date);
	}
	
	public static Date converterParaData(String string, Locale locale) throws ParseException {
		return new SimpleDateFormat(formatoData(locale)).parse(string);
	}
	
	private static String formatoData (Locale locale) {
		if (locale.equals(Locale.ENGLISH))
			return "MM/dd/yyyy HH:mm:ss";
		else
			return "dd/MM/yyyy HH:mm:ss";
	}
	
}
