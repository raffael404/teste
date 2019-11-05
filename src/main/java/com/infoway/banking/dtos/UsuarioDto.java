package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;

public abstract class UsuarioDto {
	
	private String senha;

	@NotEmpty(message = "Senha não pode ser vazia.")
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
}
