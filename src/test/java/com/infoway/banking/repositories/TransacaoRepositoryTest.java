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
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class TransacaoRepositoryTest {
	
	private static final String CODIGO_BANCO_ORIGEM = "001";
	private static final String NUMERO_CONTA_ORIGEM = "1234567";
	private static final String CODIGO_BANCO_DESTINO = "260";
	private static final String NUMERO_CONTA_DESTINO = "0000001";
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private TransacaoRepository transacaoRepository;

	@BeforeAll
	public void preparar() {
		Transacao transacao1 = TesteUtils.criarTransacao(TesteUtils.TRANSFERENCIA_100,
				TesteUtils.BANCO_001, TesteUtils.CONTA_1234567, TesteUtils.CLIENTE_70336818017,
				TesteUtils.BANCO_260, TesteUtils.CONTA_0000001, TesteUtils.CLIENTE_20867531010);
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
	public void limpar() {
		this.transacaoRepository.deleteAll();
		this.contaRepository.deleteAll();
		this.bancoRepository.deleteAll();
		this.clienteRepository.deleteAll();
	}
	
	@Test
	void testBuscarPorOrigemOuDestino() {
		Banco bancoOrigem = this.bancoRepository.findByCodigo(CODIGO_BANCO_ORIGEM).get();
		Conta contaOrigem = this.contaRepository.findByBancoAndNumero(bancoOrigem, NUMERO_CONTA_ORIGEM);
		Banco bancoDestino = this.bancoRepository.findByCodigo(CODIGO_BANCO_DESTINO).get();
		Conta contaDestino = this.contaRepository.findByBancoAndNumero(bancoDestino, NUMERO_CONTA_DESTINO);
		List<Transacao> transacoesOrigem = this.transacaoRepository.findAllByOrigemOrDestino(contaOrigem, contaOrigem);
		List<Transacao> transacoesDestino = this.transacaoRepository.findAllByOrigemOrDestino(contaDestino, contaDestino);
		assertEquals(transacoesOrigem.size(), 2);
		assertEquals(transacoesDestino.size(), 2);
	}

}
