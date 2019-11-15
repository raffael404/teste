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

import com.infoway.banking.entities.Banco;
import com.infoway.banking.repositories.BancoRepository;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
class BancoServiceTest {
	
	private static final String CODIGO = "001";
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@AfterEach
	public void limpar() {
		bancoRepository.deleteAll();
	}
	
	@Test
	void testBuscar() {
		this.bancoService.persistir(TesteUtils.criarBanco(TesteUtils.BANCO_001));
		Optional<Banco> banco = bancoService.buscar(CODIGO);
		assertTrue(banco.isPresent());
	}
	
	@Test
	void testPersistir() {
		Banco banco = this.bancoService.persistir(TesteUtils.criarBanco(TesteUtils.BANCO_001));
		assertNotNull(banco);
	}
	
	@Test
	void testRemover() {
		this.bancoService.persistir(TesteUtils.criarBanco(TesteUtils.BANCO_001));
		this.bancoService.remover(CODIGO);
		Optional<Banco> banco = bancoService.buscar(CODIGO);
		assertFalse(banco.isPresent());
	}

}
