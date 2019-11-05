package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;

import com.infoway.banking.entities.Agencia;

public class AgenciaDto {
	
	private String cnpj;
	private String numero;
	private String codigoBanco;
	
	public AgenciaDto() {}
	
	public AgenciaDto(Agencia agencia) {
		this.cnpj = agencia.getCnpj();
		this.numero = agencia.getNumero();
		this.codigoBanco = agencia.getBanco().getCodigo();
	}

	@NotEmpty(message = "CNPJ não pode ser vazio.")
	@Length(min = 14, max = 14, message = "CNPJ deve conter 14 caracteres.")
	@CNPJ(message = "CNPJ inválido.")
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
	@NotEmpty(message = "Número não pode ser vazio.")
	@Length(min = 1, max = 5, message = "Número deve conter entre 1 e 5 caracteres.")
	@Pattern(regexp = "[\\d]*", message = "Número deve conter apenas números.")
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	@NotEmpty(message = "Código do banco não pode ser vazio.")
	public String getCodigoBanco() {
		return codigoBanco;
	}
	public void setCodigoBanco(String codigoBanco) {
		this.codigoBanco = codigoBanco;
	}

	@Override
	public String toString() {
		return "AgenciaDto [cnpj=" + cnpj + ", numero=" + numero + ", codigoBanco=" + codigoBanco + "]";
	}
	
}
