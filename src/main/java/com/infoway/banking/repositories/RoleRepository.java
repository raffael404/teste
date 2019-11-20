package com.infoway.banking.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.infoway.banking.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	@Transactional(readOnly = true)
	Optional<Role> findByName(String name);
	void deleteByName(String name);
}
