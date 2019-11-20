package com.infoway.banking.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.infoway.banking.entities.User;

public interface UserDetailRepository extends JpaRepository<User, Integer> {
	@Transactional(readOnly = true)
	Optional<User> findByUsername(String name);
}
