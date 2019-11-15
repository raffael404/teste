package com.infoway.banking.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;
import com.infoway.banking.repositories.BancoRepository;
import com.infoway.banking.repositories.ClienteRepository;
import com.infoway.banking.repositories.ContaRepository;
import com.infoway.banking.repositories.TransacaoRepository;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
class TransacaoServiceTest {
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ContaService contaService;
	
	@Autowired
	private TransacaoService transacaoService;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private TransacaoRepository transacaoRepository;
	
	@AfterEach
	public void limpar() {
		transacaoRepository.deleteAll();
		contaRepository.deleteAll();
		bancoRepository.deleteAll();
		clienteRepository.deleteAll();
	}
	
	@Test
	void testBuscarTodas() {
		List<Transacao> transacoes = criarTransacoes();
		transacoes.forEach(transacao -> this.transacaoService.persistir(transacao));
		Optional<List<Transacao>> transacoesOrigem = this.transacaoService.buscarTodas(transacoes.get(0).getOrigem());
		Optional<List<Transacao>> transacoesDestino = this.transacaoService.buscarTodas(transacoes.get(0).getDestino());
		assertEquals(transacoesOrigem.get().size(), 2);
		assertEquals(transacoesDestino.get().size(), 2);
	}
	
	@Test
	void testPersistir() {
		List<Transacao> transacoes = criarTransacoes();
		for (Transacao t : transacoes) {
			Transacao transacao = this.transacaoService.persistir(t);
			assertNotNull(transacao);
		}
	}
	
	private List<Transacao> criarTransacoes() {
		List<Transacao> transacoes = new ArrayList<Transacao>();
		Transacao t1 = TesteUtils.criarTransacao(TesteUtils.TRANSFERENCIA_100,
				TesteUtils.BANCO_001, TesteUtils.CONTA_1234567, TesteUtils.CLIENTE_70336818017,
				TesteUtils.BANCO_260, TesteUtils.CONTA_0000001, TesteUtils.CLIENTE_20867531010);
		Conta origem = t1.getOrigem();
		Conta destino = t1.getDestino();
		Transacao t2 = new Transacao();
		t2.setValor(200.0);
		t2.setDestino(destino);
		t2.setTipo(TipoTransacao.DEPOSITO);
		Transacao t3 = new Transacao();
		t3.setValor(50.0);
		t3.setDestino(origem);
		t3.setTipo(TipoTransacao.SAQUE);
		this.bancoService.persistir(origem.getBanco());
		this.bancoService.persistir(destino.getBanco());
		this.clienteService.persistir(origem.getCliente());
		this.clienteService.persistir(destino.getCliente());
		this.contaService.persistir(origem);
		this.contaService.persistir(destino);
		transacoes.add(t1);
		transacoes.add(t2);
		transacoes.add(t3);
		return transacoes;
	}

}
