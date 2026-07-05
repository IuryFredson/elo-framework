package com.studybuddy.exception;


import com.elo.exception.RegraNegocioException;
public class EstudanteInativoException extends RegraNegocioException {

    public EstudanteInativoException(String message) {
        super(message);
    }
}
