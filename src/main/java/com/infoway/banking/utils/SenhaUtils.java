package com.infoway.banking.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SenhaUtils {
	/**
	 * Gera um hash utilizando o BCrypt.
	 * 
	 * @param senha
	 * @return String
	 */
	public static String criptografar(String senha) {
		if (senha == null)
			return senha;
		return new BCryptPasswordEncoder().encode(senha);
	}
	
	/**
	 * Verifica se a senha é válida. 
	 * 
	 * @param senha
	 * @param senhaCriptografada
	 * @return boolean
	 */
	public static boolean verificarValidade(String senha, String senhaCriptografada) {
		return new BCryptPasswordEncoder().matches(senha, senhaCriptografada);
	}
}
