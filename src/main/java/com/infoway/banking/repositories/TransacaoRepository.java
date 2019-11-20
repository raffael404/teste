package com.infoway.banking.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
	@Transactional(readOnly = true)
	List<Transacao> findAllByOrigemOrDestino(Conta origem, Conta destino);
}
