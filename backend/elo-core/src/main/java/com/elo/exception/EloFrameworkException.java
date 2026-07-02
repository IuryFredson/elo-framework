package com.elo.exception;

public abstract class EloFrameworkException extends RuntimeException {

	protected EloFrameworkException(String mensagem) {
		super(mensagem);
	}

	protected EloFrameworkException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
