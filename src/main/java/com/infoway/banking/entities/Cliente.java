package com.infoway.banking.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {
	
	private static final long serialVersionUID = -6305361764541275910L;
	
	private String cpf;
	private String nome;
	private List<Conta> contas;
	
	public Cliente() {}

	@Id
	@Column(name = "cpf", nullable = false)
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	@Column(name = "nome", nullable = false)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	@OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public List<Conta> getContas() {
		return contas;
	}
	public void setContas(List<Conta> contas) {
		this.contas = contas;
	}

	@Override
	public String toString() {
		return "Cliente [cpf=" + cpf + ", nome=" + nome + "]";
	}
	
}
