package com.infoway.banking.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infoway.banking.entities.Cliente;
import com.infoway.banking.entities.User;
import com.infoway.banking.repositories.ClienteRepository;

@Service
public class ClienteService {
	
	private static final Logger log = LoggerFactory.getLogger(ClienteService.class);
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	/**
	 * 
	 * Cadastra um novo cliente na base de dados.
	 * 
	 * @param cliente
	 * @return Cliente
	 */
	public Cliente persistir(Cliente cliente) {
		log.info("Persistindo cliente: {}", cliente);
		return clienteRepository.save(cliente);
	}
	
	/**
	 * 
	 * Retorna um cliente, dado um CPF.
	 * 
	 * @param cpf
	 * @return Optional<Cliente>
	 */
	public Optional<Cliente> buscarPorCpf(String cpf) {
		log.info("Buscando um cliente com o CPF {}", cpf);
		return clienteRepository.findByCpf(cpf);
	}
	
	/**
	 * 
	 * Retorna um cliente, dado um nome de usuário.
	 * 
	 * @param nomeUsuario
	 * @return Optional<Cliente>
	 */
	public Optional<Cliente> buscarPorNomeUsuario(String nomeUsuario) {
		log.info("Buscando um cliente com o nome de usuário {}", nomeUsuario);
		Optional<User> user = clienteRepository.findByUsername(nomeUsuario);
		if (user.isPresent())
			return Optional.ofNullable((Cliente) user.get());
		else return Optional.empty();
	}
	
	/**
	 * 
	 * Remove um cliente da base de dados.
	 * 
	 * @param cpf
	 */
	public void remover(String cpf) {
		log.info("Removendo cliente com o CPF {}", cpf);
		clienteRepository.deleteByCpf(cpf);
	}

}
