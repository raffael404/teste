package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.infoway.banking.entities.Banco;

public class BancoDto {
	
	private String codigo;
	private String nome;
	
	public BancoDto() {}
		
	public BancoDto(Banco banco) {
		this.codigo = banco.getCodigo();
		this.nome = banco.getNome();
	}

	@NotEmpty(message = "Código não pode ser vazio.")
	@Length(min = 3, max = 3, message = "Código deve conter 3 caracteres.")
	@Pattern(regexp = "[\\d]*", message = "Código deve conter apenas números.")
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@NotEmpty(message = "Nome não pode ser vazio.")
	@Length(min = 1, max = 50, message = "Nome deve conter entre 1 e 50 caracteres.")
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
