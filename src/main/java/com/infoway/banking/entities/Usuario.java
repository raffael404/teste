package com.infoway.banking.entities;

import javax.persistence.Column;

public abstract class Usuario {
	private String senha;

	@Column(name = "senha", nullable = false)
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
}
