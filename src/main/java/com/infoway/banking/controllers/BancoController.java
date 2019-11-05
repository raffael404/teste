package com.infoway.banking.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infoway.banking.dtos.AgenciaDto;
import com.infoway.banking.dtos.BancoDto;
import com.infoway.banking.entities.Agencia;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.responses.Response;
import com.infoway.banking.services.BancoService;

@RestController
@RequestMapping("/banking/bancos")
public class BancoController {
	
	private static final Logger log = LoggerFactory.getLogger(BancoController.class);
	
	@Autowired
	private BancoService bancoService;
	
	public BancoController() {}
	
	/**
	 * 
	 * Cadastra uma nova agencia no sistema.
	 * 
	 * @param agenciaDto
	 * @param senha
	 * @param result
	 * @return ResponseEntity<Response<ClienteDto>>
	 */
	@PostMapping(value = "/cadastrar/agencia")
	public ResponseEntity<Response<AgenciaDto>> cadastrarCliente(@Valid @RequestBody AgenciaDto agenciaDto, BindingResult result) {
		log.info("Cadastrando agencia: {}", agenciaDto.toString());
		
		Optional<Banco> banco = bancoService.buscar(agenciaDto.getCodigoBanco());
		if (!banco.isPresent())
			result.addError(new ObjectError("banco", "Banco inexistente."));
		
		Response<AgenciaDto> response = new Response<AgenciaDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados da agencia: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Agencia agencia = new Agencia();
		agencia.setCnpj(agenciaDto.getCnpj());
		agencia.setNumero(agenciaDto.getNumero());
		agencia.setBanco(banco.get());
		
		banco.get().getAgencias().add(agencia);
		bancoService.persistir(banco.get());
		
		response.setData(agenciaDto);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * Retorna um banco, dado um código.
	 * 
	 * @param codigo
	 * @return ResponseEntity<Response<BancoDto>>
	 */
	@GetMapping(value = "/codigo/{codigo}")
	public ResponseEntity<Response<BancoDto>> buscarPorCodigo(@PathVariable("codigo") String codigo) {
		log.info("Buscando banco por codigo: {}", codigo);
		Response<BancoDto> response = new Response<BancoDto>();
		Optional<Banco> banco = bancoService.buscar(codigo);

		if (!banco.isPresent()) {
			log.info("Banco não encontrado para o codigo: {}", codigo);
			response.getErrors().add("Banco não encontrado para o codigo " + codigo);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(new BancoDto(banco.get()));
		return ResponseEntity.ok(response);
	}
	
}
