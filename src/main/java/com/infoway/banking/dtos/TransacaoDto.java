package com.infoway.banking.dtos;

import java.util.Locale;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;
import com.infoway.banking.utils.DataUtils;

public class TransacaoDto {
	
	private Long id;
	private String data;
	private Double valor;
	private TipoTransacao tipo;
	private String bancoOrigem;
	private String contaOrigem;
	private String bancoDestino;
	private String contaDestino;
	
	public TransacaoDto() {}
	
	public TransacaoDto(Transacao transacao, Locale locale) {
		this.id = transacao.getId();
		this.data = DataUtils.converterParaString(transacao.getData(), locale);
		this.valor = transacao.getValor();
		this.tipo = transacao.getTipo();
		if (transacao.getOrigem() != null) {
			this.bancoOrigem = transacao.getOrigem().getBanco().getCodigo();
			this.contaOrigem = transacao.getOrigem().getNumero();
		}
		if (transacao.getDestino() != null) {
			this.bancoDestino = transacao.getDestino().getBanco().getCodigo();
			this.contaDestino = transacao.getDestino().getNumero();
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
	
	@NotEmpty(message = "error.empty.bank.origin.code")
	public String getBancoOrigem() {
		return bancoOrigem;
	}
	public void setBancoOrigem(String bancoOrigem) {
		this.bancoOrigem = bancoOrigem;
	}
	
	@NotEmpty(message = "error.empty.account.origin.number")
	public String getContaOrigem() {
		return contaOrigem;
	}
	public void setContaOrigem(String contaOrigem) {
		this.contaOrigem = contaOrigem;
	}
	
	@NotEmpty(message = "error.empty.bank.destination.code")
	public String getBancoDestino() {
		return bancoDestino;
	}
	public void setBancoDestino(String bancoDestino) {
		this.bancoDestino = bancoDestino;
	}
	
	@NotEmpty(message = "error.empty.account.destination.number")
	public String getContaDestino() {
		return contaDestino;
	}
	public void setContaDestino(String contaDestino) {
		this.contaDestino = contaDestino;
	}

	public TipoTransacao getTipo() {
		return tipo;
	}
	public void setTipo(TipoTransacao tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return "TransacaoDto [id=" + id + ", data=" + data + ", valor=" + valor + ", tipo=" + tipo
				+ ", bancoOrigem=" + bancoOrigem + ", contaOrigem=" + contaOrigem + ", bancoDestino=" + bancoDestino
				+ ", contaDestino=" + contaDestino + "]";
	}
	
}
