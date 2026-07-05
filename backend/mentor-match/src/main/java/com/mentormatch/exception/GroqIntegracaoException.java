package com.mentormatch.exception;

public class GroqIntegracaoException extends RuntimeException {
    public GroqIntegracaoException(String message) { super(message); }
    public GroqIntegracaoException(String message, Throwable cause) { super(message, cause); }
}
