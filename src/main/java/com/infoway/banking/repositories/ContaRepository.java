package com.infoway.banking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long> {
	@Transactional(readOnly = true)
	Conta findByNumeroAndBanco(String numero, Banco banco);
}
