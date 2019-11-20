package com.infoway.banking.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import com.infoway.banking.dtos.BancoDto;
import com.infoway.banking.dtos.ClienteDto;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.entities.Role;
import com.infoway.banking.responses.Response;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ClienteService;
import com.infoway.banking.services.RoleService;
import com.infoway.banking.utils.SenhaUtils;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
    private MessageSource ms;
	
	/**
	 * 
	 * Cadastra um novo cliente no sistema.
	 * 
	 * @param locale
	 * @param clienteDto
	 * @param result
	 * @return ResponseEntity<Response<ClienteDto>>
	 */
	@PostMapping(value = "/cadastrar/cliente")
	public ResponseEntity<Response<ClienteDto>> cadastrarCliente(Locale locale,
			@Valid @RequestBody ClienteDto clienteDto, BindingResult result) {
		log.info("Cadastrando cliente: {}", clienteDto.toString());
		
		if (clienteDto.getCpf() != null && clienteService.buscarPorCpf(clienteDto.getCpf()).isPresent())
			result.addError(new ObjectError("cliente", "error.existing.cpf"));
		if (clienteDto.getNomeUsuario() != null
				&& clienteService.buscarPorNomeUsuario(clienteDto.getNomeUsuario()).isPresent())
			result.addError(new ObjectError("cliente", "error.existing.username"));
		
		Response<ClienteDto> response = new Response<ClienteDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados do cliente: {}", result.getAllErrors());
			result.getAllErrors().forEach(
					error -> response.getErrors().add(ms.getMessage(error.getDefaultMessage(), null, locale)));
			return ResponseEntity.badRequest().body(response);
		}
		
		Cliente cliente = new Cliente();
		cliente.setCpf(clienteDto.getCpf());
		cliente.setNome(clienteDto.getNomeCliente());
		cliente.setUsername(clienteDto.getNomeUsuario());
		cliente.setEmail(clienteDto.getEmail());
		cliente.setPassword("{bcrypt}" + SenhaUtils.criptografar(clienteDto.getSenha()));
		cliente.setAccountNonExpired(true);
		cliente.setAccountNonLocked(true);
		cliente.setCredentialsNonExpired(true);
		cliente.setEnabled(true);
		List<Role> roles = new ArrayList<Role>();
		roles.add(roleService.buscar("ROLE_cliente").get());
		cliente.setRoles(roles);
		clienteService.persistir(cliente);
		
		clienteDto.setSenha(cliente.getPassword());
		response.setData(clienteDto);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 
	 * Cadastra um novo banco no sistema.
	 * 
	 * @param locale
	 * @param bancoDto
	 * @param result
	 * @return ResponseEntity<Response<BancoDto>>
	 */
	@PostMapping(value = "/cadastrar/banco")
	public ResponseEntity<Response<BancoDto>> cadastrarBanco(Locale locale,
			@Valid @RequestBody BancoDto bancoDto, BindingResult result) {
		log.info("Cadastrando banco: {}", bancoDto.toString());
		
		if (bancoDto.getCodigo() != null && bancoService.buscarPorCodigo(bancoDto.getCodigo()).isPresent())
			result.addError(new ObjectError("banco", "error.existing.code"));
		if (bancoDto.getNomeUsuario() != null && bancoService.buscarPorNomeUsuario(bancoDto.getNomeUsuario()).isPresent())
			result.addError(new ObjectError("banco", "error.existing.username"));
		
		Response<BancoDto> response = new Response<BancoDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados do banco: {}", result.getAllErrors());
			result.getAllErrors().forEach(
					error -> response.getErrors().add(ms.getMessage(error.getDefaultMessage(), null, locale)));
			return ResponseEntity.badRequest().body(response);
		}
		
		Banco banco = new Banco();
		banco.setCodigo(bancoDto.getCodigo());
		banco.setNome(bancoDto.getNomeBanco());
		banco.setEmail(bancoDto.getEmail());
		banco.setPassword("{bcrypt}" + SenhaUtils.criptografar(bancoDto.getSenha()));
		banco.setUsername(bancoDto.getNomeUsuario());
		banco.setAccountNonExpired(true);
		banco.setAccountNonLocked(true);
		banco.setCredentialsNonExpired(true);
		banco.setEnabled(true);
		List<Role> roles = new ArrayList<Role>();
		roles.add(roleService.buscar("ROLE_banco").get());
		banco.setRoles(roles);
		bancoService.persistir(banco);
		
		bancoDto.setSenha(banco.getPassword());
		response.setData(bancoDto);
		return ResponseEntity.ok(response);
	}
	
}
