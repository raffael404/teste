package com.infoway.banking.repositories;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.infoway.banking.entities.Banco;

public interface BancoRepository extends UserBaseRepository<Banco> {
	@Transactional(readOnly = true)
	Optional<Banco> findByCodigo(String codigo);
	@Transactional
	void deleteByCodigo(String codigo);
}
