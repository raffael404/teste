package com.infoway.banking.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.infoway.banking.entities.Banco;
import com.infoway.banking.utils.MockupUtils;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class BancoRepositoryTest {
	
	private static final String codigo = "001";
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@BeforeAll
	public void criar() {
		this.bancoRepository.save(MockupUtils.criarBanco(MockupUtils.BANCO_001));
	}
	
	@AfterAll
	public void destruir() {
		this.bancoRepository.deleteAll();
	}
	
	@Test
	void testBuscarPorCodigo() {
		Banco banco = this.bancoRepository.findById(codigo).get();
		assertEquals(codigo, banco.getCodigo());
	}

}
