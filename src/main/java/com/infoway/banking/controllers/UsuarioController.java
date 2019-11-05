package com.infoway.banking.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infoway.banking.dtos.BancoDto;
import com.infoway.banking.dtos.ClienteDto;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.responses.Response;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ClienteService;
import com.infoway.banking.utils.SenhaUtils;

@RestController
@RequestMapping("/banking/usuario")
public class UsuarioController {

	private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private BancoService bancoService;
	
	/**
	 * 
	 * Cadastra um novo cliente no sistema.
	 * 
	 * @param clienteDto
	 * @param result
	 * @return ResponseEntity<Response<ClienteDto>>
	 */
	@PostMapping(value = "/cadastrar/cliente")
	public ResponseEntity<Response<ClienteDto>> cadastrarCliente(@Valid @RequestBody ClienteDto clienteDto, BindingResult result) {
		log.info("Cadastrando cliente: {}", clienteDto.toString());
		
		if (clienteService.buscar(clienteDto.getCpf()).isPresent())
			result.addError(new ObjectError("cliente", "CPF já existente."));
		
		Response<ClienteDto> response = new Response<ClienteDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro do cliente: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Cliente cliente = new Cliente();
		cliente.setCpf(clienteDto.getCpf());
		cliente.setNome(clienteDto.getNome());
		cliente.setSenha(SenhaUtils.criptografar(clienteDto.getSenha()));
		clienteService.persistir(cliente);
		
		response.setData(clienteDto);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 
	 * Cadastra um novo banco no sistema.
	 * 
	 * @param bancoDto
	 * @param result
	 * @return ResponseEntity<Response<BancoDto>>
	 */
	@PostMapping(value = "/cadastrar/banco")
	public ResponseEntity<Response<BancoDto>> cadastrarBanco( @Valid @RequestBody BancoDto bancoDto, BindingResult result) {
		log.info("Cadastrando banco: {}", bancoDto.toString());
		
		if (bancoService.buscar(bancoDto.getCodigo()).isPresent())
			result.addError(new ObjectError("banco", "Código já existente."));
		
		Response<BancoDto> response = new Response<BancoDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro do banco: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Banco banco = new Banco();
		banco.setCodigo(bancoDto.getCodigo());
		banco.setNome(bancoDto.getNome());
		banco.setSenha(SenhaUtils.criptografar(bancoDto.getSenha()));
		bancoService.persistir(banco);
		
		bancoDto.setSenha(banco.getSenha());
		response.setData(bancoDto);
		return ResponseEntity.ok(response);
	}
	
}
