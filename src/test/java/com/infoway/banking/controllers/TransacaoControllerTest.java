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
import com.infoway.banking.dtos.TransacaoDto;
import com.infoway.banking.dtos.TransacaoSimplesDto;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Role;
import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;
import com.infoway.banking.repositories.ContaRepository;
import com.infoway.banking.repositories.TransacaoRepository;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ClienteService;
import com.infoway.banking.services.ContaService;
import com.infoway.banking.services.RoleService;
import com.infoway.banking.services.TransacaoService;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class TransacaoControllerTest {
	
	private static final String NOME_USUARIO = "jaiminho";
	private static final String SENHA = "tangamandapio";
	private static final String URL_DEPOSITAR = "/conta/depositar";
	private static final String URL_SACAR = "/conta/sacar";
	private static final String URL_TRANSFERIR = "/conta/transferir";
	private static final String URL_EXTRATO = "/conta/extrato";
	private static final String CODIGO_BANCO_1 = "001";
	private static final String CODIGO_BANCO_2 = "260";
	private static final String NUMERO_CONTA_1 = "1234567";
	private static final String NUMERO_CONTA_2 = "0000001";
	private static final String CPF_CLIENTE_1 = "70336818017";
	private static final String CPF_CLIENTE_2 = "20867531010";
	
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
	private TransacaoService transacaoService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private ContaRepository contaRepository;
	
	@Autowired
	private TransacaoRepository transacaoRepository;

	@BeforeAll
	public void preparar() {
		Banco b1 = TesteUtils.criarBanco(TesteUtils.BANCO_001);
		Banco b2 = TesteUtils.criarBanco(TesteUtils.BANCO_260);
		Cliente c1 = TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017);
		Cliente c2 = TesteUtils.criarCliente(TesteUtils.CLIENTE_20867531010);
		List<Role> rolesBanco = new ArrayList<Role>();
		List<Role> rolesCliente = new ArrayList<Role>();
		rolesBanco.add(roleService.buscar("ROLE_banco").get());
		rolesCliente.add(roleService.buscar("ROLE_cliente").get());
		b1.setRoles(rolesBanco);
		b2.setRoles(rolesBanco);
		c1.setRoles(rolesCliente);
		c2.setRoles(rolesCliente);
		bancoService.persistir(b1);
		bancoService.persistir(b2);
		clienteService.persistir(c1);
		clienteService.persistir(c2);
	}

	@AfterEach
	public void limpar() {
		this.transacaoRepository.deleteAll();
		this.contaRepository.deleteAll();
	}
	
	@Test
	public void testFazerDepositoSemAutorizacao() throws Exception {
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		mvc.perform(MockMvcRequestBuilders.post(URL_DEPOSITAR)
				.content(mapper.writeValueAsString(transacaoDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	void testFazerDepositoValido() throws Exception {
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1);
		contaService.persistir(conta);
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		mvc.perform(MockMvcRequestBuilders.post(URL_DEPOSITAR)
				.header("Authorization", "Bearer " + TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc))
				.content(mapper.writeValueAsString(transacaoDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").exists())
				.andExpect(jsonPath("$.data.data").exists())
				.andExpect(jsonPath("$.data.valor").value(100))
				.andExpect(jsonPath("$.data.codigoBanco").value(CODIGO_BANCO_1))
				.andExpect(jsonPath("$.data.numeroConta").value(NUMERO_CONTA_1))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testFazerDepositoInvalido() throws Exception {
		String token = TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc);
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1);
		contaService.persistir(conta);
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco("002");
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.nonexistent.bank", URL_DEPOSITAR, token, mvc, ms);
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta("1111111");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.nonexistent.account", URL_DEPOSITAR, token, mvc, ms);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(-100.0);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.invalid.value", URL_DEPOSITAR, token, mvc, ms);
	}
	
	@Test
	public void testFazerSaqueSemAutorizacao() throws Exception {
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		mvc.perform(MockMvcRequestBuilders.post(URL_SACAR)
				.content(mapper.writeValueAsString(transacaoDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	void testFazerSaqueValido() throws Exception {
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1);
		conta.creditar(100);
		this.contaService.persistir(conta);
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		mvc.perform(MockMvcRequestBuilders.post(URL_SACAR)
				.header("Authorization", "Bearer " + TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc))
				.content(mapper.writeValueAsString(transacaoDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").exists())
				.andExpect(jsonPath("$.data.data").exists())
				.andExpect(jsonPath("$.data.valor").value(100))
				.andExpect(jsonPath("$.data.codigoBanco").value(CODIGO_BANCO_1))
				.andExpect(jsonPath("$.data.numeroConta").value(NUMERO_CONTA_1))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testFazerSaqueInvalido() throws Exception {
		String token = TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc);
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1);
		conta.creditar(100);
		this.contaService.persistir(conta);
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco("002");
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.nonexistent.bank", URL_SACAR, token, mvc, ms);
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta("1111111");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.nonexistent.account", URL_SACAR, token, mvc, ms);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(-100.0);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.invalid.value", URL_SACAR, token, mvc, ms);
		transacaoDto.setValor(120.0);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.invalid.balance", URL_SACAR, token, mvc, ms);
	}
	
	@Test
	public void testFazerTransferenciaSemAutorizacao() throws Exception {
		TransacaoDto transacaoDto = new TransacaoDto();
		transacaoDto.setBancoOrigem(CODIGO_BANCO_1);
		transacaoDto.setContaOrigem(NUMERO_CONTA_1);
		transacaoDto.setBancoDestino(CODIGO_BANCO_2);
		transacaoDto.setContaDestino(NUMERO_CONTA_2);
		transacaoDto.setValor(100.0);
		mvc.perform(MockMvcRequestBuilders.post(URL_TRANSFERIR)
				.content(mapper.writeValueAsString(transacaoDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	void testFazerTransferenciaValida() throws Exception {
		Conta c1 = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1);
		Conta c2 = criarConta(CODIGO_BANCO_2, NUMERO_CONTA_2, CPF_CLIENTE_2);
		c1.creditar(100.0);
		contaService.persistir(c1);
		contaService.persistir(c2);
		TransacaoDto transacaoDto = new TransacaoDto();
		transacaoDto.setBancoOrigem(CODIGO_BANCO_1);
		transacaoDto.setContaOrigem(NUMERO_CONTA_1);
		transacaoDto.setBancoDestino(CODIGO_BANCO_2);
		transacaoDto.setContaDestino(NUMERO_CONTA_2);
		transacaoDto.setValor(100.0);
		mvc.perform(MockMvcRequestBuilders.post(URL_TRANSFERIR)
				.header("Authorization", "Bearer " + TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc))
				.content(mapper.writeValueAsString(transacaoDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").exists())
				.andExpect(jsonPath("$.data.data").exists())
				.andExpect(jsonPath("$.data.valor").value(100))
				.andExpect(jsonPath("$.data.bancoOrigem").value(CODIGO_BANCO_1))
				.andExpect(jsonPath("$.data.contaOrigem").value(NUMERO_CONTA_1))
				.andExpect(jsonPath("$.data.bancoDestino").value(CODIGO_BANCO_2))
				.andExpect(jsonPath("$.data.contaDestino").value(NUMERO_CONTA_2))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testFazerTransferenciaInvalida() throws Exception {
		String token = TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc);
		Conta c1 = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1);
		Conta c2 = criarConta(CODIGO_BANCO_2, NUMERO_CONTA_2, CPF_CLIENTE_2);
		c1.creditar(100);
		c2.creditar(100);
		contaService.persistir(c1);
		contaService.persistir(c2);
		TransacaoDto transacaoDto = new TransacaoDto();
		transacaoDto.setBancoOrigem("002");
		transacaoDto.setContaOrigem(NUMERO_CONTA_1);
		transacaoDto.setBancoDestino(CODIGO_BANCO_2);
		transacaoDto.setContaDestino(NUMERO_CONTA_2);
		transacaoDto.setValor(100.0);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.nonexistent.bank.origin", URL_TRANSFERIR, token, mvc, ms);
		transacaoDto.setBancoOrigem(CODIGO_BANCO_1);
		transacaoDto.setBancoDestino("002");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.nonexistent.bank.destination", URL_TRANSFERIR, token, mvc, ms);
		transacaoDto.setBancoDestino(CODIGO_BANCO_2);
		transacaoDto.setContaOrigem("1111111");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.nonexistent.account.origin", URL_TRANSFERIR, token, mvc, ms);
		transacaoDto.setContaOrigem(NUMERO_CONTA_1);
		transacaoDto.setContaDestino("1111111");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.nonexistent.account.destination", URL_TRANSFERIR, token, mvc, ms);
		transacaoDto.setContaDestino(NUMERO_CONTA_1);
		transacaoDto.setBancoDestino(CODIGO_BANCO_1);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.equal.account", URL_TRANSFERIR, token, mvc, ms);
		transacaoDto.setContaDestino(NUMERO_CONTA_2);
		transacaoDto.setBancoDestino(CODIGO_BANCO_2);
		transacaoDto.setValor(-100.0);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.invalid.value", URL_TRANSFERIR, token, mvc, ms);
		transacaoDto.setValor(120.0);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				transacaoDto, "error.invalid.balance", URL_TRANSFERIR, token, mvc, ms);
	}
	
	@Test
	public void testVerExtratoSemAutorizacao() throws Exception {
		ContaDto contaDto = new ContaDto();
		contaDto.setCodigoBanco(CODIGO_BANCO_1);
		contaDto.setNumero(NUMERO_CONTA_1);
		mvc.perform(MockMvcRequestBuilders.post(URL_EXTRATO)
				.content(mapper.writeValueAsString(contaDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	void testVerExtratoValido() throws Exception {
		Conta c1 = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1);
		Conta c2 = criarConta(CODIGO_BANCO_2, NUMERO_CONTA_2, CPF_CLIENTE_2);
		contaService.persistir(c1);
		contaService.persistir(c2);
		Transacao t1 = new Transacao();
		t1.setOrigem(c1);
		t1.setDestino(c2);
		t1.setTipo(TipoTransacao.TRANSFERENCIA);
		t1.setValor(100.0);
		Transacao t2 = new Transacao();
		t2.setOrigem(c1);
		t2.setTipo(TipoTransacao.SAQUE);
		t2.setValor(50.0);
		Transacao t3 = new Transacao();
		t3.setDestino(c1);
		t3.setTipo(TipoTransacao.DEPOSITO);
		t3.setValor(150.0);
		transacaoService.persistir(t1);
		transacaoService.persistir(t2);
		transacaoService.persistir(t3);
		ContaDto contaDto = new ContaDto(c1);
		mvc.perform(MockMvcRequestBuilders.post(URL_EXTRATO)
				.header("Authorization", "Bearer " + TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc))
				.content(mapper.writeValueAsString(contaDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].id").exists())
				.andExpect(jsonPath("$.data[1].id").exists())
				.andExpect(jsonPath("$.data[2].id").exists())
				.andExpect(jsonPath("$.data[3]").doesNotExist())
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testVerExtratoInvalido() throws Exception {
		String token = TesteUtils.obterToken(NOME_USUARIO, SENHA, mvc);
		Banco banco = bancoService.buscarPorCodigo(CODIGO_BANCO_1).get();
		Cliente cliente = clienteService.buscarPorCpf(CPF_CLIENTE_1).get();
		Conta conta = new Conta();
		conta.setBanco(banco);
		conta.setCliente(cliente);
		conta.setNumero(NUMERO_CONTA_1);
		ContaDto contaDto = new ContaDto(conta);
		contaDto.setCodigoBanco("002");
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				contaDto, "error.nonexistent.bank", URL_EXTRATO, token, mvc, ms);
		contaDto.setCodigoBanco(CODIGO_BANCO_1);
		contaDto.setNumero(NUMERO_CONTA_2);
		TesteUtils.testarRequisicaoInvalidaAutenticada(
				contaDto, "error.nonexistent.account", URL_EXTRATO, token, mvc, ms);
		contaDto.setNumero(NUMERO_CONTA_1);
	}
	
	private Conta criarConta(String codigoBanco, String numeroConta, String cpfCliente) {
		Banco banco = bancoService.buscarPorCodigo(codigoBanco).get();
		Cliente cliente = clienteService.buscarPorCpf(cpfCliente).get();
		Conta conta = new Conta();
		conta.setBanco(banco);
		conta.setCliente(cliente);
		conta.setNumero(numeroConta);
		return conta;
	}

}
