package com.apto.exception;


import com.elo.exception.RegraNegocioException;
public class ModeracaoInvalidaException extends RegraNegocioException {
    public ModeracaoInvalidaException(String message) {
        super(message);
    }
}