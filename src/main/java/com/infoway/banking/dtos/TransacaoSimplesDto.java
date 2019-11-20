package com.infoway.banking.dtos;

import java.util.Locale;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.infoway.banking.entities.Transacao;
import com.infoway.banking.utils.DataUtils;

public class TransacaoSimplesDto {
	
	private Long id;
	private String data;
	private Double valor;
	private String codigoBanco;
	private String numeroConta;
	
	public TransacaoSimplesDto() {}
	
	public TransacaoSimplesDto(Transacao transacao, Locale locale) {
		this.id = transacao.getId();
		this.data = DataUtils.converterParaString(transacao.getData(), locale);
		this.valor = transacao.getValor();
		if (transacao.getOrigem() != null) {
			this.codigoBanco = transacao.getOrigem().getBanco().getCodigo();
			this.numeroConta = transacao.getOrigem().getNumero();
		} else {
			this.codigoBanco = transacao.getDestino().getBanco().getCodigo();
			this.numeroConta = transacao.getDestino().getNumero();
		}
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	@NotNull(message = "error.empty.value")
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	
	@NotEmpty(message = "error.empty.bank.code")
	public String getCodigoBanco() {
		return codigoBanco;
	}
	public void setCodigoBanco(String codigoBanco) {
		this.codigoBanco = codigoBanco;
	}
	
	@NotEmpty(message = "error.empty.account.number")
	public String getNumeroConta() {
		return numeroConta;
	}
	public void setNumeroConta(String numeroConta) {
		this.numeroConta = numeroConta;
	}

	@Override
	public String toString() {
		return "TransacaoSimplesDto [id=" + id + ", data=" + data + ", valor=" + valor + ", codigoBanco=" + codigoBanco
				+ ", numeroConta=" + numeroConta + "]";
	}
	
}
