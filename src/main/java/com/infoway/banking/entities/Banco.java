package com.infoway.banking.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "banco")
public class Banco extends User implements Serializable{
	
	private static final long serialVersionUID = 6877614390427038828L;
	
	private String codigo;
	private String nome;
//	private List<Conta> contas;
	private List<Agencia> agencias;
	
	public Banco() {
//		this.contas = new ArrayList<Conta>();
		this.agencias = new ArrayList<Agencia>();
	}
	
	@Column(name = "codigo", unique = true , nullable = false, length = 3)
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "nome", nullable = false, length = 50)
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
//	@OneToMany(mappedBy = "banco", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	public List<Conta> getContas() {
//		return contas;
//	}
//	public void setContas(List<Conta> contas) {
//		this.contas = contas;
//	}
	
	@OneToMany(mappedBy = "banco", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	public List<Agencia> getAgencias() {
		return agencias;
	}
	public void setAgencias(List<Agencia> agencias) {
		this.agencias = agencias;
	}

	@Override
	public String toString() {
		return "Banco [codigo=" + codigo + ", nome=" + nome + "]";
	}

}
