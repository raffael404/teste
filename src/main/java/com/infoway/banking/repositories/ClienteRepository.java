package com.infoway.banking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infoway.banking.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, String> {
	
}
