package com.infoway.banking.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;
import com.infoway.banking.utils.MockupUtils;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class TransacaoRepositoryTest {
	
	private static final String codigoBancoOrigem = "001";
	private static final String numeroContaOrigem = "1234567";
	private static final String codigoBancoDestino = "260";
	private static final String numeroContaDestino = "0000001";
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private TransacaoRepository transacaoRepository;

	@BeforeAll
	public void criar() {
		Transacao transacao1 = MockupUtils.criarTransacao(MockupUtils.TRANSFERENCIA_100,
				MockupUtils.BANCO_001, MockupUtils.CONTA_1234567, MockupUtils.CLIENTE_70336818017,
				MockupUtils.BANCO_260, MockupUtils.CONTA_0000001, MockupUtils.CLIENTE_20867531010);
		Conta origem = transacao1.getOrigem();
		Conta destino = transacao1.getDestino();
		Transacao transacao2 = new Transacao();
		transacao2.setValor(200.0);
		transacao2.setDestino(destino);
		transacao2.setTipo(TipoTransacao.DEPOSITO);
		Transacao transacao3 = new Transacao();
		transacao3.setValor(50.0);
		transacao3.setDestino(origem);
		transacao3.setTipo(TipoTransacao.SAQUE);
		this.bancoRepository.save(origem.getBanco());
		this.bancoRepository.save(destino.getBanco());
		this.clienteRepository.save(origem.getCliente());
		this.clienteRepository.save(destino.getCliente());
		this.contaRepository.save(origem);
		this.contaRepository.save(destino);
		this.transacaoRepository.save(transacao1);
		this.transacaoRepository.save(transacao2);
		this.transacaoRepository.save(transacao3);
	}
	
	@AfterAll
	public void destruir() {
		this.transacaoRepository.deleteAll();
		this.contaRepository.deleteAll();
		this.bancoRepository.deleteAll();
		this.clienteRepository.deleteAll();
	}
	
	@Test
	void testBuscarPorOrigemOuDestino() {
		Banco bancoOrigem = this.bancoRepository.findById(codigoBancoOrigem).get();
		Conta contaOrigem = this.contaRepository.findByBancoAndNumero(bancoOrigem, numeroContaOrigem);
		Banco bancoDestino = this.bancoRepository.findById(codigoBancoDestino).get();
		Conta contaDestino = this.contaRepository.findByBancoAndNumero(bancoDestino, numeroContaDestino);
		List<Transacao> transacoesOrigem = this.transacaoRepository.findAllByOrigemOrDestino(contaOrigem, contaOrigem);
		List<Transacao> transacoesDestino = this.transacaoRepository.findAllByOrigemOrDestino(contaDestino, contaDestino);
		assertEquals(transacoesOrigem.size(), 2);
		assertEquals(transacoesDestino.size(), 2);
	}

}
