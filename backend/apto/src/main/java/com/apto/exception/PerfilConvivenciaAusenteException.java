package com.apto.exception;


import com.elo.exception.RegraNegocioException;
public class PerfilConvivenciaAusenteException extends RegraNegocioException {
    public PerfilConvivenciaAusenteException(String message) {
        super(message);
    }
}