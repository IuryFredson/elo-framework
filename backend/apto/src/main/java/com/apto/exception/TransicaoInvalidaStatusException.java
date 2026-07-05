package com.apto.exception;


import com.elo.exception.TransicaoInvalidaException;
public class TransicaoInvalidaStatusException extends TransicaoInvalidaException {
    public TransicaoInvalidaStatusException(String message) {
        super(message);
    }
}
