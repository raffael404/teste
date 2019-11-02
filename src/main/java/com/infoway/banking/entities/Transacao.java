package com.infoway.banking.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.infoway.banking.enums.TipoTransacao;

@Entity
@Table(name = "transacao")
public class Transacao implements Serializable {

	private static final long serialVersionUID = 8796347154862150635L;
	
	private Long id;
	private Date data;
	private TipoTransacao tipo;
	private Double valor;
	private Conta origem;
	private Conta destino;
	
	public Transacao() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo", nullable = false)
	public TipoTransacao getTipo() {
		return tipo;
	}
	public void setTipo(TipoTransacao tipo) {
		this.tipo = tipo;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	public Conta getOrigem() {
		return origem;
	}
	public void setOrigem(Conta origem) {
		this.origem = origem;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	public Conta getDestino() {
		return destino;
	}
	public void setDestino(Conta destino) {
		this.destino = destino;
	}

	@Column(name = "valor", nullable = false)
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	
	@PrePersist
	public void prePersist() {
		this.data = new Date();
	}
	
	@Override
	public String toString() {
		return "Transacao [id=" + id + ", data=" + data + ", tipo=" + tipo + ", valor=" + valor + ", origem=" + origem
				+ ", destino=" + destino + "]";
	}
	
}
