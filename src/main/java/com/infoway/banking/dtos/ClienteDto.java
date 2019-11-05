package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.infoway.banking.entities.Cliente;

public class ClienteDto extends UsuarioDto {
	
	private String cpf;
	private String nome;
	
	public ClienteDto() {}

	public ClienteDto(Cliente cliente) {
		this.cpf = cliente.getCpf();
		this.nome = cliente.getNome();
	}
	
	@NotEmpty(message = "CPF não pode ser vazio.")
	@CPF(message = "CPF inválido.")
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	@NotEmpty(message = "Nome não pode ser vazio")
	@Length(min = 5, max = 255, message = "Nome deve conter entre 5 e 255 caracteres.")
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return "ClienteDto [cpf=" + cpf + ", nome=" + nome + "]";
	}
	
}
