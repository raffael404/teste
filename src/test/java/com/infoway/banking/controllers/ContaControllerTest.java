package com.infoway.banking.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;
import com.infoway.banking.repositories.ContaRepository;
import com.infoway.banking.repositories.TransacaoRepository;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ClienteService;
import com.infoway.banking.services.ContaService;
import com.infoway.banking.services.TransacaoService;
import com.infoway.banking.utils.SenhaUtils;
import com.infoway.banking.utils.TesteUtils;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
class ContaControllerTest {
	
	private static final String URL_ABRIR_CONTA = "/conta/abrir";
	private static final String URL_FECHAR_CONTA = "/conta/fechar";
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
	private static final String SENHA_1 = "tangamandapio";
	private static final String SENHA_2 = "madruguinha";
	
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
	private ContaRepository contaRepository;
	
	@Autowired
	private TransacaoRepository transacaoRepository;
	
	@BeforeAll
	public void preparar() {
		Banco b1 = TesteUtils.criarBanco(TesteUtils.BANCO_001);
		Banco b2 = TesteUtils.criarBanco(TesteUtils.BANCO_260);
		Cliente c1 = TesteUtils.criarCliente(TesteUtils.CLIENTE_70336818017);
		Cliente c2 = TesteUtils.criarCliente(TesteUtils.CLIENTE_20867531010);
		b1.setSenha(SenhaUtils.criptografar(b1.getSenha()));
		b2.setSenha(SenhaUtils.criptografar(b2.getSenha()));
		c1.setSenha(SenhaUtils.criptografar(c1.getSenha()));
		c2.setSenha(SenhaUtils.criptografar(c2.getSenha()));
		this.bancoService.persistir(b1);
		this.bancoService.persistir(b2);
		this.clienteService.persistir(c1);
		this.clienteService.persistir(c2);
	}

	@AfterEach
	public void limpar() {
		this.transacaoRepository.deleteAll();
		this.contaRepository.deleteAll();
	}
	
