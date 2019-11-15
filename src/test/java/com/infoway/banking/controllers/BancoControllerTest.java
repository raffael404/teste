package com.infoway.banking.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoway.banking.dtos.AgenciaDto;
import com.infoway.banking.entities.Agencia;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.repositories.BancoRepository;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.utils.SenhaUtils;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class BancoControllerTest {

	private static final String URL_CADASTRAR_AGENCIA = "/banco/cadastrar/agencia";
	private static final String URL_REMOVER_AGENCIA = "/banco/remover/agencia";
	private static final String CODIGO_BANCO = "001";
	private static final String CNPJ = "59253921000109";
	private static final String NUMERO = "00001";
	private static final String SENHA = "fila";
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private MessageSource ms;
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@AfterEach
	public void limpar() {
		bancoRepository.deleteAll();
	}
	
	@Test
	void testCadastrarAgenciaValida() throws Exception {
		criarBanco();
		mvc.perform(MockMvcRequestBuilders.post(URL_CADASTRAR_AGENCIA)
				.content(mapper.writeValueAsString(criarAgenciaDto()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.cnpj").value(CNPJ))
				.andExpect(jsonPath("$.data.codigoBanco").value(CODIGO_BANCO))
				.andExpect(jsonPath("$.data.numero").value(NUMERO))
				.andExpect(jsonPath("$.data.senha").value(SENHA))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testCadastrarAgenciaInvalida() throws Exception {
		Banco banco = criarBanco();
		AgenciaDto agenciaDto = criarAgenciaDto();
		agenciaDto.setSenha("123456");
		TesteUtils.fazerRequisicaoInvalida(agenciaDto, "error.invalid.password", URL_CADASTRAR_AGENCIA, mvc, ms);
		agenciaDto = criarAgenciaDto();
		agenciaDto.setCodigoBanco("002");
		TesteUtils.fazerRequisicaoInvalida(agenciaDto, "error.nonexistent.bank", URL_CADASTRAR_AGENCIA, mvc, ms);
		persistirAgencia(banco);
		agenciaDto = criarAgenciaDto();
		agenciaDto.setNumero("00002");
		TesteUtils.fazerRequisicaoInvalida(agenciaDto, "error.existing.cnpj", URL_CADASTRAR_AGENCIA, mvc, ms);
		agenciaDto = criarAgenciaDto();
		agenciaDto.setCnpj("70721603000168");
		TesteUtils.fazerRequisicaoInvalida(agenciaDto, "error.existing.number", URL_CADASTRAR_AGENCIA, mvc, ms);
	}

	@Test
	void testRemoverAgenciaValida() throws Exception {
		Banco banco = criarBanco();
		persistirAgencia(banco);
		mvc.perform(MockMvcRequestBuilders.post(URL_REMOVER_AGENCIA)
				.content(mapper.writeValueAsString(criarAgenciaDto()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.cnpj").value(CNPJ))
				.andExpect(jsonPath("$.data.codigoBanco").value(CODIGO_BANCO))
				.andExpect(jsonPath("$.data.numero").value(NUMERO))
				.andExpect(jsonPath("$.data.senha").value(SENHA))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testRemoverAgenciaInvalida() throws Exception {
		Banco banco = criarBanco();
		persistirAgencia(banco);
		AgenciaDto agenciaDto = new AgenciaDto();
		agenciaDto.setCnpj(CNPJ);
		agenciaDto.setCodigoBanco("002");
		agenciaDto.setNumero(NUMERO);
		agenciaDto.setSenha(SENHA);
		TesteUtils.fazerRequisicaoInvalida(agenciaDto, "error.nonexistent.bank", URL_REMOVER_AGENCIA, mvc, ms);
		agenciaDto.setCodigoBanco(CODIGO_BANCO);
		agenciaDto.setNumero("11111");
		TesteUtils.fazerRequisicaoInvalida(agenciaDto, "error.nonexistent.branch", URL_REMOVER_AGENCIA, mvc, ms);
		agenciaDto.setNumero(NUMERO);
		agenciaDto.setSenha("no");
		TesteUtils.fazerRequisicaoInvalida(agenciaDto, "error.invalid.password", URL_REMOVER_AGENCIA, mvc, ms);
	}
	
	private AgenciaDto criarAgenciaDto() throws JsonProcessingException {
		AgenciaDto agenciaDto = new AgenciaDto();
		agenciaDto.setCnpj(CNPJ);
		agenciaDto.setCodigoBanco(CODIGO_BANCO);
		agenciaDto.setNumero(NUMERO);
		agenciaDto.setSenha(SENHA);
		return agenciaDto;
	}
	
	private Banco criarBanco() throws Exception {
		Banco banco = TesteUtils.criarBanco(TesteUtils.BANCO_001);
		banco.setSenha(SenhaUtils.criptografar(banco.getSenha()));
		this.bancoService.persistir(banco);
		return this.bancoService.persistir(banco);
	}
	
	private void persistirAgencia(Banco banco) {
		Agencia agencia = new Agencia();
		agencia.setCnpj(CNPJ);
		agencia.setNumero(NUMERO);
		agencia.setBanco(banco);
		banco.getAgencias().add(agencia);
		bancoService.persistir(banco);
	}
	
}
