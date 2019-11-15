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
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class BancoRepositoryTest {
	
	private static final String CODIGO = "001";
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@BeforeAll
	public void preparar() {
		this.bancoRepository.save(TesteUtils.criarBanco(TesteUtils.BANCO_001));
	}
	
	@AfterAll
	public void limpar() {
		this.bancoRepository.deleteAll();
	}
	
	@Test
	void testBuscarPorCodigo() {
		Banco banco = this.bancoRepository.findById(CODIGO).get();
		assertEquals(CODIGO, banco.getCodigo());
	}

}
