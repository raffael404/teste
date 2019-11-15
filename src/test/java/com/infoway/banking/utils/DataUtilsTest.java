package com.infoway.banking.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DataUtilsTest {

	@Test
	void testConverterParaString() {
		Date data = criarData();
		assertEquals(DataUtils.converterParaString(data, new Locale("pt", "BR")), "20/06/1971 06:00:00");
	}
	
	@Test
	void testConverterParaData() throws ParseException {
		Date data = criarData();
		assertEquals(
				DataUtils.converterParaData("20/06/1971 06:00:00", new Locale("pt", "BR")).toString(), data.toString());
	}
	
	private Date criarData() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(1971, 5, 20, 6, 0, 0);
		return calendar.getTime();
	}

}
