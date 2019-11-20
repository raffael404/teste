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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infoway.banking.dtos.ContaDto;
import com.infoway.banking.dtos.TransacaoDto;
import com.infoway.banking.dtos.TransacaoSimplesDto;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;
import com.infoway.banking.exception.SaldoInsuficienteException;
import com.infoway.banking.exception.ValorInvalidoException;
import com.infoway.banking.responses.Response;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ContaService;
import com.infoway.banking.services.TransacaoService;
import com.infoway.banking.utils.DataUtils;

@RestController
@RequestMapping("/conta")
public class TransacaoController {
	
private static final Logger log = LoggerFactory.getLogger(ContaController.class);
	
	@Autowired
	private ContaService contaService;
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private TransacaoService transacaoService;
	
	@Autowired
	private MessageSource ms;
	
	public TransacaoController() {}
	
	/**
	 * 
	 * Aumenta o saldo de uma conta por um valor dado.
	 * 
	 * @param locale
	 * @param transacaoDto
	 * @param result
	 * @return ResponseEntity<Response<TransacaoSimplesDto>>
	 */
	@PostMapping(value = "/depositar")
	@PreAuthorize("hasAuthority('create_transacao')")
	public ResponseEntity<Response<TransacaoSimplesDto>> depositar(Locale locale,
			@Valid @RequestBody TransacaoSimplesDto transacaoDto, BindingResult result) {
		log.info("Depositando R$ {} na conta {} no banco {}", transacaoDto.getValor(),
				transacaoDto.getNumeroConta(), transacaoDto.getCodigoBanco());
		
		Optional<Banco> banco = null;
		Optional<Conta> conta = null;
		if (transacaoDto.getCodigoBanco() != null && transacaoDto.getNumeroConta() != null
				&& transacaoDto.getValor() != null) {
			banco = bancoService.buscarPorCodigo(transacaoDto.getCodigoBanco());
			if (!banco.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank"));
			else {
				conta = contaService.buscar(banco.get(), transacaoDto.getNumeroConta());
				if (!conta.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account"));
				else try {
					conta.get().creditar(transacaoDto.getValor());
				} catch (ValorInvalidoException e) {
					result.addError(new ObjectError("conta", e.getMessage()));
				}				
			}
		}
		
		Response<TransacaoSimplesDto> response = new Response<TransacaoSimplesDto>();
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
	 * @return ResponseEntity<Response<TransacaoSimplesDto>>
	 */
	@PostMapping(value = "/sacar")
	@PreAuthorize("hasAuthority('create_transacao')")
	public ResponseEntity<Response<TransacaoSimplesDto>> sacar(Locale locale,
			@Valid @RequestBody TransacaoSimplesDto transacaoDto, BindingResult result) {
		log.info("Sacando R$ {} na conta {} no banco {}", transacaoDto.getValor(),
				transacaoDto.getNumeroConta(), transacaoDto.getCodigoBanco());
		
		Optional<Banco> banco = null;
		Optional<Conta> conta = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (transacaoDto.getCodigoBanco() != null && transacaoDto.getNumeroConta() != null
				&& transacaoDto.getValor() != null) {
			banco = bancoService.buscarPorCodigo(transacaoDto.getCodigoBanco());
			if (!banco.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank"));
			else {
				conta = contaService.buscar(banco.get(), transacaoDto.getNumeroConta());
				if (!conta.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account"));
				else {
					if (!conta.get().getCliente().getUsername().contentEquals(auth.getName()))
						result.addError(new ObjectError("conta", "error.unauthorized"));
					else try {
						conta.get().debitar(transacaoDto.getValor());
					} catch (SaldoInsuficienteException | ValorInvalidoException e) {
						result.addError(new ObjectError("conta", e.getMessage()));
					}
				}
			}
		}
		
		Response<TransacaoSimplesDto> response = new Response<TransacaoSimplesDto>();
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
	@PreAuthorize("hasAuthority('create_transacao')")
	public ResponseEntity<Response<TransacaoDto>> transferir(Locale locale,
			@Valid @RequestBody TransacaoDto transacaoDto, BindingResult result) {
		log.info("Transferindo R$ {} da conta {} no banco {} para a conta {} no banco {}", transacaoDto.getValor(),
				transacaoDto.getContaOrigem(), transacaoDto.getBancoOrigem(),
				transacaoDto.getContaDestino(), transacaoDto.getBancoDestino());
		
		Optional<Banco> bancoOrigem = null;
		Optional<Conta> contaOrigem = null;
		boolean operacaoAutorizada = false;
		if (transacaoDto.getBancoOrigem() != null && transacaoDto.getContaOrigem() != null
				&& transacaoDto.getValor() != null) {
			bancoOrigem = bancoService.buscarPorCodigo(transacaoDto.getBancoOrigem());
			if (!bancoOrigem.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank.origin"));
			else {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				contaOrigem = contaService.buscar(bancoOrigem.get(), transacaoDto.getContaOrigem());
				if (!contaOrigem.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account.origin"));
				else if (!contaOrigem.get().getCliente().getUsername().contentEquals(auth.getName()))
					result.addError(new ObjectError("conta", "error.unauthorized"));
				else {
					operacaoAutorizada = true;
					try {
						contaOrigem.get().debitar(transacaoDto.getValor());
					} catch (ValorInvalidoException | SaldoInsuficienteException e) {
						operacaoAutorizada = false;
						result.addError(new ObjectError("conta", e.getMessage()));
					}
				}
			}
		}
		
		Optional<Banco> bancoDestino = null;
		Optional<Conta> contaDestino = null;
		if (transacaoDto.getBancoDestino() != null && transacaoDto.getContaDestino() != null
				&& transacaoDto.getValor() != null) {
			bancoDestino = bancoService.buscarPorCodigo(transacaoDto.getBancoDestino());
			if (!bancoDestino.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank.destination"));
			else {
				contaDestino = contaService.buscar(bancoDestino.get(), transacaoDto.getContaDestino());
				if (!contaDestino.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account.destination"));
				else if (operacaoAutorizada)
					try {
						contaDestino.get().creditar(transacaoDto.getValor());
					} catch (ValorInvalidoException e) {}
			}
			
			if (transacaoDto.getBancoOrigem().contentEquals(transacaoDto.getBancoDestino())
					&& transacaoDto.getContaOrigem().contentEquals(transacaoDto.getContaDestino()))
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
		transacaoDto.setTipo(TipoTransacao.TRANSFERENCIA);
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
	@PreAuthorize("hasAuthority('read_extrato')")
	public ResponseEntity<Response<List<TransacaoDto>>> extrato(Locale locale,
			@Valid @RequestBody ContaDto contaDto, BindingResult result) {
		log.info("Buscando extrato da conta: {}", contaDto.toString());
		
		Optional<Banco> banco = null;
		Optional<Conta> conta = null;
		if (contaDto.getCodigoBanco() != null && contaDto.getNumero() != null) {
			banco = bancoService.buscarPorCodigo(contaDto.getCodigoBanco());
			if (!banco.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank"));
			else {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				conta = contaService.buscar(banco.get(), contaDto.getNumero());
				if (!conta.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account"));
				else if (!conta.get().getCliente().getUsername().contentEquals(auth.getName()))
					result.addError(new ObjectError("conta", "error.unauthorized"));
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
