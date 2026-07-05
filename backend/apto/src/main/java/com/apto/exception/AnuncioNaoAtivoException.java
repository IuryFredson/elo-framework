package com.apto.exception;


import com.elo.exception.RegraNegocioException;
public class AnuncioNaoAtivoException extends RegraNegocioException {
    public AnuncioNaoAtivoException(String message) {
        super(message);
    }
}
