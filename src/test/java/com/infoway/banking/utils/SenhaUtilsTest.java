package com.infoway.banking.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class SenhaUtilsTest {

	private static final String SENHA = "123456";
	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	
	@Test
	public void testCriptografar() throws Exception {
		String senhaCriptografada = SenhaUtils.criptografar(SENHA);
		assertTrue(encoder.matches(SENHA, senhaCriptografada));
	}

}
