package com.infoway.banking.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infoway.banking.entities.Role;
import com.infoway.banking.repositories.RoleRepository;

@Service
public class RoleService {
	
	private static final Logger log = LoggerFactory.getLogger(BancoService.class);
	
	@Autowired
	private RoleRepository roleRepository;
	
	/**
	 * 
	 * Cadastra um novo role na base de dados.
	 * 
	 * @param role
	 * @return Role
	 */
	public Role persistir(Role role) {
		log.info("Persistindo role: {}", role);
		return roleRepository.save(role);
	}
	
	/**
	 * 
	 * Retorna um role, dado um nome.
	 * 
	 * @param name
	 * @return Optional<Role>
	 */
	public Optional<Role> buscar(String name) {
		log.info("Buscando um role com o nome {}", name);
		return roleRepository.findByName(name);
	}
	
	/**
	 * 
	 * Remove um role da base de dados.
	 * 
	 * @param name
	 */
	public void remover(String name) {
		log.info("Removendo role com o nome {}", name);
		roleRepository.deleteByName(name);
	}
	
}
