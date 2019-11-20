package com.infoway.banking.controllers;

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
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.responses.Response;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ClienteService;
import com.infoway.banking.services.ContaService;

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
	private MessageSource ms;
	
	public ContaController() {}
	
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
	@PreAuthorize("hasAuthority('create_conta')")
	public ResponseEntity<Response<ContaDto>> abrir(Locale locale,
			@Valid @RequestBody ContaDto contaDto, BindingResult result) {
		log.info("Abrindo conta: {}", contaDto.toString());
		
		Optional<Banco> banco = null;
		if (contaDto.getCodigoBanco() != null && contaDto.getNumero() != null) {
			banco = bancoService.buscarPorCodigo(contaDto.getCodigoBanco());
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
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Optional<Cliente> cliente = clienteService.buscarPorNomeUsuario(auth.getName());
		
		Conta conta = new Conta();
		conta.setNumero(contaDto.getNumero());
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
	@PreAuthorize("hasAuthority('delete_conta')")
	public ResponseEntity<Response<ContaDto>> fechar(Locale locale,
			@Valid @RequestBody ContaDto contaDto, BindingResult result) {
		log.info("Fechando conta: {}", contaDto.toString());
		
		Optional<Banco> banco = null;
		Optional<Conta> conta = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (contaDto.getCodigoBanco() != null && contaDto.getNumero() != null) {
			banco = bancoService.buscarPorCodigo(contaDto.getCodigoBanco());
			if (!banco.isPresent())
				result.addError(new ObjectError("banco", "error.nonexistent.bank"));
			else {
				conta = contaService.buscar(banco.get(), contaDto.getNumero());
				if (!conta.isPresent())
					result.addError(new ObjectError("conta", "error.nonexistent.account"));
				else if (!conta.get().getCliente().getUsername().contentEquals(auth.getName()))
					result.addError(new ObjectError("conta", "error.unauthorized"));
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
	
}
