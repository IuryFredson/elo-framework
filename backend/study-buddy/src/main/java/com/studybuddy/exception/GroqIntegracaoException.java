package com.studybuddy.exception;

public class GroqIntegracaoException extends RuntimeException {

    public GroqIntegracaoException(String mensagem) {
        super(mensagem);
    }

    public GroqIntegracaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
