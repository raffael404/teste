package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.infoway.banking.entities.Conta;

public class ContaDto extends DadoProtegido {
	
	private Long id;
	private String numero;
	private Double saldo;
	private String codigoBanco;
	private String cpfCliente;
	
	public ContaDto() {
		this.saldo = 0.0;
	}
	
	public ContaDto(Conta conta) {
		this.id = conta.getId();
		this.numero = conta.getNumero();
		this.saldo = conta.getSaldo();
		this.codigoBanco = conta.getBanco().getCodigo();
		this.cpfCliente = conta.getCliente().getCpf();
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@NotEmpty(message = "error.empty.number")
	@Length(min = 1, max = 8, message = "error.size.account.number")
	@Pattern(regexp = "[\\d]*", message = "error.invalid.number")
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	public Double getSaldo() {
		return saldo;
	}
	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}
	
	@NotEmpty(message = "error.empty.bank.code")
	public String getCodigoBanco() {
		return codigoBanco;
	}
	public void setCodigoBanco(String codigoBanco) {
		this.codigoBanco = codigoBanco;
	}
	
	@NotEmpty(message = "error.empty.client.cpf")
	public String getCpfCliente() {
		return cpfCliente;
	}
	public void setCpfCliente(String cpfCliente) {
		this.cpfCliente = cpfCliente;
	}

	@Override
	public String toString() {
		return "ContaDto [id=" + id + ", numero=" + numero + ", saldo=" + saldo + ", codigoBanco="
				+ codigoBanco + ", cpfCliente=" + cpfCliente + "]";
	}
	
}
