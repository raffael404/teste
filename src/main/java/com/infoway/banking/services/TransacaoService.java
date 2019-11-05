package com.infoway.banking.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Transacao;
import com.infoway.banking.repositories.TransacaoRepository;

@Service
public class TransacaoService {
	
	private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
	
	@Autowired
	private TransacaoRepository transacaoRepository;
	
	/**
	 * 
	 * Adiciona uma nova transação na base de dados.
	 * 
	 * @param transacao
	 * @return Transacao
	 */
	public Transacao persistir(Transacao transacao) {
		log.info("Persistindo transação: {}", transacao);
		return transacaoRepository.save(transacao);
	}
	
	/**
	 * 
	 * Retorna a lista de transações de uma conta.
	 * 
	 * @param conta
	 * @return Optional<Cliente>
	 */
	public Optional<List<Transacao>> buscarTodas(Conta conta) {
		log.info("Buscando a lista de transações da conta {}", conta);
		return Optional.ofNullable(transacaoRepository.findAllByOrigemOrDestino(conta, conta));
	}
	
}
