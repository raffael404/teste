package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;

import com.infoway.banking.entities.Cliente;

public class ClienteDto extends UsuarioDto {
	
	private String cpf;
	private String nomeCliente;
	
	public ClienteDto() {}
	
	public ClienteDto(Cliente cliente) {
		this.cpf = cliente.getCpf();
		this.nomeCliente = cliente.getNome();
		this.setEmail(cliente.getEmail());
		this.setNomeUsuario(cliente.getUsername());
		this.setSenha(cliente.getPassword());
	}
	
	@NotEmpty(message = "error.empty.cpf")
	@CPF(message = "error.invalid.cpf")
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	@NotEmpty(message = "error.empty.client.name")
	@Length(min = 5, max = 255, message = "error.size.client.name")
	public String getNomeCliente() {
		return nomeCliente;
	}
	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	@Override
	public String toString() {
		return "NovoClienteDto [cpf=" + cpf + ", nomeCliente=" + nomeCliente + ", usuario=" + super.toString() + "]";
	}

}
