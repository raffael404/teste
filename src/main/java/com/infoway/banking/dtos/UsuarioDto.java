package com.infoway.banking.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

public abstract class UsuarioDto {
	
	private String nomeUsuario;
	private String email;
	private String senha;
	
	public UsuarioDto() {}

	@NotEmpty(message = "error.empty.username")
	@Length(min = 2, max = 100, message = "error.size.username")
	public String getNomeUsuario() {
		return nomeUsuario;
	}
	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	@NotEmpty(message = "error.empty.email")
	@Length(min = 10, max = 100, message = "error.size.email")
	@Email(message = "error.invalid.email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	@NotEmpty(message = "error.empty.password")
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public String toString() {
		return "NovoUsuarioDto [nomeUsuario=" + nomeUsuario + ", email=" + email + ", senha=" + senha + "]";
	}
	
}
