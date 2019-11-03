package com.infoway.banking.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.repositories.ContaRepository;

@Service
public class ContaService {
	
	private static final Logger log = LoggerFactory.getLogger(ContaService.class);
	
	@Autowired
	private ContaRepository contaRepository;
	
	/**
	 * 
	 * Cadastra uma nova conta na base de dados.
	 * 
	 * @param conta
	 * @return Conta
	 */
	public Conta persistir(Conta conta) {
		log.info("Persistindo conta: {}", conta);
		return contaRepository.save(conta);
	}
	
	/**
	 * 
	 * Retorna uma conta, dados um banco e um número.
	 * 
	 * @param banco
	 * @param numero
	 * @return Conta
	 */
	public Conta buscar(Banco banco, String numero) {
		log.info("Buscando uma conta no banco {} com o numero {}", banco.getNome(), numero);
		return contaRepository.findByBancoAndNumero(banco, numero);
	}
	
	/**
	 * 
	 * Remove uma conta da base de dados.
	 * 
	 * @param banco
	 * @param numero
	 */
	public void remover(Banco banco, String numero) {
		log.info("Removendo a conta de numero {} no banco {}", numero, banco.getNome());
		contaRepository.deleteById(contaRepository.findByBancoAndNumero(banco, numero).getId());
	}
	
}
