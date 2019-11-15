package com.infoway.banking.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Transacao;

class TesteUtilsTest {

	@Test
	void testCriarBanco() {
		Banco bancoNulo = TesteUtils.criarBanco(null);
		Banco banco = TesteUtils.criarBanco(TesteUtils.BANCO_001);
		assertNull(bancoNulo);
		assertNotNull(banco.getCodigo());
		assertNotNull(banco.getNome());
		assertNotNull(banco.getSenha());
	}
	
	@Test
	void testCriarCliente() {
		Cliente clienteNulo = TesteUtils.criarCliente(null);
		Cliente cliente = TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017);
		assertNull(clienteNulo);
		assertNotNull(cliente.getCpf());
		assertNotNull(cliente.getNome());
		assertNotNull(cliente.getSenha());
	}
	
	@Test
	void testCriarConta() {
		Conta contaNula = TesteUtils.criarConta(null, null, null);
		Conta conta = TesteUtils.criarConta(
				TesteUtils.CONTA_1234567, TesteUtils.BANCO_001, TesteUtils.CLIENTE_70336818017);
		assertNull(contaNula);
		assertNotNull(conta.getNumero());
		assertNotNull(conta.getSaldo());
		assertNotNull(conta.getBanco());
		assertNotNull(conta.getCliente());
	}
	
	@Test
	void testCriarTransacao() {
		Transacao transacaoNula = TesteUtils.criarTransacao(null, null, null, null, null, null, null);
		Transacao transacao = TesteUtils.criarTransacao(TesteUtils.TRANSFERENCIA_100,
				TesteUtils.BANCO_001, TesteUtils.CONTA_1234567, TesteUtils.CLIENTE_70336818017,
				TesteUtils.BANCO_260, TesteUtils.CONTA_0000001, TesteUtils.CLIENTE_20867531010);
		assertNull(transacaoNula);
		assertNotNull(transacao.getTipo());
		assertNotNull(transacao.getValor());
		assertNotNull(transacao.getOrigem());
		assertNotNull(transacao.getDestino());
	}
}
