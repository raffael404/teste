package com.infoway.banking.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtils {
	
	public static String converterParaString(Date date) {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);
	}
	
	public static Date converterParaData(String string) throws ParseException {
		return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(string);
	}
	
}
