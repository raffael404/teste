package com.infoway.banking.exception;

public class SaldoInsuficienteException extends Exception 	{
	
	private static final long serialVersionUID = 5851345995244782789L;

	public SaldoInsuficienteException() {
		super("error.invalid.balance");
	}
	
}
