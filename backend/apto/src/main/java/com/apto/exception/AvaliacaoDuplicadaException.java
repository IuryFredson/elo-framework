package com.apto.exception;


import com.elo.exception.RegraNegocioException;
public class AvaliacaoDuplicadaException extends RegraNegocioException {
    public AvaliacaoDuplicadaException(String message) {
        super(message);
    }
}
