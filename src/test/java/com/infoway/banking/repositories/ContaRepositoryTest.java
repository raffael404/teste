package com.infoway.banking.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import com.infoway.banking.utils.MockupUtils;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class ContaRepositoryTest {

	private static final String codigoBanco = "001";
	private static final String numero = "1234567";
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@BeforeAll
	public void criar() {
		Conta conta = MockupUtils.criarConta(
				MockupUtils.CONTA_1234567, MockupUtils.BANCO_001, MockupUtils.CLIENTE_70336818017);
		this.bancoRepository.save(conta.getBanco());
		this.clienteRepository.save(conta.getCliente());
		this.contaRepository.save(conta);
	}
	
	@AfterAll
	public void destruir() {
		this.contaRepository.deleteAll();
		this.bancoRepository.deleteAll();
		this.clienteRepository.deleteAll();
	}
	
	@Test
	void testBuscarPorBancoENumero() {
		Banco banco = this.bancoRepository.findById(codigoBanco).get();
		Conta conta = this.contaRepository.findByBancoAndNumero(banco, numero);
		assertEquals(numero, conta.getNumero());
		assertNotNull(conta.getId());
		assertEquals(0, conta.getSaldo());
	}

}
