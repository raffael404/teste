package com.infoway.banking.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infoway.banking.dtos.ContaDto;
import com.infoway.banking.dtos.TransacaoDto;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;
import com.infoway.banking.responses.Response;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ClienteService;
import com.infoway.banking.services.ContaService;
import com.infoway.banking.services.TransacaoService;
import com.infoway.banking.utils.DataUtils;
import com.infoway.banking.utils.SenhaUtils;

@RestController
@RequestMapping("/conta")
public class ContaController {
	
	private static final Logger log = LoggerFactory.getLogger(ContaController.class);
	
	@Autowired
	private ContaService contaService;
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private TransacaoService transacaoService;
	
	@Autowired
	private MessageSource ms;
	
	private ContaController() {}
	
	/**
	 * 
	 * Cria uma nova conta na base de dados associada à um cliente e um banco previamente cadastrados.
	 * 
	 * @param locale
	 * @param contaDto
	 * @param result
	 * @return ResponseEntity<Response<ContaDto>>
	 */
	@PostMapping(value = "/abrir")
	public ResponseEntity<Response<ContaDto>> abrir(Locale locale,
			@Valid @RequestBody ContaDto contaDto, BindingResult result) {
		log.info("Abrindo conta: {}", contaDto.toString());
		
		Optional<Cliente> cliente = null;
		if (contaDto.getCpfCliente() != null && contaDto.getSenha() != null) {
			cliente = clienteService.buscar(contaDto.getCpfCliente());
			if(!cliente.isPresent())
				result.addError(new ObjectError("cliente", "error.nonexistent.client"));
			else if (!SenhaUtils.verificarValidade(contaDto.getSenha(), cliente.get().getSenha()))
				result.addError(new ObjectError("cliente", "error.invalid.password"));
		}
		
		Optional<Banco> banco = null;
		if (contaDto.getCodigoBanco() != null && contaDto.getNumero() != null) {
			banco = bancoService.buscar(contaDto.getCodigoBanco());
			if (!banco.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank"));
			else if (contaService.buscar(banco.get(), contaDto.getNumero()).isPresent())
				result.addError(new ObjectError("conta", "error.existing.account"));
		}
		
		Response<ContaDto> response = new Response<ContaDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados da conta: {}", result.getAllErrors());
			result.getAllErrors().forEach(
					error -> response.getErrors().add(ms.getMessage(error.getDefaultMessage(), null, locale)));
			return ResponseEntity.badRequest().body(response);
		}
		
		Conta conta = new Conta();
		conta.setNumero(contaDto.getNumero());
		conta.setSenha(contaDto.getSenha());
		conta.setBanco(banco.get());
		conta.setCliente(cliente.get());
		contaService.persistir(conta);
		
		contaDto.setId(conta.getId());
		contaDto.setSaldo(0.0);
		response.setData(contaDto);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 
	 * Remove uma conta da base de dados.
	 * 
	 * @param locale
	 * @param contaDto
	 * @param result
	 * @return ResponseEntity<Response<ContaDto>>
	 */
	@PostMapping(value = "/fechar")
	public ResponseEntity<Response<ContaDto>> fechar(Locale locale,
			@Valid @RequestBody ContaDto contaDto, BindingResult result) {
		log.info("Fechando conta: {}", contaDto.toString());
		
		Optional<Banco> banco = null;
		if (contaDto.getCodigoBanco() != null && contaDto.getSenha() != null && contaDto.getNumero() != null) {
			banco = bancoService.buscar(contaDto.getCodigoBanco());
			if (!banco.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank"));
			else {
				if (!SenhaUtils.verificarValidade(contaDto.getSenha(), 
						clienteService.buscar(contaDto.getCpfCliente()).get().getSenha()))
					result.addError(new ObjectError("cliente", "error.invalid.password"));
				else if (!contaService.buscar(banco.get(), contaDto.getNumero()).isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account"));
			}			
		}
		
		Response<ContaDto> response = new Response<ContaDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados da conta: {}", result.getAllErrors());
			result.getAllErrors().forEach(
					error -> response.getErrors().add(ms.getMessage(error.getDefaultMessage(), null, locale)));
			return ResponseEntity.badRequest().body(response);
		}
		
		contaService.remover(banco.get(), contaDto.getNumero());
		response.setData(contaDto);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 
	 * Aumenta o saldo de uma conta por um valor dado.
	 * 
	 * @param locale
	 * @param transacaoDto
	 * @param result
	 * @return ResponseEntity<Response<TransacaoDto>>
	 */
	@PostMapping(value = "/depositar")
	public ResponseEntity<Response<TransacaoDto>> depositar(Locale locale,
			@Valid @RequestBody TransacaoDto transacaoDto, BindingResult result) {
		log.info("Depositando R$ {} na conta {} no banco {}", transacaoDto.getValor(),
				transacaoDto.getContaDestino(), transacaoDto.getBancoDestino());
		
		Optional<Banco> banco = null;
		Optional<Conta> conta = null;
		if (transacaoDto.getBancoDestino() != null && transacaoDto.getContaDestino() != null && transacaoDto.getValor() != null) {
			banco = bancoService.buscar(transacaoDto.getBancoDestino());
			if (!banco.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank"));
			else {
				conta = contaService.buscar(banco.get(), transacaoDto.getContaDestino());
				if (!conta.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account"));
				if (!conta.get().creditar(transacaoDto.getValor()))
					result.addError(new ObjectError("conta", "error.invalid.value"));
			}
		}
		
		Response<TransacaoDto> response = new Response<TransacaoDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados da conta: {}", result.getAllErrors());
			result.getAllErrors().forEach(
					error -> response.getErrors().add(ms.getMessage(error.getDefaultMessage(), null, locale)));
			return ResponseEntity.badRequest().body(response);
		}
		
		Transacao transacao = new Transacao();
		transacao.setDestino(conta.get());
		transacao.setTipo(TipoTransacao.DEPOSITO);
		transacao.setValor(transacaoDto.getValor());
		transacaoService.persistir(transacao);
		contaService.persistir(conta.get());
		
		transacaoDto.setId(transacao.getId());
		transacaoDto.setData(DataUtils.converterParaString(transacao.getData(), locale));
		transacaoDto.setTipo(transacao.getTipo());
		response.setData(transacaoDto);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 
	 * Reduz o saldo de uma conta por um valor dado.
	 * 
	 * @param locale
	 * @param transacaoDto
	 * @param result
	 * @return ResponseEntity<Response<TransacaoDto>>
	 */
	@PostMapping(value = "/sacar")
	public ResponseEntity<Response<TransacaoDto>> sacar(Locale locale,
			@Valid @RequestBody TransacaoDto transacaoDto, BindingResult result) {
		log.info("Sacando R$ {} na conta {} no banco {}", transacaoDto.getValor(),
				transacaoDto.getContaOrigem(), transacaoDto.getBancoOrigem());
		
		Optional<Banco> banco = null;
		Optional<Conta> conta = null;
		if (transacaoDto.getBancoOrigem() != null && transacaoDto.getContaOrigem() != null
				&& transacaoDto.getSenha() != null && transacaoDto.getValor() != null) {
			banco = bancoService.buscar(transacaoDto.getBancoOrigem());
			if (!banco.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank"));
			else {
				conta = contaService.buscar(banco.get(), transacaoDto.getContaOrigem());
				if (!conta.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account"));
				else {
					if (!SenhaUtils.verificarValidade(transacaoDto.getSenha(), 
							conta.get().getCliente().getSenha()))
						result.addError(new ObjectError("conta", "error.invalid.password"));
					else if (!conta.get().debitar(transacaoDto.getValor()))
						result.addError(new ObjectError("conta", "error.invalid.balance"));
				}
			}
		}
		
		Response<TransacaoDto> response = new Response<TransacaoDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados da conta: {}", result.getAllErrors());
			result.getAllErrors().forEach(
					error -> response.getErrors().add(ms.getMessage(error.getDefaultMessage(), null, locale)));
			return ResponseEntity.badRequest().body(response);
		}
		
		Transacao transacao = new Transacao();
		transacao.setOrigem(conta.get());
		transacao.setTipo(TipoTransacao.SAQUE);
		transacao.setValor(transacaoDto.getValor());
		transacaoService.persistir(transacao);
		contaService.persistir(conta.get());
		
		transacaoDto.setId(transacao.getId());
		transacaoDto.setData(DataUtils.converterParaString(transacao.getData(), locale));
		transacaoDto.setTipo(transacao.getTipo());
		response.setData(transacaoDto);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 
	 * Reduz o saldo de uma conta de origem e aumenta o saldo de uma conta de destino por um dado valor.
	 * 
	 * @param locale
	 * @param transacaoDto
	 * @param result
	 * @return ResponseEntity<Response<TransacaoDto>>
	 */
	@PostMapping(value = "/transferir")
	public ResponseEntity<Response<TransacaoDto>> transferir(Locale locale,
			@Valid @RequestBody TransacaoDto transacaoDto, BindingResult result) {
		log.info("Transferindo R$ {} da conta {} no banco {} para a conta {} no banco {}", transacaoDto.getValor(),
				transacaoDto.getContaOrigem(), transacaoDto.getBancoOrigem(),
				transacaoDto.getContaDestino(), transacaoDto.getBancoDestino());
		
		Optional<Banco> bancoOrigem = null;
		Optional<Conta> contaOrigem = null;
		if (transacaoDto.getBancoOrigem() != null && transacaoDto.getContaOrigem() != null
				&& transacaoDto.getSenha() != null && transacaoDto.getValor() != null) {
			bancoOrigem = bancoService.buscar(transacaoDto.getBancoOrigem());
			if (!bancoOrigem.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank.origin"));
			else {
				contaOrigem = contaService.buscar(bancoOrigem.get(), transacaoDto.getContaOrigem());
				if (!contaOrigem.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account.origin"));
				else if (!SenhaUtils.verificarValidade(transacaoDto.getSenha(),
						contaOrigem.get().getCliente().getSenha()))
					result.addError(new ObjectError("conta", "error.invalid.password"));
				else if (!contaOrigem.get().debitar(transacaoDto.getValor()))
					result.addError(new ObjectError("conta", "error.invalid.balance"));
			}
		}
		
		Optional<Banco> bancoDestino = null;
		Optional<Conta> contaDestino = null;
		if (transacaoDto.getBancoDestino() != null && transacaoDto.getContaDestino() != null
				&& transacaoDto.getValor() != null) {
			bancoDestino = bancoService.buscar(transacaoDto.getBancoDestino());
			if (!bancoDestino.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank.destination"));
			else {
				contaDestino = contaService.buscar(bancoDestino.get(), transacaoDto.getContaDestino());
				if (!contaDestino.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account.destination"));
				if (!contaDestino.get().creditar(transacaoDto.getValor()))
					result.addError(new ObjectError("transacao", "error.invalid.value"));
			}
			
			if (transacaoDto.getBancoOrigem() == transacaoDto.getBancoDestino()
					&& transacaoDto.getContaOrigem() == transacaoDto.getContaDestino())
				result.addError(new ObjectError("conta", "error.equal.account"));
		}
		
		
		Response<TransacaoDto> response = new Response<TransacaoDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados das contas: {}", result.getAllErrors());
			result.getAllErrors().forEach(
					error -> response.getErrors().add(ms.getMessage(error.getDefaultMessage(), null, locale)));
			return ResponseEntity.badRequest().body(response);
		}
		
		Transacao transacao = new Transacao();
		transacao.setOrigem(contaOrigem.get());
		transacao.setDestino(contaDestino.get());
		transacao.setTipo(TipoTransacao.TRANSFERENCIA);
		transacao.setValor(transacaoDto.getValor());
		transacaoService.persistir(transacao);
		
		contaService.persistir(contaOrigem.get());
		contaService.persistir(contaDestino.get());
		
		transacaoDto.setId(transacao.getId());
		transacaoDto.setData(DataUtils.converterParaString(transacao.getData(), locale));
		transacaoDto.setTipo(transacao.getTipo());
		response.setData(transacaoDto);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 
	 * Retorna a lista de transações associadas à uma dada conta.
	 * 
	 * @param locale
	 * @param contaDto
	 * @param result
	 * @return ResponseEntity<Response<List<TransacaoDto>>>
	 */
	@PostMapping(value = "/extrato")
	public ResponseEntity<Response<List<TransacaoDto>>> extrato(Locale locale,
			@Valid @RequestBody ContaDto contaDto, BindingResult result) {
		log.info("Buscando extrato da conta: {}", contaDto.toString());
		
		Optional<Banco> banco = null;
		Optional<Conta> conta = null;
		if (contaDto.getCodigoBanco() != null && contaDto.getNumero() != null
				&& contaDto.getSenha() != null) {
			banco = bancoService.buscar(contaDto.getCodigoBanco());
			if (!banco.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank"));
			else {
				conta = contaService.buscar(banco.get(), contaDto.getNumero());
				if (!conta.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account"));
				else if (!SenhaUtils.verificarValidade(contaDto.getSenha(), conta.get().getCliente().getSenha()))
					result.addError(new ObjectError("conta", "error.invalid.password"));
			}
		}
		
		Response<List<TransacaoDto>> response = new Response<List<TransacaoDto>>();
		if (result.hasErrors()) {
			log.error("Erro validando dados da conta: {}", result.getAllErrors());
			result.getAllErrors().forEach(
					error -> response.getErrors().add(ms.getMessage(error.getDefaultMessage(), null, locale)));
			return ResponseEntity.badRequest().body(response);
		}
		
		List<TransacaoDto> transacoes = new ArrayList<TransacaoDto>();
		transacaoService.buscarTodas(conta.get()).get().forEach(
				transacao -> transacoes.add(new TransacaoDto(transacao, locale)));
				
		response.setData(transacoes);
		return ResponseEntity.ok(response);
	}
	
}
