package com.infoway.banking.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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
import com.infoway.banking.entities.Role;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.RoleService;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BancoControllerTest {

	private static final String NOME_USUARIO = "bb";
	private static final String SENHA = "fila";
	private static final String URL_CADASTRAR_AGENCIA = "/banco/cadastrar/agencia";
	private static final String URL_REMOVER_AGENCIA = "/banco/remover/agencia";
	private static final String CODIGO_BANCO = "001";
	private static final String CNPJ = "59253921000109";
	private static final String NUMERO = "00001";
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private MessageSource ms;
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private RoleService roleService;
	
	@AfterEach
	public void limpar() {
		bancoService.remover(CODIGO_BANCO);
	}
	
	@Test
	public void testCadastrarAgenciaSemAutorizacao() throws Exception {
	    mvc.perform(MockMvcRequestBuilders.post(URL_CADASTRAR_AGENCIA)
				.content(mapper.writeValueAsString(criarAgenciaDto()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testRemoverAgenciaSemAutorizacao() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post(URL_REMOVER_AGENCIA)
				.content(mapper.writeValueAsString(criarAgenciaDto()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	void testCadastrarAgenciaValida() throws Exception {
		persistirBanco();
		mvc.perform(MockMvcRequestBuilders.post(URL_CADASTRAR_AGENCIA)
				.header("Authorization", "Bearer " + TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc))
				.content(mapper.writeValueAsString(criarAgenciaDto()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.cnpj").value(CNPJ))
				.andExpect(jsonPath("$.data.numero").value(NUMERO))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testCadastrarAgenciaInvalida() throws Exception {
		Banco banco = persistirBanco();
		persistirAgencia(banco);
		String token = TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc);
		AgenciaDto agenciaDto = criarAgenciaDto();
		agenciaDto.setNumero("00002");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				agenciaDto, "error.existing.cnpj", URL_CADASTRAR_AGENCIA, token, mvc, ms);
		agenciaDto = criarAgenciaDto();
		agenciaDto.setCnpj("70721603000168");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				agenciaDto, "error.existing.number", URL_CADASTRAR_AGENCIA, token, mvc, ms);
	}

	@Test
	void testRemoverAgenciaValida() throws Exception {
		Banco banco = persistirBanco();
		persistirAgencia(banco);
		mvc.perform(MockMvcRequestBuilders.post(URL_REMOVER_AGENCIA)
				.header("Authorization", "Bearer " + TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc))
				.content(mapper.writeValueAsString(criarAgenciaDto()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.cnpj").value(CNPJ))
				.andExpect(jsonPath("$.data.numero").value(NUMERO))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testRemoverAgenciaInvalida() throws Exception {
		Banco banco = persistirBanco();
		persistirAgencia(banco);
		String token = TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc);
		AgenciaDto agenciaDto = criarAgenciaDto();
		agenciaDto.setNumero("11111");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				agenciaDto, "error.nonexistent.branch", URL_REMOVER_AGENCIA, token, mvc, ms);
	}
	
	private AgenciaDto criarAgenciaDto() throws JsonProcessingException {
		AgenciaDto agenciaDto = new AgenciaDto();
		agenciaDto.setCnpj(CNPJ);
		agenciaDto.setNumero(NUMERO);
		return agenciaDto;
	}
	
	private Banco persistirBanco() {
		Banco banco = TesteUtils.criarBanco(TesteUtils.BANCO_001);
		List<Role> roles = new ArrayList<Role>();
		roles.add(roleService.buscar("ROLE_banco").get());
		banco.setRoles(roles);
		return bancoService.persistir(banco);
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
