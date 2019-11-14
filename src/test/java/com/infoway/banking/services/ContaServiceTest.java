package com.infoway.banking.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.infoway.banking.entities.Conta;
import com.infoway.banking.repositories.BancoRepository;
import com.infoway.banking.repositories.ClienteRepository;
import com.infoway.banking.repositories.ContaRepository;
import com.infoway.banking.utils.MockupUtils;

@SpringBootTest
@ActiveProfiles("test")
class ContaServiceTest {

	private static final String numero = "1234567";
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ContaService contaService;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@AfterEach
	public void destruir() {
		contaRepository.deleteAll();
		bancoRepository.deleteAll();
		clienteRepository.deleteAll();
	}
	
	@Test
	void testBuscar() {
		Conta c = MockupUtils.criarConta(
				MockupUtils.CONTA_1234567, MockupUtils.BANCO_001, MockupUtils.CLIENTE_70336818017);
		this.bancoService.persistir(c.getBanco());
		this.clienteService.persistir(c.getCliente());
		this.contaService.persistir(c);
		Optional<Conta> conta = contaService.buscar(c.getBanco(), numero);
		assertTrue(conta.isPresent());
	}
	
	@Test
	void testPersistir() {
		Conta c = MockupUtils.criarConta(
				MockupUtils.CONTA_1234567, MockupUtils.BANCO_001, MockupUtils.CLIENTE_70336818017);
		this.bancoService.persistir(c.getBanco());
		this.clienteService.persistir(c.getCliente());
		Conta conta = this.contaService.persistir(c);
		assertNotNull(conta);
	}
	
	@Test
	void testRemover() {
		Conta c = MockupUtils.criarConta(
				MockupUtils.CONTA_1234567, MockupUtils.BANCO_001, MockupUtils.CLIENTE_70336818017);
		this.bancoService.persistir(c.getBanco());
		this.clienteService.persistir(c.getCliente());
		this.contaService.persistir(c);
		this.contaService.remover(c.getBanco(), numero);
		Optional<Conta> conta = contaService.buscar(c.getBanco(), numero);
		assertFalse(conta.isPresent());
	}

}
