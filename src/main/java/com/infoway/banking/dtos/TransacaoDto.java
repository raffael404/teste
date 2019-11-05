package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;
import com.infoway.banking.utils.DataUtils;

public class TransacaoDto {
	
	private Long id;
	private String data;
	private TipoTransacao tipo;
	private Double valor;
	private String bancoOrigem;
	private String contaOrigem;
	private String bancoDestino;
	private String contaDestino;
	
	public TransacaoDto() {}
	
	public TransacaoDto(Transacao transacao) {
		this.id = transacao.getId();
		this.data = DataUtils.converterParaString(transacao.getData());
		this.tipo = transacao.getTipo();
		this.valor = transacao.getValor();
		if (transacao.getOrigem() == null) {
			this.bancoOrigem = "";
			this.contaOrigem = "";
		} else {
			this.bancoOrigem = transacao.getOrigem().getBanco().getCodigo();
			this.contaOrigem = transacao.getOrigem().getNumero();
		}
		if (transacao.getDestino() == null) {
			this.bancoDestino = "";
			this.contaDestino = "";
		} else {
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

	public TipoTransacao getTipo() {
		return tipo;
	}
	public void setTipo(TipoTransacao tipo) {
		this.tipo = tipo;
	}

	@NotNull(message = "Valor não pode ser vazio.")
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	
	@NotEmpty(message = "Código do banco de origem não pode ser vazio.")
	public String getBancoOrigem() {
		return bancoOrigem;
	}
	public void setBancoOrigem(String bancoOrigem) {
		this.bancoOrigem = bancoOrigem;
	}
	
	@NotEmpty(message = "Número da conta de origem não pode ser vazio.")
	public String getContaOrigem() {
		return contaOrigem;
	}
	public void setContaOrigem(String contaOrigem) {
		this.contaOrigem = contaOrigem;
	}
	
	@NotEmpty(message = "Código do banco de destino não pode ser vazio.")
	public String getBancoDestino() {
		return bancoDestino;
	}
	public void setBancoDestino(String bancoDestino) {
		this.bancoDestino = bancoDestino;
	}
	
	@NotEmpty(message = "Número da conta de destino não pode ser vazio.")
	public String getContaDestino() {
		return contaDestino;
	}
	public void setContaDestino(String contaDestino) {
		this.contaDestino = contaDestino;
	}
	
	@Override
	public String toString() {
		return "TransacaoDto [valor=" + valor + ", bancoOrigem=" + bancoOrigem + ", contaOrigem=" + contaOrigem
				+ ", bancoDestino=" + bancoDestino + ", contaDestino=" + contaDestino + "]";
	}
	
}
