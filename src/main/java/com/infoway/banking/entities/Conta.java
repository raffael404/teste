package com.infoway.banking.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.infoway.banking.exception.SaldoInsuficienteException;
import com.infoway.banking.exception.ValorInvalidoException;

@Entity
@Table(name = "conta")
public class Conta implements Serializable {
	
	private static final long serialVersionUID = 9042512513705722979L;
	
	private Long id;
	private String numero;
	private String senha;
	private Double saldo;
	private Banco banco;
	private Cliente cliente;
	
	public Conta() {
		this.saldo = 0.0;
	}
	
	public void debitar(double valor) throws ValorInvalidoException, SaldoInsuficienteException {
		if (valor <= 0)
			throw new ValorInvalidoException();
		else if (valor > this.saldo)
			throw new SaldoInsuficienteException();
		else this.saldo -= valor;
	}
	
	public void creditar(double valor) throws ValorInvalidoException {
		if (valor <= 0)
			throw new ValorInvalidoException();
		else
			this.saldo += valor;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name = "numero", nullable = false, length = 8)
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	@Column(name = "senha", nullable = false)
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	@Column(name = "saldo", nullable = false)
	public Double getSaldo() {
		return saldo;
	}
	public void setSaldo(Double saldo) {
		this.saldo = saldo;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	public Banco getBanco() {
		return banco;
	}
	public void setBanco(Banco banco) {
		this.banco = banco;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	@Override
	public String toString() {
		return "Conta [id=" + id + ", numero=" + numero + ", senha=" + senha + ", saldo=" + saldo + ", banco=" + banco
				+ ", cliente=" + cliente + "]";
	}
	
}
