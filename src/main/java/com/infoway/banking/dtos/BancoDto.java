package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.infoway.banking.entities.Banco;

public class BancoDto extends UsuarioDto {
		
	private String codigo;
	private String nome;
	
	public BancoDto() {}
		
	public BancoDto(Banco banco) {
		this.codigo = banco.getCodigo();
		this.nome = banco.getNome();
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
	
	@NotEmpty(message = "error.empty.name")
	@Length(min = 1, max = 50, message = "error.size.bank.name")
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return "BancoDto [codigo=" + codigo + ", nome=" + nome + "]";
	}
	
}