	@Test
	void testAbrirContaValida() throws Exception {
		ContaDto contaDto = criarContaDto(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		mvc.perform(MockMvcRequestBuilders.post(URL_ABRIR_CONTA)
				.content(mapper.writeValueAsString(contaDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").exists())
				.andExpect(jsonPath("$.data.numero").value(NUMERO_CONTA_1))
				.andExpect(jsonPath("$.data.saldo").value(0))
				.andExpect(jsonPath("$.data.codigoBanco").value(CODIGO_BANCO_1))
				.andExpect(jsonPath("$.data.cpfCliente").value(CPF_CLIENTE_1))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testAbrirContaInvalida() throws Exception {
		ContaDto contaDto = criarContaDto(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		contaDto.setCpfCliente("29886775068");
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.nonexistent.client", URL_ABRIR_CONTA, mvc, ms);
		contaDto = criarContaDto(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		contaDto.setSenha("aluguel");
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.invalid.password", URL_ABRIR_CONTA, mvc, ms);
		contaDto = criarContaDto(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		contaDto.setCodigoBanco("002");
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.nonexistent.bank", URL_ABRIR_CONTA, mvc, ms);
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		this.contaService.persistir(conta);
		contaDto = criarContaDto(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.existing.account", URL_ABRIR_CONTA, mvc, ms);
	}
	
	@Test
	void testFecharContaValida() throws Exception {
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		this.contaService.persistir(conta);
		ContaDto contaDto = new ContaDto(conta);
		contaDto.setSenha(SENHA_1);
		mvc.perform(MockMvcRequestBuilders.post(URL_FECHAR_CONTA)
				.content(mapper.writeValueAsString(contaDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.numero").value(NUMERO_CONTA_1))
				.andExpect(jsonPath("$.data.codigoBanco").value(CODIGO_BANCO_1))
				.andExpect(jsonPath("$.data.cpfCliente").value(CPF_CLIENTE_1))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testFecharContaInvalida() throws Exception {
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		this.contaService.persistir(conta);
		ContaDto contaDto = criarContaDto(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		contaDto.setCodigoBanco("002");
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.nonexistent.bank", URL_FECHAR_CONTA, mvc, ms);
		contaDto = criarContaDto(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		contaDto.setSenha("aluguel");
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.invalid.password", URL_FECHAR_CONTA, mvc, ms);
		contaDto = criarContaDto(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		contaDto.setNumero("0000001");
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.nonexistent.account", URL_FECHAR_CONTA, mvc, ms);
	}
	
	@Test
	void testFazerDepositoValido() throws Exception {
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		this.contaService.persistir(conta);
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		transacaoDto.setSenha("no");
		mvc.perform(MockMvcRequestBuilders.post(URL_DEPOSITAR)
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
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		this.contaService.persistir(conta);
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco("002");
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		transacaoDto.setSenha("no");
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.nonexistent.bank", URL_DEPOSITAR, mvc, ms);
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta("1111111");
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.nonexistent.account", URL_DEPOSITAR, mvc, ms);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(-100.0);
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.invalid.value", URL_DEPOSITAR, mvc, ms);
	}
	
	@Test
	void testFazerSaqueValido() throws Exception {
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		conta.creditar(100);
		this.contaService.persistir(conta);
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		transacaoDto.setSenha(SENHA_1);
		mvc.perform(MockMvcRequestBuilders.post(URL_SACAR)
				.content(mapper.writeValueAsString(transacaoDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.id").exists())
				.andExpect(jsonPath("$.data.data").exists())
				.andExpect(jsonPath("$.data.valor").value(100))
				.andExpect(jsonPath("$.data.codigoBanco").value(CODIGO_BANCO_1))
				.andExpect(jsonPath("$.data.numeroConta").value(NUMERO_CONTA_1))
				.andExpect(jsonPath("$.data.senha").value(SENHA_1))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testFazerSaqueInvalido() throws Exception {
		Conta conta = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		conta.creditar(100);
		this.contaService.persistir(conta);
		TransacaoSimplesDto transacaoDto = new TransacaoSimplesDto();
		transacaoDto.setCodigoBanco("002");
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(100.0);
		transacaoDto.setSenha(SENHA_1);
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.nonexistent.bank", URL_SACAR, mvc, ms);
		transacaoDto.setCodigoBanco(CODIGO_BANCO_1);
		transacaoDto.setNumeroConta("1111111");
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.nonexistent.account", URL_SACAR, mvc, ms);
		transacaoDto.setNumeroConta(NUMERO_CONTA_1);
		transacaoDto.setValor(-100.0);
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.invalid.value", URL_SACAR, mvc, ms);
		transacaoDto.setValor(120.0);
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.invalid.balance", URL_SACAR, mvc, ms);
		transacaoDto.setSenha("no");
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.invalid.password", URL_SACAR, mvc, ms);
	}
	
	@Test
	void testFazerTransferenciaValida() throws Exception {
		Conta c1 = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		Conta c2 = criarConta(CODIGO_BANCO_2, NUMERO_CONTA_2, CPF_CLIENTE_2, SENHA_2);
		c1.creditar(100.0);
		this.contaService.persistir(c1);
		this.contaService.persistir(c2);
		TransacaoDto transacaoDto = new TransacaoDto();
		transacaoDto.setBancoOrigem(CODIGO_BANCO_1);
		transacaoDto.setContaOrigem(NUMERO_CONTA_1);
		transacaoDto.setBancoDestino(CODIGO_BANCO_2);
		transacaoDto.setContaDestino(NUMERO_CONTA_2);
		transacaoDto.setValor(100.0);
		transacaoDto.setSenha(SENHA_1);
		mvc.perform(MockMvcRequestBuilders.post(URL_TRANSFERIR)
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
				.andExpect(jsonPath("$.data.senha").value(SENHA_1))
				.andExpect(jsonPath("$.errors").isEmpty());
	}
	
	@Test
	void testFazerTransferenciaInvalida() throws Exception {
		Conta c1 = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		Conta c2 = criarConta(CODIGO_BANCO_2, NUMERO_CONTA_2, CPF_CLIENTE_2, SENHA_2);
		c1.creditar(100);
		c2.creditar(100);
		this.contaService.persistir(c1);
		this.contaService.persistir(c2);
		TransacaoDto transacaoDto = new TransacaoDto();
		transacaoDto.setBancoOrigem("002");
		transacaoDto.setContaOrigem(NUMERO_CONTA_1);
		transacaoDto.setBancoDestino(CODIGO_BANCO_2);
		transacaoDto.setContaDestino(NUMERO_CONTA_2);
		transacaoDto.setValor(100.0);
		transacaoDto.setSenha(SENHA_1);
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.nonexistent.bank.origin", URL_TRANSFERIR, mvc, ms);
		transacaoDto.setBancoOrigem(CODIGO_BANCO_1);
		transacaoDto.setBancoDestino("002");
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.nonexistent.bank.destination", URL_TRANSFERIR, mvc, ms);
		transacaoDto.setBancoDestino(CODIGO_BANCO_2);
		transacaoDto.setContaOrigem("1111111");
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.nonexistent.account.origin", URL_TRANSFERIR, mvc, ms);
		transacaoDto.setContaOrigem(NUMERO_CONTA_1);
		transacaoDto.setContaDestino("1111111");
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.nonexistent.account.destination", URL_TRANSFERIR, mvc, ms);
		transacaoDto.setContaDestino(NUMERO_CONTA_1);
		transacaoDto.setBancoDestino(CODIGO_BANCO_1);
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.equal.account", URL_TRANSFERIR, mvc, ms);
		transacaoDto.setContaDestino(NUMERO_CONTA_2);
		transacaoDto.setBancoDestino(CODIGO_BANCO_2);
		transacaoDto.setValor(-100.0);
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.invalid.value", URL_TRANSFERIR, mvc, ms);
		transacaoDto.setValor(120.0);
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.invalid.balance", URL_TRANSFERIR, mvc, ms);
		transacaoDto.setSenha("no");
		TesteUtils.fazerRequisicaoInvalida(transacaoDto, "error.invalid.password", URL_TRANSFERIR, mvc, ms);
	}
	
	@Test
	void testVerExtratoValido() throws Exception {
		Conta c1 = criarConta(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		Conta c2 = criarConta(CODIGO_BANCO_2, NUMERO_CONTA_2, CPF_CLIENTE_2, SENHA_2);
		this.contaService.persistir(c1);
		this.contaService.persistir(c2);
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
		contaDto.setSenha(SENHA_1);
		mvc.perform(MockMvcRequestBuilders.post(URL_EXTRATO)
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
		ContaDto contaDto = criarContaDto(CODIGO_BANCO_1, NUMERO_CONTA_1, CPF_CLIENTE_1, SENHA_1);
		contaDto.setCodigoBanco("002");
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.nonexistent.bank", URL_EXTRATO, mvc, ms);
		contaDto.setCodigoBanco(CODIGO_BANCO_1);
		contaDto.setNumero(NUMERO_CONTA_2);
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.nonexistent.account", URL_EXTRATO, mvc, ms);
		contaDto.setNumero(NUMERO_CONTA_1);
		contaDto.setSenha("no");
		TesteUtils.fazerRequisicaoInvalida(contaDto, "error.nonexistent.account", URL_EXTRATO, mvc, ms);
	}
	
	private ContaDto criarContaDto(String codigoBanco, String numeroConta, String cpfCliente, String senha) {
		Banco banco = this.bancoService.buscar(codigoBanco).get();
		Cliente cliente = this.clienteService.buscar(cpfCliente).get();
		Conta conta = new Conta();
		conta.setBanco(banco);
		conta.setCliente(cliente);
		conta.setNumero(numeroConta);
		ContaDto contaDto = new ContaDto(conta);
		contaDto.setSenha(senha);
		return contaDto;
	}
	
	private Conta criarConta(String codigoBanco, String numeroConta, String cpfCliente, String senha) {
		Banco banco = this.bancoService.buscar(codigoBanco).get();
		Cliente cliente = this.clienteService.buscar(cpfCliente).get();
		Conta conta = new Conta();
		conta.setBanco(banco);
		conta.setCliente(cliente);
		conta.setNumero(numeroConta);
		return conta;
	}
	
}
