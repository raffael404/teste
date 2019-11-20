package com.infoway.banking.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "agencia")
public class Agencia implements Serializable {

	private static final long serialVersionUID = 8363672344428972513L;
	
	private String cnpj;
	private String numero;
	private Banco banco;

	public Agencia() {}
	
	@Id
	@Column(name = "cnpj", nullable = false, length = 14)
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
	@Column(name = "numero", nullable = false, length = 5)
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	public Banco getBanco() {
		return banco;
	}
	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	@Override
	public String toString() {
		return "Agencia [cnpj=" + cnpj + ", numero=" + numero + ", banco=" + super.toString() + "]";
	}
	
}
