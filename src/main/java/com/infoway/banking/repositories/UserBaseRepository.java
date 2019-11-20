package com.infoway.banking.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import com.infoway.banking.entities.User;

@NoRepositoryBean
public interface UserBaseRepository<T extends User> extends CrudRepository<T, Integer> {
	@Transactional(readOnly = true)
	Optional<User> findByUsername(String name);
}
