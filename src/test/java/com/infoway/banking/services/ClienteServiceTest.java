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

import com.infoway.banking.entities.Cliente;
import com.infoway.banking.repositories.ClienteRepository;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
class ClienteServiceTest {

	private static final String CPF = "70336818017";
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@AfterEach
	public void limpar() {
		clienteRepository.deleteAll();
	}
	
	@Test
	void testBuscar() {
		this.clienteService.persistir(TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017));
		Optional<Cliente> cliente = clienteService.buscar(CPF);
		assertTrue(cliente.isPresent());
	}
	
	@Test
	void testPersistir() {
		Cliente cliente = this.clienteService.persistir(TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017));
		assertNotNull(cliente);
	}
	
	@Test
	void testRemover() {
		this.clienteService.persistir(TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017));
		this.clienteService.remover(CPF);
		Optional<Cliente> cliente = clienteService.buscar(CPF);
		assertFalse(cliente.isPresent());
	}

}
