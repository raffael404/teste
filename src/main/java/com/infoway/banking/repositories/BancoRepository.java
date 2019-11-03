package com.infoway.banking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infoway.banking.entities.Banco;

public interface BancoRepository extends JpaRepository<Banco, String>{
	Banco findByNome(String codigo);
}
