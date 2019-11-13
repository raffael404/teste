package com.infoway.banking.exception;

public class ValorInvalidoException extends Exception {
	
	private static final long serialVersionUID = -1485690983261234225L;

	public ValorInvalidoException() {
		super("error.invalid.value");
	}
	
}
