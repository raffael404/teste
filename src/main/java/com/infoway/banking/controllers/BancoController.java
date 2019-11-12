package com.infoway.banking.controllers;

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

import com.infoway.banking.dtos.AgenciaDto;
import com.infoway.banking.entities.Agencia;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.responses.Response;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.utils.SenhaUtils;

@RestController
@RequestMapping("/banco")
public class BancoController {
	
	private static final Logger log = LoggerFactory.getLogger(BancoController.class);
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private MessageSource ms;
	
	public BancoController() {}
	
	/**
	 * 
	 * Cadastra uma nova agencia no sistema.
	 * 
	 * @param locale
	 * @param agenciaDto
	 * @param result
	 * @return ResponseEntity<Response<AgenciaDto>>
	 */
	@PostMapping(value = "/cadastrar/agencia")
	public ResponseEntity<Response<AgenciaDto>> cadastrarAgencia(Locale locale,
			@Valid @RequestBody AgenciaDto agenciaDto, BindingResult result) {
		log.info("Cadastrando agencia: {}", agenciaDto.toString());
		
		Optional<Banco> banco = bancoService.buscar(agenciaDto.getCodigoBanco());
		if (!banco.isPresent())
			result.addError(new ObjectError("banco", "error.nonexistent.bank"));
		else {
			if (!SenhaUtils.verificarValidade(agenciaDto.getSenha(), banco.get().getSenha()))
				result.addError(new ObjectError("banco", "error.invalid.password"));
			for (Agencia agencia : banco.get().getAgencias()) {
				if (agencia.getNumero() == agenciaDto.getNumero()) {
					result.addError(new ObjectError("agencia", "error.existing.number"));
					break;
				}
			}
			for (Agencia agencia : banco.get().getAgencias()) {
				if (agencia.getCnpj() == agenciaDto.getCnpj()) {
					result.addError(new ObjectError("agencia", "error.existing.cnpj"));
					break;
				}
			}
		}
		
		Response<AgenciaDto> response = new Response<AgenciaDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados da agencia: {}", result.getAllErrors());
			result.getAllErrors().forEach(
					error -> response.getErrors().add(ms.getMessage(error.getDefaultMessage(), null, locale)));
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
	
}
