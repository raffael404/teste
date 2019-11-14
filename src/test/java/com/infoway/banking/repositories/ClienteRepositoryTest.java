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
import com.infoway.banking.utils.MockupUtils;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class ClienteRepositoryTest {

	private static final String cpf = "70336818017";
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@BeforeAll
	public void criar() {
		clienteRepository.save(MockupUtils.criarCliente(MockupUtils.CLIENTE_70336818017));
	}
	
	@AfterAll
	public void destruir() {
		clienteRepository.deleteAll();
	}
	
	@Test
	void testBuscarPorCpf() {
		Cliente cliente = clienteRepository.findById(cpf).get();
		assertEquals(cpf, cliente.getCpf());
	}

}
