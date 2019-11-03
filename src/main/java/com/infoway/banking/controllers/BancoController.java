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

import com.infoway.banking.dtos.BancoDto;
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
	 * Cadastra um novo banco no sistema.
	 * 
	 * @param bancoDto
	 * @param result
	 * @return ResponseEntity<Response<BancoDto>>
	 */
	@PostMapping
	public ResponseEntity<Response<BancoDto>> cadastrar( @Valid @RequestBody BancoDto bancoDto, BindingResult result) {
		log.info("Cadastrando banco: {}", bancoDto.toString());
		
		if (bancoService.buscarPorCodigo(bancoDto.getCodigo()).isPresent())
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
		bancoService.persistir(banco);
		
		response.setData(bancoDto);
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
		Optional<Banco> banco = bancoService.buscarPorCodigo(codigo);

		if (!banco.isPresent()) {
			log.info("Banco não encontrado para o codigo: {}", codigo);
			response.getErrors().add("Banco não encontrado para o codigo " + codigo);
			return ResponseEntity.badRequest().body(response);
		}

		response.setData(new BancoDto(banco.get().getCodigo(), banco.get().getNome()));
		return ResponseEntity.ok(response);
	}
	
}
