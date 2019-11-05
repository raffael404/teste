package com.infoway.banking.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "conta")
public class Conta implements Serializable {
	
	private static final long serialVersionUID = 9042512513705722979L;
	
	private Long id;
	private String numero;
	private String senha;
	private double saldo;
	private Banco banco;
	private Cliente cliente;
	private List<Transacao> transacoes;
	
	public Conta() {}
	
	public boolean sacar(double valor) {
		if (valor <= 0 || valor > this.saldo)
			return false;
		this.saldo -= valor;
		return true;
	}
	
	public boolean depositar(double valor) {
		if (valor <= 0)
			return false;
		this.saldo += valor;
		return true;
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
	public double getSaldo() {
		return saldo;
	}
	public void setSaldo(double saldo) {
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
	
	@OneToMany(mappedBy = "origem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public List<Transacao> getTransacoes() {
		return transacoes;
	}
	public void setTransacoes(List<Transacao> transacoes) {
		this.transacoes = transacoes;
	}
	
	@Override
	public String toString() {
		return "Conta [id=" + id + ", numero=" + numero + ", senha=" + senha + ", saldo=" + saldo + ", banco=" + banco
				+ ", cliente=" + cliente + "]";
	}
	
}
