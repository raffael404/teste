package com.infoway.banking.repositories;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.infoway.banking.entities.Cliente;

public interface ClienteRepository extends UserBaseRepository<Cliente> {
	@Transactional(readOnly = true)
	Optional<Cliente> findByCpf(String cpf);
	@Transactional
	void deleteByCpf(String cpf);
}
