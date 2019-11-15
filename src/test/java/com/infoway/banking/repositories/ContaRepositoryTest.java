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
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class ContaRepositoryTest {

	private static final String CODIGO_BANCO = "001";
	private static final String NUMERO = "1234567";
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@BeforeAll
	public void preparar() {
		Conta conta = TesteUtils.criarConta(
				TesteUtils.CONTA_1234567, TesteUtils.BANCO_001, TesteUtils.CLIENTE_70336818017);
		this.bancoRepository.save(conta.getBanco());
		this.clienteRepository.save(conta.getCliente());
		this.contaRepository.save(conta);
	}
	
	@AfterAll
	public void limpar() {
		this.contaRepository.deleteAll();
		this.bancoRepository.deleteAll();
		this.clienteRepository.deleteAll();
	}
	
	@Test
	void testBuscarPorBancoENumero() {
		Banco banco = this.bancoRepository.findById(CODIGO_BANCO).get();
		Conta conta = this.contaRepository.findByBancoAndNumero(banco, NUMERO);
		assertEquals(NUMERO, conta.getNumero());
		assertNotNull(conta.getId());
		assertEquals(0, conta.getSaldo());
	}

}
