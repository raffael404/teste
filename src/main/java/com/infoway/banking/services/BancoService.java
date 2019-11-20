package com.infoway.banking.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.User;
import com.infoway.banking.repositories.BancoRepository;

@Service
public class BancoService {
	
	private static final Logger log = LoggerFactory.getLogger(BancoService.class);
	
	@Autowired
	private BancoRepository bancoRepository;
	
	/**
	 * 
	 * Cadastra um novo banco na base de dados.
	 * 
	 * @param banco
	 * @return Banco
	 */
	public Banco persistir(Banco banco) {
		log.info("Persistindo banco: {}", banco);
		return bancoRepository.save(banco);
	}
	
	/**
	 * 
	 * Retorna um banco, dado um código.
	 * 
	 * @param codigo
	 * @return Optional<Banco>
	 */
	public Optional<Banco> buscarPorCodigo(String codigo) {
		log.info("Buscando um banco com o codigo {}", codigo);
		return bancoRepository.findByCodigo(codigo);
	}
	
	/**
	 * 
	 * Retorna um banco, dado um nome de usuário.
	 * 
	 * @param nomeUsuario
	 * @return Optional<Banco>
	 */
	public Optional<Banco> buscarPorNomeUsuario(String nomeUsuario) {
		log.info("Buscando um banco com o usuário {}", nomeUsuario);
		Optional<User> user = bancoRepository.findByUsername(nomeUsuario);
		if (user.isPresent())
			return Optional.ofNullable((Banco)user.get());
		else return Optional.empty();
	}
	
	/**
	 * 
	 * Remove um banco da base de dados.
	 * 
	 * @param codigo
	 */
	public void remover(String codigo) {
		log.info("Removendo banco com o codigo {}", codigo);
		bancoRepository.deleteByCodigo(codigo);
	}
	
}
