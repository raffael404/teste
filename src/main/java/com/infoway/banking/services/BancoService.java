package com.infoway.banking.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infoway.banking.entities.Banco;
import com.infoway.banking.repositories.BancoRepository;

@Service
public class BancoService {
	
	private static final Logger log = LoggerFactory.getLogger(BancoService.class);
	
	@Autowired
	private BancoRepository bancoRepository;
	
	public Banco persistir(Banco banco) {
		log.info("Persistindo banco: {}", banco);
		return bancoRepository.save(banco);
	}
	
	public Optional<Banco> buscarPorCodigo(String codigo) {
		log.info("Buscando um banco com o codigo {}", codigo);
		return bancoRepository.findById(codigo);
	}
	
}
