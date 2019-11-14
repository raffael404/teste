package com.infoway.banking.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.infoway.banking.entities.Banco;
import com.infoway.banking.utils.MockupUtils;

@SpringBootTest
@ActiveProfiles("test")
class BancoServiceTest {
	
	private static final String codigo = "001";
	
	@Autowired
	private BancoService bancoService;
	
	@Test
	void testBuscar() {
		this.bancoService.persistir(MockupUtils.criarBanco(MockupUtils.BANCO_001));
		Optional<Banco> banco = bancoService.buscar(codigo);
		assertTrue(banco.isPresent());
	}
	
	@Test
	void testPersistir() {
		Banco banco = this.bancoService.persistir(MockupUtils.criarBanco(MockupUtils.BANCO_001));
		assertNotNull(banco);
	}
	
	@Test
	void testRemover() {
		this.bancoService.persistir(MockupUtils.criarBanco(MockupUtils.BANCO_001));
		this.bancoService.remover(codigo);
		Optional<Banco> banco = bancoService.buscar(codigo);
		assertFalse(banco.isPresent());
	}

}
