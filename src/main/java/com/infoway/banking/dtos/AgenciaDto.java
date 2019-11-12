package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CNPJ;

import com.infoway.banking.entities.Agencia;

public class AgenciaDto extends DadoProtegido {
	
	private String cnpj;
	private String numero;
	private String codigoBanco;
	
	public AgenciaDto() {}
	
	public AgenciaDto(Agencia agencia) {
		this.cnpj = agencia.getCnpj();
		this.numero = agencia.getNumero();
		this.codigoBanco = agencia.getBanco().getCodigo();
	}

	@NotEmpty(message = "error.empty.cnpj")
	@Length(min = 14, max = 14, message = "error.size.cnpj")
	@CNPJ(message = "error.invalid.cnpj")
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
	@NotEmpty(message = "error.empty.number")
	@Length(min = 1, max = 5, message = "error.size.branch.number")
	@Pattern(regexp = "[\\d]*", message = "error.invalid.number")
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	@NotEmpty(message = "error.empty.bank.code")
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
