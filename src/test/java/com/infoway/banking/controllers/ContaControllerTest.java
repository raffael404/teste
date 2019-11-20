package com.infoway.banking.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import com.infoway.banking.dtos.ContaDto;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Role;
import com.infoway.banking.repositories.ContaRepository;
import com.infoway.banking.repositories.TransacaoRepository;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ClienteService;
import com.infoway.banking.services.ContaService;
import com.infoway.banking.services.RoleService;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class ContaControllerTest {
	
	private static final String NOME_USUARIO = "jaiminho";
	private static final String SENHA = "tangamandapio";
	private static final String URL_ABRIR_CONTA = "/conta/abrir";
	private static final String URL_FECHAR_CONTA = "/conta/fechar";
	private static final String CODIGO_BANCO = "001";
	private static final String NUMERO_CONTA = "1234567";
	private static final String CPF_CLIENTE = "70336818017";
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private MessageSource ms;
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private ContaService contaService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private TransacaoRepository transacaoRepository;
	
	@BeforeAll
	public void preparar() {
		Banco banco = TesteUtils.criarBanco(TesteUtils.BANCO_001);
		Cliente cliente = TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017);
		List<Role> rolesBanco = new ArrayList<Role>();
		List<Role> rolesCliente = new ArrayList<Role>();
		rolesBanco.add(roleService.buscar("ROLE_banco").get());
		rolesCliente.add(roleService.buscar("ROLE_cliente").get());
		banco.setRoles(rolesBanco);
		cliente.setRoles(rolesCliente);
		this.bancoService.persistir(banco);
		this.clienteService.persistir(cliente);
	}

	@AfterEach
	public void limpar() {
		this.transacaoRepository.deleteAll();
		this.contaRepository.deleteAll();
	}
	
	@Test
	public void testAbrirContaSemAutorizacao() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post(URL_ABRIR_CONTA)
				.content(mapper.writeValueAsString(criarContaDto()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	void testAbrirContaValida() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post(URL_ABRIR_CONTA)
				.header("Authorization", "Bearer " + TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc))
				.content(mapper.writeValueAsString(criarContaDto()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").exists())
				.andExpect(jsonPath("$.data.numero").value(NUMERO_CONTA))
				.andExpect(jsonPath("$.data.saldo").value(0))
				.andExpect(jsonPath("$.data.codigoBanco").value(CODIGO_BANCO))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testAbrirContaInvalida() throws Exception {
		String token = TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc);
		ContaDto contaDto = criarContaDto();
		contaDto.setCodigoBanco("002");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				contaDto, "error.nonexistent.bank", URL_ABRIR_CONTA, token, mvc, ms);
		Conta conta = criarConta();
		contaService.persistir(conta);
		contaDto = criarContaDto();
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				contaDto, "error.existing.account", URL_ABRIR_CONTA, token, mvc, ms);
	}
	
	@Test
	public void testFecharContaSemAutorizacao() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post(URL_FECHAR_CONTA)
				.content(mapper.writeValueAsString(criarContaDto()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	void testFecharContaValida() throws Exception {
		Conta conta = criarConta();
		contaService.persistir(conta);
		ContaDto contaDto = new ContaDto(conta);
		mvc.perform(MockMvcRequestBuilders.post(URL_FECHAR_CONTA)
				.header("Authorization", "Bearer " + TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc))
				.content(mapper.writeValueAsString(contaDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.numero").value(NUMERO_CONTA))
				.andExpect(jsonPath("$.data.codigoBanco").value(CODIGO_BANCO))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testFecharContaInvalida() throws Exception {
		String token = TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc);
		Conta conta = criarConta();
		contaService.persistir(conta);
		ContaDto contaDto = criarContaDto();
		contaDto.setCodigoBanco("002");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				contaDto, "error.nonexistent.bank", URL_FECHAR_CONTA, token, mvc, ms);
		contaDto = criarContaDto();
		contaDto.setNumero("0000001");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				contaDto, "error.nonexistent.account", URL_FECHAR_CONTA, token, mvc, ms);
	}
	
	private ContaDto criarContaDto() {
		ContaDto contaDto = new ContaDto();
		contaDto.setCodigoBanco(CODIGO_BANCO);
		contaDto.setNumero(NUMERO_CONTA);
		return contaDto;
	}
	
	private Conta criarConta() {
		Banco banco = bancoService.buscarPorCodigo(CODIGO_BANCO).get();
		Cliente cliente = clienteService.buscarPorCpf(CPF_CLIENTE).get();
		Conta conta = new Conta();
		conta.setBanco(banco);
		conta.setCliente(cliente);
		conta.setNumero(NUMERO_CONTA);
		return conta;
	}
	
}
