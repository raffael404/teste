package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.infoway.banking.entities.Banco;

public class BancoDto extends UsuarioDto {
	
	private String codigo;
	private String nomeBanco;
	
	public BancoDto() {}
	
	public BancoDto(Banco banco) {
		this.codigo = banco.getCodigo();
		this.nomeBanco = banco.getNome();
		this.setEmail(banco.getEmail());
		this.setNomeUsuario(banco.getUsername());
		this.setSenha(banco.getPassword());
	}

	@NotEmpty(message = "error.empty.code")
	@Length(min = 3, max = 3, message = "error.size.code")
	@Pattern(regexp = "[\\d]*", message = "error.invalid.code")
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@NotEmpty(message = "error.empty.bank.name")
	@Length(min = 1, max = 50, message = "error.size.bank.name")
	public String getNomeBanco() {
		return nomeBanco;
	}
	public void setNomeBanco(String nomeBanco) {
		this.nomeBanco = nomeBanco;
	}

	@Override
	public String toString() {
		return "NovoBancoDto [codigo=" + codigo + ", nomeBanco=" + nomeBanco + ", usuario=" + super.toString() + "]";
	}
	
}
