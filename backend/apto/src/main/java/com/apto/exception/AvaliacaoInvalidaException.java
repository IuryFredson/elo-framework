package com.apto.exception;


import com.elo.exception.RegraNegocioException;
public class AvaliacaoInvalidaException extends RegraNegocioException {
    public AvaliacaoInvalidaException(String message) {
        super(message);
    }
}
