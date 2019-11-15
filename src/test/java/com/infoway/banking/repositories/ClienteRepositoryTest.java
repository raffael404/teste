package com.infoway.banking.repositories;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.infoway.banking.entities.Cliente;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class ClienteRepositoryTest {

	private static final String CPF = "70336818017";
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@BeforeAll
	public void preparar() {
		clienteRepository.save(TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017));
	}
	
	@AfterAll
	public void limpar() {
		clienteRepository.deleteAll();
	}
	
	@Test
	void testBuscarPorCpf() {
		Cliente cliente = clienteRepository.findById(CPF).get();
		assertEquals(CPF, cliente.getCpf());
	}

}
