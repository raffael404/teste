package com.infoway.banking.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterAll;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infoway.banking.dtos.BancoDto;
import com.infoway.banking.dtos.ClienteDto;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.repositories.BancoRepository;
import com.infoway.banking.repositories.ClienteRepository;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ClienteService;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class UsuarioControllerTest {

	private static final String URL_CADASTRAR_CLIENTE = "/usuario/cadastrar/cliente";
	private static final String URL_CADASTRAR_BANCO = "/usuario/cadastrar/banco";
	private static final String CODIGO_BANCO = "001";
	private static final String NOME_BANCO = "Banco do Brasil S.A.";
	private static final String CPF_CLIENTE = "70336818017";
	private static final String NOME_CLIENTE = "Jaiminho";
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private MessageSource ms;
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Test
	void testCadastrarBancoValido() throws Exception {
		Banco banco = TesteUtils.criarBanco(TesteUtils.BANCO_001);
		BancoDto bancoDto = new BancoDto(banco);
		mvc.perform(MockMvcRequestBuilders.post(URL_CADASTRAR_BANCO)
				.content(mapper.writeValueAsString(bancoDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.codigo").value(CODIGO_BANCO))
				.andExpect(jsonPath("$.data.nome").value(NOME_BANCO))
				.andExpect(jsonPath("$.data.senha").isNotEmpty())
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	public void cadastrarBancoInvalido() throws Exception {
		Banco banco = TesteUtils.criarBanco(TesteUtils.BANCO_001);
		bancoService.persistir(banco);
		BancoDto bancoDto = new BancoDto(banco);
		TesteUtils.fazerRequisicaoInvalida(bancoDto, "error.existing.code", URL_CADASTRAR_BANCO, mvc, ms);
	}
	
	@Test
	void testCadastrarClienteValido() throws Exception {
		Cliente cliente = TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017);
		ClienteDto clienteDto = new ClienteDto(cliente);
		mvc.perform(MockMvcRequestBuilders.post(URL_CADASTRAR_CLIENTE)
				.content(mapper.writeValueAsString(clienteDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.cpf").value(CPF_CLIENTE))
				.andExpect(jsonPath("$.data.nome").value(NOME_CLIENTE))
				.andExpect(jsonPath("$.data.senha").isNotEmpty())
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	public void cadastrarClienteInvalido() throws Exception {
		Cliente cliente = TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017);
		clienteService.persistir(cliente);
		ClienteDto clienteDto = new ClienteDto(cliente);
		TesteUtils.fazerRequisicaoInvalida(clienteDto, "error.existing.cpf", URL_CADASTRAR_CLIENTE, mvc, ms);
		clienteService.remover(CPF_CLIENTE);
	}
	
	@AfterAll
	public void limpar() {
		this.bancoRepository.deleteAll();
		this.clienteRepository.deleteAll();
	}
	
}
