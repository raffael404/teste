package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.infoway.banking.entities.Conta;

public class ContaDto {
	
	private Long id;
	private String numero;
	private String senha;
	private double saldo;
	private String codigoBanco;
	private String cpfCliente;
	
	public ContaDto() {}
	
	public ContaDto(Conta conta) {
		this.id = conta.getId();
		this.numero = conta.getNumero();
		this.senha = conta.getSenha();
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

	@NotEmpty(message = "Número não pode ser vazio.")
	@Length(min = 1, max = 12, message = "Número deve conter entre 1 e 12 caracteres.")
	@Pattern(regexp = "[\\d]*", message = "Número deve conter apenas números.")
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	@NotEmpty(message = "Senha não pode ser vazia.")
	@Length(min = 1, max = 255, message = "Senha deve conter entre 1 e 255 caracteres.")
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public double getSaldo() {
		return saldo;
	}
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	
	@NotEmpty(message = "Código do banco não pode ser vazio.")
	public String getCodigoBanco() {
		return codigoBanco;
	}
	public void setCodigoBanco(String codigoBanco) {
		this.codigoBanco = codigoBanco;
	}
	
	@NotEmpty(message = "CPF do cliente não pode ser vazio.")
	public String getCpfCliente() {
		return cpfCliente;
	}
	public void setCpfCliente(String cpfCliente) {
		this.cpfCliente = cpfCliente;
	}

	@Override
	public String toString() {
		return "ContaDto [id=" + id + ", numero=" + numero + ", senha=" + senha + ", saldo=" + saldo + ", codigoBanco="
				+ codigoBanco + ", cpfCliente=" + cpfCliente + "]";
	}
	
}
